package com.xiaoyou.adsdkIntegration.demoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaoyou.adsdkIntegration.demoapp.ui.TopOnActivity;
import com.xiaoyou.adsdkIntegration.demoapp.utils.LogUtil;
import com.xiaoyou.adsdkIntegration.demoapp.utils.analyzer.TopOnAdContentAnalyzer;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 应用启动时展示的闪屏页 (Splash Screen)，展示 1 秒后自动跳转到主页面 MainActivity。
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public static String extractTemplateParamValue(String str, String key) {
        if (str == null || key == null || key.trim().isEmpty()) return "";
        key = key.trim();

        String pattern = "\\{%\\s*" + Pattern.quote(key) + "\\s*%\\}\\s*=\\s*([^,]*)";
        Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE); // 忽略大小写
        Matcher matcher = regex.matcher(str);

        if (matcher.find()) {
            return Objects.requireNonNull(matcher.group(1), "未匹配到！").trim();
        }
        return "";

        // int count = 0;
        // while (matcher.find()) {
        //     count++;
        // }
        // return String.valueOf(count);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String str = TopOnAdContentAnalyzer.readLogFromAssets();
        String keyValue = extractTemplateParamValue(str, "is_app");
        LogUtil.i("is_app:" + keyValue);

        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, TopOnActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NO_ANIMATION);

            // 跳转回主页面
            startActivity(intent);
        }, 0); // 延迟 1 秒


        // // 使用 AppLovin 工具类，在主线程延迟 2 秒后跳转到 MainActivity
        // AppLovinSdkUtils.runOnUiThreadDelayed(() -> {
        //     // 创建跳转到主页面的 Intent
        //     Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        //
        //     // 设置启动模式：清除任务栈、避免动画、作为新任务启动
        //     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
        //             Intent.FLAG_ACTIVITY_CLEAR_TASK |
        //             Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //
        //     // 执行跳转
        //     startActivity(intent);
        // }, TimeUnit.SECONDS.toMillis(1)); // 延迟时间设为 2 秒

        // 跳转到目标 Activity
        // public static void OpenMainActivity(Context context, Class<? extends AppCompatActivity> targetActivityClass) {
        //     Intent openIntent = new Intent(context, targetActivityClass);
        //     openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //     context.startActivity(openIntent);
        // }

        // String str = readAssetFileAsString(this, "log.txt");

        // try {
        // TopOnAdContentAnalyzer.test(str);
        // } catch (JSONException | UnsupportedEncodingException e) {
        //     LogUtil.e(e.getMessage());
        //     throw new RuntimeException(e);
        // }

    }

}
