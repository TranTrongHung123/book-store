CREATE DATABASE IF NOT EXISTS db_bookstore
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE db_bookstore;

-- 1. PHÂN QUYỀN & NGƯỜI DÙNG
CREATE TABLE `role`
(
    `role_id`     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh vai trò',
    `role_name`   VARCHAR(50) NOT NULL COMMENT 'Tên vai trò',
    `description` VARCHAR(255) COMMENT 'Mô tả chi tiết quyền hạn',
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối'
) COMMENT ='Bảng lưu trữ vai trò người dùng';

CREATE TABLE `user`
(
    `user_id`      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh duy nhất của người dùng',
    `role_id`      BIGINT       NOT NULL COMMENT 'Liên kết với vai trò trong bảng role',
    `username`     VARCHAR(50)  NOT NULL UNIQUE COMMENT 'Tên đăng nhập duy nhất',
    `password`     VARCHAR(255) NOT NULL COMMENT 'Mật khẩu mã hóa bảo mật',
    `email`        VARCHAR(100) COMMENT 'Địa chỉ email',
    `phone`        VARCHAR(20) COMMENT 'Số điện thoại để liên hệ giao nhận hàng',
    `address`      VARCHAR(255) COMMENT 'Địa chỉ mặc định lưu trong hồ sơ',
    `full_name`    VARCHAR(100) COMMENT 'Họ và tên đầy đủ của khách hàng',
    `total_points` INT       DEFAULT 0 COMMENT 'Tổng số điểm tích lũy hiện có của khách' CHECK (`total_points` >= 0),
    `status`       TINYINT   DEFAULT 1 COMMENT 'Trạng thái tài khoản (1: Hoạt động, 0: Bị khóa)',
    `created_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian đăng ký tài khoản',
    `updated_at`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật hồ sơ',
    FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) COMMENT ='Bảng thông tin người dùng';

-- 2. DANH MỤC & ĐỐI TÁC
CREATE TABLE `category`
(
    `category_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh thể loại sách',
    `parent_id`   BIGINT       NULL COMMENT 'Danh mục cha (nếu có)',
    `name`        VARCHAR(100) NOT NULL COMMENT 'Tên thể loại',
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`parent_id`) REFERENCES `category` (`category_id`)
) COMMENT ='Bảng danh mục thể loại sách';

CREATE TABLE `author`
(
    `author_id`  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh tác giả',
    `name`       VARCHAR(150) NOT NULL COMMENT 'Họ và tên của tác giả',
    `biography`  TEXT COMMENT 'Tiểu sử tóm tắt',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối'
) COMMENT ='Bảng thông tin tác giả';

CREATE TABLE `publisher`
(
    `publisher_id`  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh nhà xuất bản',
    `name`          VARCHAR(150) NOT NULL COMMENT 'Tên công ty xuất bản',
    `contact_email` VARCHAR(100) COMMENT 'Email liên hệ',
    `phone`         VARCHAR(20) COMMENT 'Số điện thoại văn phòng',
    `address`       VARCHAR(255) COMMENT 'Địa chỉ trụ sở chính',
    `created_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối'
) COMMENT ='Bảng thông tin nhà xuất bản';

CREATE TABLE `supplier`
(
    `supplier_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh đơn vị cung cấp hàng',
    `name`        VARCHAR(150) NOT NULL COMMENT 'Tên công ty cung cấp hàng',
    `phone`       VARCHAR(20) COMMENT 'Số điện thoại liên hệ nhập hàng',
    `address`     VARCHAR(255) COMMENT 'Địa chỉ kho nhà cung cấp',
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối'
) COMMENT ='Bảng thông tin nhà cung cấp';

