package com.ptit.etax.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Thông tin thuế của User
 *
 */

@Entity
@Data
@NoArgsConstructor
@Table(name = "tax")
public class Tax {
    @Id
    @Column(name = "id")
    private String id;  //MST

    @Column(name = "name",nullable = false)
    private String name;    // Nguời nộp thế

    @Column(name = "address",nullable = false)
    private String address; // Địa chỉ trụ sở

    @Column(name = "city",nullable = false)
    private String city;    // Tỉnh/Thành phố

    @Column(name = "district",nullable = false)
    private String district;    // Quận/huyện

    @Column(name = "phone",nullable = false)
    private String phone;   //SĐT

    @Column(name = "fax")
    private String fax; // số fax

    @Column(name = "email")
    private String email;   // email

    @Column(name = "business")
    private String business;    // Ngành nghề kinh doanh

    @Column(name = "account_number")
    private String accountNumber;   // Số tk ngân hàng
}
