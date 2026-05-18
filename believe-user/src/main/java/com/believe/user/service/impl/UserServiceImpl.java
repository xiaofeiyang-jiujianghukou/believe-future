package com.believe.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.believe.common.core.exception.BizException;
import com.believe.common.core.result.PageResult;
import com.believe.user.dto.UserUpdateRequest;
import com.believe.user.entity.UserInfo;
import com.believe.user.mapper.UserInfoMapper;
import com.believe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserInfoMapper userInfoMapper;

    @Override
    public UserInfo getById(Long id) {
        UserInfo user = userInfoMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    @Override
    public UserInfo getByUsername(String username) {
        UserInfo user = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUsername, username));
        if (user == null) {
            throw new BizException("用户不存在");
        }
        return user;
    }

    @Override
    public void update(Long id, UserUpdateRequest request) {
        UserInfo user = userInfoMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        userInfoMapper.updateById(user);
        log.info("User updated: id={}", id);
    }

    @Override
    public void delete(Long id) {
        UserInfo user = userInfoMapper.selectById(id);
        if (user == null) {
            throw new BizException("用户不存在");
        }
        userInfoMapper.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    @Override
    public PageResult<UserInfo> list(int pageNum, int pageSize, String keyword) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(UserInfo::getUsername, keyword)
                    .or()
                    .like(UserInfo::getNickname, keyword);
        }
        wrapper.orderByDesc(UserInfo::getCreateTime);
        Page<UserInfo> page = userInfoMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }
}
