USE db_bookstore;

SET FOREIGN_KEY_CHECKS = 0;
-- =========================================================
-- 1. ROLE
-- =========================================================
INSERT INTO `role` (`role_id`, `role_name`, `description`) VALUES
(1, 'ADMIN', 'Quản trị toàn bộ hệ thống'),
(2, 'MANAGER', 'Quản lý cửa hàng, kho, đơn hàng, báo cáo'),
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
(9, 2, 'Khởi nghiệp'),
(10, 5, 'Lập trình Java');

-- =========================================================
-- 3. AUTHOR
-- =========================================================
INSERT INTO `author` (`author_id`, `name`, `biography`) VALUES
(1, 'Nguyễn Nhật Ánh', 'Tác giả nổi tiếng với các tác phẩm tuổi học trò.'),
(2, 'Dale Carnegie', 'Tác giả sách kỹ năng sống nổi tiếng thế giới.'),
(3, 'Robert Kiyosaki', 'Tác giả sách tài chính cá nhân.'),
(4, 'J.K. Rowling', 'Tác giả bộ truyện Harry Potter.'),
(5, 'Tô Hoài', 'Nhà văn Việt Nam nổi tiếng.'),
(6, 'Martin Fowler', 'Chuyên gia phần mềm và refactoring.'),
(7, 'Joshua Bloch', 'Tác giả nổi tiếng בתחום Java.'),
(8, 'Adam Khoo', 'Tác giả sách phát triển bản thân.'),
(9, 'Philip Kotler', 'Chuyên gia marketing hàng đầu.'),
(10, 'Nam Cao', 'Nhà văn hiện thực phê phán Việt Nam.');

-- =========================================================
-- 4. PUBLISHER
-- =========================================================
INSERT INTO `publisher` (`publisher_id`, `name`, `contact_email`, `phone`, `address`) VALUES
(1, 'NXB Trẻ', 'contact@nxbtre.vn', '02838223344', 'TP. Hồ Chí Minh'),
(2, 'NXB Kim Đồng', 'info@kimdong.vn', '02439434730', 'Hà Nội'),
(3, 'NXB Lao Động', 'support@nxblaodong.vn', '02437734567', 'Hà Nội'),
(4, 'NXB Tổng Hợp TP.HCM', 'cskh@tonghop.vn', '02839303030', 'TP. Hồ Chí Minh'),
(5, 'NXB Văn Học', 'vanhoc@nxb.vn', '02438221111', 'Hà Nội'),
(6, 'NXB Thế Giới', 'thegioi@nxb.vn', '02439445566', 'Hà Nội'),
(7, 'NXB Giáo Dục', 'giaoduc@nxb.vn', '02439717171', 'Hà Nội'),
(8, 'NXB Thanh Niên', 'thanhnien@nxb.vn', '02439393939', 'Hà Nội'),
(9, 'NXB Công Thương', 'congthuong@nxb.vn', '02436668888', 'Hà Nội'),
(10, 'NXB Hồng Đức', 'hongduc@nxb.vn', '02435557777', 'Hà Nội');

-- =========================================================
-- 5. SUPPLIER
-- =========================================================
INSERT INTO `supplier` (`supplier_id`, `name`, `phone`, `address`) VALUES
(1, 'Công ty Sách Miền Bắc', '0911000001', 'Hà Nội'),
(2, 'Công ty Sách Miền Nam', '0911000002', 'TP. Hồ Chí Minh'),
(3, 'Nhà cung cấp Alpha', '0911000003', 'Đà Nẵng'),
(4, 'Nhà cung cấp Beta', '0911000004', 'Hải Phòng'),
(5, 'Nhà cung cấp Gamma', '0911000005', 'Cần Thơ'),
(6, 'Kho sách An Phát', '0911000006', 'Bắc Ninh'),
(7, 'Kho sách Hoàng Gia', '0911000007', 'Hưng Yên'),
(8, 'Sách và Thiết bị Việt', '0911000008', 'Nghệ An'),
(9, 'Nguồn sách Toàn Quốc', '0911000009', 'Huế'),
(10, 'Đại lý Sách Minh Tâm', '0911000010', 'Thanh Hóa');

