package com.lyne.module.material.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatis plus 自动填充
 *
 * @author lyne
 */
@Component
public class MybatisPlusHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (getFieldValByName("createBy", metaObject) == null) {
            this.strictInsertFill(metaObject, "createBy", String.class, "system");
        }
        this.strictInsertFill(metaObject, "createDate", Date::new, Date.class);
        if (getFieldValByName("createBy", metaObject) == null) {
            this.strictInsertFill(metaObject, "updateBy", String.class, "system");
        }
        this.strictUpdateFill(metaObject, "updateDate", Date::new, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (getFieldValByName("createBy", metaObject) == null) {
            this.strictInsertFill(metaObject, "updateBy", String.class, "system");
        }
        this.strictUpdateFill(metaObject, "updateBy", String.class, "system");
    }
}
