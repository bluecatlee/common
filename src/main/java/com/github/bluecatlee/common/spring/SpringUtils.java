package com.github.bluecatlee.common.spring;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Set;

@Data
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtils.applicationContext == null){
            SpringUtils.applicationContext  = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }


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
