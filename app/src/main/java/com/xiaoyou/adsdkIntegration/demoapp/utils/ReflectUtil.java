package com.xiaoyou.adsdkIntegration.demoapp.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {
    private ReflectUtil() {
    }

    /**
     * 获取对象的所有字段值
     *
     * @param obj 对象
     * @return 字段值列表
     */
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
