package com.xiaoyou.adsdkIntegration.demoapp.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflecxtUtil {
    private ReflecxtUtil() {
    }

    // 获取一个对象所属的类以及其所有父类的 Class 列表
    public static ArrayList<Class<?>> getAllClass(Object obj) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        classes.add(clazz);

        // 一直向上查找父类，直到没有父类为止
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            classes.add(clazz);
        }

        return classes;
    }


    // 获取指定类中声明的字段在指定父类对象中的所有非空值
    public static List<Object> getAllFieldValues(Class<?> clazz, Object obj) {
        List<Object> values = new ArrayList<>();
        if (obj == null) {
            return values;
        }

        Field[] fields;
        try {
            fields = clazz.getDeclaredFields();
        } catch (Throwable e) {
            return values;
        }

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    values.add(value);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return values;
    }

    // 获取指定对象所属类中所有非空字段的值
    public static List<Object> getAllFieldValues(Object obj) {
        List<Object> values = new ArrayList<>();
        if (obj == null) {
            return values;
        }

        Field[] fields;
        try {
            fields = obj.getClass().getDeclaredFields();
        } catch (Throwable e) {
            return values;
        }

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    values.add(value);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return values;
    }


}
