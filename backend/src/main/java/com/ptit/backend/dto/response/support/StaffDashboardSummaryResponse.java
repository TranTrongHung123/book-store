package com.ptit.backend.dto.response.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDashboardSummaryResponse {

    @JsonProperty("total_books")
    private long totalBooks;

    @JsonProperty("total_orders")
    private long totalOrders;

    @JsonProperty("pending_orders")
    private long pendingOrders;

    @JsonProperty("shipping_orders")
    private long shippingOrders;

    @JsonProperty("total_customers")
    private long totalCustomers;
}
