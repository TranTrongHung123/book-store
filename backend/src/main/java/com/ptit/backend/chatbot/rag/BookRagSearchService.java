package com.ptit.backend.chatbot.rag;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * RAG Service
 *
 * Sử dụng MySQL FULLTEXT Search để tìm sách liên quan đến câu hỏi của user,
 * sau đó JOIN với các bảng author, promotion, flash_sale để tạo ra một
 * "Context Payload" giàu thông tin nhất để nhúng vào System Prompt cho AI.
 *
 * Thứ tự ưu tiên tìm kiếm:
 * 1. FULLTEXT search trên book(title, description)
 * 2. FULLTEXT search trên author(name) → lấy sách của tác giả đó
 * 3. LIKE fallback trên title, description, author name
 * 4. General context (sách nổi bật + flash sale + promo) nếu query rỗng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookRagSearchService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Native SQL query sử dụng MySQL FULLTEXT BOOLEAN MODE search trên sách.
     * Giới hạn 8 kết quả, sắp xếp theo relevance score
     */
    private static final String RAG_FULLTEXT_QUERY = """
            SELECT
                b.book_id,
                b.title,
                b.description,
                b.selling_price,
                b.total_stock,
                b.publication_year,
                COALESCE(GROUP_CONCAT(DISTINCT a.name ORDER BY a.name SEPARATOR ', '), 'Chưa xác định') AS authors,
                COALESCE(pub.name, 'Chưa xác định') AS publisher_name,
                COALESCE(GROUP_CONCAT(DISTINCT cat.name ORDER BY cat.name SEPARATOR ', '), '') AS categories,
                fsi.flash_sale_price,
                fsc.name AS campaign_name,
                MATCH(b.title, b.description) AGAINST(? IN BOOLEAN MODE) AS relevance_score
            FROM book b
            LEFT JOIN book_author  ba  ON b.book_id = ba.book_id
            LEFT JOIN author        a  ON ba.author_id = a.author_id
            LEFT JOIN publisher   pub  ON b.publisher_id = pub.publisher_id
            LEFT JOIN book_category bc ON b.book_id = bc.book_id
            LEFT JOIN category    cat  ON bc.category_id = cat.category_id
            LEFT JOIN flash_sale_item fsi ON b.book_id = fsi.book_id
            LEFT JOIN flash_sale_campaign fsc
                   ON fsi.campaign_id = fsc.campaign_id
                  AND fsc.status = 'ACTIVE'
                  AND fsc.start_time <= NOW()
                  AND fsc.end_time   >= NOW()
            WHERE b.status = 1
              AND MATCH(b.title, b.description) AGAINST(? IN BOOLEAN MODE)
            GROUP BY
                b.book_id, b.title, b.description, b.selling_price, b.total_stock,
                b.publication_year, pub.name, fsi.flash_sale_price, fsc.name
            ORDER BY relevance_score DESC
            LIMIT 8
            """;

    /**
     * FULLTEXT search trên bảng author, sau đó lấy sách của tác giả đó
     */
    private static final String AUTHOR_FULLTEXT_QUERY = """
            SELECT
                b.book_id,
                b.title,
                b.description,
                b.selling_price,
                b.total_stock,
                b.publication_year,
                COALESCE(GROUP_CONCAT(DISTINCT a2.name ORDER BY a2.name SEPARATOR ', '), 'Chưa xác định') AS authors,
                COALESCE(pub.name, 'Chưa xác định') AS publisher_name,
                COALESCE(GROUP_CONCAT(DISTINCT cat.name ORDER BY cat.name SEPARATOR ', '), '') AS categories,
                fsi.flash_sale_price,
                fsc.name AS campaign_name,
                1.0 AS relevance_score
            FROM author a
            JOIN book_author ba ON a.author_id = ba.author_id
            JOIN book b ON ba.book_id = b.book_id
            LEFT JOIN book_author  ba2 ON b.book_id = ba2.book_id
            LEFT JOIN author       a2  ON ba2.author_id = a2.author_id
            LEFT JOIN publisher   pub  ON b.publisher_id = pub.publisher_id
            LEFT JOIN book_category bc ON b.book_id = bc.book_id
            LEFT JOIN category    cat  ON bc.category_id = cat.category_id
            LEFT JOIN flash_sale_item fsi ON b.book_id = fsi.book_id
            LEFT JOIN flash_sale_campaign fsc
                   ON fsi.campaign_id = fsc.campaign_id
                  AND fsc.status = 'ACTIVE'
                  AND fsc.start_time <= NOW()
                  AND fsc.end_time   >= NOW()
            WHERE b.status = 1
              AND MATCH(a.name, a.biography) AGAINST(? IN BOOLEAN MODE)
            GROUP BY
                b.book_id, b.title, b.description, b.selling_price, b.total_stock,
                b.publication_year, pub.name, fsi.flash_sale_price, fsc.name
            ORDER BY b.created_at DESC
            LIMIT 8
            """;

    /**
     * Fallback query khi FULLTEXT không tìm thấy kết quả: LIKE search đơn giản
     */
    private static final String FALLBACK_LIKE_QUERY = """
            SELECT
                b.book_id,
                b.title,
                b.description,
                b.selling_price,
                b.total_stock,
                b.publication_year,
                COALESCE(GROUP_CONCAT(DISTINCT a.name ORDER BY a.name SEPARATOR ', '), 'Chưa xác định') AS authors,
                COALESCE(pub.name, 'Chưa xác định') AS publisher_name,
                COALESCE(GROUP_CONCAT(DISTINCT cat.name ORDER BY cat.name SEPARATOR ', '), '') AS categories,
                fsi.flash_sale_price,
                fsc.name AS campaign_name,
                1.0 AS relevance_score
            FROM book b
            LEFT JOIN book_author  ba  ON b.book_id = ba.book_id
            LEFT JOIN author        a  ON ba.author_id = a.author_id
            LEFT JOIN publisher   pub  ON b.publisher_id = pub.publisher_id
            LEFT JOIN book_category bc ON b.book_id = bc.book_id
            LEFT JOIN category    cat  ON bc.category_id = cat.category_id
            LEFT JOIN flash_sale_item fsi ON b.book_id = fsi.book_id
            LEFT JOIN flash_sale_campaign fsc
                   ON fsi.campaign_id = fsc.campaign_id
                  AND fsc.status = 'ACTIVE'
                  AND fsc.start_time <= NOW()
                  AND fsc.end_time   >= NOW()
            WHERE b.status = 1
              AND (b.title LIKE ? OR b.description LIKE ? OR a.name LIKE ?)
            GROUP BY
                b.book_id, b.title, b.description, b.selling_price, b.total_stock,
                b.publication_year, pub.name, fsi.flash_sale_price, fsc.name
            LIMIT 8
            """;

    /**
     * General context: sách đang flash sale + sách mới nhất
     * Dùng khi query rỗng hoặc chung chung
     */
    private static final String GENERAL_CONTEXT_QUERY = """
            (
                SELECT
                    b.book_id,
                    b.title,
                    b.description,
                    b.selling_price,
                    b.total_stock,
                    b.publication_year,
                    COALESCE(GROUP_CONCAT(DISTINCT a.name ORDER BY a.name SEPARATOR ', '), 'Chưa xác định') AS authors,
                    COALESCE(pub.name, 'Chưa xác định') AS publisher_name,
                    COALESCE(GROUP_CONCAT(DISTINCT cat.name ORDER BY cat.name SEPARATOR ', '), '') AS categories,
                    fsi.flash_sale_price,
                    fsc.name AS campaign_name,
                    2.0 AS relevance_score
                FROM book b
                JOIN flash_sale_item fsi ON b.book_id = fsi.book_id
                JOIN flash_sale_campaign fsc
                     ON fsi.campaign_id = fsc.campaign_id
                    AND fsc.status = 'ACTIVE'
                    AND fsc.start_time <= NOW()
                    AND fsc.end_time   >= NOW()
                LEFT JOIN book_author  ba  ON b.book_id = ba.book_id
                LEFT JOIN author        a  ON ba.author_id = a.author_id
                LEFT JOIN publisher   pub  ON b.publisher_id = pub.publisher_id
                LEFT JOIN book_category bc ON b.book_id = bc.book_id
                LEFT JOIN category    cat  ON bc.category_id = cat.category_id
                WHERE b.status = 1
                GROUP BY
                    b.book_id, b.title, b.description, b.selling_price, b.total_stock,
                    b.publication_year, pub.name, fsi.flash_sale_price, fsc.name
                LIMIT 4
            )
            UNION ALL
            (
                SELECT
                    b.book_id,
                    b.title,
                    b.description,
                    b.selling_price,
                    b.total_stock,
                    b.publication_year,
                    COALESCE(GROUP_CONCAT(DISTINCT a.name ORDER BY a.name SEPARATOR ', '), 'Chưa xác định') AS authors,
                    COALESCE(pub.name, 'Chưa xác định') AS publisher_name,
                    COALESCE(GROUP_CONCAT(DISTINCT cat.name ORDER BY cat.name SEPARATOR ', '), '') AS categories,
                    NULL AS flash_sale_price,
                    NULL AS campaign_name,
                    1.0 AS relevance_score
                FROM book b
                LEFT JOIN book_author  ba  ON b.book_id = ba.book_id
                LEFT JOIN author        a  ON ba.author_id = a.author_id
                LEFT JOIN publisher   pub  ON b.publisher_id = pub.publisher_id
                LEFT JOIN book_category bc ON b.book_id = bc.book_id
                LEFT JOIN category    cat  ON bc.category_id = cat.category_id
                WHERE b.status = 1
                GROUP BY
                    b.book_id, b.title, b.description, b.selling_price, b.total_stock,
                    b.publication_year, pub.name
                ORDER BY b.created_at DESC
                LIMIT 4
            )
            """;

    /**
     * Tìm kiếm sách liên quan và build context payload cho RAG
     */
    public String buildContextPayload(String userQuery) {
        // Query rỗng → trả về general context (flash sale + sách mới)
        if (userQuery == null || userQuery.isBlank()) {
            return buildGeneralContext();
        }

        try {
            // 1. FULLTEXT search trên book title/description
            String fulltextQuery = prepareFulltextQuery(userQuery);
            List<BookRagResult> results = Collections.emptyList();

            if (!fulltextQuery.isBlank()) {
                results = executeFulltextSearch(fulltextQuery);
            }

            // 2. FULLTEXT search trên author name nếu không đủ kết quả
            if (results.size() < 3) {
                List<BookRagResult> authorResults = executeAuthorSearch(fulltextQuery);
                // Gộp kết quả, tránh trùng book_id
                for (BookRagResult ar : authorResults) {
                    boolean alreadyExists = results.stream()
                            .anyMatch(r -> r.getBookId().equals(ar.getBookId()));
                    if (!alreadyExists) {
                        results = new java.util.ArrayList<>(results);
                        ((java.util.ArrayList<BookRagResult>) results).add(ar);
                    }
                }
            }

            // 3. LIKE fallback nếu vẫn trống
            if (results.isEmpty()) {
                log.debug("FULLTEXT search không có kết quả, fallback sang LIKE search cho: {}", userQuery);
                results = executeLikeSearch(userQuery);
            }

            // 4. Nếu vẫn không có → general context
            if (results.isEmpty()) {
                log.debug("LIKE search không có kết quả cho: {}, dùng general context", userQuery);
                return buildGeneralContext();
            }

            String activePromotions = getActivePromotionsContext();
            return formatContextPayload(results) + "\n\n" + activePromotions;

        } catch (Exception e) {
            log.error("Lỗi khi thực hiện RAG search: {}", e.getMessage(), e);
            return "Không thể truy vấn dữ liệu sách lúc này. Vui lòng thử lại.";
        }
    }

    /**
     * Build general context khi không có query cụ thể
     */
    private String buildGeneralContext() {
        try {
            List<BookRagResult> results = jdbcTemplate.query(
                    GENERAL_CONTEXT_QUERY,
                    (rs, rowNum) -> BookRagResult.builder()
                            .bookId(rs.getLong("book_id"))
                            .title(rs.getString("title"))
                            .description(truncateText(rs.getString("description"), 200))
                            .sellingPrice(rs.getBigDecimal("selling_price"))
                            .totalStock(rs.getInt("total_stock"))
                            .publicationYear(rs.getObject("publication_year") != null ? rs.getInt("publication_year") : null)
                            .authors(rs.getString("authors"))
                            .publisherName(rs.getString("publisher_name"))
                            .categories(rs.getString("categories"))
                            .flashSalePrice(rs.getBigDecimal("flash_sale_price"))
                            .campaignName(rs.getString("campaign_name"))
                            .promotionCode(null)
                            .discountPercent(null)
                            .build()
            );

            if (results.isEmpty()) {
                return "Hiện chưa có sách nào trong hệ thống. Vui lòng thử lại sau.";
            }

            String activePromotions = getActivePromotionsContext();
            StringBuilder sb = new StringBuilder();
            sb.append("📋 TỔNG QUAN KHO SÁCH (Flash sale + Sách mới nhất):\n");
            sb.append(formatContextPayload(results));
            sb.append("\n").append(activePromotions);
            return sb.toString();

        } catch (Exception e) {
            log.error("Lỗi khi build general context: {}", e.getMessage(), e);
            return "Không thể tải dữ liệu sách lúc này.";
        }
    }

    private List<BookRagResult> executeFulltextSearch(String fulltextQuery) {
        if (fulltextQuery == null || fulltextQuery.isBlank()) return Collections.emptyList();
        return jdbcTemplate.query(
                RAG_FULLTEXT_QUERY,
                (rs, rowNum) -> BookRagResult.builder()
                        .bookId(rs.getLong("book_id"))
                        .title(rs.getString("title"))
                        .description(truncateText(rs.getString("description"), 250))
                        .sellingPrice(rs.getBigDecimal("selling_price"))
                        .totalStock(rs.getInt("total_stock"))
                        .publicationYear(rs.getObject("publication_year") != null ? rs.getInt("publication_year") : null)
                        .authors(rs.getString("authors"))
                        .publisherName(rs.getString("publisher_name"))
                        .categories(rs.getString("categories"))
                        .flashSalePrice(rs.getBigDecimal("flash_sale_price"))
                        .campaignName(rs.getString("campaign_name"))
                        .promotionCode(null)
                        .discountPercent(null)
                        .build(),
                fulltextQuery, fulltextQuery
        );
    }

    private List<BookRagResult> executeAuthorSearch(String fulltextQuery) {
        if (fulltextQuery == null || fulltextQuery.isBlank()) return Collections.emptyList();
        try {
            return jdbcTemplate.query(
                    AUTHOR_FULLTEXT_QUERY,
                    (rs, rowNum) -> BookRagResult.builder()
                            .bookId(rs.getLong("book_id"))
                            .title(rs.getString("title"))
                            .description(truncateText(rs.getString("description"), 250))
                            .sellingPrice(rs.getBigDecimal("selling_price"))
                            .totalStock(rs.getInt("total_stock"))
                            .publicationYear(rs.getObject("publication_year") != null ? rs.getInt("publication_year") : null)
                            .authors(rs.getString("authors"))
                            .publisherName(rs.getString("publisher_name"))
                            .categories(rs.getString("categories"))
                            .flashSalePrice(rs.getBigDecimal("flash_sale_price"))
                            .campaignName(rs.getString("campaign_name"))
                            .promotionCode(null)
                            .discountPercent(null)
                            .build(),
                    fulltextQuery
            );
        } catch (Exception e) {
            log.debug("Author FULLTEXT search lỗi: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<BookRagResult> executeLikeSearch(String userQuery) {
        String likePattern = "%" + userQuery.trim() + "%";
        return jdbcTemplate.query(
                FALLBACK_LIKE_QUERY,
                (rs, rowNum) -> BookRagResult.builder()
                        .bookId(rs.getLong("book_id"))
                        .title(rs.getString("title"))
                        .description(truncateText(rs.getString("description"), 250))
                        .sellingPrice(rs.getBigDecimal("selling_price"))
                        .totalStock(rs.getInt("total_stock"))
                        .publicationYear(rs.getObject("publication_year") != null ? rs.getInt("publication_year") : null)
                        .authors(rs.getString("authors"))
                        .publisherName(rs.getString("publisher_name"))
                        .categories(rs.getString("categories"))
                        .flashSalePrice(rs.getBigDecimal("flash_sale_price"))
                        .campaignName(rs.getString("campaign_name"))
                        .promotionCode(null)
                        .discountPercent(null)
                        .build(),
                likePattern, likePattern, likePattern
        );
    }


    private String prepareFulltextQuery(String query) {
        return Arrays.stream(query.trim().split("\\s+"))
                .filter(word -> word.length() >= 2)
                .map(word -> {
                    // Loại bỏ ký tự đặc biệt của FULLTEXT
                    String clean = word.replaceAll("[+\\-><()~*\"@{}\\[\\]|!]", "");
                    return clean.isEmpty() ? null : clean + "*";
                })
                .filter(word -> word != null)
                .collect(Collectors.joining(" "));
    }


    private String formatContextPayload(List<BookRagResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("DANH SÁCH SÁCH TÌM ĐƯỢC (").append(results.size()).append(" kết quả):\n");
        sb.append("─".repeat(60)).append("\n\n");

        for (int i = 0; i < results.size(); i++) {
            BookRagResult r = results.get(i);

            sb.append(i + 1).append(". **").append(r.getTitle()).append("**\n");
            sb.append("   • Tác giả  : ").append(r.getAuthors()).append("\n");
            sb.append("   • NXB      : ").append(r.getPublisherName()).append("\n");
            sb.append("   • Thể loại : ").append(
                    r.getCategories() != null && !r.getCategories().isBlank()
                            ? r.getCategories() : "Chưa phân loại"
            ).append("\n");
            if (r.getPublicationYear() != null) {
                sb.append("   • Năm XB   : ").append(r.getPublicationYear()).append("\n");
            }
            sb.append("   • Giá bán  : ").append(formatPrice(r.getSellingPrice())).append(" VNĐ\n");
            sb.append("   • Tồn kho  : ").append(r.getTotalStock()).append(" cuốn\n");

            if (r.getDescription() != null && !r.getDescription().isBlank()) {
                sb.append("   • Mô tả    : ").append(r.getDescription()).append("\n");
            }

            if (r.getFlashSalePrice() != null) {
                sb.append("   ⚡ FLASH SALE [").append(r.getCampaignName()).append("]: ")
                        .append("Giá ưu đãi chỉ còn ").append(formatPrice(r.getFlashSalePrice()))
                        .append(" VNĐ! (tiết kiệm ")
                        .append(formatPrice(r.getSellingPrice().subtract(r.getFlashSalePrice())))
                        .append(" VNĐ)\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private String getActivePromotionsContext() {
        String promoQuery = """
                SELECT code, discount_percent, min_order_value, max_discount_amount
                FROM promotion
                WHERE status = 1
                  AND start_date <= NOW()
                  AND end_date >= NOW()
                  AND (usage_limit IS NULL OR used_count < usage_limit)
                """;
        List<String> promos = jdbcTemplate.query(promoQuery, (rs, rowNum) -> {
            StringBuilder info = new StringBuilder();
            info.append(rs.getString("code"))
                    .append(" (Giảm ").append(rs.getBigDecimal("discount_percent")).append("%");
            BigDecimal minOrder = rs.getBigDecimal("min_order_value");
            if (minOrder != null) {
                info.append(", đơn tối thiểu ").append(formatPrice(minOrder)).append(" VNĐ");
            }
            BigDecimal maxDiscount = rs.getBigDecimal("max_discount_amount");
            if (maxDiscount != null) {
                info.append(", giảm tối đa ").append(formatPrice(maxDiscount)).append(" VNĐ");
            }
            info.append(")");
            return info.toString();
        });

        if (promos.isEmpty()) {
            return "🎁 MÃ GIẢM GIÁ: Hiện tại không có mã giảm giá nào đang áp dụng.";
        }
        return "🎁 MÃ GIẢM GIÁ ĐANG ÁP DỤNG:\n- " + String.join("\n- ", promos);
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) return "Liên hệ";
        return String.format("%,.0f", price);
    }

    private String truncateText(String text, int maxLength) {
        if (text == null || text.isBlank()) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