-- 3. THÔNG TIN SÁCH & VẬT PHẨM
CREATE TABLE `book`
(
    `book_id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh của đầu sách',
    `publisher_id`     BIGINT COMMENT 'Khóa ngoại tham chiếu đến nhà xuất bản',
    `title`            VARCHAR(255) NOT NULL COMMENT 'Tên của bộ sách',
    `publication_year` INT COMMENT 'Năm xuất bản',
    `language`         VARCHAR(50) COMMENT 'Ngôn ngữ sách',
    `original_price`   DECIMAL(15, 2) COMMENT 'Giá nhập gốc' CHECK (`original_price` >= 0),
    `selling_price`    DECIMAL(15, 2) COMMENT 'Giá bán niêm yết khi bán sách mới' CHECK (`selling_price` >= 0),
    `total_stock`      INT       DEFAULT 0 COMMENT 'Tổng số lượng tồn kho' CHECK (`total_stock` >= 0),
    `description`      TEXT COMMENT 'Nội dung tóm tắt của cuốn sách',
    `cover_image`      VARCHAR(255) COMMENT 'Đường dẫn lưu trữ ảnh bìa sách',
    `status`           TINYINT   DEFAULT 1 COMMENT 'Trạng thái kinh doanh (1: Đang bán, 0: Ngừng bán)',
    `created_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`publisher_id`)
) COMMENT ='Bảng thông tin đầu sách';

CREATE TABLE `book_item`
(
    `book_item_id`         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã cho từng quyển',
    `book_id`              BIGINT NOT NULL COMMENT 'Liên kết với đầu sách tương ứng',
    `barcode`              VARCHAR(100) UNIQUE COMMENT 'Mã vạch thực tế in trên sách',
    `condition_type`       VARCHAR(50) COMMENT 'Phân loại tình trạng (Mới, Cũ...)',
    `condition_note`       TEXT COMMENT 'Ghi chú chi tiết về tình trạng (ố vàng, rách...)',
    `deposit_amount`       DECIMAL(15, 2) COMMENT 'Tiền đặt cọc thực tế của quyển này' CHECK (`deposit_amount` >= 0),
    `current_rental_price` DECIMAL(15, 2) COMMENT 'Giá thuê thực tế của quyển này' CHECK (`current_rental_price` >= 0),
    `is_for_rent`          TINYINT   DEFAULT 0 COMMENT 'Mục đích (1: Để cho thuê, 0: Để bán mới)',
    `status`               VARCHAR(50) COMMENT 'Trạng thái vật lý (Sẵn sàng, Đang thuê, Đã bán, Mất)',
    `position`             VARCHAR(100) COMMENT 'Vị trí chính xác trong kho',
    `created_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`           TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
) COMMENT ='Bảng quản lý từng cuốn sách vật lý cụ thể';

