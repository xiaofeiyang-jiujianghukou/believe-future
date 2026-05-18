package com.believe.common.data.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.believe.common.core.context.AuthContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * <p>自动填充 createTime、updateTime、createBy、updateBy</p>
 */
@Slf4j
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 创建人
        String currentUser = getCurrentUser();
        this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
        // 更新人
        this.strictUpdateFill(metaObject, "updateBy", String.class, currentUser);
        // 逻辑删除标志默认为0
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 更新人
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUser());
    }

    /**
     * 获取当前登录用户ID
     */
    private String getCurrentUser() {
        try {
            AuthContext authContext = AuthContext.get();
            if (authContext != null && authContext.getUserId() != null) {
                return String.valueOf(authContext.getUserId());
            }
        } catch (Exception e) {
            log.debug("获取当前用户失败，使用默认值");
        }
        return "system";
    }
}