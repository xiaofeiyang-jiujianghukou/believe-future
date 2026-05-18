package com.believe.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.believe.common.core.result.PageResult;
import com.believe.user.dto.UserUpdateRequest;
import com.believe.user.entity.UserInfo;

public interface UserService {

    UserInfo getById(Long id);

    UserInfo getByUsername(String username);

    void update(Long id, UserUpdateRequest request);

    void delete(Long id);

    PageResult<UserInfo> list(int pageNum, int pageSize, String keyword);
}
