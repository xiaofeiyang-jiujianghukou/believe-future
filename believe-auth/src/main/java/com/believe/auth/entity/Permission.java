package com.believe.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.believe.common.data.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

    private String name;
    private String code;
    private Long parentId;
    private Integer type;
    private String path;
    private String icon;
    private Integer sort;
}
