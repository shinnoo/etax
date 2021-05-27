package com.ptit.etax.controller.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

public class TaxRequest {
    @Data
    public static class TaxCreate {
        @NotBlank(message = "Mã số thuế không được để trống")
        private String id;    // Nguời nộp thế

        @NotBlank(message = "Nguời nộp thế không được để trống")
        private String name;    // Nguời nộp thế

        @NotBlank(message = "Địa chỉ trụ sở không được để trống")
        private String address; // Địa chỉ trụ sở

        @NotBlank(message = "Tỉnh/Thành phố không được để trống")
        private String city;    // Tỉnh/Thành phố

        @NotBlank(message = "Quận/huyện không được để trống")
        private String district;    // Quận/huyện

        private String phone;   //SĐT
        private String fax; // số fax
        private String email;   // email
        private String business;    // Ngành nghề kinh doanh
        private String accountNumber;   // Số tk ngân hàng
    }
}
