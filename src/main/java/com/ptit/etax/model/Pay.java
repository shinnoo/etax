package com.ptit.etax.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Thông tin việc đóng thuế của user
 *
 */

@Entity
@Data
@NoArgsConstructor
@Table(name = "pay")
public class Pay {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id; // tự động gen

    @Column(name = "tax_id", nullable = false)
    private String taxId; // cần có taxId để biết là nộp cho thông tin thuế nào

    @Column(name = "calculate_date",nullable = false)
    private Instant calculateDate;  // Ngày kết thúc tính thuế  (Cuối ngày)

    @Column(name = "status", nullable = false)
    private String status;  // Trạng thái: Đã khai báo (CREATED), Chưa nộp đủ (INCOMPLETE), Đã nộp đủ(COMPLETE)

    @Column(name = "price", nullable = false)
    private Long price; // Số tiền cần đóng

    @Column(name = "income", nullable = false)
    private Long income;    //Tổng thu nhập

    @Column(name = "number_of_dependents")
    private Integer numberOfDependents;

    @Column(name = "payment", nullable = false)
    private Long payment; // Số tiền đã trả
}
