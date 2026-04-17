package com.ptit.backend.dto.response;

public class VnpIpnResponseConst {
    public static final IpnResponse SUCCESS = new IpnResponse("00", "Confirm Success");
    public static final IpnResponse ORDER_NOT_FOUND = new IpnResponse("01", "Order not found");
    public static final IpnResponse ORDER_ALREADY_CONFIRMED = new IpnResponse("02", "Order already confirmed");
    public static final IpnResponse INVALID_AMOUNT = new IpnResponse("04", "Invalid Amount");
    public static final IpnResponse SIGNATURE_FAILED = new IpnResponse("97", "Invalid Checksum");
    public static final IpnResponse UNKNOWN_ERROR = new IpnResponse("99", "Unknown Error");
}