package com.believe.auth.config;

import cn.dev33.satoken.stp.StpInterface;
import com.believe.auth.mapper.PermissionMapper;
import com.believe.auth.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return permissionMapper.selectPermCodesByUserId(Long.valueOf(loginId.toString()));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return roleMapper.selectRoleCodesByUserId(Long.valueOf(loginId.toString()));
    }
}
