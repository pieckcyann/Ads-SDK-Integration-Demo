package com.xiaoyou.adsdkIntegration.demoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import com.xiaoyou.adsdkIntegration.demoapp.utils.FileUtil;
import com.xiaoyou.adsdkIntegration.demoapp.utils.LogUtil;
import com.xiaoyou.adsdkIntegration.demoapp.utils.TopOnAdContentAnalyzer;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class DebugInfoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 判断 action
        if ("com.xiaoyou.action.DUMP_INFO".equals(intent.getAction())) {
            LogUtil.d("DumpInfoReceiver", "dumpInfo action received");

            // 获取当前显示的 Activity 实例
            Activity activity = TopOnAdContentAnalyzer.getCurrentActivity();
            if (activity == null) {
                LogUtil.e("无法获取当前的 activity");
                return;
            }
            int maxLoop = 30;
            LogUtil.e("maxLoop:" + maxLoop);

            LogUtil.e("getCurrentWebViewUrl: " + TopOnAdContentAnalyzer.getCurrentWebViewUrl());

            new Thread(() -> {
                StringBuffer stringBuffer = new StringBuffer();

                // 记录当前 Activity 的类名
                stringBuffer.append(activity.getClass().getName()).append("\n");

                // 分析当前 Activity 的字段内容
                TopOnAdContentAnalyzer.printFieldsMyActivityE(activity, 0, maxLoop, stringBuffer);

                // 改为应用可写目录，比如外部私有目录
                File baseDir = activity.getExternalFilesDir(null);
                if (baseDir == null) {
                    LogUtil.e("无法获取外部文件目录");
                    return;
                }
                String path = baseDir.getAbsolutePath() + "/log";

                // 创建目录（mkdirs 失败表示目录已存在或没有权限）
                File dir = new File(path);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        LogUtil.e("目录无法创建");
                        return;
                    }
                }

                // 构建完整日志路径
                String filePath = path + "/log.txt";

                // 将分析结果编码为 Base64 并写入文件
                FileUtil.write(
                        Base64.encodeToString(stringBuffer.toString().getBytes(StandardCharsets.UTF_8), 0),
                        filePath
                );
            }).start();


        }
    }
}