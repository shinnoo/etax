package com.ptit.etax.controller.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

public class UserRequest {
    @Data
    public static class UserCreate {
        @NotBlank(message = "Mã số thuế không được để trống")
        private String id;
    }

    @Data
    public static class UserLogin {
        @NotBlank(message = "Mã số thuế không được để trống")
        private String id;
    }
}