CREATE TABLE `book_author`
(
    `book_id`     BIGINT NOT NULL COMMENT 'Mã đầu sách',
    `author_id`   BIGINT NOT NULL COMMENT 'Mã tác giả',
    `author_role` VARCHAR(100) COMMENT 'Vai trò cụ thể (Tác giả chính, Dịch giả, ...)',
    PRIMARY KEY (`book_id`, `author_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
    FOREIGN KEY (`author_id`) REFERENCES `author` (`author_id`)
) COMMENT ='Bảng trung gian liên kết Sách và Tác giả';

CREATE TABLE `book_category`
(
    `book_id`     BIGINT NOT NULL COMMENT 'Mã đầu sách',
    `category_id` BIGINT NOT NULL COMMENT 'Mã thể loại',
    `is_primary`  TINYINT DEFAULT 0 COMMENT 'Đánh dấu thể loại chính (1: Có, 0: Không)',
    PRIMARY KEY (`book_id`, `category_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
    FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) COMMENT ='Bảng trung gian liên kết Sách và Thể loại';

-- 4. HỆ THỐNG FLASH SALE
CREATE TABLE `flash_sale_campaign`
(
    `campaign_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh đợt sale',
    `name`        VARCHAR(150) NOT NULL COMMENT 'Tên đợt sale',
    `start_time`  DATETIME     NOT NULL COMMENT 'Thời gian bắt đầu',
    `end_time`    DATETIME     NOT NULL COMMENT 'Thời gian kết thúc',
    `status`      VARCHAR(20) DEFAULT 'UPCOMING' COMMENT 'Trạng thái (Upcoming, Active, Ended)',
    `created_at`  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT ='Bảng chiến dịch Flash Sale';

CREATE TABLE `flash_sale_item`
(
    `flash_sale_item_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `campaign_id`        BIGINT         NOT NULL COMMENT 'Nối với Campaign',
    `book_id`            BIGINT         NOT NULL COMMENT 'Nối với Book',
    `flash_sale_price`   DECIMAL(15, 2) NOT NULL COMMENT 'Giá sale',
    `quantity`           INT            NOT NULL COMMENT 'Tổng số lượng đem ra sale',
    `sold_quantity`      INT DEFAULT 0 COMMENT 'Số lượng đã bán',
    `max_per_user`       INT DEFAULT 1 COMMENT 'Giới hạn mua mỗi người',
    FOREIGN KEY (`campaign_id`) REFERENCES `flash_sale_campaign` (`campaign_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
) COMMENT ='Bảng chi tiết hàng Flash Sale';

-- 5. GIỎ HÀNG & KHUYẾN MÃI
CREATE TABLE `promotion`
(
    `promotion_id`        BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh chương trình',
    `code`                VARCHAR(50) NOT NULL UNIQUE COMMENT 'Mã giảm giá khách nhập',
    `discount_percent`    DECIMAL(5, 2) COMMENT 'Phần trăm giảm giá trên đơn hàng' CHECK (`discount_percent` >= 0 AND `discount_percent` <= 100),
    `max_discount_amount` DECIMAL(15, 2) COMMENT 'Số tiền giảm tối đa cho phép' CHECK (`max_discount_amount` >= 0),
    `min_order_value`     DECIMAL(15, 2) COMMENT 'Giá trị đơn hàng tối thiểu để áp dụng' CHECK (`min_order_value` >= 0),
    `usage_limit`         INT         NULL COMMENT 'Giới hạn tổng số lượt sử dụng mã' CHECK (`usage_limit` > 0),
    `used_count`          INT       DEFAULT 0 COMMENT 'Số lượt mã đã được khách hàng sử dụng thực tế' CHECK (`used_count` >= 0),
    `start_date`          DATETIME COMMENT 'Ngày bắt đầu có hiệu lực',
    `end_date`            DATETIME COMMENT 'Ngày kết thúc chương trình',
    `status`              TINYINT   DEFAULT 1 COMMENT 'Trạng thái (1: Kích hoạt, 0: Khóa)',
    `created_at`          TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối'
) COMMENT ='Bảng chương trình khuyến mãi';

CREATE TABLE `cart`
(
    `cart_id`    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh giỏ hàng',
    `user_id`    BIGINT NOT NULL UNIQUE COMMENT 'Mỗi user có 1 giỏ hàng duy nhất',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) COMMENT ='Bảng giỏ hàng của người dùng';

CREATE TABLE `cart_item`
(
    `cart_item_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã chi tiết giỏ hàng',
    `cart_id`      BIGINT NOT NULL COMMENT 'Thuộc giỏ hàng nào',
    `book_id`      BIGINT NOT NULL COMMENT 'Đầu sách được thêm',
    `quantity`     INT    NOT NULL DEFAULT 1 COMMENT 'Số lượng' CHECK (`quantity` > 0),
    `created_at`   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`   TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
) COMMENT ='Bảng chi tiết các sách trong giỏ hàng';

-- 6. ĐƠN HÀNG & CHI TIẾT ĐƠN HÀNG
CREATE TABLE `orders`
(
    `order_id`                  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh đơn hàng',
    `user_id`                   BIGINT NOT NULL COMMENT 'Người thực hiện mua hàng',
    `promotion_id`              BIGINT NULL COMMENT 'Mã giảm giá được sử dụng',
    `points_used`               INT            DEFAULT 0 COMMENT 'Số điểm khách đã dùng để giảm giá tiền mặt' CHECK (`points_used` >= 0),
    `promotion_discount_amount` DECIMAL(15, 2) DEFAULT 0 COMMENT 'Số tiền được giảm từ mã khuyến mãi' CHECK (`promotion_discount_amount` >= 0),
    `point_discount_amount`     DECIMAL(15, 2) DEFAULT 0 COMMENT 'Số tiền được giảm từ đổi điểm' CHECK (`point_discount_amount` >= 0),
    `total_amount`              DECIMAL(15, 2) COMMENT 'Tổng số tiền khách thực trả sau giảm giá' CHECK (`total_amount` >= 0),
    `shipping_address`          TEXT COMMENT 'Địa chỉ giao hàng thực tế',
    `payment_method`            VARCHAR(50) COMMENT 'Cách trả tiền (Tiền mặt, Chuyển khoản...)',
    `payment_status`            VARCHAR(50) COMMENT 'Trạng thái (Chưa thanh toán, Đã thanh toán)',
    `order_status`              VARCHAR(50) COMMENT 'Trạng thái đơn (Chờ duyệt, Đang giao, Thành công)',
    `created_at`                TIMESTAMP      DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm khách đặt hàng',
    `updated_at`                TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`promotion_id`) REFERENCES `promotion` (`promotion_id`)
) COMMENT ='Bảng quản lý đơn hàng bán sách';

CREATE TABLE `order_detail`
(
    `order_detail_id`    BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh dòng chi tiết',
    `order_id`           BIGINT NOT NULL COMMENT 'Liên kết với đơn hàng tổng',
    `book_id`            BIGINT NOT NULL COMMENT 'Đầu sách khách mua',
    `flash_sale_item_id` BIGINT NULL COMMENT 'Liên kết nếu mua trong đợt sale',
    `quantity`           INT    NOT NULL COMMENT 'Số lượng khách mua' CHECK (`quantity` > 0),
    `unit_price`         DECIMAL(15, 2) COMMENT 'Giá bán tại thời điểm mua' CHECK (`unit_price` >= 0),
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`),
    FOREIGN KEY (`flash_sale_item_id`) REFERENCES `flash_sale_item` (`flash_sale_item_id`)
) COMMENT ='Bảng chi tiết đơn bán';

-- 7. QUẢN LÝ THUÊ SÁCH
CREATE TABLE `rental`
(
    `rental_id`      BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh hợp đồng thuê',
    `user_id`        BIGINT   NOT NULL COMMENT 'Người thực hiện thuê sách',
    `book_item_id`   BIGINT   NOT NULL COMMENT 'Quyển sách cụ thể khách đã thuê',
    `rent_date`      DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày giờ bắt đầu thuê sách',
    `due_date`       DATETIME NOT NULL COMMENT 'Ngày hẹn phải mang sách trả lại',
    `return_date`    DATETIME NULL COMMENT 'Ngày thực tế khách mang trả sách',
    `actual_deposit` DECIMAL(15, 2) COMMENT 'Tiền cọc thực tế đã thu' CHECK (`actual_deposit` >= 0),
    `rental_fee`     DECIMAL(15, 2) COMMENT 'Tổng phí thuê thực phải trả' CHECK (`rental_fee` >= 0),
    `penalty_fee`    DECIMAL(15, 2) DEFAULT 0 COMMENT 'Phí phạt do làm hỏng hoặc trả quá hạn' CHECK (`penalty_fee` >= 0),
    `payment_method` VARCHAR(50) COMMENT 'Cách khách trả tiền cọc/phí thuê',
    `payment_status` VARCHAR(50) COMMENT 'Trạng thái tiền (Đã thu cọc, Đã hoàn cọc...)',
    `status`         VARCHAR(50) COMMENT 'Tình trạng hợp đồng (Đang thuê, Đã trả, Quá hạn)',
    `created_at`     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`book_item_id`) REFERENCES `book_item` (`book_item_id`)
) COMMENT ='Bảng quản lý hợp đồng thuê sách';

