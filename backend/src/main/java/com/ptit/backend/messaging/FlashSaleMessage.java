package com.ptit.backend.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleMessage implements Serializable {

    private String reservationId;
    private Long flashSaleItemId;
    private Long userId;
    private Long orderId;
    private Integer quantity;

    /** Loại message: CANCEL, ORDER_CONFIRMED */
    private String type;
}
