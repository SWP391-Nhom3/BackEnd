package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.VoucherRequest;
import com.anhduc.mevabe.dto.response.CategoryResponse;
import com.anhduc.mevabe.dto.response.VoucherResponse;
import com.anhduc.mevabe.entity.Category;
import com.anhduc.mevabe.entity.Voucher;
import com.anhduc.mevabe.enums.VoucherType;
import com.anhduc.mevabe.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    ModelMapper modelMapper;


    public VoucherResponse create(VoucherRequest request) {
        Voucher voucher = new Voucher();
        voucher.setCode(generateUniqueCode());
        voucher.setVoucherType(request.getType());
        voucher.setValue(request.getValue());
        voucher.setMaxUses(request.getMaxUses());
        voucher.setExpiryDate(request.getExpiryDate());
        voucherRepository.save(voucher);
        return modelMapper.map(voucher, VoucherResponse.class);
    }

    private String generateUniqueCode() {
        // Sinh mã voucher duy nhất (có thể sử dụng UUID hoặc bất kỳ phương pháp nào khác)
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    public List<VoucherResponse> findAll() {
        return voucherRepository.findAll().stream()
                .map(this::convert)
                .toList();
    }

    public void delete(UUID id) {
        voucherRepository.deleteById(id);
    }

    private VoucherResponse convert(Voucher voucher) {
        return modelMapper.map(voucher, VoucherResponse.class);
    }

}
