package com.lyne.module.material.config;

import com.lyne.common.core.base.BaseEntity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author lyne
 */
public class StatusHandler extends BaseTypeHandler<BaseEntity.Status> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    BaseEntity.Status parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public BaseEntity.Status getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        BaseEntity.Status status = null;
        if (!rs.wasNull()) {
            status = BaseEntity.Status.getEnumByValue(value);
        }
        return status;
    }

    @Override
    public BaseEntity.Status getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        BaseEntity.Status status = null;
        if (!rs.wasNull()) {
            status = BaseEntity.Status.getEnumByValue(value);
        }
        return status;
    }

    @Override
    public BaseEntity.Status getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        BaseEntity.Status status = null;
        if (!cs.wasNull()) {
            status = BaseEntity.Status.getEnumByValue(value);
        }
        return status;
    }
}
