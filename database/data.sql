USE db_bookstore;
SET NAMES utf8mb4;
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 0. XÓA TOÀN BỘ DỮ LIỆU CŨ
-- =========================================================
DELETE FROM `chat_message`;
DELETE FROM `payment_transaction`;
DELETE FROM `review`;
DELETE FROM `inventory_log`;
DELETE FROM `point_transaction`;
DELETE FROM `import_detail`;
DELETE FROM `order_detail`;
DELETE FROM `cart_item`;
DELETE FROM `rental`;
DELETE FROM `chat_session`;
DELETE FROM `book_import`;
DELETE FROM `orders`;
DELETE FROM `cart`;
DELETE FROM `book_author`;
DELETE FROM `book_category`;
DELETE FROM `book_item`;
DELETE FROM `book`;
DELETE FROM `user`;
DELETE FROM `promotion`;
DELETE FROM `supplier`;
DELETE FROM `publisher`;
DELETE FROM `author`;
DELETE FROM `category`;
DELETE FROM `role`;

ALTER TABLE `role` AUTO_INCREMENT = 1;
ALTER TABLE `category` AUTO_INCREMENT = 1;
ALTER TABLE `author` AUTO_INCREMENT = 1;
ALTER TABLE `publisher` AUTO_INCREMENT = 1;
ALTER TABLE `supplier` AUTO_INCREMENT = 1;
ALTER TABLE `promotion` AUTO_INCREMENT = 1;
ALTER TABLE `user` AUTO_INCREMENT = 1;
ALTER TABLE `book` AUTO_INCREMENT = 1;
ALTER TABLE `book_item` AUTO_INCREMENT = 1;
ALTER TABLE `cart` AUTO_INCREMENT = 1;
ALTER TABLE `cart_item` AUTO_INCREMENT = 1;
ALTER TABLE `orders` AUTO_INCREMENT = 1;
ALTER TABLE `rental` AUTO_INCREMENT = 1;
ALTER TABLE `book_import` AUTO_INCREMENT = 1;
ALTER TABLE `order_detail` AUTO_INCREMENT = 1;
ALTER TABLE `import_detail` AUTO_INCREMENT = 1;
ALTER TABLE `point_transaction` AUTO_INCREMENT = 1;
ALTER TABLE `inventory_log` AUTO_INCREMENT = 1;
ALTER TABLE `review` AUTO_INCREMENT = 1;
ALTER TABLE `payment_transaction` AUTO_INCREMENT = 1;
ALTER TABLE `chat_session` AUTO_INCREMENT = 1;
ALTER TABLE `chat_message` AUTO_INCREMENT = 1;

-- =========================================================
-- 1. ROLE
-- =========================================================
INSERT INTO `role` (`role_id`, `role_name`, `description`) VALUES
(1, 'ADMIN', 'Quản trị toàn bộ hệ thống'),
(2, 'STAFF', 'Nhân viên xử lý đơn hàng, kho, hỗ trợ vận hành'),
(3, 'USER', 'Khách hàng mua và thuê sách');

-- =========================================================
-- 2. CATEGORY
-- =========================================================
INSERT INTO `category` (`category_id`, `parent_id`, `name`) VALUES
(1, NULL, 'Văn học'),
(2, NULL, 'Kinh tế'),
(3, NULL, 'Thiếu nhi'),
(4, NULL, 'Kỹ năng sống'),
(5, NULL, 'Công nghệ thông tin'),
(6, 1, 'Tiểu thuyết'),
(7, 1, 'Truyện ngắn'),
(8, 2, 'Marketing'),
(9, 2, 'Tài chính cá nhân'),
(10, 5, 'Lập trình Java');

