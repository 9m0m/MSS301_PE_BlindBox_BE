package com.mss301.msaccount_se184531.service;

import com.mss301.msaccount_se184531.dto.LoginRequest;
import com.mss301.msaccount_se184531.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request) throws Exception;
}
