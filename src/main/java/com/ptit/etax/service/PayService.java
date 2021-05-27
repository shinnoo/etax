package com.ptit.etax.service;

import com.ptit.etax.common.constant.StatusConstant;
import com.ptit.etax.common.error.BadRequestException;
import com.ptit.etax.common.error.NotFoundException;
import com.ptit.etax.common.util.CalculateUtil;
import com.ptit.etax.controller.request.PayRequest;
import com.ptit.etax.model.Tax;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.PayRepository;
import com.ptit.etax.repository.TaxRepository;
import com.ptit.etax.model.Pay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class PayService {
    @Autowired
    TaxRepository taxRepository;

    @Autowired
    PayRepository payRepository;

    @Autowired
    GenericMapper genericMapper;

    public Pay create(PayRequest.PayCreate request) {

        // Check xem đã khai báo thông tin thuế chưa
        Tax tax =  taxRepository.findById(request.getTaxId()).orElse(null);
        if (tax == null) {
            throw new NotFoundException("Thông tin thuế chưa được khai báo");
        }

        // Tìm danh sách các khoản thuế xem có bị trùng thời gian đóng thuế hay không
        Pay pay = genericMapper.mapIgnoreNull(request,Pay.class);
        LocalDate calculateDate = pay.getCalculateDate().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate firstDayOfMonth = calculateDate.withDayOfMonth(1);
        LocalDate endDayOfMonth = calculateDate.withDayOfMonth(calculateDate.lengthOfMonth());
        LocalDateTime atFirstDay = firstDayOfMonth.atTime(0,0,0);
        LocalDateTime atEndDay = endDayOfMonth.atTime(23,59,59);
        boolean isOverlap = payRepository.existsByTaxIdAndCalculateDateBetween(tax.getId(),atFirstDay.atZone(ZoneOffset.UTC).toInstant(),atEndDay.atZone(ZoneOffset.UTC).toInstant());
        if (isOverlap){
            throw new BadRequestException("Đã khai báo thuế trong thời gian này");
        }

        // business logic
        pay.setPrice(CalculateUtil.calculate(request.getIncome()));
        pay.setStatus(StatusConstant.CREATED);
        pay.setPayment(0l);
        return payRepository.save(pay);
    }


    public Pay update(String id, PayRequest.PayUpdate request) {
        Pay pay = payRepository.findById(id).orElse(null);
        if (pay == null){
            throw new NotFoundException("Không tìm thấy lần khai báo này");
        }
        if (StatusConstant.COMPLETE.equals(pay.getStatus())){
            throw new BadRequestException("Lần khai báo này đã được đóng đủ tiền");
        }
        pay.setPayment(pay.getPayment() + request.getPaymentAmount());
        if (Float.compare(pay.getPayment(),pay.getPrice()) == 0 ) {
            pay.setStatus(StatusConstant.COMPLETE);
        } else {
            pay.setStatus(StatusConstant.INCOMPLETE);
        }
        return payRepository.save(pay);
    }

    public List<Pay> getAll(String taxId) {
        return payRepository.findAllByTaxId(taxId);
    }

    public Long calculate(Long income) {
        return CalculateUtil.calculate(income);
    }
}
