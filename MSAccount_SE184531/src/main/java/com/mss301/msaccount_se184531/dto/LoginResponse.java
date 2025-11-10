package com.mss301.msaccount_se184531.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Integer accountId;
    private String email;
    private Integer role;
    private Boolean isActive;
}
