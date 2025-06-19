package com.xiaoyou.adsdkIntegration.demoapp.utils;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Creaed by chenliangquan on 2017/3/13.
 */

public class LogUtil {

    private static String TAG = "Hookad";

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {
            // 长度小于等于限制直接打印
            Log.e(tag, msg);
        } else {
            // 循环分段打印日志
            while (msg.length() > segmentSize) {
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.e(tag, logContent);
            }
            // 打印剩余日志
            Log.e(tag, msg);
        }
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {
            // 长度小于等于限制直接打印
            Log.i(tag, msg);
        } else {
            // 循环分段打印日志
            while (msg.length() > segmentSize) {
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.i(tag, logContent);
            }
            // 打印剩余日志
            Log.i(tag, msg);
        }
    }


    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {


        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {
            // 长度小于等于限制直接打印
            Log.d(tag, msg);
        } else {
            // 循环分段打印日志
            while (msg.length() > segmentSize) {
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.d(tag, logContent);
            }
            // 打印剩余日志
            Log.d(tag, msg);
        }
    }

    public static void append(String fileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
