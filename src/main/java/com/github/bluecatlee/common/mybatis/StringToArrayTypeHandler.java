package com.github.bluecatlee.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class StringToArrayTypeHandler implements TypeHandler<List<String>> {

  @Override
  public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
    if (parameter != null && !parameter.isEmpty()) {
      StringBuilder result = new StringBuilder();
      for (String value : parameter)
        result.append(value).append(",");
      result.deleteCharAt(result.length() - 1);
      ps.setString(i, result.toString());
    } else {
      ps.setNull(i, Types.VARCHAR);
    }
  }

  @Override
  public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
    String columnValue = rs.getString(columnName);
    return getStringArray(columnValue);
  }

  @Override
  public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
    String columnValue = rs.getString(columnIndex);
    return getStringArray(columnValue);
  }

  @Override
  public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
    String columnValue = cs.getString(columnIndex);
    return getStringArray(columnValue);
  }

  private List<String> getStringArray(String columnValue) {
    if (columnValue == null)
      return null;
    return Arrays.asList(columnValue.split(","));
  }

}
