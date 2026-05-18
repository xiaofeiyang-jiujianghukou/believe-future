package com.believe.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.believe.auth.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT p.code FROM sys_permission p INNER JOIN sys_role_permission rp ON p.id = rp.permission_id INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id WHERE ur.user_id = #{userId} AND p.deleted = 0")
    List<String> selectPermCodesByUserId(Long userId);
}
