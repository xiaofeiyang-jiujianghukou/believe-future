package com.believe.auth.service;

import com.believe.auth.dto.LoginRequest;
import com.believe.auth.dto.RegisterRequest;

import java.util.Map;

public interface AuthService {

    Map<String, Object> login(LoginRequest request);

    void register(RegisterRequest request);

    void logout();

    Map<String, Object> userInfo(Long userId);
}
