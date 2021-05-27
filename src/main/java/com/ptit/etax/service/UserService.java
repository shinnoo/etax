package com.ptit.etax.service;

import com.ptit.etax.common.error.ConflictException;
import com.ptit.etax.common.error.NotFoundException;
import com.ptit.etax.controller.request.UserRequest;
import com.ptit.etax.model.mapper.GenericMapper;
import com.ptit.etax.repository.UserRepository;
import com.ptit.etax.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    GenericMapper genericMapper;

    public User create(UserRequest.UserCreate request) {
        User user = userRepository.findById(request.getId()).orElse(null);
        if (user != null){
            throw new ConflictException("Mã số thuế đã tồn tại");
        }
        return userRepository.save(genericMapper.mapToType(request,User.class));
    }

    public User login(UserRequest.UserLogin request) {
        User user = userRepository.findById(request.getId()).orElse(null);
        if (user == null){
            throw new NotFoundException("Mã số thuế chưa tồn tại");
        }
        return user;
    }
}
