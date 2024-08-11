package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateBrandRequest;
import com.anhduc.mevabe.dto.response.BrandResponse;
import com.anhduc.mevabe.entity.Brand;
import com.anhduc.mevabe.repository.BrandRepository;
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
public class BrandService {

    BrandRepository brandRepository;
    ModelMapper modelMapper;

    public void create(CreateBrandRequest request) {
        brandRepository.save(modelMapper.map(request, Brand.class));
    }

    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
                .map(this::convert)
                .toList();
    }

    public void delete(UUID id) {
        brandRepository.deleteById(id);
    }

    private BrandResponse convert(Brand brand) {
        return modelMapper.map(brand, BrandResponse.class);
    }
}