-- =========================================================
-- 3. AUTHOR
-- =========================================================
INSERT INTO `author` (`author_id`, `name`, `biography`) VALUES
(1, 'Nguyễn Nhật Ánh', 'Nhà văn Việt Nam nổi tiếng với những tác phẩm viết về tuổi học trò và ký ức tuổi trẻ.'),
(2, 'Dale Carnegie', 'Tác giả kinh điển về giao tiếp, thuyết phục và phát triển bản thân.'),
(3, 'Robert T. Kiyosaki', 'Tác giả nổi tiếng về tài chính cá nhân và giáo dục tiền bạc.'),
(4, 'J.K. Rowling', 'Tác giả bộ truyện Harry Potter nổi tiếng toàn cầu.'),
(5, 'Tô Hoài', 'Nhà văn Việt Nam nổi tiếng với tác phẩm Dế Mèn Phiêu Lưu Ký.'),
(6, 'Martin Fowler', 'Chuyên gia phần mềm, nổi tiếng với Refactoring và kiến trúc phần mềm.'),
(7, 'Joshua Bloch', 'Tác giả Effective Java, chuyên gia hàng đầu về ngôn ngữ Java.'),
(8, 'Robert C. Martin', 'Còn được biết đến là Uncle Bob, tác giả Clean Code.'),
(9, 'Philip Kotler', 'Chuyên gia marketing hàng đầu thế giới.'),
(10, 'Nam Cao', 'Nhà văn hiện thực lớn của văn học Việt Nam.');

-- =========================================================
-- 4. PUBLISHER
-- =========================================================
INSERT INTO `publisher` (`publisher_id`, `name`, `contact_email`, `phone`, `address`) VALUES
(1, 'NXB Trẻ', 'contact@nxbtre.vn', '02838223344', 'TP. Hồ Chí Minh'),
(2, 'NXB Kim Đồng', 'info@kimdong.vn', '02439434730', 'Hà Nội'),
(3, 'Simon & Schuster', 'support@simonandschuster.com', '18005551234', 'New York, USA'),
(4, 'Warner Books', 'support@warnerbooks.com', '18005552345', 'New York, USA'),
(5, 'Scholastic', 'support@scholastic.com', '18005553456', 'New York, USA'),
(6, 'Addison-Wesley Professional', 'support@awprofessional.com', '18005554567', 'Boston, USA'),
(7, 'Pearson', 'support@pearson.com', '18005555678', 'London, UK'),
(8, 'Wiley', 'support@wiley.com', '18005556789', 'Hoboken, USA'),
(9, 'NXB Văn Học', 'vanhoc@nxb.vn', '02438221111', 'Hà Nội'),
(10, 'Pragmatic Bookshelf', 'support@pragprog.com', '18005557890', 'Texas, USA');

-- =========================================================
-- 5. SUPPLIER
-- =========================================================
INSERT INTO `supplier` (`supplier_id`, `name`, `phone`, `address`) VALUES
(1, 'Công ty Sách Miền Bắc', '0911000001', 'Hà Nội'),
(2, 'Công ty Sách Miền Nam', '0911000002', 'TP. Hồ Chí Minh'),
(3, 'Kho sách Đại Phát', '0911000003', 'Đà Nẵng'),
(4, 'Nhà cung cấp Alpha Books', '0911000004', 'Hà Nội'),
(5, 'Kho sách Tri Thức', '0911000005', 'Hải Phòng'),
(6, 'Kho sách Công Nghệ', '0911000006', 'Bắc Ninh'),
(7, 'Sách Giáo Dục Việt', '0911000007', 'Hưng Yên'),
(8, 'Nguồn sách Toàn Quốc', '0911000008', 'Huế'),
(9, 'Đại lý Minh Tâm', '0911000009', 'Thanh Hóa'),
(10, 'Nhà cung cấp Văn Học Việt', '0911000010', 'Nghệ An');

