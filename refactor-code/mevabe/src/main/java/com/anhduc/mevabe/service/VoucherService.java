package com.anhduc.mevabe.service;

import com.anhduc.mevabe.dto.request.CreateVoucherRequest;
import com.anhduc.mevabe.dto.response.VoucherResponse;
import com.anhduc.mevabe.entity.Voucher;
import com.anhduc.mevabe.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherService {

    VoucherRepository voucherRepository;
    ModelMapper modelMapper;

    public VoucherResponse create(CreateVoucherRequest request) {
        Voucher voucher = modelMapper.map(request, Voucher.class);
        voucher.setCode(generateUniqueCode());
        voucherRepository.save(voucher);
        return modelMapper.map(voucher, VoucherResponse.class);
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
