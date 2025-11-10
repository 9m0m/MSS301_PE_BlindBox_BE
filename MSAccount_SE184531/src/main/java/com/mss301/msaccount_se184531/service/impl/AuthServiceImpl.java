package com.mss301.msaccount_se184531.service.impl;

import com.mss301.msaccount_se184531.dto.LoginRequest;
import com.mss301.msaccount_se184531.dto.LoginResponse;
import com.mss301.msaccount_se184531.entity.SystemAccount;
import com.mss301.msaccount_se184531.repository.SystemAccountRepository;
import com.mss301.msaccount_se184531.service.AuthService;
import com.mss301.msaccount_se184531.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SystemAccountRepository accountRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) throws Exception {
        SystemAccount account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new Exception("Invalid email or password"));

        if (!account.getPassword().equals(request.getPassword())) {
            throw new Exception("Invalid email or password");
        }

        if (!account.getIsActive()) {
            throw new Exception("Account is not active");
        }

        String token = jwtUtil.generateToken(
                account.getAccountId(),
                account.getEmail(),
                account.getRole(),
                account.getIsActive());

        return new LoginResponse(
                token,
                account.getAccountId(),
                account.getEmail(),
                account.getRole(),
                account.getIsActive());
    }
}
