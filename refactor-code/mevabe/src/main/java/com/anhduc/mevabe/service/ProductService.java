package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateProductRequest;
import com.anhduc.mevabe.dto.response.ProductResponse;
import com.anhduc.mevabe.entity.Product;
import com.anhduc.mevabe.entity.User;
import com.anhduc.mevabe.exception.AppException;
import com.anhduc.mevabe.exception.ErrorCode;
import com.anhduc.mevabe.repository.ProductRepository;
import com.anhduc.mevabe.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductService {

    ProductRepository productRepository;
    ModelMapper modelMapper;
    S3StorageService s3StorageService;
    UserRepository userRepository;

    public void create(CreateProductRequest request) throws IOException {
        List<String> images = new ArrayList<>();
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (MultipartFile file : request.getFiles()) {
                String filename = file.getOriginalFilename();
                // lay dinh dang file
                String extension = filename.substring(filename.lastIndexOf("."));
                // tao ten moi
                String newFilename = UUID.randomUUID().toString() + extension;

                // Upload file to S3 and get the URL
                String fileUrl = s3StorageService.uploadFile(newFilename, file);
                images.add(fileUrl);

                // Set the first fileUrl as the cover
                if (request.getCoverImageUrl() == null) {
                    request.setCoverImageUrl(fileUrl);
                }
            }
            request.setImageUrl(images); // save to db
        }
        request.setUser(user);
        productRepository.save(modelMapper.map(request, Product.class));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(this::convert)
                .toList();
    }

    public ProductResponse findById(UUID id) {
        return convert(productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)
        ));
    }

    public ProductResponse update(UUID id, CreateProductRequest request) throws IOException {
        Product product =productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));
        List<String> images = new ArrayList<>();

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (MultipartFile file : request.getFiles()) {
                String filename = file.getOriginalFilename();
                // lay dinh dang file
                String extension = filename.substring(filename.lastIndexOf("."));
                // tao ten moi
                String newFilename = UUID.randomUUID().toString() + extension;

                // Upload file to S3 and get the URL
                String fileUrl = s3StorageService.uploadFile(newFilename, file);
                images.add(fileUrl);

                // Set the first fileUrl as the cover
                if (request.getCoverImageUrl() == null) {
                    request.setCoverImageUrl(fileUrl);
                }
            }
            request.setImageUrl(images); // save to db
        }
        request.setUser(user);
        modelMapper.map(request, product);
        return convert(productRepository.save(product));

    }

    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    private ProductResponse convert(Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }
}
