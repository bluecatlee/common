package com.github.bluecatlee.common.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * java.util.Date 与 int(11)转换类型处理
 */
public class DateTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.getTime() / 1000);
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long time = rs.getLong(columnName);
        if (time == 0L) {
            return null;
        }
        return new Date(time * 1000);
    }

    @Override
    public Date getNullableResult(ResultSet rs, int i) throws SQLException {
        long time = rs.getLong(i);
        if (time == 0L) {
            return null;
        }
        return new Date(time * 1000);
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int i) throws SQLException {
        long time = cs.getLong(i);
        if (time == 0L) {
            return null;
        }
        return new Date(time * 1000);
    }
}
