package com.xiaoyou.adsdkIntegration.demoapp.utils;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.applovin.mediation.MaxAd;
import com.applovin.sdk.AppLovinSdk;
import com.mbridge.msdk.foundation.entity.CampaignEx;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static ArrayList<String> list = new ArrayList<>();
    private static long sendTime = 0;

    public static void printFieldsMyActivityE(Object obj, int loop, int maxLoop, StringBuffer stringBuffer) {
        list.clear();
        printFieldsMyActivity(obj, loop, maxLoop, stringBuffer);
    }

    public static void printFieldsMyActivity(Object obj, int loop, int maxLoop, StringBuffer stringBuffer) {
        if (obj instanceof Activity) {
            printFields(obj, loop, maxLoop, stringBuffer);
            String name = obj.getClass().getName();
            LogUtil.e("name:" + name);
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

    private static void dealFields(Object obj, String name, int loop, int maxLoop, StringBuffer stringBuffer, Field field) {
        try {
            field.setAccessible(true);
            String fieldName = field.getName();
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
                String s = Integer.toHexString(fieldValue.hashCode());
                if (list.contains(s)) {
                    return;
                }
                if (obj instanceof WebView) {
                    WebView webView = (WebView) obj;
                    String tempStr = "loop:" + loop + ":" + name + ":url->" + webView.getUrl() + ":OriginalUrl->" + webView.getOriginalUrl();
                    stringBuffer.append(tempStr.replace(" ", "").replace("\n", "")).append("\n");
                }
                list.add(s);
                String tempStr = "loop:" + loop + ":" + name + ":" + fieldName + "->" + fieldValue;
                stringBuffer.append(tempStr.replace(" ", "").replace("\n", "")).append("\n");
            } catch (Throwable e) {
                e.printStackTrace();
            }
//            LogUtil.e(tempStr);

            if (fieldValue instanceof String
                    || fieldValue instanceof JSONObject
                    || fieldValue instanceof JSONArray
            )
                return;


            if (fieldValue instanceof SparseArray) {
                SparseArray temp = (SparseArray) fieldValue;
                for (int i = 0; i < temp.size(); i++) {
                    Object o = temp.valueAt(i);
                    printFields(o, loop, maxLoop, stringBuffer);
                }
            }
            if (fieldValue instanceof AtomicReference) {
                Object o = ((AtomicReference<?>) fieldValue).get();
                printFields(o, loop, maxLoop, stringBuffer);
            }
            if (fieldValue instanceof WeakReference) {
                Object o = ((WeakReference<?>) fieldValue).get();
                printFields(o, loop, maxLoop, stringBuffer);
            }
            printFields(fieldValue, loop, maxLoop, stringBuffer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void printFields1(Class clazz, Object obj, int loop, int maxLoop, StringBuffer stringBuffer) {
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
            dealFields(obj, name, loop, maxLoop, stringBuffer, field);
        }
    }

    public static void printFields(Object obj, int loop, int maxLoop, StringBuffer stringBuffer) {
        if (loop > maxLoop) {
            return;
        }
        loop++;
        if (obj == null) {
            return;
        }
        ArrayList<Class> list = getClass(obj);
        for (Class clazz : list) {
            printFields1(clazz, obj, loop, maxLoop, stringBuffer);
        }


    }

    private static boolean dealField(Object obj, String platform, int loop, int maxLoop, Field field, HashMap<String, String> map) {
        field.setAccessible(true);
        Object fieldValue = null;
        try {
            fieldValue = field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (fieldValue == null) {
            return false;
        }
        if (fieldValue instanceof View
                || fieldValue instanceof ViewGroup.LayoutParams
                || fieldValue instanceof OrientationEventListener
                || fieldValue instanceof ByteBuffer
        ) {
            return false;
        }

        String hash = Integer.toHexString(fieldValue.hashCode());
        if (list.contains(hash)) {
            return false;
        }
        list.add(hash);
        try {
            String str = "";
            try {
                str = "" + fieldValue;
            } catch (Throwable e) {

            }

            if ("TopOn".equals(platform)) {// topOn
                String temp = str;
                if (temp.contains("\"impression\"") && temp.contains("\"click\"")) {
                    try {
                        JSONObject jsonObject = new JSONObject(temp);
                        JSONArray jsonArray = jsonObject.getJSONArray("impression");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            temp = jsonArray.getString(i);
                            boolean result = getMarketPackageName(platform, temp, "bdl");
                            if (result) {
                                return true;
                            }
                        }
                        jsonArray = jsonObject.getJSONArray("click");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            temp = jsonArray.getString(i);
                            boolean result = getMarketPackageName(platform, temp, "bdl");
                            if (result) {
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (temp.contains("details")) {
                    if (getMarketPackageName("TopOn", temp, "id"))
                        return true;
                }
            }

            if ("MTG".equals(platform)) {

                if (fieldValue instanceof CampaignEx) {
                    CampaignEx campaignEx = (CampaignEx) fieldValue;
                    int link_type = campaignEx.getLinkType();
                    String packageName = campaignEx.getPackageName();
                    String click_url = campaignEx.getClickURL();
                    String mraid = campaignEx.getMraid();
                    LogUtil.e(String.format("link_type:%s,packageName:%s,click_url:%s,mraid:%s", link_type, packageName, click_url, mraid));

                    if (link_type == 2) {// App
                        AdContentAnalysis.sendPackageName("MTG", "APP_" + packageName, click_url);
                        return true;
                    }
                    if (link_type == 3 && !TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(click_url)) {
                        AdContentAnalysis.sendPackageName("MTG", "APPThird_" + packageName, click_url);
                        return true;
                    }
                    if (link_type == 9 || link_type == 8) {// h5
                        if (TextUtils.isEmpty(packageName) && TextUtils.isEmpty(click_url) && !TextUtils.isEmpty(mraid)) {
                            try {
                                String temp = "";
                                if (mraid.startsWith("/storage/emulated/0/Android/data")) {
                                    mraid = FileUtil.read(mraid);
                                    mraid = mraid.substring(mraid.indexOf("zemCreative__=") + 14);
                                    temp = mraid.substring(0, mraid.indexOf("};") + 1);
                                } else {
                                    temp = mraid.substring(mraid.indexOf("zemCreative__=") + 14, mraid.indexOf("};") + 1);
                                }
                                JSONObject jsonObject = new JSONObject(temp);
                                String tittle = jsonObject.getString("title");
                                String url = jsonObject.getString("url");
                                if (!TextUtils.isEmpty(tittle) && !TextUtils.isEmpty(url)) {
                                    AdContentAnalysis.sendPackageName("MTG", "H5_zemCreative." + tittle, url);
                                    return true;
                                }
                            } catch (Throwable e) {
                            }
                        } else {
                            if (TextUtils.isEmpty(packageName)) {
                                try {
                                    packageName = Uri.parse(click_url).getHost();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                            AdContentAnalysis.sendPackageName("MTG", "H5_" + packageName, click_url);
                            return true;
                        }
                    }

                }
            }
            if ("InMobi".equals(platform)) {// InMobi
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    if (jsonObject == null)
                        return false;
                    if (jsonObject.has("bidBundle")) {                          // 为APP包时，bidBundle和contextData一致，为网页时，只有contextData，无bidBundle字段
                        String bidBundle = jsonObject.optString("bidBundle");
                        if (!TextUtils.isEmpty(bidBundle)) {
                            if (map != null) {
                                //-w 1 1秒
//                                String s = Shell.commandStr("ping -w 1 " + bidBundle);
//                                LogUtil.e("pingResult:" + s);
                                map.put("app", bidBundle);
                            }
                        }
                    } else if (jsonObject.has("contextData")) {
                        JSONObject jsonObject1 = jsonObject.optJSONObject("contextData");
                        if (jsonObject1.has("advertisedContent")) {
                            String advertisedContent = jsonObject1.optString("advertisedContent");
                            if (map != null) {
                                map.put("h5", advertisedContent);
                            }
                        }
                    }
                    if (str.contains("<ClickThrough><![CDATA[")) {
                        str = str.replace("\\n", "").replace("\\", "");
                        String tempStr = str.substring(str.indexOf("<ClickThrough><![CDATA[") + 23);
                        tempStr = tempStr.substring(0, tempStr.indexOf("]]></ClickThrough>"));
                        if (!isMarketUrl(tempStr)) {
                            if (map != null) {
                                map.put("h5", tempStr);
                            }
                        }
                    }
                    if (str.contains("zemCreative__=")) {
                        str = str.substring(str.indexOf("zemCreative__=") + 14, str.indexOf(";"));
                        jsonObject = new JSONObject(str);
                        if (jsonObject.has("url")) {
                            str = jsonObject.getString("url");
                            if (!isMarketUrl(str)) {
                                if (map != null) {
                                    map.put("h5", str);
                                }
                            }
                        }
                    }
                    return true;
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            if ("Chartboost".equals(platform) || "Fyber".equals(platform)) {
                if (str.contains("details?id=")) {
                    boolean result = getMarketPackageName(platform, str, "id");
                    if (result)
                        return true;
                }
                if (str.contains("{% adm %}=")) {
                    str = str.substring(str.indexOf("{% adm %}=") + 10);
                    str = str.substring(0, str.indexOf(","));
                    String s = new String(Base64.decode(str, 0));
                    String tempStr = "";
                    try {
                        tempStr = s.substring(s.indexOf("JSON.parse(decodeURIComponent('") + 31);
                        tempStr = tempStr.substring(0, tempStr.indexOf("')"));
                        tempStr = URLDecoder.decode(tempStr);
                        JSONObject jsonObject = new JSONObject(tempStr);
                        jsonObject = jsonObject.getJSONObject("clickURLs");
                        jsonObject = jsonObject.getJSONObject("clickthroughURL");
                        tempStr = jsonObject.getString("url");
                        boolean result = getMarketPackageName(platform, tempStr, "id");
                        if (result)
                            return true;
                    } catch (Exception e) {
                    }
                    try {
                        tempStr = s.substring(s.indexOf("<ClickThrough><![CDATA[") + 23);
                        tempStr = tempStr.substring(0, tempStr.indexOf("]]></ClickThrough>"));
                        boolean result = getMarketPackageName(platform, tempStr, "id");
                        if (result)
                            return true;
                    } catch (Exception e) {
                    }
                }

                if (str.contains("<ClickThrough>") && !str.contains("<ClickThrough><![CDATA[")) {
                    String tempStr = str.substring(str.indexOf("<ClickThrough>") + 14);
                    tempStr = tempStr.substring(0, tempStr.indexOf("</ClickThrough>"));
                    tempStr = tempStr.replace("&amp;", "&");
                    boolean result = getMarketPackageName(platform, tempStr, "id");
                    if (result)
                        return true;
                }

                if ("Fyber".equals(platform)) {
                    if (str.contains("x-ia-app-bundle=")) {
                        String packageName = str.substring(str.indexOf("x-ia-app-bundle=") + 16);
                        packageName = packageName.substring(0, packageName.indexOf(","));
                        if (!TextUtils.isEmpty(packageName)) {
                            boolean result = sendPackageName(platform, "APP_" + packageName);
                            if (result)
                                return true;
                        }
                    }
                }
            }

            if ("Bigo".equals(platform)) {
                str = str.replace(" ", "").replace("\n", "");
                try {
                    if (str.contains("<ClickThrough><![CDATA[")) {
                        String tempStr = str.substring(str.indexOf("<ClickThrough><![CDATA[") + 23);
                        tempStr = tempStr.substring(0, tempStr.indexOf("]]></ClickThrough>"));
                        boolean result = getMarketPackageName(platform, tempStr, "id");
                        if (result)
                            return true;
                    }
                    if (str.contains("ori_bundle=")) {
                        String packageName = str.substring(str.indexOf("ori_bundle=") + 11);
                        packageName = packageName.substring(0, packageName.indexOf("&"));
                        if (!TextUtils.isEmpty(packageName)) {
                            boolean result = sendPackageName(platform, "APP_" + packageName);
                            if (result)
                                return true;
                        }
                    }

//                    if (str.contains("zemCreative__=")) {
//                        AdContentUtil.trySaveBigoAdContent(str);
//                    }
                } catch (Exception e) {
                }
            }

            if ("Vungle".equals(platform)) {
                if (str.contains("PlacementAdUnit(placementReferenceId")) {
                    String tempStr = str.substring(str.indexOf("APP_STORE_ID=") + 13);
                    tempStr = tempStr.substring(0, tempStr.indexOf(","));
                    tempStr = tempStr.replace("}", "");
                    if (!TextUtils.isEmpty(tempStr)) {
                        boolean result = sendPackageName(platform, "APP_" + tempStr);
                        if (result)
                            return true;
                    }
                    tempStr = str.substring(str.indexOf("adMarketId=") + 11);
                    tempStr = tempStr.substring(0, tempStr.indexOf(","));
                    if (!TextUtils.isEmpty(tempStr)) {
                        boolean result = sendPackageName(platform, "APP_" + tempStr);
                        if (result)
                            return true;
                    }

                    tempStr = str.substring(str.indexOf("EC_CTA_URL=") + 11);
                    tempStr = tempStr.substring(0, tempStr.indexOf(","));
                    boolean result = getMarketPackageName(platform, tempStr, "id");
                    if (result)
                        return true;

                    tempStr = str.substring(str.indexOf("CTA_BUTTON_URL=") + 15);
                    tempStr = tempStr.substring(0, tempStr.indexOf(","));
                    result = getMarketPackageName(platform, tempStr, "id");
                    if (result)
                        return true;
                }
            }

            if ("Pangle".equals(platform)) {
                if (str.contains("package_name")) {
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        if (jsonObject.has("creatives")) {
                            JSONArray creatives = jsonObject.getJSONArray("creatives");
                            JSONObject jsonObject1 = creatives.getJSONObject(0);
                            if (jsonObject1.has("app")) {
                                JSONObject app = jsonObject1.getJSONObject("app");
                                String packageName = app.getString("package_name");
                                boolean result = sendPackageName(platform, "APP_" + packageName);
                                if (result)
                                    return true;
                            }
                        }
                    } catch (Throwable e) {
                    }
                }
                try {
                    String tempStr = new String(Base64.decode(str, 10));
                    JSONObject jsonObject = new JSONObject(tempStr);
                    String adomain = jsonObject.getString("adomain");
                    String[] tempArr = adomain.split("\\.");
                    String packageName = "";
                    for (int i = tempArr.length - 1; i >= 0; i--) {
                        packageName = packageName + tempArr[i] + ".";
                    }
                    packageName = packageName.substring(0, packageName.length() - 1);
                    boolean result = sendPackageName(platform, "APP_" + packageName);
                    if (result)
                        return true;
                } catch (Throwable e) {
                }
            }

            if ("Applovin".equals(platform)) {
                if (str.contains("click_url")) {
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        String url = jsonObject.getString("click_url");
                        if (url.contains("details")) {
                            url = URLDecoder.decode(url);
                            boolean result = getMarketPackageName(platform, url, "id");
                            if (result)
                                return true;
                        }
                    } catch (Throwable th) {

                    }
                }
                if ("adObject".equals(field.getName())) {
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        String tempStr = "";
                        String packageName = "";
                        if (jsonObject.has("xml")) {
                            tempStr = jsonObject.optString("xml");
                        } else if (jsonObject.has("html")) {
                            tempStr = jsonObject.optString("html");
                        }
                        tempStr = tempStr.substring(tempStr.indexOf("details") + 7);
                        Matcher matcher = Pattern.compile("%[0-9A-Fa-f]{2}").matcher(tempStr);
                        if (matcher.find()) {
                            tempStr = URLDecoder.decode(tempStr);
                        }
                        tempStr = tempStr.substring(tempStr.indexOf("id=") + 3);
                        packageName = tempStr.substring(0, tempStr.indexOf("&"));
                        if (!packageName.isEmpty()) {
                            boolean result = sendPackageName(platform, "APP_" + packageName);
                            if (result)
                                return true;
                        }
                    } catch (Throwable r) {
                    }
                }
            }
            if ("BidMachine".equals(platform)) {
                if (str.contains("adomain") && str.contains("bundle")) {
                    String s = str.replaceAll("\\s+", "");
                    String regex = "bundle:\"([^\"]+)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.find()) {
                        String bundleValue = matcher.group(1); // 获取第一个捕获组中的内容
                        boolean bidMachine = sendPackageName("BidMachine", "APP_" + bundleValue);
                        if (bidMachine) {
                            return true;
                        }
                    } else {
                        System.out.println("No bundle value found.");
                    }

                }

            }
        } catch (Throwable e) {

        }
        if (fieldValue instanceof SparseArray) {
            SparseArray temp = (SparseArray) fieldValue;
            for (int i = 0; i < temp.size(); i++) {
                Object o = temp.valueAt(i);
                if (getPackageName(o, platform, loop, maxLoop, map))
                    return true;
            }
        }
        if (fieldValue instanceof String
                || fieldValue instanceof JSONObject
                || fieldValue instanceof JSONArray
        )
            return false;
        if (fieldValue instanceof AtomicReference) {
            Object o = ((AtomicReference<?>) fieldValue).get();
            if (getPackageName(o, platform, loop, maxLoop, map))
                return true;
        }
        if (fieldValue instanceof WeakReference) {
            Object o = ((WeakReference<?>) fieldValue).get();
            if (getPackageName(o, platform, loop, maxLoop, map))
                return true;
        }

        return getPackageName(fieldValue, platform, loop, maxLoop, map);
    }

    public static boolean getPakcageNameE(Object obj, String platform, int loop, int maxLoop) {
        list.clear();
        LogUtil.e("getPakcageNameE->platform:" + platform + ",maxLoop:" + maxLoop);
        return getPackageName(obj, platform, loop, maxLoop, null);
    }

    public static boolean getPakcageNameE(Object obj, String platform, int loop, int maxLoop, HashMap<String, String> map) {
        list.clear();
        LogUtil.e("getPakcageNameE->platform:" + platform + ",maxLoop:" + maxLoop);
        return getPackageName(obj, platform, loop, maxLoop, map);
    }

    public static boolean getPackageName(Object obj, String platform, int loop, int maxLoop, HashMap<String, String> map) {
        if (loop > maxLoop) {
            return false;
        }
        loop++;
        if (obj == null) {
            return false;
        }
        ArrayList<Class> list = getClass(obj);
        for (Class clazz : list) {
            if (getPackageName(clazz, obj, platform, loop, maxLoop, map)) {
                return true;
            }
        }

        return false;
    }

    private static boolean getPackageName(Class clazz, Object obj, String platform, int loop, int maxLoop, HashMap<String, String> map) {
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
                || name.startsWith("kotlin.")
                || "com.fyber.inneractive.sdk.config.enums.UnitDisplayType".equals(name)
        )
            return false;
        Field[] fields = new Field[0];
        try {
            fields = clazz.getDeclaredFields();
        } catch (Throwable e) {
        }
        for (Field field : fields) {
            if (dealField(obj, platform, loop, maxLoop, field, map)) {
                return true;
            }
        }
        return false;
    }

    public static boolean getMarketPackageName(String platform, String url, String key) {
        try {
            Uri uri = Uri.parse(url);
            String packageName = uri.getQueryParameter(key);
            if (!TextUtils.isEmpty(packageName)) {
                return sendPackageName(platform, "APP_" + packageName);
            }
        } catch (Throwable e) {
        }
        return false;
    }

    public static Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread", new Class[0]).invoke((Object) null, new Object[0]);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            for (Object activityRecord : ((Map) activitiesField.get(activityThread)).values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        return null;
    }

    public static List<View> getAllViews(View rootView) {

        List<View> allViews = new ArrayList<>();
        if (rootView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) rootView;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = viewGroup.getChildAt(i);
                allViews.add(childView);
                allViews.addAll(getAllViews(childView)); // 递归遍历子视图
            }
        } else {

        }

        return allViews;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendPackageName(String platform, String packageName) {
        if (!isPkg(packageName)) {
            return false;
        }
        long temp = System.currentTimeMillis();
        if (temp - sendTime < 1000)
            return false;
        sendTime = temp;
        LogUtil.e("获取到广告内容，当前平台：" + platform + ",当前包名：" + packageName);
        new Thread(() ->
//                Shell.command("am broadcast -a android.intent.lmt.ADPACKAGENAME --es packageName " + "\"" + packageName + "\"")

        {
            String action = "android.intent.lmt.ADPACKAGENAME";
//            String[] data = new String[2];
//            data[0] = "packageName";
//            data[1] = packageName;
//            sendBroadCast(action, data);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("packageName", packageName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // putLogMessage(action, jsonObject.toString());
        }).start();
        return true;
    }

    public static boolean sendPackageName(String platform, String packageName, String Url) {
        if (!isPkg(packageName)) {
            return false;
        }
        long temp = System.currentTimeMillis();
        if (temp - sendTime < 1000)
            return false;
        sendTime = temp;
        LogUtil.e("获取到广告内容，当前平台：" + platform + ",当前包名：" + packageName);
        new Thread(() ->
//                Shell.command("am broadcast -a android.intent.lmt.ADPACKAGENAME --es packageName " + "\"" + packageName + "\"")

        {
            String action = "android.intent.lmt.ADPACKAGENAME";
            String[] data = new String[2];
            data[0] = "packageName";
            data[1] = packageName;
            // sendBroadCast(action, data);
        }).start();
        return true;
    }

    /// /        ThirdSDKImpl.saveContentInfo(url);
//        new Thread(() -> Shell.command("am broadcast -a android.intent.lmt.ADPACKAGENAME --es packageName " + "\"" + url + "\"")).start();
//        return true;
//    }
    private static boolean isPkg(String str) {
        // Java/Android合法包名，可以包含大写字母、小写字母、数字和下划线，用点(英文句号)分隔称为段，且至少包含2个段，隔开的每一段都必须以字母开头
        Pattern pattern = Pattern.compile("[a-zA-Z]+[0-9a-zA-Z_]*(\\.[a-zA-Z]+[0-9a-zA-Z_]*)*\\.[a-zA-Z]+[0-9a-zA-Z_]*(\\$[a-zA-Z]+[0-9a-zA-Z_]*)*");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
//    public static boolean sendWebPageUrl(String platform, String url) {
//        long temp = System.currentTimeMillis();
//        if (temp - sendTime < 1000)
//            return false;
//        sendTime = temp;
//        LogUtil.e("获取到网页广告内容，当前平台：" + platform + ",网页链接：" + url);

    public static boolean isMarketUrl(String url) {
        if ((!url.contains("play.google.com") && !url.contains("market:") && !url.contains("market.android.com"))) {
            return false;
        } else {
            return true;
        }
    }

    // public static void sendBroadCast(String action, String[] data) {
    //     Intent intent = new Intent(action);
    //     for (int i = 0; i < data.length; i++) {
    //         String key = data[i];
    //         String value = data[++i];
    //         if (!TextUtils.isEmpty(key))
    //             intent.putExtra(key, value);
    //     }
    //     LogUtil.e("sendBroadCast:" + intent + ",Data:" + Arrays.toString(data));
    //     ThirdSDKImpl.getInstance().context.sendBroadcast(intent);
    // }

//     public static void putLogMessage(String key, String value) {
//
//         Class clazz = null;
//         try {
//             clazz = Class.forName("ninja.com.device.faker.manager.LmtServiceManager");
//             Object lmtServiceManager = ReflectUtil.invoke(null, clazz, "get", null, null);
//             Method putLog = clazz.getDeclaredMethod("putLogMessage", String.class, String.class);
// //            Method putLog = clazz.getDeclaredMethod("putClientLogMessage", String.class, String.class);
//             putLog.setAccessible(true);
//             putLog.invoke(lmtServiceManager, key, value);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

    public static void dumpInfo(String platform, Context context) {
        LogUtil.e(String.format("%s", "dumpInfo:platform:" + platform));
        Object maxAd = getMaxAd(context);
        if (maxAd == null) {
            LogUtil.e(String.format("%s", "maxAd is null."));
            return;
        }
        AdContentAnalysis.getAdContent(maxAd);
    }

    private static Object getMaxAd(Context context) {
        try {
            AppLovinSdk instance = AppLovinSdk.getInstance(context);
            if (instance == null) {
                LogUtil.e("AppLovinSdk is null");
                return null;
            }
            Field[] declaredFields = AppLovinSdk.class.getDeclaredFields();
            for (Field field : declaredFields) {
                Object object = getValue(field, instance);
                if (object == null || object instanceof String) continue;
                Field[] declaredFields1 = object.getClass().getDeclaredFields();
                for (Field field1 : declaredFields1) {
                    Object object1 = getValue(field1, object);
                    if (object1 == null || !(object1 instanceof AtomicReference)) continue;
                    AtomicReference atomicReference = (AtomicReference) object1;
                    object1 = atomicReference.get();
                    if (object1 == null) continue;
                    Field[] declaredFields2 = object1.getClass().getDeclaredFields();
                    for (Field field2 : declaredFields2) {
                        Object object2 = getValue(field2, object1);
                        if (object2 instanceof MaxAd) {
                            return object2;
                        }

                    }
                }
            }
        } catch (Throwable e) {
            LogUtil.e(e.getMessage());
        }
        return null;
    }

    private static Object getValue(Field field, Object object) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    public View getViews(List<View> viewList, String tag) {

        if (viewList == null || viewList.size() == 0 || TextUtils.isEmpty(tag))
            return null;

        for (View view : viewList) {
            if (view.toString().contains(tag))
                return view;
        }
        return null;

    }
}
