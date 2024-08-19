package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateCategoryRequest;
import com.anhduc.mevabe.dto.response.CategoryResponse;
import com.anhduc.mevabe.entity.Category;
import com.anhduc.mevabe.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;
    ModelMapper modelMapper;

    public void create(CreateCategoryRequest request) {
        categoryRepository.save(modelMapper.map(request, Category.class));
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::convert)
                .toList();
    }

    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponse convert(Category category) {
        return modelMapper.map(category, CategoryResponse.class);
    }
}
