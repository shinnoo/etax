package com.ptit.etax.controller.api;

import com.ptit.etax.common.enums.ResponseCodeEnum;
import com.ptit.etax.controller.request.PayRequest;
import com.ptit.etax.controller.response.ResponseBodyDTO;
import com.ptit.etax.model.Pay;
import com.ptit.etax.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class PayController {
    @Autowired
    PayService payService;

    @GetMapping(value = "/pays/taxs/{taxId}")
    public ResponseEntity<ResponseBodyDTO<Pay>> getAll(@PathVariable String taxId) {
        List<Pay> payList = payService.getAll(taxId);
        ResponseBodyDTO<Pay> response = new ResponseBodyDTO<>(payList,ResponseCodeEnum.R_200,"OK",payList.size());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/pays", produces = "application/json")
    public ResponseEntity<ResponseBodyDTO<Pay>> create(@RequestBody @Valid PayRequest.PayCreate request) {
        ResponseBodyDTO<Pay> response = new ResponseBodyDTO<>();
        Pay pay = payService.create(request);
        response.setItem(pay).setCode(ResponseCodeEnum.R_201).setMessage("Thêm thành công");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/pays/{id}", produces = "application/json")
    public ResponseEntity<ResponseBodyDTO<Pay>> update(@RequestBody @Valid PayRequest.PayUpdate request, @PathVariable String id) {
        ResponseBodyDTO<Pay> response = new ResponseBodyDTO<>();
        Pay pay = payService.update(id,request);
        response.setItem(pay).setCode(ResponseCodeEnum.R_201).setMessage("Đóng thuế thành công");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/pays/calculate", produces = "application/json")
    public ResponseEntity<Long> calculate(@RequestBody @Valid PayRequest.PayCalculate request) {
        Long calculatedAmount = payService.calculate(request.getIncome());
        return new ResponseEntity<>(calculatedAmount, HttpStatus.OK);
    }
}
