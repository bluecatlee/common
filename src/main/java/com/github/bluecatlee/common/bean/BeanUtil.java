package com.github.bluecatlee.common.bean;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtil {

    public static JSONObject convertBean(Object bean)  {
        Class type=bean.getClass();
        JSONObject returnMap = new JSONObject();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(type);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor:propertyDescriptors) {
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")){
                Method readMethod = descriptor.getReadMethod();
                Object result = null;
                try {
                    result = readMethod.invoke(bean);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (result!=null){
                    if(result instanceof  String && StringUtils.isBlank((String)result)){
                        continue;
                    }
                    returnMap.put(propertyName,result);
                }
            }
        }
        return returnMap;
    }

}
