package com.xiaoyou.adsdkIntegration.demoapp.utils;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;

import com.fyber.inneractive.sdk.bidder.adm.AdmParametersOuterClass$AdmParameters;
import com.ironsource.mediationsdk.utils.IronSourceAES;
import com.xiaoyou.adsdkIntegration.demoapp.constants.AdContentConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sg.bigo.ads.BigoAdSdk;

public class AdContentAnalysis {

    private static HashMap<String, Object> map = new HashMap<>();


    // 获取一个对象所属的类以及其所有父类的 Class 列表
    private static ArrayList<Class<?>> getAllClass(Object obj) {
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


    public static void getAdContent(Object obj) {
        new Thread(() -> {

            String name = obj.getClass().getName();
            if (TextUtils.isEmpty(name)) return;
            ArrayList<Class<?>> classes = getAllClass(obj);

            for (Class<?> clazz : classes) {
                if (name.contains("com.applovin."))
                    if (getMaxAdContent(clazz, obj))
                        return;

                if (name.contains("com.anythink.")) { // 这里报错
                    if (getTopOnAdContent(clazz, obj))
                        return;
                }
            }

        }).start();
    }

    public static boolean getMaxAdContent(Class<?> clazz, Object obj) {
        Field[] fields = new Field[0];
        try {
            fields = clazz.getDeclaredFields();
        } catch (Throwable ignored) {
        }
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj); // 获取字段值
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            // 不处理 null 值和非 JSON 格式的字段
            if (!(fieldValue instanceof JSONObject)) continue;
            JSONObject jsonObjectField = (JSONObject) fieldValue;

            // 检查是否有指定字段
            if (!jsonObjectField.has("bid_response") || !jsonObjectField.has("network_name"))
                continue;

            try {
                String network_name = jsonObjectField.getString("network_name");
                String bid_response = jsonObjectField.getString("bid_response");
                if (TextUtils.isEmpty(bid_response)) {
                    LogUtil.e("bid_response is null");
                    return false;
                }
                if ("AppLovin".equals(network_name)) {
                    if (dealApplovin(network_name, bid_response))
                        return true;
                    continue;
                }

                if ("APPLOVIN_EXCHANGE".equals(network_name)) {
                    if (dealApplovinExchange(network_name, bid_response))
                        return true;
                    continue;
                }

                if ("Unity Ads".equals(network_name)) {
                    if (dealUnity(network_name, bid_response))
                        return true;
                    continue;
                }

                if ("Liftoff Monetize".equals(network_name)) {
                    if (dealVungle(bid_response))
                        return true;
                    continue;
                }
                if ("DT Exchange".equals(network_name)) {
                    if (dealFyber(network_name, bid_response)) // TODO
                        return true;
                    continue;
                }
                if ("BidMachine".equals(network_name)) {
                    if (dealBidMachine(network_name, bid_response))
                        return true;
                    continue;
                }
                if ("ironSource".equals(network_name)) {
                    if (dealIronSource(network_name, bid_response)) // TODO
                        return true;
                    continue;
                }
                if ("InMobi".equals(network_name)) { // 拿不到跳转链接，只能拿到广告包名
                    if (dealMaxInmobi(network_name, bid_response))
                        return true;
                    continue;
                }
                if ("BIGO Ads".equals(network_name)) {
                    if (dealBigo(network_name, bid_response))
                        return true;
                    continue;
                }
                LogUtil.e("没有适配广告平台：" + network_name);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    public static byte[] bigoStrTobyteArr(String str) {
        if (TextUtils.isEmpty(str)) {
            return new byte[0];
        }
        String upperCase = str.toUpperCase();
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) (((byte) "0123456789ABCDEF".indexOf(charArray[i2 + 1])) | (((byte) "0123456789ABCDEF".indexOf(charArray[i2])) << 4));
        }
        return bArr;
    }

