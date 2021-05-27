package com.ptit.etax.controller.api;

import com.ptit.etax.common.enums.ResponseCodeEnum;
import com.ptit.etax.controller.response.ResponseBodyDTO;
import com.ptit.etax.model.Tax;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.controller.request.TaxRequest;
import com.ptit.etax.service.TaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class TaxController {
    @Autowired
    TaxService taxService;
    @Autowired
    GenericMapper genericMapper;

    @PostMapping(value = "/taxs", produces = "application/json")
    public ResponseEntity<ResponseBodyDTO<Tax>> create(@RequestBody @Valid TaxRequest.TaxCreate request) {

        ResponseBodyDTO<Tax> response = new ResponseBodyDTO<>();
        Tax tax = taxService.create(request);
        response.setItem(tax).setCode(ResponseCodeEnum.R_201).setMessage("Đã đăng kí thông tin mã số thuế " + tax.getId() + " thành công");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(value = "/taxs/{id}")
    public ResponseEntity<ResponseBodyDTO<Tax>> getById(@PathVariable String id){
        ResponseBodyDTO<Tax> response = new ResponseBodyDTO<>();
        Tax tax = taxService.getById(id);
        response.setItem(tax).setCode(ResponseCodeEnum.R_200).setMessage("OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
