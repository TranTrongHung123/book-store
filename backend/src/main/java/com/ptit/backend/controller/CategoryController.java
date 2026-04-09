package com.ptit.backend.controller;

import com.ptit.backend.dto.request.CategoryRequest;
import com.ptit.backend.dto.response.ApiResponse;
import com.ptit.backend.dto.response.CategoryResponse;
import com.ptit.backend.dto.response.PagedResponse;
import com.ptit.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private static final int SUCCESS_CODE = 1000;
    private static final String SUCCESS_MESSAGE = "Thanh cong";

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<CategoryResponse>>> getCategories(
            @RequestParam(name = "_page", defaultValue = "0") int page,
            @RequestParam(name = "_limit", defaultValue = "10") int limit,
            @RequestParam(name = "_sort", defaultValue = "categoryId") String sort,
            @RequestParam(name = "_order", defaultValue = "asc") String order,
            @RequestParam(name = "q", required = false) String q
    ) {
        Pageable pageable = buildPageable(page, limit, sort, order);
        Page<CategoryResponse> result = categoryService.getCategories(q, pageable);

        ApiResponse<PagedResponse<CategoryResponse>> response = ApiResponse.<PagedResponse<CategoryResponse>>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(toPagedResponse(result))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse result = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse result = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse result = categoryService.updateCategory(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(success(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body(success(null));
    }

    private Pageable buildPageable(int page, int limit, String sort, String order) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        int safePage = Math.max(page, 0);
        int safeLimit = Math.max(limit, 1);
        return PageRequest.of(safePage, safeLimit, Sort.by(direction, sort));
    }

    private <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder()
                .code(SUCCESS_CODE)
                .message(SUCCESS_MESSAGE)
                .result(result)
                .build();
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .limit(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}

