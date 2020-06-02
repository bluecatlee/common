package com.github.bluecatlee.common.spring;

import lombok.Data;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import java.util.Set;

@Data
public class SpringUtils {

    /**
     * 获取bean的所有注解名
     * @param clazz
     */
    public static Set<String> getBeanAnnotations(Class clazz) {
        AnnotatedBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(clazz);
        return beanDefinition.getMetadata().getAnnotationTypes();
    }

    public static void main(String[] args) {
        // Set<String> beanAnnotations = getBeanAnnotations(MyBeanUtils.class);
        // System.out.println(beanAnnotations);
        // getBeanAnnotations(SpringUtils.class);

        // AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SpringUtils.class).getBeanDefinition();
        // SpringUtils bean = MyBeanUtils.getBean(SpringUtils.class);
        //
        // Set<String> beanAnnotations = getBeanAnnotations(SpringUtils.class);
        // System.out.println(beanAnnotations);
    }

}