    private static byte[] bigoAESDecode(byte[] bArr, byte[] bArr2) {

        if (bArr != null && bArr2 != null) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            final byte[] a = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivParameterSpec = new IvParameterSpec(a);
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(2, secretKeySpec, ivParameterSpec);
                return cipher.doFinal(bArr);
            } catch (InvalidAlgorithmParameterException | InvalidKeyException |
                     NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                     NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean dealBigo(String network_name, String bid_response) {
        try {
            String key = "FEFFFFFFFFFAFFFDCBFFFFFFFFFFFF4F";
            byte[] bytes = bigoAESDecode(bigoStrTobyteArr(bid_response), bigoStrTobyteArr(key));
            String data = gzipDecode(bytes);
            JSONObject bigoData = new JSONObject(data);
            JSONObject dataJson = bigoData.optJSONObject("data");
            int adx_type = dataJson.optInt("adx_type");
            String packageName = "";
            String url = "";
            if (adx_type == 1) {
                url = dataJson.optString("land_url");
                if (dataJson.has("title")) {
                    packageName = dataJson.optString("title");
                } else {
                    packageName = Uri.parse(url).getHost();
                }
            } else if (adx_type == 2) {
                if (dataJson.has("ad_bundle_id")) {
                    packageName = dataJson.optString("ad_bundle_id");
                } else if (dataJson.has("title")) {
                    packageName = dataJson.optString("title");
                }
                String videoData = dataJson.optJSONObject("video").optString("data");
                Document document = Jsoup.parse(videoData, "", org.jsoup.parser.Parser.xmlParser());
                Elements items = document.select("ClickThrough");
                if (!items.isEmpty()) {
                    url = items.get(0).text();
                }
            } else if (adx_type == 3) {
                if (dataJson.has("display")) {
                    String displayData = dataJson.optJSONObject("display").optString("data");
                    if (displayData.contains("zemCreative__=")) {
                        displayData = displayData.substring(displayData.indexOf("zemCreative__=") + 14);
                        String temp = displayData.substring(0, displayData.indexOf("};") + 1);
                        JSONObject tempJson = new JSONObject(temp);
                        packageName = tempJson.getString("title");
                        url = tempJson.getString("url");
                    }

                }
            }
            if (isMarketUrl(url)) {
                Uri uri = Uri.parse(url);
                packageName = "APP_" + uri.getQueryParameter("id");
            } else {
                packageName = "H5_" + packageName;
            }
            sendPackageName(network_name, packageName, url);
            return true;
        } catch (Throwable e) {
        }
        return false;
    }

    public static boolean dealMaxInmobi(String network_name, String bid_response) {
        try {
            JSONObject jsonObject = new JSONObject(bid_response);
            JSONObject adJsonObjet = jsonObject.optJSONArray("adSets").optJSONObject(0).optJSONArray("ads").optJSONObject(0);

            String packageName = "";
            JSONObject contextData = adJsonObjet.optJSONObject("contextData");
            if (adJsonObjet.has("bidBundle")) {
                packageName = "APP_" + adJsonObjet.optString("bidBundle");
            } else if (contextData.has("advertisedContent")) {
                packageName = "H5_" + contextData.optString("advertisedContent");
            }
            LogUtil.e("packageName " + packageName);
            sendPackageName(network_name, packageName, "");
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean dealBidMachine(String network_name, String bid_response) {
        try {
            byte[] decodeResult = Base64.decode(bid_response, Base64.DEFAULT);
            String result = new String(decodeResult, StandardCharsets.UTF_8);
            String url = "";
            String packageName = "";
            if (result.contains("store_url")) {
                int start = result.indexOf("\"store_url\": \"");
                String temp = result.substring(start + 14);
                temp = temp.substring(0, temp.indexOf("\""));
                url = temp;
                if (url.startsWith("https://click.liftoff.io/v1/campaign_click")) {
                    String regex = "(https://play\\.google\\.com/store/apps/details\\?id=|market://details\\?id=)[a-zA-Z0-9._&;=?-]+";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(result);
                    if (matcher.find()) {
                        url = matcher.group(0);
                    }
                }
            }
            if (!TextUtils.isEmpty(url)) {
                if (isMarketUrl(url)) {
                    Uri uri = Uri.parse(url);
                    packageName = "APP_" + uri.getQueryParameter("id");
                } else {
                    packageName = "H5_" + Uri.parse(url).getHost();
                }
                sendPackageName(network_name, packageName, url);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean dealFyber(String network_name, String bid_response) {
        try {
            // 通过反射加载 Protobuf 生成的类 (由 Fyber SDK 提供，编译时生成)
            Class<?> clazz = Class.forName("com.fyber.inneractive.sdk.bidder.adm.AdmParametersOuterClass$AdmParameters");

            // Fyber 的 bid_response = Base64 + Protobuf
            // 获取静态方法 parseFrom(byte[])，用于将二进制数据解析为 AdmParameters 对象

            // 调用 parseFrom 方法，解析 Base64 编码的 bid_response 为 Protobuf 对象
            Method parseFrom = clazz.getDeclaredMethod("parseFrom", byte[].class);
            parseFrom.setAccessible(true);
            Object invoke = parseFrom.invoke(null, (Object) Base64.decode(bid_response, Base64.DEFAULT));
            // LogUtil.e("反射解码的结果是 " + invoke);

            AdmParametersOuterClass$AdmParameters admParameters = (AdmParametersOuterClass$AdmParameters) invoke; // 强制转换为实际的 Protobuf 类型
            if (admParameters == null) return false;

            String packageName = admParameters.getAppBundleId();
            String storeUrl = admParameters.getStoreUrl(); // Fyber 仅有商店链接
            // String networkName = admParameters.getAdNetworkName();

            LogUtil.e(
                    "包名：" + packageName
                            + "\n\t 跳转链接: " + storeUrl
            );

            if (!TextUtils.isEmpty(packageName)) {
                return sendPackageName(network_name, AdContentConstant.PREFIX_APP + packageName, storeUrl);
            }
        } catch (Throwable ignored) {
            // 反射异常、解析异常
        }

        return false;
    }


    public static boolean dealIronSource(String network_name, String bid_response) {
        try {
            String decodeResult = IronSourceAES.decode("C38FB23A402222A0C17D34A92F971D1F", bid_response);
            JSONObject decodeJson = new JSONObject(decodeResult);
            JSONObject waterfallJson = decodeJson.optJSONArray("waterfall").optJSONObject(0);
            JSONObject serverDataJson = waterfallJson.optJSONObject("serverData");
            String adMarkup = serverDataJson.optString("adMarkup");
            adMarkup = URLDecoder.decode(adMarkup);
            JSONObject adMarkupJson = new JSONObject(adMarkup);
            JSONObject bidJson = adMarkupJson.optJSONObject("seatbid").optJSONArray("bid").optJSONObject(0);
            String packageName = bidJson.optString("bundleId");
            String url = bidJson.optJSONObject("ext").optJSONObject("clickTags").optString("clickURL");
            if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(url)) {
                sendPackageName(network_name, "APP_" + packageName, url);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    public static boolean dealVungle(String vungleData) {
        try {
            JSONObject jsonObject = new JSONObject(vungleData);
            vungleData = jsonObject.getString("adunit");
//            JSONObject jsonObject = new JSONObject();
//            LogUtil.e("vungleData " + vungleData);

            byte[] compressed = Base64.decode(vungleData, Base64.DEFAULT);
            LogUtil.e("  Base64解码后的数据长度 " + compressed.length);
            String result = gzipDecode(compressed);
//            LogUtil.e("result\n " + result);

            jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("ads");
            jsonObject = jsonArray.getJSONObject(0);
            jsonObject = jsonObject.getJSONObject("ad_markup");
            jsonObject = jsonObject.getJSONObject("template_settings");
            jsonObject = jsonObject.getJSONObject("normal_replacements");
            String click_url = jsonObject.getString("EC_CTA_URL");
            if (TextUtils.isEmpty(click_url))
                click_url = jsonObject.getString("CTA_BUTTON_URL");
            String APP_STORE_ID = jsonObject.getString("APP_STORE_ID");
            sendPackageName("Vungle", "APP_" + APP_STORE_ID, click_url);
            return true;
        } catch (Throwable e) {
        }
        return false;

    }

    private static boolean dealUnity(String network_name, String bid_response) {

        try {
            JSONObject jsonObject = new JSONObject(new String(Base64.decode(bid_response, Base64.DEFAULT)));
            if (!jsonObject.has("media")) {
                LogUtil.e("没有广告数据 media");
                return false;
            }

            jsonObject = jsonObject.getJSONObject("media");
            Iterator<String> keys = jsonObject.keys();

            if (!keys.hasNext()) {
                LogUtil.e("media 没有数据");
                return false;
            }
            String key = keys.next();
            jsonObject = jsonObject.getJSONObject(key);
            if (!jsonObject.has("content")) {
                LogUtil.e("没有content");
                return false;
            }
            String content = jsonObject.getString("content");
            if (TextUtils.isEmpty(content)) {
                LogUtil.e("content 内容为空");
                return false;
            }

            jsonObject = new JSONObject(content);
            String store = jsonObject.getString("store");
            String appStoreId = jsonObject.getString("appStoreId");
            String referrer = jsonObject.getString("referrer");
            if (!"google".equals(store)) {
                LogUtil.e("不是google商店产品");
                return false;
            }
            String click_url = "https://play.google.com/store/apps/details?id=" + appStoreId + "&" + referrer;
            sendPackageName(network_name, "APP_" + appStoreId, click_url);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean dealApplovinExchange(String network_name, String bid_response) {
        if (!bid_response.contains("json_v3!")) {
            LogUtil.e("bid_response not json_v3!");
            return false;
        }
        bid_response = bid_response.replace("json_v3!", "");
        try {
            JSONObject jsonObject = new JSONObject(new String(Base64.decode(bid_response, Base64.DEFAULT))); // Base64 解码
            if (!jsonObject.has("ads")) {
                LogUtil.e("没有广告数据 ads");
                return false;
            }
            String adomain = "";
            if (jsonObject.has("adomain")) {
                adomain = jsonObject.optString("adomain");
                LogUtil.e("域名 " + adomain);
            }

            JSONArray adsArray = jsonObject.getJSONArray("ads");
            if (adsArray.length() == 0) {
                LogUtil.e("广告数据 ads 为空");
                return false;
            }
            jsonObject = adsArray.getJSONObject(0);
            String click_url = jsonObject.optString("click_url");

            // 属于应用商店链接
            if (isMarketUrl(click_url)) {
                Uri uri = Uri.parse(click_url);
                String packageName = AdContentConstant.PREFIX_APP + uri.getQueryParameter("id");
                sendPackageName(network_name, packageName, click_url);
                return true;
            }

            // 属于 H5 网页广告
            if (jsonObject.has("html")) {
                String htmlData = jsonObject.optString("html");

                // 提取跳转链接
                String regex = "url:\\s*'([^']*)'"; // eg. url: 'https://www.baidu.com'
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(htmlData);
                String url = "";
                if (matcher.find()) url = matcher.group(1);

                String packageName = AdContentConstant.PREFIX_H5 + adomain;
                return sendPackageName(network_name, packageName, url);
            }
            // 属于 XML 格式
            if (jsonObject.has("xml")) {
                String xmlData = jsonObject.optString("xml");
                Document document = Jsoup.parse(xmlData, "", org.jsoup.parser.Parser.xmlParser());
                Elements items = document.select("ClickThrough");
                if (!items.isEmpty()) {
                    String url = items.get(0).text();
                    if (isMarketUrl(url)) {
                        Uri uri = Uri.parse(url);
                        String packageName = "APP_" + uri.getQueryParameter("id");
                        sendPackageName(network_name, packageName, url);
                    } else {
                        LogUtil.e("是网页广告 " + url);
                        String packageName = "H5_" + adomain;
                        sendPackageName(network_name, packageName, url);
                    }
                } else {
                    // H5网页广告，但是xml数据中没有跳转链接
                    String packageName = "H5_" + adomain;
                    sendPackageName(network_name, packageName, "");
                }
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean dealApplovin(String network_name, String bid_response) {
        if (!bid_response.contains("json_v3!")) {
            LogUtil.e("bid_response not json_v3!");
            return false;
        }
        bid_response = bid_response.replace("json_v3!", "");
        try {
            JSONObject jsonObject = new JSONObject(new String(Base64.decode(bid_response, Base64.DEFAULT)));
            if (!jsonObject.has("ads")) {
                LogUtil.e("没有广告数据 ads");
                return false;
            }

            JSONArray adsArray = jsonObject.getJSONArray("ads");
            if (adsArray.length() == 0) {
                LogUtil.e("广告数据 ads 为空");
                return false;
            }
            jsonObject = adsArray.getJSONObject(0);
//            if (!jsonObject.has("click_url")) {
//                LogUtil.e("没有click_url");
//                return false;
//            }

            // 获取跳转链接
            String click_url = jsonObject.optString("click_url");

            // 属于应用商店链接
            if (isMarketUrl(click_url)) {
                Uri uri = Uri.parse(click_url);
                String packageName = "APP_" + uri.getQueryParameter("id");
                return sendPackageName(network_name, packageName, click_url);
            }

            // 属于 H5 网页广告
            if (jsonObject.has("html")) {
                // 获取跳转链接
                Document document = Jsoup.parse(jsonObject.optString("html")); // 将 html 属性值解析为 DOM 文档对象
                Element scriptElement = document.getElementById("ad-context");
                if (scriptElement == null) return false;
                String ad_context = scriptElement.html(); // 获取插入在 script 中的 JSON
                JSONObject ad_contextJson = new JSONObject(ad_context);
                JSONObject openJson = ad_contextJson.getJSONObject("open"); // 使用 get 严格获取
                String redirectUrl = openJson.optString("redirectUrl");

                // 属于应用商店链接
                if (isMarketUrl(redirectUrl)) {
                    Uri uri = Uri.parse(redirectUrl);
                    String packageName = AdContentConstant.PREFIX_APP + uri.getQueryParameter("id");
                    return sendPackageName(network_name, packageName, redirectUrl);
                }
                // 否则为网页广告
                else {
                    String domain = Uri.parse(redirectUrl).getHost();
                    return sendPackageName(network_name, AdContentConstant.PREFIX_H5 + domain, redirectUrl);
                }
            }

            // 不处理 XML
            if (jsonObject.has("xml")) {
                LogUtil.e(jsonObject.optString("xml"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否是应用商店的跳转链接
     */
    public static boolean isMarketUrl(String url) {
        final Set<String> MARKET_KEYWORDS = new HashSet<>(Arrays.asList(
                "play.google.com",
                "market:",
                "market.android.com"
        ));

        return !TextUtils.isEmpty(url) &&
                MARKET_KEYWORDS.stream().anyMatch(url::contains);
    }


    public static boolean sendPackageName(String platform, String packageName, String destination_url) {

        LogUtil.e("获取到广告内容：\n" +
                "  平台         = " + platform + "\n" +
                "  包名         = " + packageName + "\n" +
                "  destination = " + destination_url);

        new Thread(() -> {
            boolean emptyPackageName = true;
            boolean emptyDestinationUrl = true;
//            String command = "am broadcast -a android.intent.lmt.ADPACKAGENAME";

            try {
                JSONObject jsonObject = new JSONObject();

//                String[] data = new String[4];
                if (!TextUtils.isEmpty(packageName)) {
                    emptyPackageName = false;
                    jsonObject.put("packageName", packageName);
//                data[0] = "packageName";
//                data[1] = packageName;
//                command = command + " --es packageName \"" + packageName + "\"";
                }
                if (!TextUtils.isEmpty(destination_url)) {
                    emptyDestinationUrl = false;
                    String url = destination_url;
                    if (destination_url.length() > 300) {
                        url = destination_url.substring(0, 300);
                    }
                    jsonObject.put("destination_url", url);
//                data[2] = "destination_url";
//                data[3] = destination_url;
//                command = command + " --es destination_url \"" + destination_url + "\"";e
                }
                if (emptyPackageName && emptyDestinationUrl) {
                    LogUtil.e("包名和地址点击地址都为空，不发广播");
                    return;
                }
                String action = "android.intent.lmt.ADPACKAGENAME";
                if ("Unity Ads".equals(platform)) {
                    action = "android.intent.lmt.ADPACKAGENAME_UNITY";
                }
//            Utils.sendBroadCast(action, data);
//                 Utils.putLogMessage(action, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).start();
        return true;
    }

    public static String gzipDecode(byte[] compressed) throws IOException {
        int bufferSize = 1024;
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        ByteArrayInputStream uByteArrayIn = new ByteArrayInputStream(compressed);
        try (GZIPInputStream gZIPInputStr = new GZIPInputStream(uByteArrayIn)) {
            byte[] data = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = gZIPInputStr.read(data)) != -1) {
                result.write(data, 0, bytesRead);
            }
        }
        String str = result.toString("UTF-8");
        if (str == null) {
            throw new NullPointerException("Decoded string is null");
        }
        return str;
    }

    private static boolean getTopOnAdContent(Class clazz, Object obj) {

        Field[] fields = new Field[0];
        try {
            fields = clazz.getDeclaredFields();
        } catch (Throwable e) {
        }
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (fieldValue == null) {
                continue;
            }
            String data = fieldValue + "";
            String platForm = "";
            if (data.contains("VungleATInterstitialAdapter") || data.contains("VungleATRewardedVideoAdapter")) {
                platForm = "Vungle";
                data = getNetworkDataByName(fieldValue, platForm);
                if (dealVungle(data)) {
                    return true;
                }
            }
//            if (data.contains("BigoATInterstitialAdapter") || data.contains("BigoATRewardedVideoAdapter")) {
//                platForm = "Bigo";
//                data = getNetworkDataByName(fieldValue, platForm);
//                if (dealBigo(platForm, data)) {
//                    return true;
//                }
//            }
//            if (data.contains("InmobiATInterstitialAdapter") || data.contains("InmobiATRewardedVideoAdapter")) {
//                platForm = "Inmobi";
//                data = getNetworkDataByName(fieldValue, platForm);
//                if (dealInmobi(platForm, data)) {
//                    return true;
//                }
//            }

        }

        return false;
    }

    private static String getNetworkDataByName(Object object, String platForm) {
        try {
            Field[] fields = new Field[0];
            try {
                fields = object.getClass().getDeclaredFields();
            } catch (Throwable e) {
            }
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldValue = null;
                try {
                    fieldValue = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (fieldValue == null) {
                    continue;
                }
                String vungleData = fieldValue + "";
                if ("Vungle".equals(platForm)) {
                    if (vungleData.contains("version") && vungleData.contains("adunit")) {
                        return vungleData;
                    }
                }
                if ("Bigo".equals(platForm)) {

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean dealBigo() {
        try {

            int bigoVersion = Integer.parseInt(BigoAdSdk.getSDKVersion());
            if (bigoVersion <= 20401) {
                LogUtil.e("Bigo版本:" + bigoVersion + ",版本太低没有适配");
                return false;
            }
            LogUtil.e("Bigo版本:" + bigoVersion);
            printFieldsMyActivity(Utils.getCurrentActivity(), 0, 5);
            Object object;
            if (bigoVersion > 40200) {
                object = map.get("sg.bigo.ads.core.e.a.p");
            } else {
                object = map.get("sg.bigo.ads.core.f.a.p");
            }
            if (object != null) {
                Class clazz = object.getClass();
                Field destinationUrlField;
//                if (bigoVersion > 20401) {
                destinationUrlField = clazz.getDeclaredField("m");
//                } else {
//                    destinationUrlField = clazz.getDeclaredField("l");
//                }
                destinationUrlField.setAccessible(true);
                String destination_url = (String) destinationUrlField.get(object);
                if (isMarketUrl(destination_url)) {
                    Uri uri = Uri.parse(destination_url);
                    String packageName = "APP_" + uri.getQueryParameter("id");
                    sendPackageName("Bigo", packageName, destination_url);
                    return true;
                }
                Field adContentField;
//                if (bigoVersion > 20401) {
                adContentField = clazz.getDeclaredField("p");
//                } else {
//                    adContentField = clazz.getDeclaredField("o");
//                }
                adContentField.setAccessible(true);
                String adContent = (String) adContentField.get(object);
                sendPackageName("Bigo", "H5_" + adContent, destination_url);
                return true;
            }
        } catch (Throwable e) {
        }
        return false;
    }

    public static void printFieldsMyActivity(Object obj, int loop, int maxLoop) {
        map.clear();
        if (obj instanceof Activity) {
            printFields(obj, loop, maxLoop);
        }
    }

    public static void printFields(Object obj, int loop, int maxLoop) {
        if (loop > maxLoop) {
            return;
        }
        loop++;
        if (obj == null) {
            return;
        }
        ArrayList<Class> list = getClass(obj);
        for (Class clazz : list) {
            printFields1(clazz, obj, loop, maxLoop);
        }
    }

    private static ArrayList<Class> getClass(Object obj) {
        ArrayList<Class> list = new ArrayList<>();
        Class clazz = obj.getClass();
        list.add(clazz);
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            list.add(clazz);
        }
        return list;
    }

    private static void dealFields(Object obj, int loop, int maxLoop, Field field) {
        try {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (fieldValue == null
                    || fieldValue instanceof View
                    || fieldValue instanceof ViewGroup.LayoutParams
                    || fieldValue instanceof OrientationEventListener
            )
                return;
            try {
                if (map.containsValue(fieldValue)) {
                    return;
                }
                map.put(fieldValue.getClass().getName(), fieldValue);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (fieldValue instanceof String
                    || fieldValue instanceof JSONObject
                    || fieldValue instanceof JSONArray
            )
                return;


            if (fieldValue instanceof SparseArray) {
                SparseArray temp = (SparseArray) fieldValue;
                for (int i = 0; i < temp.size(); i++) {
                    Object o = temp.valueAt(i);
                    printFields(o, loop, maxLoop);
                }
            }
            if (fieldValue instanceof AtomicReference) {
                Object o = ((AtomicReference<?>) fieldValue).get();
                printFields(o, loop, maxLoop);
            }
            if (fieldValue instanceof WeakReference) {
                Object o = ((WeakReference<?>) fieldValue).get();
                printFields(o, loop, maxLoop);
            }
            printFields(fieldValue, loop, maxLoop);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void printFields1(Class clazz, Object obj, int loop, int maxLoop) {
        String name = clazz.getName();
        if (name.startsWith("android.content")
                || name.startsWith("android.app")
                || name.startsWith("java.lang.")
                || name.startsWith("org.json")
                || name.startsWith("android.view.")
                || name.startsWith("android.os.")
                || name.startsWith("android.window.")
                || name.startsWith("java.util")
                || name.startsWith("java.io")
                || "com.fyber.inneractive.sdk.config.enums.UnitDisplayType".equals(name)
        )
            return;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            dealFields(obj, loop, maxLoop, field);
        }
    }

}
