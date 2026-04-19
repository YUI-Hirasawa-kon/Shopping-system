package com.example.shoppingsystem.Controller;

import com.example.shoppingsystem.Entity.Product;
import com.example.shoppingsystem.Repository.ProductRepository;
import com.example.shoppingsystem.dto.ApiResponse;
import com.example.shoppingsystem.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ProductController {

    private static final Integer PRODUCT_ON_SHELF = 1;
    private static final String DEFAULT_CATEGORY_NAME = "Daily Essentials";

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/api/products")
    public ApiResponse<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {

        int pageIndex = Math.max(page - 1, 0);
        int pageSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        String safeKeyword = keyword == null ? "" : keyword.trim();
        Page<Product> productPage;
        if (categoryId != null) {
            if (!safeKeyword.isEmpty()) {
                productPage = productRepository.findByNameContainingAndStatusAndCategoryId(
                        safeKeyword, PRODUCT_ON_SHELF, categoryId, pageable);
            } else {
                productPage = productRepository.findByStatusAndCategoryId(PRODUCT_ON_SHELF, categoryId, pageable);
            }
        } else {
            if (!safeKeyword.isEmpty()) {
                productPage = productRepository.findByNameContainingAndStatus(safeKeyword, PRODUCT_ON_SHELF, pageable);
            } else {
                productPage = productRepository.findByStatus(PRODUCT_ON_SHELF, pageable);
            }
        }

        List<Map<String, Object>> list = productPage.getContent().stream()
                .map(this::toProductListItem)
                .collect(Collectors.toList());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", productPage.getTotalElements());
        data.put("list", list);
        return ApiResponse.success(data);
    }

    @GetMapping("/api/products/{id}")
    public ApiResponse<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty() || !PRODUCT_ON_SHELF.equals(optionalProduct.get().getStatus())) {
            return ApiResponse.error(404, "Product not found or off shelf");
        }
        return ApiResponse.success(toProductDetail(optionalProduct.get()));
    }

    @PostMapping("/api/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> createProduct(@RequestBody ProductDTO request) {
        ApiResponse<?> validationError = validateForCreate(request);
        if (validationError != null) {
            return validationError;
        }

        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategoryId(request.getCategoryId());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(PRODUCT_ON_SHELF);

        Product saved = productRepository.save(product);
        return ApiResponse.success(toProductDetail(saved));
    }

    @PutMapping("/api/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO request) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ApiResponse.error(404, "Product not found");
        }

        Product product = optionalProduct.get();

        if (request.getName() != null) {
            if (request.getName().trim().isEmpty()) {
                return ApiResponse.error(400, "Product name cannot be empty");
            }
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            if (request.getPrice().signum() <= 0) {
                return ApiResponse.error(400, "Price must be greater than 0");
            }
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            if (request.getStock() < 0) {
                return ApiResponse.error(400, "Stock cannot be less than 0");
            }
            product.setStock(request.getStock());
        }
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        Product saved = productRepository.save(product);
        return ApiResponse.success(toProductDetail(saved));
    }

    @DeleteMapping("/api/admin/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> deleteProduct(@PathVariable Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return ApiResponse.error(404, "Product not found");
        }

        Product product = optionalProduct.get();
        product.setStatus(0);
        productRepository.save(product);
        return ApiResponse.success("Deleted successfully", null);
    }

    private ApiResponse<?> validateForCreate(ProductDTO request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ApiResponse.error(400, "Product name cannot be empty");
        }
        if (request.getPrice() == null || request.getPrice().signum() <= 0) {
            return ApiResponse.error(400, "Price must be greater than 0");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            return ApiResponse.error(400, "Stock cannot be less than 0");
        }
        return null;
    }

    private Map<String, Object> toProductListItem(Product product) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", product.getId());
        item.put("name", product.getName());
        item.put("price", product.getPrice());
        item.put("stock", product.getStock());
        item.put("imageUrl", product.getImageUrl());
        item.put("category", DEFAULT_CATEGORY_NAME);
        return item;
    }

    private Map<String, Object> toProductDetail(Product product) {
        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("id", product.getId());
        detail.put("name", product.getName());
        detail.put("description", product.getDescription());
        detail.put("price", product.getPrice());
        detail.put("stock", product.getStock());
        detail.put("imageUrl", product.getImageUrl());
        return detail;
    }
}
