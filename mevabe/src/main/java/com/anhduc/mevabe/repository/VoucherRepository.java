package com.anhduc.mevabe.repository;

import com.anhduc.mevabe.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VoucherRepository extends JpaRepository<Voucher, UUID> {
    Voucher findVoucherByCode(String code);
}
