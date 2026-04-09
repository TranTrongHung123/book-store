package com.ptit.backend.mapper;

import com.ptit.backend.dto.request.CategoryRequest;
import com.ptit.backend.dto.response.CategoryResponse;
import com.ptit.backend.entity.Category;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "parent", source = "parentId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(source = "categoryId", target = "id")
    CategoryResponse toResponse(Category entity);

    List<CategoryResponse> toResponseList(List<Category> entities);

    default Category mapParent(Long parentId) {
        if (parentId == null) {
            return null;
        }
        return Category.builder().categoryId(parentId).build();
    }
}

