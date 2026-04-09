package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.OrderRequest;
import com.ptit.backend.dto.response.OrderResponse;
import com.ptit.backend.entity.Order;
import com.ptit.backend.entity.OrderDetail;
import com.ptit.backend.entity.Promotion;
import com.ptit.backend.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "promotion", source = "promotionId")
    @Mapping(target = "createdAt", source = "orderDate")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "pointsUsed", ignore = true)
    @Mapping(target = "promotionDiscountAmount", ignore = true)
    @Mapping(target = "pointDiscountAmount", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    Order toEntity(OrderRequest request);

    @Mapping(source = "orderId", target = "id")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "promotion.promotionId", target = "promotionId")
    @Mapping(source = "createdAt", target = "orderDate")
    @Mapping(target = "items", ignore = true)
    OrderResponse toResponse(Order entity);

    @Mapping(source = "entity.orderId", target = "id")
    @Mapping(source = "entity.user.userId", target = "userId")
    @Mapping(source = "entity.promotion.promotionId", target = "promotionId")
    @Mapping(source = "entity.createdAt", target = "orderDate")
    @Mapping(source = "items", target = "items")
    OrderResponse toResponse(Order entity, List<OrderDetail> items);

    List<OrderResponse> toResponseList(List<Order> entities);

    default User mapUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder().userId(userId).build();
    }

    default Promotion mapPromotion(Long promotionId) {
        if (promotionId == null) {
            return null;
        }
        return Promotion.builder().promotionId(promotionId).build();
    }
}

