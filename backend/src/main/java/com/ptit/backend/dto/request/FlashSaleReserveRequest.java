package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashSaleReserveRequest {

    @JsonProperty("flash_sale_item_id")
    @NotNull(message = "flash_sale_item_id la bat buoc")
    private Long flashSaleItemId;

    @JsonProperty("quantity")
    @NotNull(message = "quantity la bat buoc")
    @Min(value = 1, message = "quantity phai lon hon 0")
    private Integer quantity;

    @JsonProperty("shipping_address")
    @NotNull(message = "shipping_address la bat buoc")
    private String shippingAddress;
}
