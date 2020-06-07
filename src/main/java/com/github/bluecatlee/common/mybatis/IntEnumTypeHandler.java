package com.github.bluecatlee.common.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybatis int枚举类型转换
 */
//@MappedTypes({IntEnum.class})        // todo 指定映射的java类型和jdbc类型后 能否避免在xml文件中书写大量的typeHandler属性
//@MappedJdbcTypes(JdbcType.INTEGER)
public class IntEnumTypeHandler<E extends Enum<?> & IntEnum> extends BaseTypeHandler<IntEnum> {

    private Class<E> clazz;

    public IntEnumTypeHandler(Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        clazz = enumType;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, IntEnum parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getIntValue());
    }
    @Override
    public IntEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convert(clazz, rs.getInt(columnName));
    }

    @Override
    public IntEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convert(clazz, rs.getInt(columnIndex));
    }

    @Override
    public IntEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convert(clazz, cs.getInt(columnIndex));
    }

    private <T extends Enum<?> & IntEnum> T convert(Class<T> enumClass, int value) {
        T[] enumConstants = enumClass.getEnumConstants();
        for (T t : enumConstants) {
            if (t.getIntValue() == value) {
                return t;
            }
        }
        return null;
    }

}