-- 8. NHẬP HÀNG & KHO VẬT LÝ
CREATE TABLE `book_import`
(
    `import_id`   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh phiếu nhập hàng',
    `supplier_id` BIGINT NOT NULL COMMENT 'Nhập từ nhà cung cấp nào',
    `user_id`     BIGINT NOT NULL COMMENT 'Nhân viên thực hiện nhập hàng',
    `import_date` DATETIME  DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm hàng thực tế về đến kho',
    `total_cost`  DECIMAL(15, 2) COMMENT 'Tổng tiền thực tế trả cho nhà cung cấp' CHECK (`total_cost` >= 0),
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`supplier_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) COMMENT ='Bảng phiếu nhập kho';

CREATE TABLE `import_detail`
(
    `import_detail_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh dòng chi tiết nhập',
    `import_id`        BIGINT NOT NULL COMMENT 'Liên kết với phiếu nhập tổng',
    `book_id`          BIGINT NOT NULL COMMENT 'Mã đầu sách được nhập về số lượng lớn',
    `quantity`         INT    NOT NULL COMMENT 'Số lượng đầu sách này được nhập' CHECK (`quantity` > 0),
    `import_price`     DECIMAL(15, 2) COMMENT 'Giá nhập thực tế' CHECK (`import_price` >= 0),
    FOREIGN KEY (`import_id`) REFERENCES `book_import` (`import_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
) COMMENT ='Bảng chi tiết phiếu nhập';

CREATE TABLE `inventory_log`
(
    `log_id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh dòng nhật ký',
    `user_id`             BIGINT NOT NULL COMMENT 'Nhân viên thực hiện thao tác',
    `book_item_id`        BIGINT NOT NULL COMMENT 'Quyển sách vật lý bị tác động',
    `action_type`         VARCHAR(50) COMMENT 'Loại thao tác (Nhập mới, Xuất bán, Thuê, Trả)',
    `reference_order_id`  BIGINT NULL COMMENT 'Khóa ngoại tham chiếu đơn bán (nếu xuất bán)',
    `reference_import_id` BIGINT NULL COMMENT 'Khóa ngoại tham chiếu phiếu nhập (nếu nhập mới)',
    `reference_rental_id` BIGINT NULL COMMENT 'Khóa ngoại tham chiếu hợp đồng thuê (nếu xuất cho thuê)',
    `note`                TEXT COMMENT 'Ghi chú chi tiết',
    `created_at`          TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`book_item_id`) REFERENCES `book_item` (`book_item_id`),
    FOREIGN KEY (`reference_order_id`) REFERENCES `orders` (`order_id`),
    FOREIGN KEY (`reference_import_id`) REFERENCES `book_import` (`import_id`),
    FOREIGN KEY (`reference_rental_id`) REFERENCES `rental` (`rental_id`)
) COMMENT ='Bảng nhật ký kho vật lý';

