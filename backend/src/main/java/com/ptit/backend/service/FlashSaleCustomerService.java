package com.ptit.backend.service;

import com.ptit.backend.dto.request.FlashSaleReserveRequest;
import com.ptit.backend.dto.response.FlashSaleActiveResponse;
import com.ptit.backend.dto.response.FlashSaleReservationStatusResponse;
import com.ptit.backend.dto.response.FlashSaleReserveResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface FlashSaleCustomerService {

    List<FlashSaleActiveResponse> getActiveCampaigns(Long userId);

    FlashSaleReserveResponse reserveStock(Long userId, FlashSaleReserveRequest request, HttpServletRequest httpRequest);

    FlashSaleReservationStatusResponse getReservationStatus(String reservationId, Long userId);

    void commitReservation(String reservationId);

    void cancelReservation(String reservationId);
}
