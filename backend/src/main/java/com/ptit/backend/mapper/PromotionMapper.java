package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.PromotionRequest;
import com.ptit.backend.dto.response.PromotionResponse;
import com.ptit.backend.entity.Promotion;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "startDate", source = "validFrom")
    @Mapping(target = "endDate", source = "validTo")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Promotion toEntity(PromotionRequest request);

    @Mapping(source = "promotionId", target = "id")
    @Mapping(source = "startDate", target = "validFrom")
    @Mapping(source = "endDate", target = "validTo")
    PromotionResponse toResponse(Promotion entity);

    List<PromotionResponse> toResponseList(List<Promotion> entities);
}

