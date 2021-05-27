package com.ptit.etax.controller.request;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;

public class PayRequest {
    @Data
    public static class PayCreate {
        @NotBlank(message = "Khoản thuế không được để trống")
        private String taxId;

        @NotNull(message = "Tháng không được để trống")
        private Instant calculateDate;

        @NotNull(message = "Thu nhập không được để trống")
        private Long income;

        private Integer numberOfDependents;
    }

    @Data
    public static class PayUpdate {
        @NotNull(message = "Số tiền đóng không được để trống")
        private Long paymentAmount;
    }


    @Data
    public static class PayCalculate {
        @NotNull(message = "Thu nhập không được để trống")
        private Long income;

        private Integer numberOfDependents;
    }
}
