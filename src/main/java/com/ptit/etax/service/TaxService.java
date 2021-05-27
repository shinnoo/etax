package com.ptit.etax.service;

import com.ptit.etax.common.error.ConflictException;
import com.ptit.etax.common.error.NotFoundException;
import com.ptit.etax.controller.request.TaxRequest;
import com.ptit.etax.model.Tax;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxService {

    @Autowired
    TaxRepository taxRepository;

    @Autowired
    GenericMapper genericMapper;

    public Tax create(TaxRequest.TaxCreate request) {
        // kiểm tra xem thông tin về mst đã được khai báo hay chưa
        Tax tax = taxRepository.findById(request.getId()).orElse(null);
        //nếu có thì show lỗi
        if (tax != null){
            throw new ConflictException("Thông tin mã số thuế đã được đăng ký");
        }


        // nếu không thì cho tạo
        tax = genericMapper.mapToType(request,Tax.class);
        return taxRepository.save(tax);
    }

    public Tax getById(String userId) {
        Tax tax = taxRepository.findById(userId).orElse(null);
        if (tax == null){
            throw new NotFoundException("Không tìm thấy thông tin mã số thuế này");
        }
        return tax;
    }
}
