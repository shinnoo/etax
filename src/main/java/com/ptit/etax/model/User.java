package com.ptit.etax.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 1 người dùng tương ứng với 1 MST
 * Mục đích : dùng để đăng nhập, định danh
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Column(name = "id")
    @Id
    private String id;
}
