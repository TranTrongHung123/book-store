USE db_bookstore;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `chat_message`;
TRUNCATE TABLE `chat_session`;
TRUNCATE TABLE `payment_transaction`;
TRUNCATE TABLE `review`;
TRUNCATE TABLE `inventory_log`;
TRUNCATE TABLE `point_transaction`;
TRUNCATE TABLE `import_detail`;
TRUNCATE TABLE `book_import`;
TRUNCATE TABLE `rental`;
TRUNCATE TABLE `order_detail`;
TRUNCATE TABLE `orders`;
TRUNCATE TABLE `cart_item`;
TRUNCATE TABLE `cart`;
TRUNCATE TABLE `book_category`;
TRUNCATE TABLE `book_author`;
TRUNCATE TABLE `book_item`;
TRUNCATE TABLE `book`;
TRUNCATE TABLE `promotion`;
TRUNCATE TABLE `supplier`;
TRUNCATE TABLE `publisher`;
TRUNCATE TABLE `author`;
TRUNCATE TABLE `category`;
TRUNCATE TABLE `user`;
TRUNCATE TABLE `role`;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- 1. ROLE
-- =========================================================
INSERT INTO `role`
(`role_id`, `role_name`, `description`)
VALUES (1, 'ROLE_ADMIN', 'Quản trị viên toàn hệ thống, quản lý nhân sự và cấu hình'),
       (2, 'ROLE_STAFF', 'Nhân viên cửa hàng, xử lý đơn hàng, hỗ trợ khách hàng và xem tồn kho'),
       (3, 'ROLE_USER', 'Khách hàng mua sắm và sử dụng dịch vụ trực tuyến');

-- =========================================================
-- 2. CATEGORY
-- =========================================================
INSERT INTO `category`
(`category_id`, `parent_id`, `name`)
VALUES (1, NULL, 'Văn học'),
       (2, NULL, 'Kinh tế'),
       (3, NULL, 'Thiếu nhi'),
       (4, NULL, 'Kỹ năng sống'),
       (5, NULL, 'Công nghệ thông tin'),
       (6, 1, 'Tiểu thuyết'),
       (7, 1, 'Truyện ngắn'),
       (8, 2, 'Marketing'),
       (9, 2, 'Khởi nghiệp'),
       (10, 5, 'Lập trình Java'),
       (11, 5, 'Lập trình web'),
       (12, 5, 'Cơ sở dữ liệu'),
       (13, 2, 'Tài chính cá nhân'),
       (14, 2, 'Quản trị kinh doanh'),
       (15, 3, 'Truyện tranh'),
       (16, 3, 'Khoa học thiếu nhi'),
       (17, 4, 'Phát triển bản thân'),
       (18, 4, 'Tâm lý học ứng dụng'),
       (19, 1, 'Văn học Việt Nam'),
       (20, 1, 'Văn học nước ngoài'),
       (21, NULL, 'Khoa học'),
       (22, NULL, 'Lịch sử'),
       (23, NULL, 'Ngoại ngữ'),
       (24, NULL, 'Giáo trình - tham khảo');

-- =========================================================
-- 3. AUTHOR
-- =========================================================
INSERT INTO `author`
(`author_id`, `name`, `biography`)
VALUES (1, 'Nguyễn Nhật Ánh', 'Tác giả nổi tiếng với các tác phẩm tuổi học trò.'),
       (2, 'Dale Carnegie', 'Tác giả sách kỹ năng sống nổi tiếng thế giới.'),
       (3, 'Robert Kiyosaki', 'Tác giả sách tài chính cá nhân.'),
       (4, 'J.K. Rowling', 'Tác giả bộ truyện Harry Potter.'),
       (5, 'Tô Hoài', 'Nhà văn Việt Nam nổi tiếng.'),
       (6, 'Martin Fowler', 'Chuyên gia phần mềm và refactoring.'),
       (7, 'Joshua Bloch', 'Tác giả nổi tiếng trong lĩnh vực Java.'),
       (8, 'Adam Khoo', 'Tác giả sách phát triển bản thân.'),
       (9, 'Philip Kotler', 'Chuyên gia marketing hàng đầu.'),
       (10, 'Nam Cao', 'Nhà văn hiện thực phê phán Việt Nam.'),
       (11, 'Paulo Coelho', 'Tác giả tiểu thuyết truyền cảm hứng.'),
       (12, 'Haruki Murakami', 'Nhà văn Nhật Bản đương đại.'),
       (13, 'Yuval Noah Harari', 'Tác giả các sách lịch sử và tư duy hiện đại.'),
       (14, 'Stephen R. Covey', 'Tác giả sách quản trị bản thân.'),
       (15, 'James Clear', 'Tác giả sách về thói quen và hiệu suất cá nhân.'),
       (16, 'Eric Ries', 'Tác giả sách khởi nghiệp tinh gọn.'),
       (17, 'Jim Collins', 'Tác giả sách quản trị doanh nghiệp.'),
       (18, 'Donald A. Norman', 'Chuyên gia thiết kế trải nghiệm người dùng.'),
       (19, 'Robert C. Martin', 'Chuyên gia kỹ thuật phần mềm.'),
       (20, 'Andrew Hunt', 'Đồng tác giả The Pragmatic Programmer.'),
       (21, 'David Thomas', 'Đồng tác giả The Pragmatic Programmer.'),
       (22, 'Brian W. Kernighan', 'Tác giả sách lập trình kinh điển.'),
       (23, 'Bjarne Stroustrup', 'Cha đẻ ngôn ngữ C++.'),
       (24, 'Trần Đăng Khoa', 'Nhà thơ, tác giả văn học Việt Nam.'),
       (25, 'Nguyễn Ngọc Tư', 'Nhà văn Việt Nam đương đại.'),
       (26, 'Ngô Tất Tố', 'Nhà văn hiện thực Việt Nam.'),
       (27, 'Vũ Trọng Phụng', 'Nhà văn hiện thực trào phúng Việt Nam.'),
       (28, 'Antoine de Saint-Exupéry', 'Tác giả Hoàng Tử Bé.'),
       (29, 'Lewis Carroll', 'Tác giả Alice ở xứ sở thần tiên.'),
       (30, 'E. B. White', 'Tác giả văn học thiếu nhi kinh điển.'),
       (31, 'Stephen Hawking', 'Nhà vật lý lý thuyết, tác giả sách khoa học phổ thông.'),
       (32, 'Carl Sagan', 'Nhà thiên văn học, tác giả sách phổ biến khoa học.'),
       (33, 'Bill Bryson', 'Tác giả sách phổ thông về khoa học và lịch sử.'),
       (34, 'Richard Dawkins', 'Tác giả sách khoa học phổ thông về sinh học tiến hóa.'),
       (35, 'Neil deGrasse Tyson', 'Nhà vật lý thiên văn, tác giả sách khoa học đại chúng.'),
       (36, 'Daniel Kahneman', 'Nhà tâm lý học, tác giả sách về tư duy và ra quyết định.'),
       (37, 'Mark Manson', 'Tác giả sách phát triển bản thân hiện đại.'),
       (38, 'Cal Newport', 'Tác giả sách về hiệu suất cá nhân và làm việc sâu.'),
       (39, 'Peter Thiel', 'Doanh nhân, tác giả sách khởi nghiệp.'),
       (40, 'Nir Eyal', 'Tác giả sách về sản phẩm và hành vi người dùng.'),
       (41, 'Steve Krug', 'Chuyên gia usability và thiết kế trải nghiệm người dùng.'),
       (42, 'Marijn Haverbeke', 'Tác giả sách JavaScript nổi tiếng.'),
       (43, 'Kathy Sierra', 'Đồng tác giả Head First Java.'),
       (44, 'Bert Bates', 'Đồng tác giả Head First Java.'),
       (45, 'Allen B. Downey', 'Tác giả sách lập trình và khoa học máy tính.'),
       (46, 'Thomas H. Cormen', 'Đồng tác giả Introduction to Algorithms.'),
       (47, 'Eric Matthes', 'Tác giả Python Crash Course.'),
       (48, 'Al Sweigart', 'Tác giả Automate the Boring Stuff with Python.'),
       (49, 'Raymond Murphy', 'Tác giả bộ sách English Grammar in Use.'),
       (50, 'Nhiều tác giả', 'Nhóm tác giả hoặc sách tổng hợp kiến thức.');

