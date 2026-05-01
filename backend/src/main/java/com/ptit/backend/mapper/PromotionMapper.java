package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.PromotionRequest;
import com.ptit.backend.dto.response.PromotionResponse;
import com.ptit.backend.entity.Promotion;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    // Convert string "1,3" from DB to List ["1", "3"] for response
    @Named("mapStringToList")
    default List<String> mapStringToList(String categories) {
        if (categories == null || categories.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(categories.split(","));
    }

    // Convert List ["1", "3"] from request to string "1,3" for database
    @Named("mapListToString")
    default String mapListToString(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return String.join(",", categories);
    }

    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "usedCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "applicableCategories", target = "applicableCategories", qualifiedByName = "mapListToString")
    Promotion toEntity(PromotionRequest request);

    @Mapping(source = "applicableCategories", target = "applicableCategories", qualifiedByName = "mapStringToList")
    PromotionResponse toResponse(Promotion entity);

    @Mapping(source = "applicableCategories", target = "applicableCategories", qualifiedByName = "mapStringToList")
    List<PromotionResponse> toResponseList(List<Promotion> entities);
}