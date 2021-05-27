package com.ptit.etax.controller.api;

import com.ptit.etax.common.enums.ResponseCodeEnum;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.controller.request.UserRequest;
import com.ptit.etax.controller.response.ResponseBodyDTO;
import com.ptit.etax.model.User;
import com.ptit.etax.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    GenericMapper genericMapper;

    @PostMapping(value = "/users", produces = "application/json")
    public ResponseEntity<ResponseBodyDTO<User>> create(@RequestBody @Valid UserRequest.UserCreate request) {
        ResponseBodyDTO<User> response = new ResponseBodyDTO<>();
        User user = userService.create(request);
        response.setItem(user).setCode(ResponseCodeEnum.R_201).setMessage("Đã đăng ký thành công mã số thuế " + user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<ResponseBodyDTO<User>> login(@RequestBody @Valid UserRequest.UserLogin request) {
        ResponseBodyDTO<User> response = new ResponseBodyDTO<>();
        User user = userService.login(request);
        response.setItem(user).setCode(ResponseCodeEnum.R_200).setMessage("OK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
