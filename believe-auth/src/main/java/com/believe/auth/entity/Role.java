package com.believe.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.believe.common.data.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

    private String name;
    private String code;
    private String description;
    private Integer sort;
    private Integer status;
}
