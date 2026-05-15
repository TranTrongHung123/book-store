package com.ptit.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Loi he thong chua xac dinh", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(9000, "Du lieu dau vao khong hop le", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(9101, "Ban can dang nhap de truy cap tai nguyen", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(9102, "Ban khong co quyen thuc hien thao tac nay", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(9103, "Token khong hop le", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(9104, "Token da het han", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS(9105, "Ten dang nhap hoac mat khau khong dung", HttpStatus.UNAUTHORIZED),

    BOOK_NOT_FOUND(1001, "Khong tim thay sach trong he thong", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1002, "Nguoi dung khong ton tai", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1003, "The loai khong hop le", HttpStatus.NOT_FOUND),
    PUBLISHER_NOT_FOUND(1004, "Nha xuat ban khong ton tai", HttpStatus.NOT_FOUND),
    INSUFFICIENT_STOCK(1005, "So luong sach trong kho khong du", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1006, "Vai tro khong ton tai", HttpStatus.NOT_FOUND),
    DUPLICATE_USERNAME(1007, "Ten dang nhap da ton tai", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL(1008, "Email da ton tai", HttpStatus.BAD_REQUEST),
    INVALID_TOTAL_POINTS(1009, "Diem tich luy khong hop le", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND(1010, "Dia chi giao hang khong ton tai", HttpStatus.NOT_FOUND),

    ORDER_NOT_FOUND(2001, "Khong tim thay don hang", HttpStatus.NOT_FOUND),
    ORDER_ITEMS_EMPTY(2002, "Don hang phai co it nhat mot san pham", HttpStatus.BAD_REQUEST),
    ORDER_ITEM_INVALID(2003, "Thong tin san pham trong don hang khong hop le", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_FOUND(2004, "Ma khuyen mai khong ton tai", HttpStatus.NOT_FOUND),
    PROMOTION_NOT_ACTIVE(2005, "Ma khuyen mai khong kha dung", HttpStatus.BAD_REQUEST),
    PROMOTION_EXPIRED(2006, "Ma khuyen mai da het han", HttpStatus.BAD_REQUEST),
    PROMOTION_NOT_STARTED(2007, "Ma khuyen mai chua den thoi gian ap dung", HttpStatus.BAD_REQUEST),
    PROMOTION_USAGE_LIMIT_EXCEEDED(2008, "Ma khuyen mai da het luot su dung", HttpStatus.BAD_REQUEST),
    PROMOTION_MIN_ORDER_NOT_MET(2009, "Don hang chua dat gia tri toi thieu de ap ma", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_BE_CANCELLED(2010, "Don hang hien tai khong the huy", HttpStatus.BAD_REQUEST),

    RENTAL_NOT_FOUND(3001, "Khong tim thay phieu thue", HttpStatus.NOT_FOUND),
    BOOK_ITEM_NOT_FOUND(3002, "Khong tim thay cuon sach vat ly", HttpStatus.NOT_FOUND),
    BOOK_ITEM_NOT_FOR_RENT(3003, "Cuon sach nay khong ho tro cho thue", HttpStatus.BAD_REQUEST),
    BOOK_ITEM_NOT_AVAILABLE(3004, "Cuon sach hien khong san sang de cho thue", HttpStatus.BAD_REQUEST),
    INVALID_RENTAL_DATA(3005, "Du lieu thue sach khong hop le", HttpStatus.BAD_REQUEST),

    CHATBOT_SESSION_NOT_FOUND(4001, "Phien chat khong ton tai hoac da ket thuc", HttpStatus.NOT_FOUND),
    CHATBOT_AI_QUOTA_EXCEEDED(4002, "Dich vu AI tam thoi khong kha dung do het quota, vui long thu lai sau", HttpStatus.SERVICE_UNAVAILABLE),
    CHATBOT_AI_UNAVAILABLE(4003, "Dich vu AI dang gap su co, vui long thu lai sau", HttpStatus.SERVICE_UNAVAILABLE),
    CHATBOT_INVALID_RESPONSE(4004, "AI tra ve dinh dang khong hop le", HttpStatus.INTERNAL_SERVER_ERROR),
    CHATBOT_MESSAGE_EMPTY(4005, "Noi dung tin nhan khong duoc de trong", HttpStatus.BAD_REQUEST),

    // Flash sale
    FLASH_SALE_NOT_ACTIVE(5001, "Flash sale chua bat dau hoac da ket thuc", HttpStatus.BAD_REQUEST),
    FLASH_SALE_SOLD_OUT(5002, "San pham flash sale da het hang", HttpStatus.CONFLICT),
    FLASH_SALE_MAX_PER_USER(5003, "Bạn đã mua sản phẩm này trong chương trình Flash Sale rồi. Mỗi khách hàng chỉ được mua tối đa 1 lần.", HttpStatus.CONFLICT),
    FLASH_SALE_RESERVATION_EXPIRED(5004, "Reservation da het han", HttpStatus.GONE),
    FLASH_SALE_DUPLICATE_REQUEST(5005, "Yeu cau dang duoc xu ly, vui long khong nhan lai", HttpStatus.CONFLICT),
    FLASH_SALE_ITEM_NOT_FOUND(5006, "Khong tim thay san pham flash sale", HttpStatus.NOT_FOUND),
    FLASH_SALE_RESERVATION_NOT_FOUND(5007, "Khong tim thay reservation", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}

