package com.xiaoyou.adsdkIntegration.demoapp.utils;

import android.text.TextUtils;

import java.util.ArrayList;

public class AdContentAnalyzer {

    public static void getAdContent(Object obj) {
        new Thread(() -> {

            String name = obj.getClass().getName();
            if (TextUtils.isEmpty(name)) return;
            ArrayList<Class<?>> classes = ReflectUtil.getAllClass(obj);

            for (Class<?> clazz : classes) {
                if (name.contains("com.applovin.") && MAXAdContentAnalyzer.getMaxAdContent(clazz, obj)) {
                    return;
                }

                if (name.contains("com.anythink.") && TopOnAdContentAnalyzer.getTopOnAdContent(clazz, obj)) { // TODO 这里报错
                    return;
                }
            }

        }).start();
    }


}