-- 9. LỊCH SỬ ĐIỂM, ĐÁNH GIÁ & THANH TOÁN
CREATE TABLE `point_transaction`
(
    `transaction_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh giao dịch điểm',
    `user_id`        BIGINT NOT NULL COMMENT 'Khách hàng nhận hoặc mất điểm',
    `order_id`       BIGINT NULL COMMENT 'Liên kết đơn bán sinh ra điểm',
    `rental_id`      BIGINT NULL COMMENT 'Liên kết đơn thuê sinh ra điểm',
    `points_changed` INT    NOT NULL COMMENT 'Số điểm thay đổi (Cộng thêm hoặc Trừ đi)',
    `reason`         VARCHAR(255) COMMENT 'Lý do (Mua hàng, Thưởng trả đúng hạn, Phạt hỏng...)',
    `created_at`     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
    FOREIGN KEY (`rental_id`) REFERENCES `rental` (`rental_id`)
) COMMENT ='Bảng lịch sử biến động điểm tích lũy';

CREATE TABLE `review`
(
    `review_id`   BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh đánh giá',
    `user_id`     BIGINT  NOT NULL COMMENT 'Khách hàng thực hiện đánh giá',
    `book_id`     BIGINT  NOT NULL COMMENT 'Đầu sách được nhận đánh giá',
    `rating`      TINYINT NOT NULL COMMENT 'Số sao (Từ 1 đến 5)' CHECK (`rating` >= 1 AND `rating` <= 5),
    `comment`     TEXT COMMENT 'Nội dung bình luận của khách hàng',
    `is_approved` TINYINT   DEFAULT 0 COMMENT 'Trạng thái duyệt (0: Chờ, 1: Duyệt, -1: Từ chối)',
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm đánh giá',
    `updated_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`)
) COMMENT ='Bảng đánh giá của khách hàng';

CREATE TABLE `payment_transaction`
(
    `payment_id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh giao dịch thanh toán',
    `order_id`                BIGINT         NOT NULL COMMENT 'Thanh toán cho đơn hàng nào',
    `provider`                VARCHAR(50)    NOT NULL COMMENT 'Cổng thanh toán (VNPAY, MOMO)',
    `provider_transaction_id` VARCHAR(100) COMMENT 'Mã giao dịch do VNPay/MoMo trả về',
    `amount`                  DECIMAL(15, 2) NOT NULL COMMENT 'Số tiền thực tế đã quẹt' CHECK (`amount` >= 0),
    `status`                  VARCHAR(50)    NOT NULL COMMENT 'Trạng thái (PENDING, SUCCESS, FAILED, REFUNDED)',
    `response_code`           VARCHAR(50) COMMENT 'Mã lỗi chi tiết từ cổng thanh toán',
    `created_at`              TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    `updated_at`              TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật cuối',
    FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) COMMENT ='Bảng lưu trữ lịch sử gọi API thanh toán';

