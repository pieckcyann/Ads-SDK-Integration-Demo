package com.xiaoyou.adsdkIntegration.demoapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.xiaoyou.adsdkIntegration.demoapp.ui.TopOnActivity;
import com.xiaoyou.adsdkIntegration.demoapp.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 应用启动时展示的闪屏页 (Splash Screen)，展示 1 秒后自动跳转到主页面 MainActivity。
 */
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static String readAssetFileAsString(Context context, String fileName) {
        AssetManager assetManager = context.getAssets();
        StringBuilder sb = new StringBuilder();

        try (InputStream is = assetManager.open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_splash);

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
    //
    // private boolean test(String str) {
    //     if (str.startsWith("AppRequest")) {
    //         LogUtil.i("识别 ChartBoost 内容");
    //         // LogUtil.d("str: " + str);
    //
    //         // {% is_app %}=
    //         String is_app = extractTemplateParamValue(str, "is_app");
    //         if (is_app == null) {
    //             LogUtil.e("未匹配到 is_app 参数");
    //             return false;
    //         }
    //
    //         if ("true".equals(is_app)) {
    //             // {% adm %}=
    //             String admValue = new String(Base64.decode(extractTemplateParamValue(str, "adm"), 0));
    //             String admContent = new String(Base64.decode(admValue, 0));
    //
    //             // For find click_through_url param
    //             Pattern urlPattern = Pattern.compile("click_through_url=([^&]+)");
    //             Matcher urlMatcher = urlPattern.matcher(admContent);
    //             if (!urlMatcher.find()) {
    //                 LogUtil.e("未匹配到 click_through_url 参数");
    //                 return false;
    //             }
    //             String urlValue = urlMatcher.group(1);
    //             if (urlValue == null || !isMarketUrl(urlValue)) {
    //                 LogUtil.e("匹配到的 click_through_url 参数值不合法");
    //                 return false;
    //             }
    //             String urlContent = URLDecoder.decode(urlValue, "UTF-8");
    //             String packageName = Uri.parse(urlContent).getQueryParameter("id");
    //             return sendPackageName("Chartboost", AdContentConstant.PREFIX_APP + packageName);
    //         }
    //
    //         if ("false".equals(is_app)) {
    //             // {% ad_domain %}=
    //             String ad_domain = extractTemplateParamValue(str, "ad_domain");
    //             if (ad_domain != null) {
    //                 return sendPackageName("Chartboost", AdContentConstant.PREFIX_H5 + ad_domain);
    //             }
    //             LogUtil.e("未匹配到 ad_domain 参数，开始尝试获取 h5 title");
    //
    //             // {% adm %}=
    //             String admValue = new String(Base64.decode(extractTemplateParamValue(str, "adm"), 0));
    //             String admContent = new String(Base64.decode(admValue, 0));
    //
    //             // 一层解码
    //             Document doc = Jsoup.parse(admContent);
    //             Element admElement = doc.getElementById("adm");
    //             if (admElement == null) {
    //                 LogUtil.e("未找到 ID 为 'adm' 的元素");
    //                 return false;
    //             }
    //             String textContent = admElement.text();
    //             String decodedContent = new String(Base64.decode(textContent, 0));
    //
    //             // 二层解码
    //             Document doc2 = Jsoup.parse(decodedContent);
    //             Element scriptElement = doc2.select("script").first();
    //             if (scriptElement == null) {
    //                 LogUtil.e("未找到 <script> 标签");
    //                 return false;
    //             }
    //             String scriptContent = scriptElement.html();
    //             Pattern pattern = Pattern.compile("var outbrain_widget_.*?__zemCreative__=(\\{.*?\\});", Pattern.DOTALL);
    //             Matcher matcher = pattern.matcher(scriptContent);
    //             if (!matcher.find()) {
    //                 LogUtil.e("未在 <script> 内容中找到 JSON 格式数据");
    //                 return false;
    //             }
    //             String jsonString = matcher.group(1);
    //             if (jsonString == null) {
    //                 LogUtil.e("<script> 的 JSON 数据为空");
    //                 return false;
    //             }
    //             JSONObject jsonObject = new JSONObject(jsonString);
    //             String title = jsonObject.getString("title");
    //             return sendPackageName("Chartboost", AdContentConstant.PREFIX_H5 + title);
    //         }
    //     }
    //     return false;
    // }

}
