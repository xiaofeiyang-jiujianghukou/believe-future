package com.believe.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.believe.common.core.result.PageResult;
import com.believe.common.core.result.Result;
import com.believe.user.dto.UserUpdateRequest;
import com.believe.user.entity.UserInfo;
import com.believe.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Result<UserInfo> currentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        return Result.ok(userService.getById(userId));
    }

    @PutMapping("/info")
    public Result<Void> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        long userId = StpUtil.getLoginIdAsLong();
        userService.update(userId, request);
        return Result.ok();
    }

    @GetMapping("/{id}")
    @SaCheckPermission("user:manage")
    public Result<UserInfo> getUserById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @GetMapping("/list")
    @SaCheckPermission("user:manage")
    public Result<PageResult<UserInfo>> listUsers(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.ok(userService.list(pageNum, pageSize, keyword));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("user:manage")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }
}