-- =========================================================
-- 6. PROMOTION
-- =========================================================
INSERT INTO `promotion`
(`promotion_id`, `code`, `discount_percent`, `max_discount_amount`, `min_order_value`, `usage_limit`, `used_count`, `start_date`, `end_date`, `status`, `version`) VALUES
(1, 'WELCOME10', 10.00, 50000, 100000, 100, 5, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(2, 'BOOK20', 20.00, 80000, 200000, 50, 10, '2026-01-01 00:00:00', '2026-06-30 23:59:59', 1, 0),
(3, 'SUMMER15', 15.00, 60000, 150000, 80, 12, '2026-04-01 00:00:00', '2026-08-31 23:59:59', 1, 0),
(4, 'STUDENT5', 5.00, 20000, 50000, 200, 20, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(5, 'VIP25', 25.00, 120000, 300000, 20, 3, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(6, 'FREESHIP', 0.00, 30000, 100000, 150, 25, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(7, 'NEWUSER', 12.00, 40000, 120000, 100, 8, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(8, 'SALE30', 30.00, 150000, 500000, 10, 1, '2026-05-01 00:00:00', '2026-05-31 23:59:59', 1, 0),
(9, 'WEEKEND7', 7.00, 25000, 80000, 100, 6, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0),
(10, 'OLDBOOK10', 10.00, 30000, 70000, 60, 4, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1, 0);

-- =========================================================
-- 7. USER
-- role_id: 1=ADMIN, 2=MANAGER, 3=USER
-- =========================================================
INSERT INTO `user`
(`user_id`, `role_id`, `username`, `password`, `email`, `phone`, `address`, `full_name`, `total_points`, `status`, `version`) VALUES
(1, 1, 'admin01', '$2a$10$adminhash01', 'admin01@bookstore.vn', '0901000001', 'Hà Nội', 'Quản trị viên 01', 0, 1, 0),
(2, 2, 'manager01', '$2a$10$managerhash01', 'manager01@bookstore.vn', '0901000002', 'Hà Nội', 'Quản lý 01', 0, 1, 0),
(3, 2, 'manager02', '$2a$10$managerhash02', 'manager02@bookstore.vn', '0901000003', 'TP. Hồ Chí Minh', 'Quản lý 02', 0, 1, 0),
(4, 3, 'nguyenvana', '$2a$10$userhash01', 'vana@gmail.com', '0901000004', 'Cầu Giấy, Hà Nội', 'Nguyễn Văn A', 120, 1, 0),
(5, 3, 'tranthib', '$2a$10$userhash02', 'thib@gmail.com', '0901000005', 'Hai Bà Trưng, Hà Nội', 'Trần Thị B', 80, 1, 0),
(6, 3, 'leminhc', '$2a$10$userhash03', 'minhc@gmail.com', '0901000006', 'Đống Đa, Hà Nội', 'Lê Minh C', 40, 1, 0),
(7, 3, 'phamthud', '$2a$10$userhash04', 'thud@gmail.com', '0901000007', 'Thanh Xuân, Hà Nội', 'Phạm Thu D', 200, 1, 0),
(8, 3, 'hoange', '$2a$10$userhash05', 'hoange@gmail.com', '0901000008', 'Long Biên, Hà Nội', 'Hoàng E', 60, 1, 0),
(9, 3, 'buiducthanh', '$2a$10$userhash06', 'thanh@gmail.com', '0901000009', 'Nam Từ Liêm, Hà Nội', 'Bùi Đức Thành', 95, 1, 0),
(10, 3, 'dodiepchi', '$2a$10$userhash07', 'diepchi@gmail.com', '0901000010', 'Hà Đông, Hà Nội', 'Đỗ Diệp Chi', 30, 1, 0);

-- =========================================================
-- 8. BOOK
-- =========================================================
INSERT INTO `book`
(`book_id`, `publisher_id`, `title`, `publication_year`, `language`, `original_price`, `selling_price`, `total_stock`, `description`, `cover_image`, `status`, `version`) VALUES
(1, 1, 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 2018, 'Tiếng Việt', 55000, 78000, 20, 'Tác phẩm nổi tiếng của Nguyễn Nhật Ánh.', 'covers/book1.jpg', 1, 0),
(2, 3, 'Đắc Nhân Tâm', 2020, 'Tiếng Việt', 60000, 90000, 25, 'Sách kỹ năng giao tiếp kinh điển.', 'covers/book2.jpg', 1, 0),
(3, 3, 'Cha Giàu Cha Nghèo', 2021, 'Tiếng Việt', 65000, 98000, 18, 'Sách tài chính cá nhân nổi tiếng.', 'covers/book3.jpg', 1, 0),
(4, 6, 'Harry Potter Và Hòn Đá Phù Thủy', 2019, 'Tiếng Việt', 90000, 135000, 15, 'Phần đầu bộ Harry Potter.', 'covers/book4.jpg', 1, 0),
(5, 2, 'Dế Mèn Phiêu Lưu Ký', 2017, 'Tiếng Việt', 45000, 68000, 30, 'Tác phẩm thiếu nhi kinh điển.', 'covers/book5.jpg', 1, 0),
(6, 4, 'Refactoring', 2022, 'Tiếng Anh', 250000, 320000, 10, 'Cải tiến cấu trúc mã nguồn.', 'covers/book6.jpg', 1, 0),
(7, 4, 'Effective Java', 2022, 'Tiếng Anh', 280000, 350000, 12, 'Các thực hành tốt nhất trong Java.', 'covers/book7.jpg', 1, 0),
(8, 8, 'Làm Chủ Tư Duy Thay Đổi Vận Mệnh', 2021, 'Tiếng Việt', 70000, 105000, 14, 'Sách phát triển bản thân.', 'covers/book8.jpg', 1, 0),
(9, 9, 'Marketing 4.0', 2023, 'Tiếng Việt', 95000, 145000, 16, 'Xu hướng marketing thời đại số.', 'covers/book9.jpg', 1, 0),
(10, 5, 'Chí Phèo', 2016, 'Tiếng Việt', 35000, 52000, 22, 'Tác phẩm hiện thực nổi tiếng.', 'covers/book10.jpg', 1, 0);

-- =========================================================
-- 9. BOOK_ITEM
-- =========================================================
INSERT INTO `book_item`
(`book_item_id`, `book_id`, `barcode`, `condition_type`, `condition_note`, `deposit_amount`, `current_rental_price`, `is_for_rent`, `status`, `position`, `version`) VALUES
(1, 1, 'BC0001', 'Mới', 'Sách mới 100%', 30000, 10000, 1, 'Sẵn sàng', 'Kệ A1', 0),
(2, 2, 'BC0002', 'Mới', 'Sách mới 100%', 35000, 12000, 1, 'Sẵn sàng', 'Kệ A2', 0),
(3, 3, 'BC0003', 'Cũ', 'Bìa hơi cũ', 25000, 9000, 1, 'Sẵn sàng', 'Kệ A3', 0),
(4, 4, 'BC0004', 'Mới', 'Nguyên seal', 50000, 15000, 1, 'Đang thuê', 'Kệ B1', 0),
(5, 5, 'BC0005', 'Cũ', 'Ố vàng nhẹ', 20000, 8000, 1, 'Sẵn sàng', 'Kệ B2', 0),
(6, 6, 'BC0006', 'Mới', 'Sách nhập mới', 80000, 25000, 1, 'Sẵn sàng', 'Kệ C1', 0),
(7, 7, 'BC0007', 'Mới', 'Sách nhập mới', 90000, 30000, 1, 'Sẵn sàng', 'Kệ C2', 0),
(8, 8, 'BC0008', 'Cũ', 'Có vài nếp gấp', 30000, 10000, 1, 'Đã bán', 'Kệ D1', 0),
(9, 9, 'BC0009', 'Mới', 'Nguyên seal', 45000, 14000, 1, 'Sẵn sàng', 'Kệ D2', 0),
(10, 10, 'BC0010', 'Cũ', 'Cũ nhẹ', 15000, 7000, 1, 'Mất', 'Kệ D3', 0);

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
(3, 2, 1),
(4, 6, 1),
(5, 3, 1),
(6, 5, 1),
(7, 10, 1),
(8, 4, 1),
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
(9, 5, 10, 3),
(10, 5, 6, 1);

-- =========================================================
-- 14. ORDERS
-- =========================================================
INSERT INTO `orders`
(`order_id`, `user_id`, `promotion_id`, `points_used`, `promotion_discount_amount`, `point_discount_amount`, `total_amount`,
 `shipping_address`, `payment_method`, `payment_status`, `order_status`) VALUES
(1, 4, 1, 10, 15000, 10000, 143000, 'Cầu Giấy, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt'),
(2, 5, 3, 0, 20000, 0, 125000, 'Hai Bà Trưng, Hà Nội', 'VNPAY', 'Đã thanh toán', 'Đang giao'),
(3, 6, NULL, 20, 0, 20000, 78000, 'Đống Đa, Hà Nội', 'MOMO', 'Đã thanh toán', 'Thành công'),
(4, 7, 5, 50, 50000, 50000, 250000, 'Thanh Xuân, Hà Nội', 'Banking', 'Đã thanh toán', 'Thành công'),
(5, 8, 9, 0, 10000, 0, 95000, 'Long Biên, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt'),
(6, 9, 4, 5, 5000, 5000, 126000, 'Nam Từ Liêm, Hà Nội', 'MOMO', 'Đã thanh toán', 'Thành công'),
(7, 10, NULL, 0, 0, 0, 145000, 'Hà Đông, Hà Nội', 'COD', 'Chưa thanh toán', 'Đang giao'),
(8, 4, 7, 10, 12000, 10000, 113000, 'Cầu Giấy, Hà Nội', 'VNPAY', 'Đã thanh toán', 'Thành công'),
(9, 5, 2, 0, 30000, 0, 200000, 'Hai Bà Trưng, Hà Nội', 'Banking', 'Đã thanh toán', 'Thành công'),
(10, 6, 10, 0, 5000, 0, 63000, 'Đống Đa, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt');

-- =========================================================
-- 15. ORDER_DETAIL
-- =========================================================
INSERT INTO `order_detail` (`order_detail_id`, `order_id`, `book_id`, `quantity`, `unit_price`) VALUES
(1, 1, 1, 1, 78000),
(2, 1, 2, 1, 90000),
(3, 2, 4, 1, 135000),
(4, 3, 1, 1, 78000),
(5, 3, 5, 1, 68000),
(6, 4, 6, 1, 320000),
(7, 5, 3, 1, 98000),
(8, 6, 8, 1, 105000),
(9, 7, 9, 1, 145000),
(10, 8, 2, 1, 90000);

-- =========================================================
-- 16. RENTAL
-- =========================================================
INSERT INTO `rental`
(`rental_id`, `user_id`, `book_item_id`, `rent_date`, `due_date`, `return_date`, `actual_deposit`, `rental_fee`, `penalty_fee`,
 `payment_method`, `payment_status`, `status`) VALUES
(1, 4, 1, '2026-04-01 09:00:00', '2026-04-08 09:00:00', '2026-04-07 10:00:00', 30000, 10000, 0, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(2, 5, 2, '2026-04-02 10:00:00', '2026-04-09 10:00:00', NULL, 35000, 12000, 0, 'MOMO', 'Đã thu cọc', 'Đang thuê'),
(3, 6, 3, '2026-04-03 11:00:00', '2026-04-10 11:00:00', '2026-04-12 12:00:00', 25000, 9000, 5000, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(4, 7, 4, '2026-04-04 08:30:00', '2026-04-11 08:30:00', NULL, 50000, 15000, 0, 'Banking', 'Đã thu cọc', 'Quá hạn'),
(5, 8, 5, '2026-04-05 14:00:00', '2026-04-12 14:00:00', '2026-04-12 13:00:00', 20000, 8000, 0, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(6, 9, 6, '2026-04-06 15:00:00', '2026-04-13 15:00:00', NULL, 80000, 25000, 0, 'MOMO', 'Đã thu cọc', 'Đang thuê'),
(7, 10, 7, '2026-04-07 16:00:00', '2026-04-14 16:00:00', NULL, 90000, 30000, 0, 'Banking', 'Đã thu cọc', 'Đang thuê'),
(8, 4, 8, '2026-04-08 09:30:00', '2026-04-15 09:30:00', '2026-04-15 10:00:00', 30000, 10000, 0, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
(9, 5, 9, '2026-04-09 10:30:00', '2026-04-16 10:30:00', NULL, 45000, 14000, 0, 'MOMO', 'Đã thu cọc', 'Đang thuê'),
(10, 6, 10, '2026-04-10 13:00:00', '2026-04-17 13:00:00', NULL, 15000, 7000, 10000, 'Tiền mặt', 'Đã thu cọc', 'Quá hạn');

-- =========================================================
-- 17. BOOK_IMPORT
-- user_id chỉ dùng manager/admin cho hợp lý nghiệp vụ
-- =========================================================
INSERT INTO `book_import` (`import_id`, `supplier_id`, `user_id`, `import_date`, `total_cost`) VALUES
(1, 1, 2, '2026-03-01 09:00:00', 1200000),
(2, 2, 2, '2026-03-05 10:00:00', 1500000),
(3, 3, 3, '2026-03-08 11:00:00', 1800000),
(4, 4, 3, '2026-03-10 14:00:00', 950000),
(5, 5, 2, '2026-03-12 15:00:00', 1100000),
(6, 6, 2, '2026-03-15 16:00:00', 1300000),
(7, 7, 3, '2026-03-18 09:30:00', 1700000),
(8, 8, 3, '2026-03-20 08:30:00', 990000),
(9, 9, 2, '2026-03-22 13:00:00', 1250000),
(10, 10, 3, '2026-03-25 10:15:00', 1400000);

-- =========================================================
-- 18. IMPORT_DETAIL
-- =========================================================
INSERT INTO `import_detail` (`import_detail_id`, `import_id`, `book_id`, `quantity`, `import_price`) VALUES
(1, 1, 1, 10, 55000),
(2, 2, 2, 15, 60000),
(3, 3, 3, 12, 65000),
(4, 4, 4, 8, 90000),
(5, 5, 5, 20, 45000),
(6, 6, 6, 5, 250000),
(7, 7, 7, 6, 280000),
(8, 8, 8, 10, 70000),
(9, 9, 9, 9, 95000),
(10, 10, 10, 18, 35000);

-- =========================================================
-- 19. POINT_TRANSACTION
-- =========================================================
INSERT INTO `point_transaction`
(`transaction_id`, `user_id`, `order_id`, `rental_id`, `points_changed`, `reason`) VALUES
(1, 4, 1, NULL, 15, 'Tích điểm từ mua hàng'),
(2, 5, 2, NULL, 12, 'Tích điểm từ mua hàng'),
(3, 6, 3, NULL, 8, 'Tích điểm từ mua hàng'),
(4, 7, 4, NULL, 25, 'Tích điểm từ mua hàng'),
(5, 8, 5, NULL, 10, 'Tích điểm từ mua hàng'),
(6, 4, NULL, 1, 5, 'Thưởng trả sách đúng hạn'),
(7, 5, NULL, 2, -3, 'Trừ điểm do thuê quá hạn'),
(8, 6, NULL, 3, -2, 'Phạt trả muộn'),
(9, 7, NULL, 4, -5, 'Phạt quá hạn thuê'),
(10, 8, NULL, 5, 4, 'Thưởng trả sách đúng hạn');

-- =========================================================
-- 20. INVENTORY_LOG
-- user_id dùng admin/manager
-- =========================================================
INSERT INTO `inventory_log`
(`log_id`, `user_id`, `book_item_id`, `action_type`, `reference_order_id`, `reference_import_id`, `reference_rental_id`, `note`) VALUES
(1, 2, 1, 'Nhập mới', NULL, 1, NULL, 'Nhập sách mới vào kho'),
(2, 2, 2, 'Nhập mới', NULL, 2, NULL, 'Nhập sách mới vào kho'),
(3, 3, 3, 'Nhập mới', NULL, 3, NULL, 'Nhập sách cũ vào kho'),
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
(1, 4, 1, 5, 'Sách rất hay và cảm động.', 1),
(2, 5, 2, 5, 'Nội dung thực tế, dễ áp dụng.', 1),
(3, 6, 3, 4, 'Phù hợp cho người mới tìm hiểu tài chính.', 1),
(4, 7, 4, 5, 'Bản dịch tốt, nội dung hấp dẫn.', 1),
(5, 8, 5, 4, 'Sách thiếu nhi rất đáng đọc.', 1),
(6, 9, 6, 5, 'Rất hữu ích cho lập trình viên.', 1),
(7, 10, 7, 5, 'Cuốn Java nên có trên giá sách.', 1),
(8, 4, 8, 4, 'Truyền động lực tốt.', 1),
(9, 5, 9, 5, 'Kiến thức marketing cập nhật.', 1),
(10, 6, 10, 4, 'Tác phẩm kinh điển, đáng đọc.', 1);

-- =========================================================
-- 22. PAYMENT_TRANSACTION
-- =========================================================
INSERT INTO `payment_transaction`
(`payment_id`, `order_id`, `provider`, `provider_transaction_id`, `amount`, `status`, `response_code`) VALUES
(1, 2, 'VNPAY', 'VNPAY_TXN_0001', 125000, 'SUCCESS', '00'),
(2, 3, 'MOMO', 'MOMO_TXN_0002', 78000, 'SUCCESS', '00'),
(3, 4, 'BANKING', 'BANK_TXN_0003', 250000, 'SUCCESS', '00'),
(4, 6, 'MOMO', 'MOMO_TXN_0004', 126000, 'SUCCESS', '00'),
(5, 8, 'VNPAY', 'VNPAY_TXN_0005', 113000, 'SUCCESS', '00'),
(6, 9, 'BANKING', 'BANK_TXN_0006', 200000, 'SUCCESS', '00'),
(7, 1, 'COD', 'COD_TXN_0007', 143000, 'PENDING', NULL),
(8, 5, 'COD', 'COD_TXN_0008', 95000, 'PENDING', NULL),
(9, 7, 'COD', 'COD_TXN_0009', 145000, 'PENDING', NULL),
(10, 10, 'COD', 'COD_TXN_0010', 63000, 'PENDING', NULL);

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
