package com.believe.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.believe.auth.dto.LoginRequest;
import com.believe.auth.dto.RegisterRequest;
import com.believe.auth.entity.Role;
import com.believe.auth.entity.User;
import com.believe.auth.mapper.RoleMapper;
import com.believe.auth.mapper.UserMapper;
import com.believe.auth.service.AuthService;
import com.believe.common.core.exception.BizException;
import com.believe.common.core.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public Map<String, Object> login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (user == null) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException("账号已被禁用");
        }
        String[] parts = user.getPassword().split(":");
        if (parts.length != 2 || !hash(request.getPassword(), parts[0]).equals(parts[1])) {
            throw new BizException("用户名或密码错误");
        }

        StpUtil.login(user.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        Map<String, Object> result = new HashMap<>();
        result.put("token", tokenInfo.getTokenValue());
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("nickname", user.getNickname());
        return result;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BizException("用户名已存在");
        }
        String salt = generateSalt();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(salt + ":" + hash(request.getPassword(), salt));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        userMapper.insert(user);

        log.info("User registered: {}", user.getUsername());
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public Map<String, Object> userInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        List<String> roles = roleMapper.selectRoleCodesByUserId(userId);
        List<String> perms = StpUtil.getPermissionList();

        Map<String, Object> info = new HashMap<>();
        info.put("userId", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("email", user.getEmail());
        info.put("phone", user.getPhone());
        info.put("avatar", user.getAvatar());
        info.put("roles", roles);
        info.put("permissions", perms);
        return info;
    }

    public static String hash(String plain, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((salt + plain).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (Exception e) {
            throw new RuntimeException("Hash failed", e);
        }
    }

    private static String generateSalt() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
