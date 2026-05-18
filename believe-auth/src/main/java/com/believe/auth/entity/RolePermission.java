package com.believe.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.believe.common.data.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermission extends BaseEntity {

    private Long roleId;
    private Long permissionId;
}
