package com.github.bluecatlee.common.spring.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

public abstract class AopUtils {
    public AopUtils() {
    }

    public static boolean isAopProxy(@Nullable Object object) {
        return object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) || object.getClass().getName().contains("$$"));
    }

    public static boolean isJdkDynamicProxy(@Nullable Object object) {
        return object instanceof SpringProxy && Proxy.isProxyClass(object.getClass());
    }

    public static boolean isCglibProxy(@Nullable Object object) {
        return object instanceof SpringProxy && object.getClass().getName().contains("$$");
    }

    public static Class<?> getTargetClass(Object candidate) {
        Assert.notNull(candidate, "Candidate object must not be null");
        Class<?> result = null;
        if (candidate instanceof TargetClassAware) {
            result = ((TargetClassAware)candidate).getTargetClass();
        }

        if (result == null) {
            result = isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass();
        }

        return result;
    }

    public static Method selectInvocableMethod(Method method, @Nullable Class<?> targetType) {
        if (targetType == null) {
            return method;
        } else {
            Method methodToUse = MethodIntrospector.selectInvocableMethod(method, targetType);
            if (Modifier.isPrivate(methodToUse.getModifiers()) && !Modifier.isStatic(methodToUse.getModifiers()) && SpringProxy.class.isAssignableFrom(targetType)) {
                throw new IllegalStateException(String.format("Need to invoke method '%s' found on proxy for target class '%s' but cannot be delegated to target bean. Switch its visibility to package or protected.", method.getName(), method.getDeclaringClass().getSimpleName()));
            } else {
                return methodToUse;
            }
        }
    }

    public static boolean isEqualsMethod(@Nullable Method method) {
        return ReflectionUtils.isEqualsMethod(method);
    }

    public static boolean isHashCodeMethod(@Nullable Method method) {
        return ReflectionUtils.isHashCodeMethod(method);
    }

    public static boolean isToStringMethod(@Nullable Method method) {
        return ReflectionUtils.isToStringMethod(method);
    }

    public static boolean isFinalizeMethod(@Nullable Method method) {
        return method != null && method.getName().equals("finalize") && method.getParameterCount() == 0;
    }

    public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
        Class<?> specificTargetClass = targetClass != null ? ClassUtils.getUserClass(targetClass) : null;
        Method resolvedMethod = ClassUtils.getMostSpecificMethod(method, specificTargetClass);
        return BridgeMethodResolver.findBridgedMethod(resolvedMethod);
    }

    public static boolean canApply(Pointcut pc, Class<?> targetClass) {
        return canApply(pc, targetClass, false);
    }

    public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
        Assert.notNull(pc, "Pointcut must not be null");
        if (!pc.getClassFilter().matches(targetClass)) {
            return false;
        } else {
            MethodMatcher methodMatcher = pc.getMethodMatcher();
            if (methodMatcher == MethodMatcher.TRUE) {
                return true;
            } else {
                IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
                if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
                    introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher)methodMatcher;
                }

                Set<Class<?>> classes = new LinkedHashSet();
                if (!Proxy.isProxyClass(targetClass)) {
                    classes.add(ClassUtils.getUserClass(targetClass));
                }

                classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
                Iterator var6 = classes.iterator();

                while(var6.hasNext()) {
                    Class<?> clazz = (Class)var6.next();
                    Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
                    Method[] var9 = methods;
                    int var10 = methods.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        Method method = var9[var11];
                        if (introductionAwareMethodMatcher != null) {
                            if (introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions)) {
                                return true;
                            }
                        } else if (methodMatcher.matches(method, targetClass)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public static boolean canApply(Advisor advisor, Class<?> targetClass) {
        return canApply(advisor, targetClass, false);
    }

    public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
        if (advisor instanceof IntroductionAdvisor) {
            return ((IntroductionAdvisor)advisor).getClassFilter().matches(targetClass);
        } else if (advisor instanceof PointcutAdvisor) {
            PointcutAdvisor pca = (PointcutAdvisor)advisor;
            return canApply(pca.getPointcut(), targetClass, hasIntroductions);
        } else {
            return true;
        }
    }

    public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        } else {
            List<Advisor> eligibleAdvisors = new ArrayList();
            Iterator var3 = candidateAdvisors.iterator();

            while(var3.hasNext()) {
                Advisor candidate = (Advisor)var3.next();
                if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
                    eligibleAdvisors.add(candidate);
                }
            }

            boolean hasIntroductions = !eligibleAdvisors.isEmpty();
            Iterator var7 = candidateAdvisors.iterator();

            while(var7.hasNext()) {
                Advisor candidate = (Advisor)var7.next();
                if (!(candidate instanceof IntroductionAdvisor) && canApply(candidate, clazz, hasIntroductions)) {
                    eligibleAdvisors.add(candidate);
                }
            }

            return eligibleAdvisors;
        }
    }

    @Nullable
    public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args) throws Throwable {
        try {
            ReflectionUtils.makeAccessible(method);
            return method.invoke(target, args);
        } catch (InvocationTargetException var4) {
            throw var4.getTargetException();
        } catch (IllegalArgumentException var5) {
            throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" + method + "] on target [" + target + "]", var5);
        } catch (IllegalAccessException var6) {
            throw new AopInvocationException("Could not access method [" + method + "]", var6);
        }
    }
}

