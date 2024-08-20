package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateBrandRequest;
import com.anhduc.mevabe.dto.response.BrandResponse;
import com.anhduc.mevabe.entity.Brand;
import com.anhduc.mevabe.entity.Review;
import com.anhduc.mevabe.repository.BrandRepository;
import com.anhduc.mevabe.repository.ReviewRepository;
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
public class ReviewService {

    ReviewRepository reviewRepository;

    public void create(Review review) {
        reviewRepository.save(review);
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review findById(UUID id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public void delete(UUID id) {
        reviewRepository.deleteById(id);
    }

}
