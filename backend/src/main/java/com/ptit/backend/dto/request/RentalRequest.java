package com.ptit.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalRequest {

    @JsonProperty("user_id")
    @NotNull(message = "Truong user_id la bat buoc")
    private Long userId;

    @JsonProperty("book_item_id")
    @NotNull(message = "Truong book_item_id la bat buoc")
    private Long bookItemId;

    @JsonProperty("rent_date")
    private LocalDateTime rentDate;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("return_date")
    private LocalDateTime returnDate;

    @JsonProperty("actual_deposit")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tien coc phai lon hon hoac bang 0")
    private BigDecimal actualDeposit;

    @JsonProperty("rental_fee")
    @DecimalMin(value = "0.0", inclusive = true, message = "Phi thue phai lon hon hoac bang 0")
    private BigDecimal rentalFee;

    @JsonProperty("penalty_fee")
    @DecimalMin(value = "0.0", inclusive = true, message = "Phi phat phai lon hon hoac bang 0")
    private BigDecimal penaltyFee;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_status")
    private String paymentStatus;

    @JsonProperty("status")
    private String status;
}

