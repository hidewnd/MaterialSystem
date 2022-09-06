package com.lyne.module.system.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;

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
        this.strictUpdateFill(metaObject, "updateDate", Date::new, Date.class);
    }


    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<?> fieldVal) {
        // 重写解决字段无法更新问题
        //if (metaObject.getValue(fieldName) == null) 不判断空值情况即可
        {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }
        return this;
    }
}