-- =========================================================
-- 6. PROMOTION
-- =========================================================
INSERT INTO `promotion`
(`promotion_id`, `code`, `discount_percent`, `max_discount_amount`, `min_order_value`, `usage_limit`, `used_count`, `start_date`, `end_date`, `status`, `version`) VALUES
(1, 'WELCOME10', 10.00, 50000, 100000, 100, 4, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(2, 'BOOK20', 20.00, 80000, 250000, 50, 7, '2026-01-01 00:00:00', '2026-06-30 23:59:59', 1, 0),
(3, 'SUMMER15', 15.00, 60000, 180000, 80, 10, '2026-04-01 00:00:00', '2026-08-31 23:59:59', 1, 0),
(4, 'STUDENT5', 5.00, 20000, 50000, 200, 16, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(5, 'VIP25', 25.00, 120000, 350000, 20, 2, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(6, 'FREESHIP', 0.00, 30000, 100000, 150, 20, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(7, 'NEWUSER', 12.00, 40000, 120000, 100, 9, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(8, 'SALE30', 30.00, 150000, 500000, 10, 1, '2026-05-01 00:00:00', '2026-05-31 23:59:59', 1, 0),
(9, 'WEEKEND7', 7.00, 25000, 80000, 100, 6, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(10, 'OLDBOOK10', 10.00, 30000, 70000, 60, 5, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0);

-- =========================================================
-- 7. USER
-- Tất cả tài khoản đều dùng mật khẩu: 123456
-- BCrypt hash: $2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q
-- =========================================================
INSERT INTO `user`
(`user_id`, `role_id`, `username`, `password`, `email`, `phone`, `address`, `full_name`, `total_points`, `status`, `version`) VALUES
(1, 1, 'admin01', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'admin01@bookstore.vn', '0901000001', 'Hà Nội', 'Quản trị viên 01', 0, 1, 0),
(2, 2, 'staff01', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'staff01@bookstore.vn', '0901000002', 'Hà Nội', 'Nhân viên 01', 0, 1, 0),
(3, 2, 'staff02', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'staff02@bookstore.vn', '0901000003', 'TP. Hồ Chí Minh', 'Nhân viên 02', 0, 1, 0),
(4, 3, 'nguyenvana', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'vana@gmail.com', '0901000004', 'Cầu Giấy, Hà Nội', 'Nguyễn Văn A', 120, 1, 0),
(5, 3, 'tranthib', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'thib@gmail.com', '0901000005', 'Hai Bà Trưng, Hà Nội', 'Trần Thị B', 80, 1, 0),
(6, 3, 'leminhc', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'minhc@gmail.com', '0901000006', 'Đống Đa, Hà Nội', 'Lê Minh C', 40, 1, 0),
(7, 3, 'phamthud', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'thud@gmail.com', '0901000007', 'Thanh Xuân, Hà Nội', 'Phạm Thu D', 200, 1, 0),
(8, 3, 'hoange', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'hoange@gmail.com', '0901000008', 'Long Biên, Hà Nội', 'Hoàng E', 60, 1, 0),
(9, 3, 'buiducthanh', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'thanh@gmail.com', '0901000009', 'Nam Từ Liêm, Hà Nội', 'Bùi Đức Thành', 95, 1, 0),
(10, 3, 'dodiepchi', '$2a$10$hvIAX.OF3vCrOcLKrtrRQuLTaik3oXR/PDq882rmJ8h0Tvt36kh/q', 'diepchi@gmail.com', '0901000010', 'Hà Đông, Hà Nội', 'Đỗ Diệp Chi', 30, 1, 0);

-- =========================================================
-- 8. BOOK
-- cover_image dùng URL ảnh bìa công khai từ Google Books
-- =========================================================
INSERT INTO `book`	
(`book_id`, `publisher_id`, `title`, `publication_year`, `language`, `original_price`, `selling_price`, `total_stock`, `description`, `cover_image`, `status`, `version`) VALUES
(1, 1, 'Mắt Biếc', 2019, 'Tiếng Việt', 65000, 95000, 20, 'Tác phẩm nổi tiếng của Nguyễn Nhật Ánh về ký ức tuổi trẻ và tình yêu học trò.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776274979/p90553mscan0001_b731411bc7d2427bbe67819d8f9b9bae_master_rywtei.jpg', 1, 0),
(2, 3, 'Đắc Nhân Tâm', 2022, 'Tiếng Việt', 78000, 115000, 25, 'Bản dịch nổi tiếng của How to Win Friends and Influence People.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776274848/-1726817123_c8yk3j.jpg', 1, 0),
(3, 4, 'Cha Giàu Cha Nghèo', 2021, 'Tiếng Việt', 90000, 128000, 18, 'Bản tiếng Việt của Rich Dad Poor Dad về tư duy tài chính cá nhân.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776275112/vn-11134207-7ra0g-m8jtafb4fgxz3f_resize_w900_nl_brwkw1.webp', 1, 0),
(4, 5, 'Harry Potter Và Hòn Đá Phù Thủy', 2020, 'Tiếng Việt', 110000, 168000, 15, 'Tập mở đầu của bộ truyện Harry Potter nổi tiếng.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776275593/harry-potter-sorcerers-stone-tap-1-vietcanbooks_eq7tqw.jpg', 1, 0),
(5, 2, 'Dế Mèn Phiêu Lưu Ký', 2024, 'Tiếng Việt', 58000, 88000, 30, 'Tác phẩm thiếu nhi kinh điển của Tô Hoài.', 'https://res.cloudinary.com/dxlz4acze/image/upload/v1776274726/vn-11134207-7qukw-li36vyzmi2vg64_resize_w900_nl_fokzd1.webp', 1, 0),
(6, 6, 'Refactoring', 2019, 'Tiếng Anh', 290000, 395000, 10, 'Cuốn sách kinh điển về cải tiến cấu trúc mã nguồn.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776275670/717daeb1183067d0ab70052d6cb6bd3436ae87687f4bc8cdc650d37893848054-1749729888262_kirmtc.jpg', 1, 0),
(7, 7, 'Effective Java', 2018, 'Tiếng Anh', 260000, 368000, 12, 'Các nguyên tắc và thực hành tốt nhất trong Java.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776275729/s-l1600_zyxnhv.webp', 1, 0),
(8, 6, 'Clean Code', 2009, 'Tiếng Anh', 275000, 385000, 14, 'Cuốn sách nền tảng về viết mã sạch và chuyên nghiệp.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776275775/81Rnac2Fq_L._AC_UF1000_1000_QL80__i7bwmt.jpg', 1, 0),
(9, 8, 'Marketing 4.0', 2017, 'Tiếng Anh', 230000, 325000, 16, 'Tác phẩm marketing hiện đại của Philip Kotler.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776275498/71DccJGxsKL_tcjcti.jpg', 1, 0),
(10, 9, 'Chí Phèo', 2020, 'Tiếng Việt', 42000, 65000, 22, 'Truyện ngắn hiện thực nổi tiếng của Nam Cao.', 'https://res.cloudinary.com/dxlz4acze/image/upload/q_auto/f_auto/v1776275045/vn-11134207-7ra0g-m6nr4tgwi1k706_1_c7ndvr.jpg', 1, 0);

-- =========================================================
-- 9. BOOK_ITEM
-- =========================================================
INSERT INTO `book_item`
(`book_item_id`, `book_id`, `barcode`, `condition_type`, `condition_note`, `deposit_amount`, `current_rental_price`, `is_for_rent`, `status`, `position`, `version`) VALUES
(1, 1, 'BC1001', 'Mới', 'Sách mới 100%', 35000, 12000, 1, 'Sẵn sàng', 'Kệ A1', 0),
(2, 2, 'BC1002', 'Mới', 'Nguyên seal', 40000, 14000, 1, 'Sẵn sàng', 'Kệ A2', 0),
(3, 3, 'BC1003', 'Cũ', 'Bìa hơi cũ, ruột sạch', 30000, 12000, 1, 'Sẵn sàng', 'Kệ A3', 0),
(4, 4, 'BC1004', 'Mới', 'Nguyên seal', 60000, 18000, 1, 'Đang thuê', 'Kệ B1', 0),
(5, 5, 'BC1005', 'Cũ', 'Ố vàng nhẹ', 25000, 10000, 1, 'Sẵn sàng', 'Kệ B2', 0),
(6, 6, 'BC1006', 'Mới', 'Sách nhập mới', 90000, 28000, 1, 'Sẵn sàng', 'Kệ C1', 0),
(7, 7, 'BC1007', 'Mới', 'Sách nhập mới', 95000, 30000, 1, 'Sẵn sàng', 'Kệ C2', 0),
(8, 8, 'BC1008', 'Cũ', 'Có vài nếp gấp nhẹ', 45000, 16000, 1, 'Đã bán', 'Kệ D1', 0),
(9, 9, 'BC1009', 'Mới', 'Nguyên seal', 50000, 17000, 1, 'Sẵn sàng', 'Kệ D2', 0),
(10, 10, 'BC1010', 'Cũ', 'Cũ nhẹ, đọc tốt', 20000, 8000, 1, 'Mất', 'Kệ D3', 0);

-- =========================================================
-- 10. BOOK_AUTHOR
-- =========================================================
INSERT INTO `book_author` (`book_id`, `author_id`, `author_role`) VALUES
(1, 1, 'Tác giả chính'),
(2, 2, 'Tác giả chính'),
(3, 3, 'Tác giả chính'),
(4, 4, 'Tác giả chính'),
(5, 5, 'Tác giả chính'),
(6, 6, 'Tác giả chính'),
(7, 7, 'Tác giả chính'),
(8, 8, 'Tác giả chính'),
(9, 9, 'Tác giả chính'),
(10, 10, 'Tác giả chính');

-- =========================================================
-- 11. BOOK_CATEGORY
-- =========================================================
INSERT INTO `book_category` (`book_id`, `category_id`, `is_primary`) VALUES
(1, 6, 1),
(2, 4, 1),
(3, 9, 1),
(4, 6, 1),
(5, 3, 1),
(6, 5, 1),
(7, 10, 1),
(8, 5, 1),
(9, 8, 1),
(10, 7, 1);

-- =========================================================
-- 12. CART
-- =========================================================
INSERT INTO `cart` (`cart_id`, `user_id`) VALUES
(1, 4),
(2, 5),
(3, 6),
(4, 7),
(5, 8),
(6, 9),
(7, 10);

-- =========================================================
-- 13. CART_ITEM
-- =========================================================
INSERT INTO `cart_item` (`cart_item_id`, `cart_id`, `book_id`, `quantity`) VALUES
(1, 1, 1, 1),
(2, 1, 2, 2),
(3, 2, 3, 1),
(4, 2, 5, 2),
(5, 3, 4, 1),
(6, 3, 7, 1),
(7, 4, 8, 1),
(8, 4, 9, 1),
(9, 5, 10, 2),
(10, 5, 6, 1),
(11, 6, 3, 1),
(12, 7, 1, 1);

-- =========================================================
-- 14. ORDERS
-- =========================================================
INSERT INTO `orders`
(`order_id`, `user_id`, `promotion_id`, `points_used`, `promotion_discount_amount`, `point_discount_amount`, `total_amount`, `shipping_address`, `payment_method`, `payment_status`, `order_status`) VALUES
(1, 4, 1, 10, 20000, 10000, 180000, 'Cầu Giấy, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt'),
(2, 5, 3, 0, 25000, 0, 143000, 'Hai Bà Trưng, Hà Nội', 'VNPAY', 'Đã thanh toán', 'Đang giao'),
(3, 6, NULL, 20, 0, 20000, 108000, 'Đống Đa, Hà Nội', 'MOMO', 'Đã thanh toán', 'Thành công'),
(4, 7, 5, 50, 60000, 50000, 285000, 'Thanh Xuân, Hà Nội', 'Banking', 'Đã thanh toán', 'Thành công'),
(5, 8, 9, 0, 10000, 0, 118000, 'Long Biên, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt'),
(6, 9, 4, 5, 5000, 5000, 315000, 'Nam Từ Liêm, Hà Nội', 'MOMO', 'Đã thanh toán', 'Thành công'),
(7, 10, NULL, 0, 0, 0, 325000, 'Hà Đông, Hà Nội', 'COD', 'Chưa thanh toán', 'Đang giao'),
(8, 4, 7, 10, 15000, 10000, 103000, 'Cầu Giấy, Hà Nội', 'VNPAY', 'Đã thanh toán', 'Thành công'),
(9, 5, 2, 0, 30000, 0, 365000, 'Hai Bà Trưng, Hà Nội', 'Banking', 'Đã thanh toán', 'Thành công'),
(10, 6, 10, 0, 5000, 0, 60000, 'Đống Đa, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt');

-- =========================================================
-- 15. ORDER_DETAIL
-- =========================================================
INSERT INTO `order_detail` (`order_detail_id`, `order_id`, `book_id`, `quantity`, `unit_price`) VALUES
(1, 1, 1, 1, 95000),
(2, 1, 2, 1, 115000),
(3, 2, 4, 1, 168000),
(4, 3, 1, 1, 95000),
(5, 3, 5, 1, 88000),
(6, 4, 6, 1, 395000),
(7, 5, 3, 1, 128000),
(8, 6, 9, 1, 325000),
(9, 7, 9, 1, 325000),
(10, 8, 2, 1, 115000),
(11, 9, 7, 1, 368000),
(12, 10, 10, 1, 65000);

-- =========================================================
-- 16. RENTAL
-- =========================================================
INSERT INTO `rental`
(`rental_id`, `user_id`, `book_item_id`, `rent_date`, `due_date`, `return_date`, `actual_deposit`, `rental_fee`, `penalty_fee`, `payment_method`, `payment_status`, `status`) VALUES
(1, 4, 1, '2026-04-01 09:00:00', '2026-04-08 09:00:00', '2026-04-07 10:00:00', 35000, 12000, 0, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(2, 5, 2, '2026-04-02 10:00:00', '2026-04-09 10:00:00', NULL, 40000, 14000, 0, 'MOMO', 'Đã thu cọc', 'Đang thuê'),
(3, 6, 3, '2026-04-03 11:00:00', '2026-04-10 11:00:00', '2026-04-12 12:00:00', 30000, 12000, 5000, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(4, 7, 4, '2026-04-04 08:30:00', '2026-04-11 08:30:00', NULL, 60000, 18000, 0, 'Banking', 'Đã thu cọc', 'Quá hạn'),
(5, 8, 5, '2026-04-05 14:00:00', '2026-04-12 14:00:00', '2026-04-12 13:00:00', 25000, 10000, 0, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(6, 9, 6, '2026-04-06 15:00:00', '2026-04-13 15:00:00', NULL, 90000, 28000, 0, 'MOMO', 'Đã thu cọc', 'Đang thuê'),
(7, 10, 7, '2026-04-07 16:00:00', '2026-04-14 16:00:00', NULL, 95000, 30000, 0, 'Banking', 'Đã thu cọc', 'Đang thuê'),
(8, 4, 8, '2026-04-08 09:30:00', '2026-04-15 09:30:00', '2026-04-15 10:00:00', 45000, 16000, 0, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(9, 5, 9, '2026-04-09 10:30:00', '2026-04-16 10:30:00', NULL, 50000, 17000, 0, 'MOMO', 'Đã thu cọc', 'Đang thuê'),
(10, 6, 10, '2026-04-10 13:00:00', '2026-04-17 13:00:00', NULL, 20000, 8000, 10000, 'Tiền mặt', 'Đã thu cọc', 'Quá hạn');

-- =========================================================
-- 17. BOOK_IMPORT
-- user_id dùng staff/admin cho hợp lý nghiệp vụ
-- =========================================================
INSERT INTO `book_import` (`import_id`, `supplier_id`, `user_id`, `import_date`, `total_cost`) VALUES
(1, 1, 2, '2026-03-01 09:00:00', 1500000),
(2, 2, 2, '2026-03-05 10:00:00', 1750000),
(3, 3, 3, '2026-03-08 11:00:00', 1650000),
(4, 4, 3, '2026-03-10 14:00:00', 1200000),
(5, 5, 2, '2026-03-12 15:00:00', 980000),
(6, 6, 2, '2026-03-15 16:00:00', 1400000),
(7, 7, 3, '2026-03-18 09:30:00', 1500000),
(8, 8, 3, '2026-03-20 08:30:00', 1320000),
(9, 9, 2, '2026-03-22 13:00:00', 1240000),
(10, 10, 3, '2026-03-25 10:15:00', 890000);

-- =========================================================
-- 18. IMPORT_DETAIL
-- =========================================================
INSERT INTO `import_detail` (`import_detail_id`, `import_id`, `book_id`, `quantity`, `import_price`) VALUES
(1, 1, 1, 12, 65000),
(2, 2, 2, 15, 78000),
(3, 3, 3, 12, 90000),
(4, 4, 4, 8, 110000),
(5, 5, 5, 20, 58000),
(6, 6, 6, 5, 290000),
(7, 7, 7, 6, 260000),
(8, 8, 8, 10, 275000),
(9, 9, 9, 8, 230000),
(10, 10, 10, 18, 42000);

-- =========================================================
-- 19. POINT_TRANSACTION
-- =========================================================
INSERT INTO `point_transaction`
(`transaction_id`, `user_id`, `order_id`, `rental_id`, `points_changed`, `reason`) VALUES
(1, 4, 1, NULL, 18, 'Tích điểm từ mua hàng'),
(2, 5, 2, NULL, 14, 'Tích điểm từ mua hàng'),
(3, 6, 3, NULL, 10, 'Tích điểm từ mua hàng'),
(4, 7, 4, NULL, 28, 'Tích điểm từ mua hàng'),
(5, 8, 5, NULL, 11, 'Tích điểm từ mua hàng'),
(6, 4, NULL, 1, 5, 'Thưởng trả sách đúng hạn'),
(7, 5, NULL, 2, -3, 'Trừ điểm do thuê quá hạn'),
(8, 6, NULL, 3, -2, 'Phạt trả muộn'),
(9, 7, NULL, 4, -5, 'Phạt quá hạn thuê'),
(10, 8, NULL, 5, 4, 'Thưởng trả sách đúng hạn');

-- =========================================================
-- 20. INVENTORY_LOG
-- user_id dùng admin/staff
-- =========================================================
INSERT INTO `inventory_log`
(`log_id`, `user_id`, `book_item_id`, `action_type`, `reference_order_id`, `reference_import_id`, `reference_rental_id`, `note`) VALUES
(1, 2, 1, 'Nhập mới', NULL, 1, NULL, 'Nhập sách mới vào kho'),
(2, 2, 2, 'Nhập mới', NULL, 2, NULL, 'Nhập sách mới vào kho'),
(3, 3, 3, 'Nhập mới', NULL, 3, NULL, 'Nhập sách tài chính vào kho'),
(4, 3, 4, 'Thuê', NULL, NULL, 4, 'Xuất sách cho khách thuê'),
(5, 2, 5, 'Trả', NULL, NULL, 5, 'Khách trả sách đúng hạn'),
(6, 2, 6, 'Thuê', NULL, NULL, 6, 'Sách đang được thuê'),
(7, 3, 7, 'Thuê', NULL, NULL, 7, 'Sách đang được thuê'),
(8, 2, 8, 'Xuất bán', 6, NULL, NULL, 'Bán sách cho khách'),
(9, 3, 9, 'Thuê', NULL, NULL, 9, 'Khách thuê sách marketing'),
(10, 2, 10, 'Mất', NULL, NULL, 10, 'Khách báo mất sách');

-- =========================================================
-- 21. REVIEW
-- =========================================================
INSERT INTO `review` (`review_id`, `user_id`, `book_id`, `rating`, `comment`, `is_approved`) VALUES
(1, 4, 1, 5, 'Câu chuyện rất đẹp và giàu cảm xúc.', 1),
(2, 5, 2, 5, 'Nội dung thực tế, dễ áp dụng trong giao tiếp.', 1),
(3, 6, 3, 4, 'Phù hợp cho người mới học quản lý tài chính.', 1),
(4, 7, 4, 5, 'Bản truyện hấp dẫn, đọc cuốn hút.', 1),
(5, 8, 5, 5, 'Sách thiếu nhi kinh điển, minh họa đẹp.', 1),
(6, 9, 6, 5, 'Rất hữu ích cho lập trình viên chuyên nghiệp.', 1),
(7, 10, 7, 5, 'Cuốn Java nên có trên giá sách của mọi dev.', 1),
(8, 4, 8, 5, 'Clean Code là sách gối đầu giường cho dân code.', 1),
(9, 5, 9, 4, 'Kiến thức marketing hiện đại, dễ nắm bắt.', 1),
(10, 6, 10, 4, 'Tác phẩm kinh điển của văn học Việt Nam.', 1);

-- =========================================================
-- 22. PAYMENT_TRANSACTION
-- =========================================================
INSERT INTO `payment_transaction`
(`payment_id`, `order_id`, `provider`, `provider_transaction_id`, `amount`, `status`, `response_code`) VALUES
(1, 2, 'VNPAY', 'VNPAY_TXN_1001', 143000, 'SUCCESS', '00'),
(2, 3, 'MOMO', 'MOMO_TXN_1002', 108000, 'SUCCESS', '00'),
(3, 4, 'BANKING', 'BANK_TXN_1003', 285000, 'SUCCESS', '00'),
(4, 6, 'MOMO', 'MOMO_TXN_1004', 315000, 'SUCCESS', '00'),
(5, 8, 'VNPAY', 'VNPAY_TXN_1005', 103000, 'SUCCESS', '00'),
(6, 9, 'BANKING', 'BANK_TXN_1006', 365000, 'SUCCESS', '00'),
(7, 1, 'COD', 'COD_TXN_1007', 180000, 'PENDING', NULL),
(8, 5, 'COD', 'COD_TXN_1008', 118000, 'PENDING', NULL),
(9, 7, 'COD', 'COD_TXN_1009', 325000, 'PENDING', NULL),
(10, 10, 'COD', 'COD_TXN_1010', 60000, 'PENDING', NULL);

-- =========================================================
-- 23. CHAT_SESSION
-- =========================================================
INSERT INTO `chat_session` (`session_id`, `user_id`, `started_at`, `ended_at`, `status`) VALUES
(1, 4, '2026-04-10 08:00:00', '2026-04-10 08:10:00', 'CLOSED'),
(2, 5, '2026-04-10 09:00:00', '2026-04-10 09:05:00', 'CLOSED'),
(3, 6, '2026-04-10 10:00:00', NULL, 'ACTIVE'),
(4, 7, '2026-04-10 11:00:00', '2026-04-10 11:07:00', 'CLOSED'),
(5, 8, '2026-04-10 12:00:00', NULL, 'ACTIVE'),
(6, NULL, '2026-04-10 13:00:00', '2026-04-10 13:03:00', 'CLOSED'),
(7, 9, '2026-04-11 08:30:00', NULL, 'ACTIVE'),
(8, 10, '2026-04-11 09:30:00', '2026-04-11 09:35:00', 'CLOSED'),
(9, 4, '2026-04-11 10:30:00', NULL, 'ACTIVE'),
(10, NULL, '2026-04-11 11:30:00', NULL, 'ACTIVE');

-- =========================================================
-- 24. CHAT_MESSAGE
-- =========================================================
INSERT INTO `chat_message` (`message_id`, `session_id`, `sender_type`, `content`) VALUES
(1, 1, 'USER', 'Shop còn sách Đắc Nhân Tâm không?'),
(2, 1, 'BOT', 'Dạ còn ạ, hiện sách đang có sẵn trong kho.'),
(3, 2, 'USER', 'Tôi muốn kiểm tra đơn hàng #2'),
(4, 2, 'BOT', 'Đơn hàng của bạn đang được giao.'),
(5, 3, 'USER', 'Làm sao để thuê sách?'),
(6, 3, 'BOT', 'Bạn chọn sách, đặt cọc và xác nhận thời gian thuê.'),
(7, 4, 'USER', 'Tôi có thể dùng mã giảm giá nào?'),
(8, 4, 'BOT', 'Bạn có thể dùng mã WELCOME10 hoặc SUMMER15 nếu đủ điều kiện.'),
(9, 5, 'USER', 'Shop mở cửa đến mấy giờ?'),
(10, 5, 'BOT', 'Shop mở cửa từ 8h đến 21h hằng ngày.');

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;
