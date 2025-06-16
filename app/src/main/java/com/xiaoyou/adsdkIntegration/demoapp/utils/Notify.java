package com.xiaoyou.adsdkIntegration.demoapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Notify {
    public static void notify(String tag, Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
        Log.w(tag, (String) text);
    }

    public static void notify(String tag, Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        Log.w(tag, (String) text);
    }
}
