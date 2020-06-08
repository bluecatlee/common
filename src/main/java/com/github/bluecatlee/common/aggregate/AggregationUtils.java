package com.github.bluecatlee.common.aggregate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bluecatlee.common.restful.RestResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.xml.transform.Source;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 聚合工具类
 */
@SuppressWarnings("all")
public class AggregationUtils {

    /**
     * 聚合同类型的list
     *  todo 仅支持单个字段匹配
     * @param oriList 原始list
     * @param mergeList 待合并list
     * @param fieldName 匹配字段
     * @return
     */
    @SuppressWarnings("all")
    public static <T> List<T> aggregate(List<T> oriList, List<T> mergeList, String fieldName) throws Exception {
        if (oriList == null || oriList.isEmpty() || mergeList == null || mergeList.isEmpty()) {
            return oriList;
        }
        if (mergeList.size() > oriList.size()) {
            throw new Exception("待合并list集合大小不能超出原始list集合大小");
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new Exception("匹配字段名称不能为空");
        }
        Class<?> oriClass = oriList.get(0).getClass();
        Class<?> mergeClass = mergeList.get(0).getClass();
        if (!oriClass.getName().equals(mergeClass.getName())) {
            throw new Exception("list中元素的类型不一致");
        }

        for (Object oriObj : oriList) {
            Class<?> aClass = oriObj.getClass();
            Field afield = aClass.getDeclaredField(fieldName); // 匹配字段
            if (afield == null) {
                throw new Exception("指定的匹配字段不存在");
            }
            afield.setAccessible(true);
            Object oriValue = afield.get(oriObj);  // 字段值

            for (Object mergeObject : mergeList) {
                Class<?> bClass = mergeObject.getClass();
                Field bfield = bClass.getDeclaredField(fieldName); // 匹配字段
                bfield.setAccessible(true);

                Object mergeValue = bfield.get(mergeObject);  // 字段值

                if (oriValue.equals(mergeValue)) {
                    // 合并
                    // 根据数据类型默认值判断是否需要覆盖值
                    Field[] afields = aClass.getDeclaredFields();
                    for (Field f : afields) {
                        // 循环原始对象的属性
                        if (f.getName().equals(fieldName)) {
                            // 匹配字段不处理
                            continue;
                        }
                        if (needsMerge(oriObj, f)) {
                            // 获取待合并对象对应字段的值
                            Field bField = bClass.getDeclaredField(f.getName());
                            bField.setAccessible(true);
                            f.set(oriObj, bField.get(mergeObject));
                        }
                    }
                    break;
                }

            }

        }

        return oriList;
    }

    /**
     * 聚合不同类型的list成一个新类型的list
     * @param list1 集合1
     * @param list2 集合2
     * @param fieldName 匹配字段
     * @param clazz 新类型
     * @return
     * @throws Exception
     */
    @SuppressWarnings("all")
    public static <T,T1,T2> List<T> aggregate2(List<T1> list1, List<T2> list2, String fieldName, Class<T> clazz) throws Exception{
        if (list1 == null || list1.isEmpty() || list2 == null || list2.isEmpty()) {
            return null;
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new Exception("匹配字段名称不能为空");
        }
        if (clazz == null) {
            throw new Exception("合并类型不能为空");
        }
        ObjectMapper mapper = new ObjectMapper();

        ArrayList<T> list = new ArrayList<>();

        for (Object oriObj : list1) {
            Map<String, Object> oriMap = copyMap(oriObj);

            HashMap<String, Object> map = new HashMap<>();
            map.putAll(oriMap);
            for (Object mergeObj : list2) {
                Map<String, Object> mergeMap = copyMap(mergeObj);
                if (oriMap.get(fieldName).equals(mergeMap.get(fieldName))) {
                    map.putAll(mergeMap);
                    // map转json
                    String json = mapper.writeValueAsString(map);
                    // json转bean
                    T newObj = (T)mapper.readValue(json, clazz);
                    list.add(newObj);
                    break;
                }
            }
        }
        return list;
    }

