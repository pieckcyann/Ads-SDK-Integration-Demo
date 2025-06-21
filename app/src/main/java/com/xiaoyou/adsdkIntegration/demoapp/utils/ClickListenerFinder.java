package com.xiaoyou.adsdkIntegration.demoapp.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClickListenerFinder {

    /**
     * 获取 Activity 里所有设置了点击监听器的控件和监听器
     *
     * @param activity 目标 Activity
     * @return List 包含控件和对应 OnClickListener
     */
    public static List<ViewClickListenerInfo> findAllClickListeners(Activity activity) {
        List<ViewClickListenerInfo> result = new ArrayList<>();
        if (activity == null) return result;

        View root = activity.getWindow().getDecorView();
        findClickListenersRecursive(root, result);
        return result;
    }

    private static void findClickListenersRecursive(View view, List<ViewClickListenerInfo> outList) {
        if (view == null) return;

        View.OnClickListener listener = getOnClickListener(view);
        if (listener != null) {
            outList.add(new ViewClickListenerInfo(view, listener));
        }

        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findClickListenersRecursive(vg.getChildAt(i), outList);
            }
        }
    }

    private static View.OnClickListener getOnClickListener(View view) {
        try {
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            if (listenerInfo == null) return null;

            Field mOnClickListenerField = listenerInfo.getClass().getDeclaredField("mOnClickListener");
            mOnClickListenerField.setAccessible(true);

            return (View.OnClickListener) mOnClickListenerField.get(listenerInfo);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ViewClickListenerInfo {
        public View view;
        public View.OnClickListener listener;

        public ViewClickListenerInfo(View v, View.OnClickListener l) {
            view = v;
            listener = l;
        }
    }
}
