package com.github.bluecatlee.common.pagination;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Query {
    /**
     * 数据偏移 (数据库用)
     */
    private int offset;

    /**
     * 每页记录
     */
    private int limit = 20;

    /**
     * 当前页
     */
    private int page = 1;

    /**
     * @return the offset
     */
    public int getOffset() {
        offset = (page - 1) * limit;
        return offset;
    }

    /**
     * @param offset
     *            the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit
     *            the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page
     *            the page to set
     */
    public void setPage(int page) {
        if (page <= 0) {
            page = 1;
        }
        this.page = page;
    }

    public Map<String, Object> transformToMap() {
        return copyMap(this);
    }
	
	private static final Map<String, Object> copyMap(Object object) {

        Map<String, Object> map = new HashMap<>();

        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(object.getClass());
        } catch (IntrospectionException e) {
        }

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            if (propertyName.toLowerCase().equals("class")) {
                continue;
            }
            Method readMethod = propertyDescriptor.getReadMethod();
            try {
                map.put(propertyName, readMethod.invoke(object, new Object[0]));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

}