    /**
     * 串行完全没必要使用CompletableFuture, 也不必使用thenCompose方法
     * 仅仅是练习
     */
    @Deprecated
    public static <T,T1,T2> List<T> aggregate3Seril(Supplier<List<T1>> supplier1, Supplier<List<T2>> supplier2, String fieldName, Class<T> clazz) {
        CompletableFuture<List<T1>> s1 = CompletableFuture.supplyAsync(supplier1);
        CompletableFuture<List<T>> future = s1.thenCompose(new Function<List<T1>, CompletionStage<List<T>>>() {
            @Override
            public CompletionStage<List<T>> apply(List<T1> t1s) {
                return CompletableFuture.supplyAsync(new Supplier<List<T>>() {
                    @Override
                    public List<T> get() {
                        try {
                            List<T2> t2s = CompletableFuture.supplyAsync(supplier2).get();
                            return AggregationUtils.aggregate2(t1s, t2s, fieldName, clazz);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
        });
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步聚合不同类型的Supplier<List<?>>成一个新类型的list
     * @param supplier1 服务调用1
     * @param supplier2 服务调用2
     * @param fieldName 匹配字段
     * @param clazz 新类型
     * @param <T>
     * @param <T1>
     * @param <T2>
     * @return
     */
    public static <T,T1,T2> List<T> aggregate3Async(Supplier<List<T1>> supplier1, Supplier<List<T2>> supplier2, String fieldName, Class<T> clazz) {
        CompletableFuture<List<T1>> s1 = CompletableFuture.supplyAsync(supplier1);
        CompletableFuture<List<T2>> s2 = CompletableFuture.supplyAsync(supplier2);
        // 异步执行后处理结果
        // CompletableFuture<List<T>> future = s1.thenCombine(s2, new BiFunction<List<T1>, List<T2>, List<T>>() {
        //     @Override
        //     public List<T> apply(List<T1> r1, List<T2> r2) {
        //         try {
        //             return AggregationUtils.aggregate2(r1, r2, fieldName, clazz);
        //         } catch (Exception e) {
        //             e.printStackTrace();
        //         }
        //         return null;
        //     }
        // });
        // try {
        //     return future.get();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // } catch (ExecutionException e) {
        //     e.printStackTrace();
        // }
        // return null;
        CompletableFuture<Object> future = s1.thenCombine(s2, (r1, r2) -> {
            try {
                return AggregationUtils.aggregate2(r1, r2, fieldName, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        try {
            return (List<T>)future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步聚合 耦合服务调用结果RestResult
     */
    @Deprecated
    public static <T,T1,T2> List<T> aggregate4(Supplier<RestResult<List<T1>>> supplier1, Supplier<RestResult<List<T2>>> supplier2, String fieldName, Class<T> clazz) {
        CompletableFuture<RestResult<List<T1>>> s1 = CompletableFuture.supplyAsync(supplier1);
        CompletableFuture<RestResult<List<T2>>> s2 = CompletableFuture.supplyAsync(supplier2);
        CompletableFuture<Object> future = s1.thenCombine(s2, (r1, r2) -> {
            if (r1.getCode() != 200) {
                return s1;
            }
            if (r2.getCode() != 200) {
                return s2;
            }
            List<T1> list1 = r1.getObject();
            List<T2> list2 = r2.getObject();
            try {
                return AggregationUtils.aggregate2(list1, list2, fieldName, clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        try {
            return (List<T>)future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据字段类型的默认值判断该字段是否需要合并
     *  todo 字段类型仅支持String、 int、 long及其对应的包装类型等
     * @param field
     * @return
     */
    private static boolean needsMerge(Object obj, Field field) throws Exception {
        if (field == null) {
            throw new Exception("字段不能为空");
        }
        Class<?> clazz = field.getType(); // 获取字段的实际类型
        Object value = field.get(obj);

        if (value == null) {
            return true;
        }

        if (clazz.equals(String.class)) {
            if ("".equals((String)value)) {
                return true;
            }
        } else if (clazz.equals(int.class)) {
            if ((int)value == 0) {
                return true;
            }
        } else if (clazz.equals(long.class)) {
            if ((long)value == 0L) {
                return true;
            }
        }

        return false;
    }

    /**
     * bean转map
     * @param object
     * @return
     */
    private static Map<String, Object> copyMap(Object object) {

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

    ////test////

    @Data
    public static class A1 {
        Long id;
        String value1;
    }

    @Data
    public static class A2 {
        Long id;
        String value2;
        int value3;
        Long value4;
    }

    @Data
    public static class A {
        Long id;
        String value1;
        String value2;
        int value3;
        Long value4;
    }

    @SuppressWarnings("all")
    public static void main(String[] args) {

        // test1();
        // test2();
        test3();

    }

    private static void test1() {
        ArrayList<A> list1 = new ArrayList<>();
        A a1 = new A();
        a1.setId(1L);
        a1.setValue1("1");
        list1.add(a1);

        A a2 = new A();
        a2.setId(2L);
        a2.setValue1("2");
        list1.add(a2);

        A a3 = new A();
        a3.setId(3L);
        a3.setValue1("3");
        list1.add(a3);

        ArrayList<A> list2 = new ArrayList<>();
        A a4 = new A();
        a4.setId(1L);
        a4.setValue2("1");
        a4.setValue3(1);
        a4.setValue4(1L);
        list2.add(a4);

        A a5 = new A();
        a5.setId(2L);
        a5.setValue2("2");
        a5.setValue3(2);
        a5.setValue4(2L);
        list2.add(a5);

        A a6 = new A();
        a6.setId(3L);
        a6.setValue2("3");
        a6.setValue3(3);
        a6.setValue4(3L);
        list2.add(a6);

        try {
            List list = aggregate(list2, list1, "id");
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void test2() {
        ArrayList<A1> list1 = new ArrayList<>();
        A1 a11 = new A1();
        a11.setId(1L);
        a11.setValue1("1");
        list1.add(a11);

        A1 a12 = new A1();
        a12.setId(2L);
        a12.setValue1("2");
        list1.add(a12);

        A1 a13 = new A1();
        a13.setId(3L);
        a13.setValue1("3");
        list1.add(a13);

        ArrayList<A2> list2 = new ArrayList<>();
        A2 a24 = new A2();
        a24.setId(1L);
        a24.setValue2("1");
        a24.setValue3(1);
        a24.setValue4(1L);
        list2.add(a24);

        A2 a25 = new A2();
        a25.setId(2L);
        a25.setValue2("2");
        a25.setValue3(2);
        a25.setValue4(2L);
        list2.add(a25);

        A2 a26 = new A2();
        a26.setId(3L);
        a26.setValue2("3");
        a26.setValue3(3);
        a26.setValue4(3L);
        list2.add(a26);

        try {
            List<A> list = aggregate2(list2, list1, "id", A.class);
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟服务调用
     */
    private static void test3() {

        class AService {
            RestResult<List<A1>> a(int x) {
                // System.out.println("service a execute, " + x);
                ArrayList<A1> list = new ArrayList<>();
                A1 a11 = new A1();
                a11.setId(1L);
                a11.setValue1("1");
                list.add(a11);

                A1 a12 = new A1();
                a12.setId(2L);
                a12.setValue1("2");
                list.add(a12);

                A1 a13 = new A1();
                a13.setId(3L);
                a13.setValue1("3");
                list.add(a13);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return RestResult.SUCCESS().object(list).build();
            }
        }
        class BService {
            RestResult<List<A2>> b() {
                // System.out.println("service b execute");
                ArrayList<A2> list = new ArrayList<>();
                A2 a24 = new A2();
                a24.setId(1L);
                a24.setValue2("1");
                a24.setValue3(1);
                a24.setValue4(1L);
                list.add(a24);

                A2 a25 = new A2();
                a25.setId(2L);
                a25.setValue2("2");
                a25.setValue3(2);
                a25.setValue4(2L);
                list.add(a25);

                A2 a26 = new A2();
                a26.setId(3L);
                a26.setValue2("3");
                a26.setValue3(3);
                a26.setValue4(3L);
                list.add(a26);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return RestResult.SUCCESS().object(list).build();
            }
        }
        long t0 = System.currentTimeMillis();
        List<A> list0 = null;
        try {
            list0 = aggregate2(new AService().a(1).getObject(), new BService().b().getObject(), "id", A.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long t1 = System.currentTimeMillis();
        List<A> list1 = aggregate3Seril(() -> new AService().a(1).getObject(), () -> new BService().b().getObject(), "id", A.class);
        long t2 = System.currentTimeMillis();
        List<A> list2 = aggregate3Async(() -> new AService().a(1).getObject(), () -> new BService().b().getObject(), "id", A.class);
        long t3 = System.currentTimeMillis();
        List<A> list3 = aggregate4(() -> new AService().a(1), () -> new BService().b(), "id", A.class);
        System.out.println(list0);
        System.out.println("serial exec " + (t1 - t0) + "ms");
        System.out.println(list1);
        System.out.println("aggregate3Seril exec " + (t2 - t1) + "ms");
        System.out.println(list2);
        System.out.println("aggregate3Async exec " + (t3 - t2) + "ms");
        System.out.println(list3);
    }
}