-- =========================================================
-- 4. PUBLISHER
-- =========================================================
INSERT INTO `publisher`
(`publisher_id`, `name`, `contact_email`, `phone`, `address`)
VALUES (1, 'NXB Trẻ', 'contact@nxbtre.vn', '02838223344', 'TP. Hồ Chí Minh'),
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
INSERT INTO `supplier`
(`supplier_id`, `name`, `phone`, `address`)
VALUES (1, 'Công ty Sách Miền Bắc', '0911000001', 'Hà Nội'),
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
(`promotion_id`, `code`, `discount_percent`, `max_discount_amount`, `min_order_value`, `usage_limit`, `used_count`, `start_date`, `end_date`, `status`)
VALUES (1, 'WELCOME10', 10.0, 50000, 100000, 100, 5, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
       (2, 'BOOK20', 20.0, 80000, 200000, 50, 10, '2026-01-01 00:00:00', '2026-06-30 23:59:59', 1),
       (3, 'SUMMER15', 15.0, 60000, 150000, 80, 12, '2026-04-01 00:00:00', '2026-08-31 23:59:59', 1),
       (4, 'STUDENT5', 5.0, 20000, 50000, 200, 20, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
       (5, 'VIP25', 25.0, 120000, 300000, 20, 3, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
       (6, 'FREESHIP', 0.0, 30000, 100000, 150, 25, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
       (7, 'NEWUSER', 12.0, 40000, 120000, 100, 8, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
       (8, 'SALE30', 30.0, 150000, 500000, 10, 1, '2026-05-01 00:00:00', '2026-05-31 23:59:59', 1),
       (9, 'WEEKEND7', 7.0, 25000, 80000, 100, 6, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
       (10, 'OLDBOOK10', 10.0, 30000, 70000, 60, 4, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1);

-- =========================================================
-- 7. USER
-- =========================================================
INSERT INTO `user`
(`user_id`, `role_id`, `username`, `password`, `email`, `phone`, `address`, `full_name`, `total_points`, `status`)
VALUES (1, 1, 'admin01', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'admin01@bookstore.vn', '0901000001', 'Hà Nội', 'Quản trị viên 01', 0, 1),
       (2, 2, 'staff01', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'staff01@bookstore.vn', '0901000002', 'Hà Nội', 'Nhân viên 01', 0, 1),
       (3, 2, 'staff02', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'staff02@bookstore.vn', '0901000003', 'TP. Hồ Chí Minh', 'Nhân viên 02', 0, 1),
       (4, 3, 'nguyenvana', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'vana@gmail.com', '0901000004', 'Cầu Giấy, Hà Nội', 'Nguyễn Văn A', 120, 1),
       (5, 3, 'tranthib', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'thib@gmail.com', '0901000005', 'Hai Bà Trưng, Hà Nội', 'Trần Thị B', 80, 1),
       (6, 3, 'leminhc', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'minhc@gmail.com', '0901000006', 'Đống Đa, Hà Nội', 'Lê Minh C', 40, 1),
       (7, 3, 'phamthud', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'thud@gmail.com', '0901000007', 'Thanh Xuân, Hà Nội', 'Phạm Thu D', 200, 1),
       (8, 3, 'hoange', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'hoange@gmail.com', '0901000008', 'Long Biên, Hà Nội', 'Hoàng E', 60, 1),
       (9, 3, 'buiducthanh', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'thanh@gmail.com', '0901000009', 'Nam Từ Liêm, Hà Nội', 'Bùi Đức Thành', 95, 1),
       (10, 3, 'dodiepchi', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'diepchi@gmail.com', '0901000010', 'Hà Đông, Hà Nội', 'Đỗ Diệp Chi', 30, 1),
       (11, 3, 'ngocanh', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'ngocanh@gmail.com', '0901000011', 'Ba Đình, Hà Nội', 'Nguyễn Ngọc Anh', 50, 1),
       (12, 3, 'minhtu', '$2a$10$4k6fcDGBdjgVI3OesCJmOuqkVBwCpUQ2KmAlnVkDWWEtu3mgua9Nu', 'minhtu@gmail.com', '0901000012', 'Tân Bình, TP. Hồ Chí Minh', 'Trần Minh Tú', 70, 1);

-- =========================================================
-- 8. BOOK - 100 CUỐN
-- Ảnh bìa: ưu tiên Open Library Covers API cho sách phổ biến, dùng ảnh sách chất lượng cao từ Unsplash cho sách seed/tổng hợp.
-- =========================================================
INSERT INTO `book`
(`book_id`, `publisher_id`, `title`, `publication_year`, `language`, `original_price`, `selling_price`, `total_stock`, `description`, `cover_image`, `status`)
VALUES (1, 1, 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 2018, 'Tiếng Việt', 55000, 78000, 20, 'Tác phẩm nổi tiếng của Nguyễn Nhật Ánh.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=800&q=85', 1),
       (2, 3, 'Đắc Nhân Tâm', 2020, 'Tiếng Việt', 60000, 90000, 25, 'Sách kỹ năng giao tiếp kinh điển.', 'https://covers.openlibrary.org/b/isbn/9780671027032-L.jpg', 1),
       (3, 3, 'Cha Giàu Cha Nghèo', 2021, 'Tiếng Việt', 65000, 98000, 18, 'Sách tài chính cá nhân nổi tiếng.', 'https://covers.openlibrary.org/b/isbn/9781612680194-L.jpg', 1),
       (4, 6, 'Harry Potter Và Hòn Đá Phù Thủy', 2019, 'Tiếng Việt', 90000, 135000, 15, 'Phần đầu bộ Harry Potter.', 'https://covers.openlibrary.org/b/isbn/9780590353427-L.jpg', 1),
       (5, 2, 'Dế Mèn Phiêu Lưu Ký', 2017, 'Tiếng Việt', 45000, 68000, 30, 'Tác phẩm thiếu nhi kinh điển.', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=800&q=85', 1),
       (6, 4, 'Refactoring', 2022, 'Tiếng Anh', 250000, 320000, 10, 'Cải tiến cấu trúc mã nguồn.', 'https://covers.openlibrary.org/b/isbn/9780134757599-L.jpg', 1),
       (7, 4, 'Effective Java', 2022, 'Tiếng Anh', 280000, 350000, 12, 'Các thực hành tốt nhất trong Java.', 'https://covers.openlibrary.org/b/isbn/9780134685991-L.jpg', 1),
       (8, 8, 'Làm Chủ Tư Duy Thay Đổi Vận Mệnh', 2021, 'Tiếng Việt', 70000, 105000, 14, 'Sách phát triển bản thân.', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=800&q=85', 1),
       (9, 9, 'Marketing 4.0', 2023, 'Tiếng Việt', 95000, 145000, 16, 'Xu hướng marketing thời đại số.', 'https://covers.openlibrary.org/b/isbn/9781119341208-L.jpg', 1),
       (10, 5, 'Chí Phèo', 2016, 'Tiếng Việt', 35000, 52000, 22, 'Tác phẩm hiện thực nổi tiếng.', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?auto=format&fit=crop&w=800&q=85', 1),
       (11, 5, 'Nhà Giả Kim', 2021, 'Tiếng Việt', 72000, 109000, 18, 'Tiểu thuyết truyền cảm hứng về hành trình theo đuổi ước mơ.', 'https://covers.openlibrary.org/b/isbn/9780061122415-L.jpg', 1),
       (12, 5, 'Rừng Na Uy', 2020, 'Tiếng Việt', 98000, 149000, 13, 'Tiểu thuyết Nhật Bản đương đại giàu cảm xúc.', 'https://covers.openlibrary.org/b/isbn/9780375704024-L.jpg', 1),
       (13, 6, 'Sapiens Lược Sử Loài Người', 2022, 'Tiếng Việt', 150000, 220000, 11, 'Cuốn sách phổ biến về lịch sử và sự phát triển của loài người.', 'https://covers.openlibrary.org/b/isbn/9780062316097-L.jpg', 1),
       (14, 3, '7 Thói Quen Hiệu Quả', 2021, 'Tiếng Việt', 98000, 145000, 20, 'Sách nền tảng về quản trị bản thân.', 'https://covers.openlibrary.org/b/isbn/9780743269513-L.jpg', 1),
       (15, 6, 'Atomic Habits', 2023, 'Tiếng Việt', 105000, 159000, 28, 'Phương pháp xây dựng thói quen nhỏ tạo kết quả lớn.', 'https://covers.openlibrary.org/b/isbn/9780735211292-L.jpg', 1),
       (16, 9, 'The Lean Startup', 2022, 'Tiếng Anh', 180000, 250000, 9, 'Phương pháp khởi nghiệp tinh gọn.', 'https://covers.openlibrary.org/b/isbn/9780307887894-L.jpg', 1),
       (17, 3, 'Từ Tốt Đến Vĩ Đại', 2021, 'Tiếng Việt', 115000, 169000, 17, 'Bài học quản trị từ các doanh nghiệp xuất sắc.', 'https://covers.openlibrary.org/b/isbn/9780066620992-L.jpg', 1),
       (18, 6, 'Design of Everyday Things', 2021, 'Tiếng Anh', 220000, 295000, 8, 'Nền tảng tư duy thiết kế trải nghiệm người dùng.', 'https://covers.openlibrary.org/b/isbn/9780465050659-L.jpg', 1),
       (19, 4, 'Clean Code', 2022, 'Tiếng Anh', 260000, 335000, 12, 'Nguyên tắc viết mã sạch và dễ bảo trì.', 'https://covers.openlibrary.org/b/isbn/9780132350884-L.jpg', 1),
       (20, 4, 'The Pragmatic Programmer', 2022, 'Tiếng Anh', 245000, 315000, 10, 'Tư duy thực dụng cho lập trình viên.', 'https://covers.openlibrary.org/b/isbn/9780201616224-L.jpg', 1),
       (21, 5, 'Cánh Đồng Bất Tận', 2019, 'Tiếng Việt', 62000, 89000, 19, 'Tác phẩm văn học Việt Nam đương đại.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=800&q=85', 1),
       (22, 5, 'Tắt Đèn', 2018, 'Tiếng Việt', 42000, 62000, 21, 'Tiểu thuyết hiện thực phê phán Việt Nam.', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?auto=format&fit=crop&w=800&q=85', 1),
       (23, 5, 'Số Đỏ', 2018, 'Tiếng Việt', 50000, 76000, 18, 'Tiểu thuyết trào phúng nổi bật.', 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=800&q=85', 1),
       (24, 2, 'Hoàng Tử Bé', 2020, 'Tiếng Việt', 55000, 85000, 24, 'Câu chuyện thiếu nhi giàu tính triết lý.', 'https://covers.openlibrary.org/b/isbn/9780156012195-L.jpg', 1),
       (25, 2, 'Alice Ở Xứ Sở Thần Tiên', 2020, 'Tiếng Việt', 64000, 96000, 17, 'Tác phẩm thiếu nhi kinh điển thế giới.', 'https://covers.openlibrary.org/b/isbn/9780141439761-L.jpg', 1),
       (26, 2, 'Charlotte Và Wilbur', 2021, 'Tiếng Việt', 68000, 99000, 16, 'Câu chuyện ấm áp dành cho thiếu nhi.', 'https://covers.openlibrary.org/b/isbn/9780061124952-L.jpg', 1),
       (27, 6, 'Lược Sử Thời Gian', 2021, 'Tiếng Việt', 98000, 142000, 18, 'Sách khoa học phổ thông giúp người đọc tiếp cận vũ trụ học hiện đại.', 'https://covers.openlibrary.org/b/isbn/9780553380163-L.jpg', 1),
       (28, 6, 'Vũ Trụ Trong Vỏ Hạt Dẻ', 2022, 'Tiếng Việt', 106000, 154000, 16, 'Giải thích các ý tưởng vật lý hiện đại bằng lối viết dễ tiếp cận.', 'https://covers.openlibrary.org/b/isbn/9780553802023-L.jpg', 1),
       (29, 6, 'Cosmos', 2021, 'Tiếng Việt', 173000, 251000, 12, 'Hành trình khám phá vũ trụ, khoa học và vị trí của con người.', 'https://covers.openlibrary.org/b/isbn/9780345539434-L.jpg', 1),
       (30, 6, 'A Short History of Nearly Everything', 2020, 'Tiếng Anh', 165000, 239000, 14, 'Cuốn sách phổ thông về lịch sử khoa học tự nhiên.', 'https://covers.openlibrary.org/b/isbn/9780767908184-L.jpg', 1),
       (31, 6, 'The Selfish Gene', 2020, 'Tiếng Anh', 170000, 246000, 10, 'Sách phổ biến khoa học về tiến hóa và di truyền học.', 'https://covers.openlibrary.org/b/isbn/9780198788607-L.jpg', 1),
       (32, 6, 'The Magic of Reality', 2021, 'Tiếng Anh', 190000, 275000, 11, 'Giải thích các hiện tượng tự nhiên bằng tư duy khoa học.', 'https://covers.openlibrary.org/b/isbn/9781451675047-L.jpg', 1),
       (33, 6, 'Astrophysics for People in a Hurry', 2022, 'Tiếng Anh', 150000, 219000, 20, 'Nhập môn vật lý thiên văn ngắn gọn và dễ đọc.', 'https://covers.openlibrary.org/b/isbn/9780393609394-L.jpg', 1),
       (34, 6, 'The Gene: An Intimate History', 2022, 'Tiếng Anh', 220000, 315000, 9, 'Câu chuyện về gen, di truyền học và tác động tới đời sống.', 'https://covers.openlibrary.org/b/isbn/9781476733500-L.jpg', 1),
       (35, 3, 'Thinking, Fast and Slow', 2021, 'Tiếng Anh', 180000, 260000, 15, 'Phân tích hai hệ thống tư duy và các thiên kiến nhận thức.', 'https://covers.openlibrary.org/b/isbn/9780374533557-L.jpg', 1),
       (36, 3, 'Tâm Lý Học Đám Đông', 2020, 'Tiếng Việt', 79000, 118000, 22, 'Một tác phẩm nền tảng về hành vi tập thể và tâm lý xã hội.', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=800&q=85', 1),
       (37, 8, 'Đi Tìm Lẽ Sống', 2020, 'Tiếng Việt', 76000, 115000, 28, 'Câu chuyện về ý nghĩa sống, nghị lực và lựa chọn tinh thần.', 'https://covers.openlibrary.org/b/isbn/9780807014295-L.jpg', 1),
       (38, 3, 'Sức Mạnh Của Thói Quen', 2021, 'Tiếng Việt', 98000, 148000, 25, 'Giải thích cơ chế hình thành và thay đổi thói quen.', 'https://covers.openlibrary.org/b/isbn/9780812981605-L.jpg', 1),
       (39, 8, 'Nghệ Thuật Tinh Tế Của Việc Đếch Quan Tâm', 2022, 'Tiếng Việt', 89000, 139000, 18, 'Sách phát triển bản thân với góc nhìn hiện đại và thẳng thắn.', 'https://covers.openlibrary.org/b/isbn/9780062457714-L.jpg', 1),
       (40, 8, 'Deep Work', 2021, 'Tiếng Anh', 115000, 169000, 17, 'Phương pháp làm việc tập trung trong môi trường nhiều xao nhãng.', 'https://covers.openlibrary.org/b/isbn/9781455586691-L.jpg', 1),
       (41, 8, 'Digital Minimalism', 2021, 'Tiếng Anh', 120000, 175000, 13, 'Cách sử dụng công nghệ có chủ đích để nâng cao chất lượng sống.', 'https://covers.openlibrary.org/b/isbn/9780525536512-L.jpg', 1),
       (42, 9, 'Zero to One', 2022, 'Tiếng Anh', 155000, 225000, 16, 'Tư duy xây dựng startup tạo ra giá trị đột phá.', 'https://covers.openlibrary.org/b/isbn/9780804139298-L.jpg', 1),
       (43, 9, 'Hooked', 2021, 'Tiếng Anh', 145000, 215000, 18, 'Cách xây dựng sản phẩm hình thành thói quen người dùng.', 'https://covers.openlibrary.org/b/isbn/9781591847786-L.jpg', 1),
       (44, 9, 'Business Model Generation', 2020, 'Tiếng Anh', 210000, 295000, 10, 'Phương pháp thiết kế mô hình kinh doanh trực quan.', 'https://covers.openlibrary.org/b/isbn/9780470876411-L.jpg', 1),
       (45, 9, 'Sprint', 2021, 'Tiếng Anh', 185000, 265000, 12, 'Quy trình kiểm chứng ý tưởng sản phẩm trong thời gian ngắn.', 'https://covers.openlibrary.org/b/isbn/9781501121746-L.jpg', 1),
       (46, 6, 'Don''t Make Me Think', 2021, 'Tiếng Anh', 160000, 235000, 12, 'Sách kinh điển về usability và trải nghiệm người dùng.', 'https://covers.openlibrary.org/b/isbn/9780321965516-L.jpg', 1),
       (47, 4, 'Eloquent JavaScript', 2022, 'Tiếng Anh', 210000, 295000, 15, 'Nhập môn và nâng cao JavaScript qua ví dụ thực tế.', 'https://covers.openlibrary.org/b/isbn/9781593279509-L.jpg', 1),
       (48, 4, 'You Don''t Know JS', 2022, 'Tiếng Anh', 220000, 310000, 13, 'Bộ sách giúp hiểu sâu cơ chế hoạt động của JavaScript.', 'https://covers.openlibrary.org/b/isbn/9781491904244-L.jpg', 1),
       (49, 4, 'Head First Java', 2021, 'Tiếng Anh', 230000, 320000, 11, 'Học Java bằng phương pháp trực quan, dễ tiếp cận.', 'https://covers.openlibrary.org/b/isbn/9780596009205-L.jpg', 1),
       (50, 4, 'Java: The Complete Reference', 2022, 'Tiếng Anh', 280000, 370000, 9, 'Tài liệu tham khảo toàn diện về ngôn ngữ Java.', 'https://covers.openlibrary.org/b/isbn/9781260440232-L.jpg', 1),
       (51, 7, 'MySQL Crash Course', 2021, 'Tiếng Anh', 155000, 225000, 20, 'Hướng dẫn thực hành MySQL nhanh và dễ hiểu.', 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=800&q=85', 1),
       (52, 7, 'SQL Antipatterns', 2021, 'Tiếng Anh', 210000, 290000, 12, 'Các lỗi thiết kế SQL thường gặp và cách tránh.', 'https://covers.openlibrary.org/b/isbn/9781934356555-L.jpg', 1),
       (53, 7, 'Database System Concepts', 2022, 'Tiếng Anh', 300000, 420000, 8, 'Giáo trình nền tảng về hệ quản trị cơ sở dữ liệu.', 'https://covers.openlibrary.org/b/isbn/9780073523323-L.jpg', 1),
       (54, 7, 'Operating System Concepts', 2022, 'Tiếng Anh', 320000, 450000, 7, 'Giáo trình tham khảo về hệ điều hành.', 'https://covers.openlibrary.org/b/isbn/9781118063330-L.jpg', 1),
       (55, 7, 'Computer Networking: A Top-Down Approach', 2022, 'Tiếng Anh', 330000, 460000, 7, 'Giáo trình mạng máy tính theo hướng tiếp cận từ tầng ứng dụng.', 'https://covers.openlibrary.org/b/isbn/9780133594140-L.jpg', 1),
       (56, 4, 'Code Complete', 2021, 'Tiếng Anh', 270000, 360000, 9, 'Sách chuyên sâu về xây dựng phần mềm chất lượng cao.', 'https://covers.openlibrary.org/b/isbn/9780735619678-L.jpg', 1),
       (57, 7, 'Introduction to Algorithms', 2022, 'Tiếng Anh', 450000, 590000, 6, 'Giáo trình thuật toán kinh điển cho sinh viên công nghệ.', 'https://covers.openlibrary.org/b/isbn/9780262033848-L.jpg', 1),
       (58, 4, 'Python Crash Course', 2023, 'Tiếng Anh', 240000, 335000, 14, 'Nhập môn Python qua dự án thực hành.', 'https://covers.openlibrary.org/b/isbn/9781593279288-L.jpg', 1),
       (59, 4, 'Automate the Boring Stuff with Python', 2023, 'Tiếng Anh', 230000, 320000, 13, 'Ứng dụng Python để tự động hóa công việc thường ngày.', 'https://covers.openlibrary.org/b/isbn/9781593275990-L.jpg', 1),
       (60, 4, 'Learning React', 2022, 'Tiếng Anh', 250000, 345000, 10, 'Học React hiện đại thông qua ví dụ và best practices.', 'https://covers.openlibrary.org/b/isbn/9781492051725-L.jpg', 1),
       (61, 4, 'Lập Trình Web Với HTML CSS JavaScript', 2022, 'Tiếng Việt', 125000, 185000, 26, 'Nền tảng xây dựng giao diện web cho người mới bắt đầu.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=800&q=85', 1),
       (62, 4, 'Spring Boot Thực Chiến', 2023, 'Tiếng Việt', 180000, 260000, 18, 'Xây dựng REST API và ứng dụng backend với Spring Boot.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=800&q=85', 1),
       (63, 7, 'Cấu Trúc Dữ Liệu Và Giải Thuật', 2022, 'Tiếng Việt', 160000, 235000, 19, 'Kiến thức nền tảng về cấu trúc dữ liệu và thuật toán.', 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=800&q=85', 1),
       (64, 7, 'Nhập Môn Khoa Học Dữ Liệu', 2023, 'Tiếng Việt', 170000, 245000, 15, 'Tư duy dữ liệu, phân tích và trực quan hóa cơ bản.', 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=800&q=85', 1),
       (65, 9, 'Marketing Căn Bản', 2021, 'Tiếng Việt', 95000, 145000, 25, 'Nhập môn marketing với các khái niệm và ví dụ thực tế.', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=800&q=85', 1),
       (66, 9, 'Nguyên Lý Marketing', 2021, 'Tiếng Việt', 120000, 180000, 20, 'Các nguyên lý cốt lõi trong quản trị marketing.', 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=800&q=85', 1),
       (67, 9, 'Marketing 5.0', 2023, 'Tiếng Việt', 150000, 220000, 18, 'Ứng dụng công nghệ trong marketing hiện đại.', 'https://covers.openlibrary.org/b/isbn/9781119668541-L.jpg', 1),
       (68, 9, 'Content Marketing Trong Kỷ Nguyên Số', 2022, 'Tiếng Việt', 105000, 155000, 24, 'Chiến lược xây dựng nội dung số cho thương hiệu.', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=800&q=85', 1),
       (69, 8, 'Xây Dựng Thương Hiệu Cá Nhân', 2022, 'Tiếng Việt', 98000, 145000, 23, 'Hướng dẫn định vị hình ảnh cá nhân trong học tập và công việc.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=800&q=85', 1),
       (70, 9, 'Khởi Nghiệp Bán Lẻ', 2021, 'Tiếng Việt', 115000, 168000, 18, 'Kinh nghiệm vận hành cửa hàng, bán lẻ và chăm sóc khách hàng.', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?auto=format&fit=crop&w=800&q=85', 1),
       (71, 9, 'Quản Trị Vận Hành', 2022, 'Tiếng Việt', 135000, 195000, 16, 'Các nguyên tắc tổ chức quy trình và tối ưu vận hành.', 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=800&q=85', 1),
       (72, 9, 'Quản Trị Chiến Lược', 2022, 'Tiếng Việt', 145000, 210000, 14, 'Khung phân tích chiến lược và năng lực cạnh tranh doanh nghiệp.', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=800&q=85', 1),
       (73, 8, 'Nhà Lãnh Đạo Không Chức Danh', 2021, 'Tiếng Việt', 96000, 139000, 22, 'Bài học lãnh đạo bản thân và tạo ảnh hưởng trong tổ chức.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=800&q=85', 1),
       (74, 3, 'Nghĩ Giàu Làm Giàu', 2021, 'Tiếng Việt', 89000, 135000, 26, 'Tư duy tài chính và động lực phát triển cá nhân.', 'https://covers.openlibrary.org/b/isbn/9781585424337-L.jpg', 1),
       (75, 3, 'Người Giàu Có Nhất Thành Babylon', 2021, 'Tiếng Việt', 69000, 105000, 28, 'Các nguyên tắc tài chính cá nhân qua câu chuyện dễ hiểu.', 'https://covers.openlibrary.org/b/isbn/9780451205360-L.jpg', 1),
       (76, 3, 'Dạy Con Làm Giàu', 2020, 'Tiếng Việt', 85000, 128000, 24, 'Tư duy tài chính cá nhân và giáo dục tiền bạc.', 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=800&q=85', 1),
       (77, 3, 'Tài Chính Cá Nhân Cho Người Việt', 2023, 'Tiếng Việt', 98000, 148000, 20, 'Hướng dẫn quản lý tiền bạc phù hợp với người Việt.', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=800&q=85', 1),
       (78, 3, 'Đầu Tư Chứng Khoán Căn Bản', 2023, 'Tiếng Việt', 125000, 185000, 16, 'Kiến thức nhập môn về chứng khoán và quản trị rủi ro.', 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=800&q=85', 1),
       (79, 3, 'Bí Mật Tư Duy Triệu Phú', 2021, 'Tiếng Việt', 88000, 132000, 23, 'Sách về tư duy tài chính và phát triển bản thân.', 'https://covers.openlibrary.org/b/isbn/9780060763282-L.jpg', 1),
       (80, 5, 'Muôn Kiếp Nhân Sinh', 2021, 'Tiếng Việt', 120000, 180000, 18, 'Tác phẩm kết hợp yếu tố tự sự, triết lý và suy ngẫm nhân sinh.', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=800&q=85', 1),
       (81, 1, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 2020, 'Tiếng Việt', 78000, 115000, 28, 'Tác phẩm tuổi thơ nổi tiếng của Nguyễn Nhật Ánh.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=800&q=85', 1),
       (82, 1, 'Mắt Biếc', 2020, 'Tiếng Việt', 76000, 112000, 26, 'Câu chuyện tình yêu và ký ức học trò.', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?auto=format&fit=crop&w=800&q=85', 1),
       (83, 1, 'Cô Gái Đến Từ Hôm Qua', 2020, 'Tiếng Việt', 72000, 108000, 24, 'Tác phẩm nhẹ nhàng về tuổi học trò và những rung động đầu đời.', 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=800&q=85', 1),
       (84, 1, 'Ngồi Khóc Trên Cây', 2020, 'Tiếng Việt', 82000, 122000, 20, 'Câu chuyện trong trẻo về tình bạn và tuổi mới lớn.', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=800&q=85', 1),
       (85, 1, 'Tôi Là Bêtô', 2019, 'Tiếng Việt', 65000, 98000, 22, 'Tác phẩm kể chuyện qua góc nhìn gần gũi, hóm hỉnh.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=800&q=85', 1),
       (86, 5, 'Những Người Khốn Khổ', 2019, 'Tiếng Việt', 180000, 260000, 12, 'Tiểu thuyết kinh điển của văn học thế giới.', 'https://covers.openlibrary.org/b/isbn/9780451419439-L.jpg', 1),
       (87, 5, 'Bố Già', 2020, 'Tiếng Việt', 125000, 185000, 15, 'Tiểu thuyết nổi tiếng về gia đình, quyền lực và thế giới ngầm.', 'https://covers.openlibrary.org/b/isbn/9780451205766-L.jpg', 1),
       (88, 5, 'Kiêu Hãnh Và Định Kiến', 2020, 'Tiếng Việt', 98000, 145000, 18, 'Tác phẩm văn học cổ điển về tình yêu và định kiến xã hội.', 'https://covers.openlibrary.org/b/isbn/9780141439518-L.jpg', 1),
       (89, 5, 'Không Gia Đình', 2019, 'Tiếng Việt', 105000, 155000, 16, 'Câu chuyện phiêu lưu và trưởng thành giàu cảm xúc.', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=800&q=85', 1),
       (90, 5, 'Sherlock Holmes Toàn Tập', 2021, 'Tiếng Việt', 210000, 310000, 10, 'Tuyển tập truyện trinh thám kinh điển.', 'https://covers.openlibrary.org/b/isbn/9780553212419-L.jpg', 1),
       (91, 2, 'Doraemon Tập 1', 2022, 'Tiếng Việt', 25000, 35000, 40, 'Truyện tranh thiếu nhi nổi tiếng, vui nhộn và giàu trí tưởng tượng.', 'https://images.unsplash.com/photo-1519682337058-a94d519337bc?auto=format&fit=crop&w=800&q=85', 1),
       (92, 2, 'Conan Tập 1', 2022, 'Tiếng Việt', 25000, 35000, 38, 'Truyện tranh trinh thám dành cho thiếu nhi và thanh thiếu niên.', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=800&q=85', 1),
       (93, 2, 'Shin Cậu Bé Bút Chì Tập 1', 2022, 'Tiếng Việt', 25000, 35000, 35, 'Truyện tranh hài hước về đời sống gia đình và học đường.', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=800&q=85', 1),
       (94, 2, 'Thần Đồng Đất Việt Tập 1', 2021, 'Tiếng Việt', 30000, 45000, 32, 'Truyện tranh Việt Nam kết hợp lịch sử và yếu tố hài hước.', 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?auto=format&fit=crop&w=800&q=85', 1),
       (95, 2, 'Những Câu Hỏi Vì Sao', 2022, 'Tiếng Việt', 70000, 105000, 30, 'Sách giải đáp các câu hỏi khoa học thường gặp cho thiếu nhi.', 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=800&q=85', 1),
       (96, 2, 'Bách Khoa Tri Thức Cho Trẻ Em', 2022, 'Tiếng Việt', 160000, 235000, 18, 'Sách bách khoa nhiều chủ đề cho trẻ em.', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=800&q=85', 1),
       (97, 2, 'Atlas Thế Giới Cho Trẻ Em', 2021, 'Tiếng Việt', 120000, 180000, 17, 'Sách bản đồ và kiến thức địa lý cơ bản dành cho trẻ em.', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=800&q=85', 1),
       (98, 2, '100 Thí Nghiệm Khoa Học Vui', 2023, 'Tiếng Việt', 98000, 145000, 21, 'Các thí nghiệm khoa học đơn giản, an toàn và dễ thực hiện.', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=800&q=85', 1),
       (99, 7, 'English Grammar in Use', 2022, 'Tiếng Anh', 180000, 260000, 14, 'Sách học ngữ pháp tiếng Anh nổi tiếng cho người tự học.', 'https://covers.openlibrary.org/b/isbn/9781108457651-L.jpg', 1),
       (100, 7, 'Oxford Word Skills Basic', 2022, 'Tiếng Anh', 160000, 235000, 13, 'Sách học từ vựng tiếng Anh theo chủ đề ở trình độ cơ bản.', 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=800&q=85', 1);

-- =========================================================
-- 9. BOOK_ITEM - 100 BẢN GHI
-- =========================================================
INSERT INTO `book_item`
(`book_item_id`, `book_id`, `barcode`, `condition_type`, `condition_note`, `deposit_amount`, `current_rental_price`, `is_for_rent`, `status`, `position`)
VALUES (1, 1, 'BC0001', 'Mới', 'Sách mới 100%', 27000, 9000, 1, 'Sẵn sàng', 'Kệ A1'),
       (2, 2, 'BC0002', 'Mới', 'Sách mới 100%', 31000, 11000, 1, 'Sẵn sàng', 'Kệ A2'),
       (3, 3, 'BC0003', 'Mới', 'Sách mới 100%', 34000, 12000, 1, 'Đang thuê', 'Kệ A3'),
       (4, 4, 'BC0004', 'Cũ', 'Sách cũ, tình trạng còn tốt', 47000, 16000, 1, 'Đã bán', 'Kệ A4'),
       (5, 5, 'BC0005', 'Mới', 'Sách mới 100%', 24000, 8000, 1, 'Sẵn sàng', 'Kệ A5'),
       (6, 6, 'BC0006', 'Mới', 'Sách mới 100%', 112000, 38000, 1, 'Sẵn sàng', 'Kệ A6'),
       (7, 7, 'BC0007', 'Mới', 'Sách mới 100%', 122000, 42000, 1, 'Sẵn sàng', 'Kệ A7'),
       (8, 8, 'BC0008', 'Cũ', 'Sách cũ, tình trạng còn tốt', 37000, 13000, 1, 'Sẵn sàng', 'Kệ A8'),
       (9, 9, 'BC0009', 'Mới', 'Sách mới 100%', 51000, 17000, 1, 'Sẵn sàng', 'Kệ A9'),
       (10, 10, 'BC0010', 'Mới', 'Sách mới 100%', 18000, 7000, 1, 'Sẵn sàng', 'Kệ A10'),
       (11, 11, 'BC0011', 'Mới', 'Sách mới 100%', 38000, 13000, 1, 'Sẵn sàng', 'Kệ B1'),
       (12, 12, 'BC0012', 'Cũ', 'Sách cũ, tình trạng còn tốt', 52000, 18000, 1, 'Sẵn sàng', 'Kệ B2'),
       (13, 13, 'BC0013', 'Mới', 'Sách mới 100%', 77000, 26000, 1, 'Đang thuê', 'Kệ B3'),
       (14, 14, 'BC0014', 'Mới', 'Sách mới 100%', 51000, 17000, 1, 'Đã bán', 'Kệ B4'),
       (15, 15, 'BC0015', 'Mới', 'Sách mới 100%', 56000, 19000, 1, 'Sẵn sàng', 'Kệ B5'),
       (16, 16, 'BC0016', 'Cũ', 'Sách cũ, tình trạng còn tốt', 88000, 30000, 1, 'Sẵn sàng', 'Kệ B6'),
       (17, 17, 'BC0017', 'Mới', 'Sách mới 100%', 59000, 20000, 1, 'Sẵn sàng', 'Kệ B7'),
       (18, 18, 'BC0018', 'Mới', 'Sách mới 100%', 103000, 35000, 1, 'Sẵn sàng', 'Kệ B8'),
       (19, 19, 'BC0019', 'Mới', 'Sách mới 100%', 117000, 40000, 1, 'Sẵn sàng', 'Kệ B9'),
       (20, 20, 'BC0020', 'Cũ', 'Sách cũ, tình trạng còn tốt', 110000, 38000, 1, 'Sẵn sàng', 'Kệ B10'),
       (21, 21, 'BC0021', 'Mới', 'Sách mới 100%', 31000, 11000, 1, 'Sẵn sàng', 'Kệ C1'),
       (22, 22, 'BC0022', 'Mới', 'Sách mới 100%', 22000, 7000, 1, 'Sẵn sàng', 'Kệ C2'),
       (23, 23, 'BC0023', 'Mới', 'Sách mới 100%', 27000, 9000, 1, 'Đang thuê', 'Kệ C3'),
       (24, 24, 'BC0024', 'Cũ', 'Sách cũ, tình trạng còn tốt', 30000, 10000, 1, 'Đã bán', 'Kệ C4'),
       (25, 25, 'BC0025', 'Mới', 'Sách mới 100%', 34000, 12000, 1, 'Sẵn sàng', 'Kệ C5'),
       (26, 26, 'BC0026', 'Mới', 'Sách mới 100%', 35000, 12000, 1, 'Sẵn sàng', 'Kệ C6'),
       (27, 27, 'BC0027', 'Mới', 'Sách mới 100%', 50000, 17000, 1, 'Sẵn sàng', 'Kệ C7'),
       (28, 28, 'BC0028', 'Cũ', 'Sách cũ, tình trạng còn tốt', 54000, 18000, 1, 'Sẵn sàng', 'Kệ C8'),
       (29, 29, 'BC0029', 'Mới', 'Sách mới 100%', 88000, 30000, 1, 'Sẵn sàng', 'Kệ C9'),
       (30, 30, 'BC0030', 'Mới', 'Sách mới 100%', 84000, 29000, 1, 'Sẵn sàng', 'Kệ C10'),
       (31, 31, 'BC0031', 'Mới', 'Sách mới 100%', 86000, 30000, 1, 'Sẵn sàng', 'Kệ D1'),
       (32, 32, 'BC0032', 'Cũ', 'Sách cũ, tình trạng còn tốt', 96000, 33000, 1, 'Sẵn sàng', 'Kệ D2'),
       (33, 33, 'BC0033', 'Mới', 'Sách mới 100%', 77000, 26000, 1, 'Đang thuê', 'Kệ D3'),
       (34, 34, 'BC0034', 'Mới', 'Sách mới 100%', 110000, 38000, 1, 'Đã bán', 'Kệ D4'),
       (35, 35, 'BC0035', 'Mới', 'Sách mới 100%', 91000, 31000, 1, 'Sẵn sàng', 'Kệ D5'),
       (36, 36, 'BC0036', 'Cũ', 'Sách cũ, tình trạng còn tốt', 41000, 14000, 1, 'Sẵn sàng', 'Kệ D6'),
       (37, 37, 'BC0037', 'Mới', 'Sách mới 100%', 40000, 14000, 1, 'Sẵn sàng', 'Kệ D7'),
       (38, 38, 'BC0038', 'Mới', 'Sách mới 100%', 52000, 18000, 1, 'Sẵn sàng', 'Kệ D8'),
       (39, 39, 'BC0039', 'Mới', 'Sách mới 100%', 49000, 17000, 1, 'Sẵn sàng', 'Kệ D9'),
       (40, 40, 'BC0040', 'Cũ', 'Sách cũ, tình trạng còn tốt', 59000, 20000, 1, 'Sẵn sàng', 'Kệ D10'),
       (41, 41, 'BC0041', 'Mới', 'Sách mới 100%', 61000, 21000, 1, 'Sẵn sàng', 'Kệ E1'),
       (42, 42, 'BC0042', 'Mới', 'Sách mới 100%', 79000, 27000, 1, 'Sẵn sàng', 'Kệ E2'),
       (43, 43, 'BC0043', 'Mới', 'Sách mới 100%', 75000, 26000, 1, 'Đang thuê', 'Kệ E3'),
       (44, 44, 'BC0044', 'Cũ', 'Sách cũ, tình trạng còn tốt', 103000, 35000, 1, 'Đã bán', 'Kệ E4'),
       (45, 45, 'BC0045', 'Mới', 'Sách mới 100%', 93000, 32000, 1, 'Sẵn sàng', 'Kệ E5'),
       (46, 46, 'BC0046', 'Mới', 'Sách mới 100%', 82000, 28000, 1, 'Sẵn sàng', 'Kệ E6'),
       (47, 47, 'BC0047', 'Mới', 'Sách mới 100%', 103000, 35000, 1, 'Sẵn sàng', 'Kệ E7'),
       (48, 48, 'BC0048', 'Cũ', 'Sách cũ, tình trạng còn tốt', 108000, 37000, 1, 'Sẵn sàng', 'Kệ E8'),
       (49, 49, 'BC0049', 'Mới', 'Sách mới 100%', 112000, 38000, 1, 'Sẵn sàng', 'Kệ E9'),
       (50, 50, 'BC0050', 'Mới', 'Sách mới 100%', 129000, 44000, 1, 'Sẵn sàng', 'Kệ E10'),
       (51, 51, 'BC0051', 'Mới', 'Sách mới 100%', 79000, 27000, 1, 'Sẵn sàng', 'Kệ F1'),
       (52, 52, 'BC0052', 'Cũ', 'Sách cũ, tình trạng còn tốt', 102000, 35000, 1, 'Sẵn sàng', 'Kệ F2'),
       (53, 53, 'BC0053', 'Mới', 'Sách mới 100%', 147000, 50000, 1, 'Đang thuê', 'Kệ F3'),
       (54, 54, 'BC0054', 'Mới', 'Sách mới 100%', 158000, 54000, 1, 'Đã bán', 'Kệ F4'),
       (55, 55, 'BC0055', 'Mới', 'Sách mới 100%', 161000, 55000, 1, 'Sẵn sàng', 'Kệ F5'),
       (56, 56, 'BC0056', 'Cũ', 'Sách cũ, tình trạng còn tốt', 126000, 43000, 1, 'Sẵn sàng', 'Kệ F6'),
       (57, 57, 'BC0057', 'Mới', 'Sách mới 100%', 206000, 71000, 1, 'Sẵn sàng', 'Kệ F7'),
       (58, 58, 'BC0058', 'Mới', 'Sách mới 100%', 117000, 40000, 1, 'Sẵn sàng', 'Kệ F8'),
       (59, 59, 'BC0059', 'Mới', 'Sách mới 100%', 112000, 38000, 1, 'Sẵn sàng', 'Kệ F9'),
       (60, 60, 'BC0060', 'Cũ', 'Sách cũ, tình trạng còn tốt', 121000, 41000, 1, 'Sẵn sàng', 'Kệ F10'),
       (61, 61, 'BC0061', 'Mới', 'Sách mới 100%', 65000, 22000, 1, 'Sẵn sàng', 'Kệ G1'),
       (62, 62, 'BC0062', 'Mới', 'Sách mới 100%', 91000, 31000, 1, 'Sẵn sàng', 'Kệ G2'),
       (63, 63, 'BC0063', 'Mới', 'Sách mới 100%', 82000, 28000, 1, 'Đang thuê', 'Kệ G3'),
       (64, 64, 'BC0064', 'Cũ', 'Sách cũ, tình trạng còn tốt', 86000, 29000, 1, 'Đã bán', 'Kệ G4'),
       (65, 65, 'BC0065', 'Mới', 'Sách mới 100%', 51000, 17000, 1, 'Sẵn sàng', 'Kệ G5'),
       (66, 66, 'BC0066', 'Mới', 'Sách mới 100%', 63000, 22000, 1, 'Sẵn sàng', 'Kệ G6'),
       (67, 67, 'BC0067', 'Mới', 'Sách mới 100%', 77000, 26000, 1, 'Sẵn sàng', 'Kệ G7'),
       (68, 68, 'BC0068', 'Cũ', 'Sách cũ, tình trạng còn tốt', 54000, 19000, 1, 'Sẵn sàng', 'Kệ G8'),
       (69, 69, 'BC0069', 'Mới', 'Sách mới 100%', 51000, 17000, 1, 'Sẵn sàng', 'Kệ G9'),
       (70, 70, 'BC0070', 'Mới', 'Sách mới 100%', 59000, 20000, 1, 'Sẵn sàng', 'Kệ G10'),
       (71, 71, 'BC0071', 'Mới', 'Sách mới 100%', 68000, 23000, 1, 'Sẵn sàng', 'Kệ H1'),
       (72, 72, 'BC0072', 'Cũ', 'Sách cũ, tình trạng còn tốt', 74000, 25000, 1, 'Sẵn sàng', 'Kệ H2'),
       (73, 73, 'BC0073', 'Mới', 'Sách mới 100%', 49000, 17000, 1, 'Đang thuê', 'Kệ H3'),
       (74, 74, 'BC0074', 'Mới', 'Sách mới 100%', 47000, 16000, 1, 'Đã bán', 'Kệ H4'),
       (75, 75, 'BC0075', 'Mới', 'Sách mới 100%', 37000, 13000, 1, 'Sẵn sàng', 'Kệ H5'),
       (76, 76, 'BC0076', 'Cũ', 'Sách cũ, tình trạng còn tốt', 45000, 15000, 1, 'Sẵn sàng', 'Kệ H6'),
       (77, 77, 'BC0077', 'Mới', 'Sách mới 100%', 52000, 18000, 1, 'Sẵn sàng', 'Kệ H7'),
       (78, 78, 'BC0078', 'Mới', 'Sách mới 100%', 65000, 22000, 1, 'Sẵn sàng', 'Kệ H8'),
       (79, 79, 'BC0079', 'Mới', 'Sách mới 100%', 46000, 16000, 1, 'Sẵn sàng', 'Kệ H9'),
       (80, 80, 'BC0080', 'Cũ', 'Sách cũ, tình trạng còn tốt', 63000, 22000, 1, 'Sẵn sàng', 'Kệ H10'),
       (81, 81, 'BC0081', 'Mới', 'Sách mới 100%', 40000, 14000, 1, 'Sẵn sàng', 'Kệ I1'),
       (82, 82, 'BC0082', 'Mới', 'Sách mới 100%', 39000, 13000, 1, 'Sẵn sàng', 'Kệ I2'),
       (83, 83, 'BC0083', 'Mới', 'Sách mới 100%', 38000, 13000, 1, 'Đang thuê', 'Kệ I3'),
       (84, 84, 'BC0084', 'Cũ', 'Sách cũ, tình trạng còn tốt', 43000, 15000, 1, 'Đã bán', 'Kệ I4'),
       (85, 85, 'BC0085', 'Mới', 'Sách mới 100%', 34000, 12000, 1, 'Sẵn sàng', 'Kệ I5'),
       (86, 86, 'BC0086', 'Mới', 'Sách mới 100%', 91000, 31000, 1, 'Sẵn sàng', 'Kệ I6'),
       (87, 87, 'BC0087', 'Mới', 'Sách mới 100%', 65000, 22000, 1, 'Sẵn sàng', 'Kệ I7'),
       (88, 88, 'BC0088', 'Cũ', 'Sách cũ, tình trạng còn tốt', 51000, 17000, 1, 'Sẵn sàng', 'Kệ I8'),
       (89, 89, 'BC0089', 'Mới', 'Sách mới 100%', 54000, 19000, 1, 'Sẵn sàng', 'Kệ I9'),
       (90, 90, 'BC0090', 'Mới', 'Sách mới 100%', 108000, 37000, 1, 'Sẵn sàng', 'Kệ I10'),
       (91, 91, 'BC0091', 'Mới', 'Sách mới 100%', 12000, 7000, 1, 'Sẵn sàng', 'Kệ J1'),
       (92, 92, 'BC0092', 'Cũ', 'Sách cũ, tình trạng còn tốt', 12000, 7000, 1, 'Sẵn sàng', 'Kệ J2'),
       (93, 93, 'BC0093', 'Mới', 'Sách mới 100%', 12000, 7000, 1, 'Đang thuê', 'Kệ J3'),
       (94, 94, 'BC0094', 'Mới', 'Sách mới 100%', 16000, 7000, 1, 'Đã bán', 'Kệ J4'),
       (95, 95, 'BC0095', 'Mới', 'Sách mới 100%', 37000, 13000, 1, 'Sẵn sàng', 'Kệ J5'),
       (96, 96, 'BC0096', 'Cũ', 'Sách cũ, tình trạng còn tốt', 82000, 28000, 1, 'Sẵn sàng', 'Kệ J6'),
       (97, 97, 'BC0097', 'Mới', 'Sách mới 100%', 63000, 22000, 1, 'Sẵn sàng', 'Kệ J7'),
       (98, 98, 'BC0098', 'Mới', 'Sách mới 100%', 51000, 17000, 1, 'Sẵn sàng', 'Kệ J8'),
       (99, 99, 'BC0099', 'Mới', 'Sách mới 100%', 91000, 31000, 1, 'Sẵn sàng', 'Kệ J9'),
       (100, 100, 'BC0100', 'Cũ', 'Sách cũ, tình trạng còn tốt', 82000, 28000, 1, 'Sẵn sàng', 'Kệ J10');

-- =========================================================
-- 10. BOOK_AUTHOR
-- =========================================================
INSERT INTO `book_author`
(`book_id`, `author_id`, `author_role`)
VALUES (1, 1, 'Tác giả chính'),
       (2, 2, 'Tác giả chính'),
       (3, 3, 'Tác giả chính'),
       (4, 4, 'Tác giả chính'),
       (5, 5, 'Tác giả chính'),
       (6, 6, 'Tác giả chính'),
       (7, 7, 'Tác giả chính'),
       (8, 8, 'Tác giả chính'),
       (9, 9, 'Tác giả chính'),
       (10, 10, 'Tác giả chính'),
       (11, 11, 'Tác giả chính'),
       (12, 12, 'Tác giả chính'),
       (13, 13, 'Tác giả chính'),
       (14, 14, 'Tác giả chính'),
       (15, 15, 'Tác giả chính'),
       (16, 16, 'Tác giả chính'),
       (17, 17, 'Tác giả chính'),
       (18, 18, 'Tác giả chính'),
       (19, 19, 'Tác giả chính'),
       (20, 20, 'Tác giả chính'),
       (21, 25, 'Tác giả chính'),
       (22, 26, 'Tác giả chính'),
       (23, 27, 'Tác giả chính'),
       (24, 28, 'Tác giả chính'),
       (25, 29, 'Tác giả chính'),
       (26, 30, 'Tác giả chính'),
       (27, 31, 'Tác giả chính'),
       (28, 31, 'Tác giả chính'),
       (29, 32, 'Tác giả chính'),
       (30, 33, 'Tác giả chính'),
       (31, 34, 'Tác giả chính'),
       (32, 34, 'Tác giả chính'),
       (33, 35, 'Tác giả chính'),
       (34, 50, 'Tác giả chính'),
       (35, 36, 'Tác giả chính'),
       (36, 50, 'Tác giả chính'),
       (37, 50, 'Tác giả chính'),
       (38, 50, 'Tác giả chính'),
       (39, 37, 'Tác giả chính'),
       (40, 38, 'Tác giả chính'),
       (41, 38, 'Tác giả chính'),
       (42, 39, 'Tác giả chính'),
       (43, 40, 'Tác giả chính'),
       (44, 50, 'Tác giả chính'),
       (45, 50, 'Tác giả chính'),
       (46, 41, 'Tác giả chính'),
       (47, 42, 'Tác giả chính'),
       (48, 50, 'Tác giả chính'),
       (49, 43, 'Tác giả chính'),
       (50, 50, 'Tác giả chính'),
       (51, 50, 'Tác giả chính'),
       (52, 50, 'Tác giả chính'),
       (53, 50, 'Tác giả chính'),
       (54, 50, 'Tác giả chính'),
       (55, 50, 'Tác giả chính'),
       (56, 50, 'Tác giả chính'),
       (57, 46, 'Tác giả chính'),
       (58, 47, 'Tác giả chính'),
       (59, 48, 'Tác giả chính'),
       (60, 50, 'Tác giả chính'),
       (61, 50, 'Tác giả chính'),
       (62, 50, 'Tác giả chính'),
       (63, 50, 'Tác giả chính'),
       (64, 50, 'Tác giả chính'),
       (65, 9, 'Tác giả chính'),
       (66, 9, 'Tác giả chính'),
       (67, 9, 'Tác giả chính'),
       (68, 50, 'Tác giả chính'),
       (69, 50, 'Tác giả chính'),
       (70, 50, 'Tác giả chính'),
       (71, 50, 'Tác giả chính'),
       (72, 50, 'Tác giả chính'),
       (73, 50, 'Tác giả chính'),
       (74, 50, 'Tác giả chính'),
       (75, 50, 'Tác giả chính'),
       (76, 3, 'Tác giả chính'),
       (77, 50, 'Tác giả chính'),
       (78, 50, 'Tác giả chính'),
       (79, 50, 'Tác giả chính'),
       (80, 50, 'Tác giả chính'),
       (81, 1, 'Tác giả chính'),
       (82, 1, 'Tác giả chính'),
       (83, 1, 'Tác giả chính'),
       (84, 1, 'Tác giả chính'),
       (85, 1, 'Tác giả chính'),
       (86, 50, 'Tác giả chính'),
       (87, 50, 'Tác giả chính'),
       (88, 50, 'Tác giả chính'),
       (89, 50, 'Tác giả chính'),
       (90, 50, 'Tác giả chính'),
       (91, 50, 'Tác giả chính'),
       (92, 50, 'Tác giả chính'),
       (93, 50, 'Tác giả chính'),
       (94, 50, 'Tác giả chính'),
       (95, 50, 'Tác giả chính'),
       (96, 50, 'Tác giả chính'),
       (97, 50, 'Tác giả chính'),
       (98, 50, 'Tác giả chính'),
       (99, 49, 'Tác giả chính'),
       (100, 50, 'Tác giả chính');

-- =========================================================
-- 11. BOOK_CATEGORY
-- =========================================================
INSERT INTO `book_category`
(`book_id`, `category_id`, `is_primary`)
VALUES (1, 19, 1),
       (2, 4, 1),
       (3, 13, 1),
       (4, 20, 1),
       (5, 3, 1),
       (6, 5, 1),
       (7, 10, 1),
       (8, 17, 1),
       (9, 8, 1),
       (10, 19, 1),
       (11, 20, 1),
       (12, 20, 1),
       (13, 22, 1),
       (14, 17, 1),
       (15, 17, 1),
       (16, 9, 1),
       (17, 14, 1),
       (18, 11, 1),
       (19, 5, 1),
       (20, 5, 1),
       (21, 19, 1),
       (22, 19, 1),
       (23, 19, 1),
       (24, 3, 1),
       (25, 3, 1),
       (26, 3, 1),
       (27, 21, 1),
       (28, 21, 1),
       (29, 21, 1),
       (30, 21, 1),
       (31, 21, 1),
       (32, 21, 1),
       (33, 21, 1),
       (34, 21, 1),
       (35, 18, 1),
       (36, 18, 1),
       (37, 4, 1),
       (38, 17, 1),
       (39, 17, 1),
       (40, 4, 1),
       (41, 4, 1),
       (42, 9, 1),
       (43, 11, 1),
       (44, 14, 1),
       (45, 9, 1),
       (46, 11, 1),
       (47, 11, 1),
       (48, 11, 1),
       (49, 10, 1),
       (50, 10, 1),
       (51, 12, 1),
       (52, 12, 1),
       (53, 12, 1),
       (54, 24, 1),
       (55, 24, 1),
       (56, 5, 1),
       (57, 24, 1),
       (58, 5, 1),
       (59, 5, 1),
       (60, 11, 1),
       (61, 11, 1),
       (62, 10, 1),
       (63, 24, 1),
       (64, 24, 1),
       (65, 8, 1),
       (66, 8, 1),
       (67, 8, 1),
       (68, 8, 1),
       (69, 4, 1),
       (70, 9, 1),
       (71, 14, 1),
       (72, 14, 1),
       (73, 4, 1),
       (74, 13, 1),
       (75, 13, 1),
       (76, 13, 1),
       (77, 13, 1),
       (78, 13, 1),
       (79, 13, 1),
       (80, 6, 1),
       (81, 19, 1),
       (82, 19, 1),
       (83, 19, 1),
       (84, 19, 1),
       (85, 19, 1),
       (86, 20, 1),
       (87, 20, 1),
       (88, 20, 1),
       (89, 20, 1),
       (90, 20, 1),
       (91, 15, 1),
       (92, 15, 1),
       (93, 15, 1),
       (94, 15, 1),
       (95, 16, 1),
       (96, 16, 1),
       (97, 16, 1),
       (98, 16, 1),
       (99, 23, 1),
       (100, 23, 1);

-- =========================================================
-- 12. CART
-- =========================================================
INSERT INTO `cart`
(`cart_id`, `user_id`)
VALUES (1, 4),
       (2, 5),
       (3, 6),
       (4, 7),
       (5, 8),
       (6, 9),
       (7, 10),
       (8, 11),
       (9, 12);

-- =========================================================
-- 13. CART_ITEM
-- =========================================================
INSERT INTO `cart_item`
(`cart_item_id`, `cart_id`, `book_id`, `quantity`)
VALUES (1, 1, 1, 1),
       (2, 1, 15, 2),
       (3, 2, 3, 1),
       (4, 2, 24, 1),
       (5, 3, 4, 1),
       (6, 3, 7, 1),
       (7, 4, 8, 1),
       (8, 4, 9, 1),
       (9, 5, 10, 3),
       (10, 5, 6, 1),
       (11, 6, 28, 1),
       (12, 7, 35, 2),
       (13, 8, 42, 1),
       (14, 9, 50, 1);

-- =========================================================
-- 14. ORDERS
-- =========================================================
INSERT INTO `orders`
(`order_id`, `user_id`, `promotion_id`, `points_used`, `promotion_discount_amount`, `point_discount_amount`, `total_amount`, `shipping_address`, `payment_method`, `payment_status`, `order_status`)
VALUES (1, 4, 1, 10, 15000, 10000, 143000, 'Cầu Giấy, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt'),
       (2, 5, 3, 0, 20000, 0, 125000, 'Hai Bà Trưng, Hà Nội', 'VNPAY', 'Đã thanh toán', 'Đang giao'),
       (3, 6, NULL, 20, 0, 20000, 78000, 'Đống Đa, Hà Nội', 'MOMO', 'Đã thanh toán', 'Thành công'),
       (4, 7, 5, 50, 50000, 50000, 250000, 'Thanh Xuân, Hà Nội', 'Banking', 'Đã thanh toán', 'Thành công'),
       (5, 8, 9, 0, 10000, 0, 95000, 'Long Biên, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt'),
       (6, 9, 4, 5, 5000, 5000, 126000, 'Nam Từ Liêm, Hà Nội', 'MOMO', 'Đã thanh toán', 'Thành công'),
       (7, 10, NULL, 0, 0, 0, 145000, 'Hà Đông, Hà Nội', 'COD', 'Chưa thanh toán', 'Đang giao'),
       (8, 4, 7, 10, 12000, 10000, 113000, 'Cầu Giấy, Hà Nội', 'VNPAY', 'Đã thanh toán', 'Thành công'),
       (9, 5, 2, 0, 30000, 0, 200000, 'Hai Bà Trưng, Hà Nội', 'Banking', 'Đã thanh toán', 'Đã giao'),
       (10, 6, 10, 0, 5000, 0, 63000, 'Đống Đa, Hà Nội', 'COD', 'Chưa thanh toán', 'Chờ duyệt'),
       (11, 11, 1, 0, 18000, 0, 162000, 'Ba Đình, Hà Nội', 'VNPAY', 'Đã thanh toán', 'Đang xử lý'),
       (12, 12, NULL, 0, 0, 0, 99000, 'Tân Bình, TP. Hồ Chí Minh', 'COD', 'Chưa thanh toán', 'Đã hủy');

-- =========================================================
-- 15. ORDER_DETAIL
-- =========================================================
INSERT INTO `order_detail`
(`order_detail_id`, `order_id`, `book_id`, `quantity`, `unit_price`)
VALUES (1, 1, 1, 1, 78000),
       (2, 1, 2, 1, 90000),
       (3, 2, 4, 1, 135000),
       (4, 3, 1, 1, 78000),
       (5, 3, 5, 1, 68000),
       (6, 4, 6, 1, 320000),
       (7, 5, 3, 1, 98000),
       (8, 6, 8, 1, 105000),
       (9, 7, 9, 1, 145000),
       (10, 8, 2, 1, 90000),
       (11, 9, 16, 1, 250000),
       (12, 9, 17, 1, 169000),
       (13, 10, 10, 1, 52000),
       (14, 11, 15, 1, 159000),
       (15, 11, 24, 1, 85000),
       (16, 12, 26, 1, 99000);

-- =========================================================
-- 16. RENTAL
-- =========================================================
INSERT INTO `rental`
(`rental_id`, `user_id`, `book_item_id`, `rent_date`, `due_date`, `return_date`, `actual_deposit`, `rental_fee`, `penalty_fee`, `payment_method`, `payment_status`, `status`)
VALUES (1, 4, 1, '2026-04-01 09:00:00', '2026-04-08 09:00:00', '2026-04-07 10:00:00', 30000, 10000, 0, 'Tiền mặt', 'Đã hoàn cọc', 'Đã trả'),
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
-- =========================================================
INSERT INTO `book_import`
(`import_id`, `supplier_id`, `user_id`, `import_date`, `total_cost`)
VALUES (1, 1, 2, '2026-03-01 09:00:00', 6744000),
       (2, 2, 3, '2026-03-02 10:00:00', 4982000),
       (3, 3, 2, '2026-03-03 11:00:00', 6790000),
       (4, 4, 3, '2026-03-04 12:00:00', 8620000),
       (5, 5, 2, '2026-03-05 13:00:00', 8116000),
       (6, 6, 3, '2026-03-06 14:00:00', 13218000),
       (7, 7, 2, '2026-03-07 15:00:00', 14156000),
       (8, 8, 3, '2026-03-08 08:00:00', 7656000),
       (9, 9, 2, '2026-03-09 09:00:00', 12678000),
       (10, 10, 3, '2026-03-10 10:00:00', 6610000),
       (11, 1, 2, '2026-03-11 11:00:00', 9442000),
       (12, 2, 3, '2026-03-12 12:00:00', 11999000),
       (13, 3, 2, '2026-03-13 13:00:00', 15816000),
       (14, 4, 3, '2026-03-14 14:00:00', 19168000),
       (15, 5, 2, '2026-03-15 15:00:00', 11832000),
       (16, 6, 3, '2026-03-16 08:00:00', 13198000),
       (17, 7, 2, '2026-03-17 09:00:00', 11430000),
       (18, 8, 3, '2026-03-18 10:00:00', 6695000),
       (19, 9, 2, '2026-03-19 11:00:00', 14580000),
       (20, 10, 3, '2026-03-20 12:00:00', 12667000);

-- =========================================================
-- 18. IMPORT_DETAIL - 100 DÒNG NHẬP SÁCH
-- =========================================================
INSERT INTO `import_detail`
(`import_detail_id`, `import_id`, `book_id`, `quantity`, `import_price`)
VALUES (1, 1, 1, 8, 55000),
       (2, 2, 2, 11, 60000),
       (3, 3, 3, 14, 65000),
       (4, 4, 4, 17, 90000),
       (5, 5, 5, 20, 45000),
       (6, 6, 6, 23, 250000),
       (7, 7, 7, 26, 280000),
       (8, 8, 8, 29, 70000),
       (9, 9, 9, 6, 95000),
       (10, 10, 10, 9, 35000),
       (11, 11, 11, 12, 72000),
       (12, 12, 12, 15, 98000),
       (13, 13, 13, 18, 150000),
       (14, 14, 14, 21, 98000),
       (15, 15, 15, 24, 105000),
       (16, 16, 16, 27, 180000),
       (17, 17, 17, 30, 115000),
       (18, 18, 18, 7, 220000),
       (19, 19, 19, 10, 260000),
       (20, 20, 20, 13, 245000),
       (21, 1, 21, 16, 62000),
       (22, 2, 22, 19, 42000),
       (23, 3, 23, 22, 50000),
       (24, 4, 24, 25, 55000),
       (25, 5, 25, 28, 64000),
       (26, 6, 26, 5, 68000),
       (27, 7, 27, 8, 98000),
       (28, 8, 28, 11, 106000),
       (29, 9, 29, 14, 173000),
       (30, 10, 30, 17, 165000),
       (31, 11, 31, 20, 170000),
       (32, 12, 32, 23, 190000),
       (33, 13, 33, 26, 150000),
       (34, 14, 34, 29, 220000),
       (35, 15, 35, 6, 180000),
       (36, 16, 36, 9, 79000),
       (37, 17, 37, 12, 76000),
       (38, 18, 38, 15, 98000),
       (39, 19, 39, 18, 89000),
       (40, 20, 40, 21, 115000),
       (41, 1, 41, 24, 120000),
       (42, 2, 42, 27, 155000),
       (43, 3, 43, 30, 145000),
       (44, 4, 44, 7, 210000),
       (45, 5, 45, 10, 185000),
       (46, 6, 46, 13, 160000),
       (47, 7, 47, 16, 210000),
       (48, 8, 48, 19, 220000),
       (49, 9, 49, 22, 230000),
       (50, 10, 50, 25, 280000),
       (51, 11, 51, 28, 155000),
       (52, 12, 52, 5, 210000),
       (53, 13, 53, 8, 300000),
       (54, 14, 54, 11, 320000),
       (55, 15, 55, 14, 330000),
       (56, 16, 56, 17, 270000),
       (57, 17, 57, 20, 450000),
       (58, 18, 58, 23, 240000),
       (59, 19, 59, 26, 230000),
       (60, 20, 60, 29, 250000),
       (61, 1, 61, 6, 125000),
       (62, 2, 62, 9, 180000),
       (63, 3, 63, 12, 160000),
       (64, 4, 64, 15, 170000),
       (65, 5, 65, 18, 95000),
       (66, 6, 66, 21, 120000),
       (67, 7, 67, 24, 150000),
       (68, 8, 68, 27, 105000),
       (69, 9, 69, 30, 98000),
       (70, 10, 70, 7, 115000),
       (71, 11, 71, 10, 135000),
       (72, 12, 72, 13, 145000),
       (73, 13, 73, 16, 96000),
       (74, 14, 74, 19, 89000),
       (75, 15, 75, 22, 69000),
       (76, 16, 76, 25, 85000),
       (77, 17, 77, 28, 98000),
       (78, 18, 78, 5, 125000),
       (79, 19, 79, 8, 88000),
       (80, 20, 80, 11, 120000),
       (81, 1, 81, 14, 78000),
       (82, 2, 82, 17, 76000),
       (83, 3, 83, 20, 72000),
       (84, 4, 84, 23, 82000),
       (85, 5, 85, 26, 65000),
       (86, 6, 86, 29, 180000),
       (87, 7, 87, 6, 125000),
       (88, 8, 88, 9, 98000),
       (89, 9, 89, 12, 105000),
       (90, 10, 90, 15, 210000),
       (91, 11, 91, 18, 25000),
       (92, 12, 92, 21, 25000),
       (93, 13, 93, 24, 25000),
       (94, 14, 94, 27, 30000),
       (95, 15, 95, 30, 70000),
       (96, 16, 96, 7, 160000),
       (97, 17, 97, 10, 120000),
       (98, 18, 98, 13, 98000),
       (99, 19, 99, 16, 180000),
       (100, 20, 100, 19, 160000);

-- =========================================================
-- 19. POINT_TRANSACTION
-- =========================================================
INSERT INTO `point_transaction`
(`transaction_id`, `user_id`, `order_id`, `rental_id`, `points_changed`, `reason`)
VALUES (1, 4, 1, NULL, 15, 'Tích điểm từ mua hàng'),
       (2, 5, 2, NULL, 12, 'Tích điểm từ mua hàng'),
       (3, 6, 3, NULL, 8, 'Tích điểm từ mua hàng'),
       (4, 7, 4, NULL, 25, 'Tích điểm từ mua hàng'),
       (5, 8, 5, NULL, 10, 'Tích điểm từ mua hàng'),
       (6, 4, NULL, 1, 5, 'Thưởng trả sách đúng hạn'),
       (7, 5, NULL, 2, -3, 'Trừ điểm do thuê quá hạn'),
       (8, 6, NULL, 3, -2, 'Phạt trả muộn'),
       (9, 7, NULL, 4, -5, 'Phạt quá hạn thuê'),
       (10, 8, NULL, 5, 4, 'Thưởng trả sách đúng hạn'),
       (11, 11, 11, NULL, 16, 'Tích điểm từ mua hàng'),
       (12, 12, 12, NULL, 0, 'Đơn hàng đã hủy không tích điểm');

-- =========================================================
-- 20. INVENTORY_LOG
-- =========================================================
INSERT INTO `inventory_log`
(`log_id`, `user_id`, `book_item_id`, `action_type`, `reference_order_id`, `reference_import_id`, `reference_rental_id`, `note`)
VALUES (1, 2, 1, 'Nhập mới', NULL, 1, NULL, 'Nhập sách mới vào kho'),
       (2, 3, 2, 'Nhập mới', NULL, 2, NULL, 'Nhập sách mới vào kho'),
       (3, 2, 3, 'Nhập mới', NULL, 3, NULL, 'Nhập sách mới vào kho'),
       (4, 3, 4, 'Nhập mới', NULL, 4, NULL, 'Nhập sách mới vào kho'),
       (5, 2, 5, 'Nhập mới', NULL, 5, NULL, 'Nhập sách mới vào kho'),
       (6, 3, 6, 'Nhập mới', NULL, 6, NULL, 'Nhập sách mới vào kho'),
       (7, 2, 7, 'Nhập mới', NULL, 7, NULL, 'Nhập sách mới vào kho'),
       (8, 3, 8, 'Nhập mới', NULL, 8, NULL, 'Nhập sách mới vào kho'),
       (9, 2, 9, 'Nhập mới', NULL, 9, NULL, 'Nhập sách mới vào kho'),
       (10, 3, 10, 'Nhập mới', NULL, 10, NULL, 'Nhập sách mới vào kho'),
       (11, 2, 11, 'Nhập mới', NULL, 11, NULL, 'Nhập sách mới vào kho'),
       (12, 3, 12, 'Nhập mới', NULL, 12, NULL, 'Nhập sách mới vào kho'),
       (13, 2, 13, 'Nhập mới', NULL, 13, NULL, 'Nhập sách mới vào kho'),
       (14, 3, 14, 'Nhập mới', NULL, 14, NULL, 'Nhập sách mới vào kho'),
       (15, 2, 15, 'Nhập mới', NULL, 15, NULL, 'Nhập sách mới vào kho'),
       (16, 3, 16, 'Nhập mới', NULL, 16, NULL, 'Nhập sách mới vào kho'),
       (17, 2, 17, 'Nhập mới', NULL, 17, NULL, 'Nhập sách mới vào kho'),
       (18, 3, 18, 'Nhập mới', NULL, 18, NULL, 'Nhập sách mới vào kho'),
       (19, 2, 19, 'Nhập mới', NULL, 19, NULL, 'Nhập sách mới vào kho'),
       (20, 3, 20, 'Nhập mới', NULL, 20, NULL, 'Nhập sách mới vào kho'),
       (21, 2, 21, 'Xuất bán', 1, NULL, NULL, 'Xuất sách bán cho khách'),
       (22, 3, 22, 'Xuất bán', 2, NULL, NULL, 'Xuất sách bán cho khách'),
       (23, 2, 23, 'Xuất bán', 3, NULL, NULL, 'Xuất sách bán cho khách'),
       (24, 3, 24, 'Xuất bán', 4, NULL, NULL, 'Xuất sách bán cho khách'),
       (25, 2, 25, 'Thuê', NULL, NULL, 1, 'Xuất sách cho khách thuê'),
       (26, 3, 26, 'Thuê', NULL, NULL, 2, 'Xuất sách cho khách thuê'),
       (27, 2, 27, 'Thuê', NULL, NULL, 3, 'Xuất sách cho khách thuê'),
       (28, 3, 28, 'Thuê', NULL, NULL, 4, 'Xuất sách cho khách thuê'),
       (29, 2, 29, 'Trả', NULL, NULL, 5, 'Khách trả sách'),
       (30, 3, 30, 'Trả', NULL, NULL, 6, 'Khách trả sách');

-- =========================================================
-- 21. REVIEW
-- =========================================================
INSERT INTO `review`
(`review_id`, `user_id`, `book_id`, `rating`, `comment`, `is_approved`)
VALUES (1, 4, 1, 5, 'Nội dung thực tế và hữu ích.', 1),
       (2, 5, 2, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (3, 6, 3, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (4, 7, 4, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (5, 8, 5, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (6, 9, 6, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (7, 10, 7, 5, 'Nội dung thực tế và hữu ích.', 1),
       (8, 11, 8, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (9, 12, 9, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (10, 4, 10, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (11, 5, 11, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (12, 6, 12, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (13, 7, 13, 5, 'Nội dung thực tế và hữu ích.', 1),
       (14, 8, 14, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (15, 9, 15, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (16, 10, 16, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (17, 11, 17, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (18, 12, 18, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (19, 4, 19, 5, 'Nội dung thực tế và hữu ích.', 1),
       (20, 5, 20, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (21, 6, 21, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (22, 7, 22, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (23, 8, 23, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (24, 9, 24, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (25, 10, 25, 5, 'Nội dung thực tế và hữu ích.', 1),
       (26, 11, 26, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (27, 12, 27, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (28, 4, 28, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (29, 5, 29, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (30, 6, 30, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (31, 7, 31, 5, 'Nội dung thực tế và hữu ích.', 1),
       (32, 8, 32, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (33, 9, 33, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (34, 10, 34, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (35, 11, 35, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (36, 12, 36, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (37, 4, 37, 5, 'Nội dung thực tế và hữu ích.', 1),
       (38, 5, 38, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (39, 6, 39, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (40, 7, 40, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (41, 8, 41, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (42, 9, 42, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (43, 10, 43, 5, 'Nội dung thực tế và hữu ích.', 1),
       (44, 11, 44, 4, 'Giao hàng nhanh, sách còn mới.', 1),
       (45, 12, 45, 5, 'Phù hợp để đọc cuối tuần.', 1),
       (46, 4, 46, 4, 'Bìa đẹp, giấy ổn trong tầm giá.', 1),
       (47, 5, 47, 5, 'Nội dung dễ hiểu cho người mới.', 1),
       (48, 6, 48, 4, 'Sách hay, trình bày dễ đọc.', 1),
       (49, 7, 49, 5, 'Nội dung thực tế và hữu ích.', 1),
       (50, 8, 50, 4, 'Giao hàng nhanh, sách còn mới.', 1);

-- =========================================================
-- 22. PAYMENT_TRANSACTION
-- =========================================================
INSERT INTO `payment_transaction`
(`payment_id`, `order_id`, `provider`, `provider_transaction_id`, `amount`, `status`, `response_code`)
VALUES (1, 2, 'VNPAY', 'VNPAY_TXN_0001', 125000, 'SUCCESS', '00'),
       (2, 3, 'MOMO', 'MOMO_TXN_0002', 78000, 'SUCCESS', '00'),
       (3, 4, 'BANKING', 'BANK_TXN_0003', 250000, 'SUCCESS', '00'),
       (4, 6, 'MOMO', 'MOMO_TXN_0004', 126000, 'SUCCESS', '00'),
       (5, 8, 'VNPAY', 'VNPAY_TXN_0005', 113000, 'SUCCESS', '00'),
       (6, 9, 'BANKING', 'BANK_TXN_0006', 200000, 'SUCCESS', '00'),
       (7, 1, 'COD', 'COD_TXN_0007', 143000, 'PENDING', NULL),
       (8, 5, 'COD', 'COD_TXN_0008', 95000, 'PENDING', NULL),
       (9, 7, 'COD', 'COD_TXN_0009', 145000, 'PENDING', NULL),
       (10, 10, 'COD', 'COD_TXN_0010', 63000, 'PENDING', NULL),
       (11, 11, 'VNPAY', 'VNPAY_TXN_0011', 162000, 'SUCCESS', '00'),
       (12, 12, 'COD', 'COD_TXN_0012', 99000, 'CANCELLED', NULL);

-- =========================================================
-- 23. CHAT_SESSION
-- =========================================================
INSERT INTO `chat_session`
(`session_id`, `user_id`, `started_at`, `ended_at`, `status`)
VALUES (1, 4, '2026-04-10 08:00:00', '2026-04-10 08:10:00', 'CLOSED'),
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
INSERT INTO `chat_message`
(`message_id`, `session_id`, `sender_type`, `content`)
VALUES (1, 1, 'USER', 'Shop còn sách Đắc Nhân Tâm không?'),
       (2, 1, 'BOT', 'Dạ còn ạ, hiện sách đang có sẵn trong kho.'),
       (3, 2, 'USER', 'Tôi muốn kiểm tra đơn hàng #2'),
       (4, 2, 'BOT', 'Đơn hàng của bạn đang được giao.'),
       (5, 3, 'USER', 'Làm sao để thuê sách?'),
       (6, 3, 'BOT', 'Bạn chọn sách, đặt cọc và xác nhận thời gian thuê.'),
       (7, 4, 'USER', 'Tôi có thể dùng mã giảm giá nào?'),
       (8, 4, 'BOT', 'Bạn có thể dùng mã WELCOME10 hoặc SUMMER15 nếu đủ điều kiện.'),
       (9, 5, 'USER', 'Shop mở cửa đến mấy giờ?'),
       (10, 5, 'BOT', 'Shop mở cửa từ 8h đến 21h hằng ngày.');

-- =========================================================
-- RESET AUTO_INCREMENT SAU KHI INSERT ID THỦ CÔNG
-- =========================================================
ALTER TABLE `role` AUTO_INCREMENT = 4;
ALTER TABLE `category` AUTO_INCREMENT = 25;
ALTER TABLE `author` AUTO_INCREMENT = 51;
ALTER TABLE `publisher` AUTO_INCREMENT = 11;
ALTER TABLE `supplier` AUTO_INCREMENT = 11;
ALTER TABLE `promotion` AUTO_INCREMENT = 11;
ALTER TABLE `user` AUTO_INCREMENT = 13;
ALTER TABLE `book` AUTO_INCREMENT = 101;
ALTER TABLE `book_item` AUTO_INCREMENT = 101;
ALTER TABLE `cart` AUTO_INCREMENT = 10;
ALTER TABLE `cart_item` AUTO_INCREMENT = 15;
ALTER TABLE `orders` AUTO_INCREMENT = 13;
ALTER TABLE `order_detail` AUTO_INCREMENT = 17;
ALTER TABLE `rental` AUTO_INCREMENT = 11;
ALTER TABLE `book_import` AUTO_INCREMENT = 21;
ALTER TABLE `import_detail` AUTO_INCREMENT = 101;
ALTER TABLE `point_transaction` AUTO_INCREMENT = 13;
ALTER TABLE `inventory_log` AUTO_INCREMENT = 31;
ALTER TABLE `review` AUTO_INCREMENT = 51;
ALTER TABLE `payment_transaction` AUTO_INCREMENT = 13;
ALTER TABLE `chat_session` AUTO_INCREMENT = 11;
ALTER TABLE `chat_message` AUTO_INCREMENT = 11;

