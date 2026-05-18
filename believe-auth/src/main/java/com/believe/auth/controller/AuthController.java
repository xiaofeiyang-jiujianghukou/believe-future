package com.believe.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.believe.auth.dto.LoginRequest;
import com.believe.auth.dto.RegisterRequest;
import com.believe.auth.service.AuthService;
import com.believe.common.core.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> token = authService.login(request);
        return Result.ok(token);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.ok();
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    @GetMapping("/user-info")
    public Result<Map<String, Object>> userInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        Map<String, Object> info = authService.userInfo(userId);
        return Result.ok(info);
    }
}
