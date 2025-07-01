package com.xiaoyou.adsdkIntegration.demoapp.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestUtil {


    public static String resolveFinalUrl(String initialUrl) throws IOException {
        String currentUrl = initialUrl;
        HttpURLConnection connection;
        int maxRedirects = 10;
        int redirectCount = 0;

        while (true) {
            URL url = new URL(currentUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false); // 不自动跳转，手动处理
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                String redirectUrl = connection.getHeaderField("Location");
                if (redirectUrl == null) break;
                currentUrl = redirectUrl;
                redirectCount++;
                if (redirectCount > maxRedirects) break;
            } else {
                break; // 已到最终目标
            }
        }

        return currentUrl;
    }

    /*String packageName = getPackageNameFromPlay("RoaringTigerLiveWallpaper");
                                if (packageName != null) {
        LogUtil.i("包名为: " + packageName);
    } else {
        LogUtil.e("未获取到包名");
    }*/
    public static String getPackageNameFromPlay(String appName) {
        try {
            String url = "https://play.google.com/store/search?q=" +
                    appName.replace(" ", "%20") + "&c=apps";

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000) // 建议加上超时
                    .get();

            // 找第一个搜索结果的链接
            Element link = doc.selectFirst("a.Si6A0c[href*=/store/apps/details?id=]");

            if (link != null) {
                String href = link.attr("href");
                int idIndex = href.indexOf("id=");
                if (idIndex != -1) {
                    return href.substring(idIndex + 3);
                }
            }

            return null; // 没找到
        } catch (Exception e) {
            // 建议记录异常信息，方便排查
            LogUtil.e("getPackageNameFromPlay 异常: " + e.getMessage());
            return null;
        }
    }

}
