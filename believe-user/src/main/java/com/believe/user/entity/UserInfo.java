package com.believe.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.believe.common.data.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserInfo extends BaseEntity {

    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
}