-- 10. HỆ THỐNG CHATBOT
CREATE TABLE `chat_session`
(
    `session_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh phiên chat',
    `user_id`    BIGINT    NULL COMMENT 'Khách hàng (NULL nếu khách hàng chưa đăng nhập)',
    `started_at` TIMESTAMP   DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm bắt đầu chat',
    `ended_at`   TIMESTAMP NULL COMMENT 'Thời điểm kết thúc chat',
    `status`     VARCHAR(50) DEFAULT 'ACTIVE' COMMENT 'Trạng thái (ACTIVE, CLOSED)',
    FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) COMMENT ='Bảng quản lý các phiên làm việc của Chatbot';

CREATE TABLE `chat_message`
(
    `message_id`  BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã định danh tin nhắn',
    `session_id`  BIGINT      NOT NULL COMMENT 'Thuộc phiên chat nào',
    `sender_type` VARCHAR(20) NOT NULL COMMENT 'Ai gửi? (USER, BOT, ADMIN)',
    `content`     TEXT        NOT NULL COMMENT 'Nội dung tin nhắn',
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`session_id`)
) COMMENT ='Bảng lưu chi tiết từng tin nhắn của Chatbot để cung cấp ngữ cảnh cho AI';

-- 11. CHỈ MỤC TỐI ƯU HÓA TRUY VẤN
CREATE INDEX idx_user_email ON `user` (`email`);
CREATE INDEX idx_user_phone ON `user` (`phone`);
CREATE INDEX idx_book_title ON `book` (`title`);
CREATE INDEX idx_book_created_at ON `book` (`created_at` DESC);
CREATE INDEX idx_orders_status ON `orders` (`order_status`, `payment_status`);
CREATE INDEX idx_orders_created_at ON `orders` (`created_at` DESC);
CREATE INDEX idx_payment_provider_txn ON `payment_transaction` (`provider_transaction_id`);
CREATE INDEX idx_promotion_validity ON `promotion` (`status`, `start_date`, `end_date`);
CREATE INDEX idx_chat_session_active ON `chat_session` (`user_id`, `status`);
CREATE INDEX idx_inventory_action ON `inventory_log` (`action_type`);
CREATE INDEX idx_flash_sale_status ON `flash_sale_campaign` (`status`, `start_time`, `end_time`);