package com.mss301.msaccount_se184531.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SystemAccounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Integer accountId;

    @Column(name = "Username", nullable = false, length = 100)
    private String username;

    @Column(name = "Email", nullable = false, length = 255)
    private String email;

    @Column(name = "Password", nullable = false, length = 255)
    private String password;

    @Column(name = "Role")
    private Integer role;

    @Column(name = "IsActive")
    private Boolean isActive;
}
