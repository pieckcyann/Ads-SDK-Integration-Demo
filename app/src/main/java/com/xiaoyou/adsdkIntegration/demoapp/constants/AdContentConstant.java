package com.xiaoyou.adsdkIntegration.demoapp.constants;

/**
 * 广告内容分析相关常量定义类
 */
public class AdContentConstant {

    public static final String PREFIX_APP = "APP_";
    public static final String PREFIX_H5 = "H5_";

    // 常用广告平台 (广告 SDK) 名称
    public static final String ADMOB = "AdMob";
    public static final String FACEBOOK = "Facebook";
    public static final String UNITY = "UnityAds";
    public static final String VUNGLE = "Vungle";
    public static final String IRONSOURCE = "IronSource";
    public static final String MOPUB = "MoPub"; // 虽然已下线，某些旧项目仍用
    public static final String APPLOVIN = "AppLovin";
    public static final String CHARTBOOST = "Chartboost";
    public static final String FYBER = "Fyber";
    public static final String TAPJOY = "Tapjoy";
    public static final String INMOBI = "InMobi";
    public static final String MINTEGRAL = "Mintegral";
    public static final String PANGLE = "Pangle"; // 字节跳动海外
    public static final String KUAISHOU = "Kuaishou"; // 快手磁力引擎
    public static final String BAIDU = "Baidu";
    public static final String TOUTIAO = "Toutiao"; // 今日头条广告
    public static final String GDT = "GDT"; // 腾讯广点通
    public static final String STARTAPP = "StartApp";
    public static final String BIGO = "BigoAds";
    public static final String SIGMOB = "Sigmob";

    // 防止实例化
    private AdContentConstant() {

    }

    // 忽略大小写地方判断字符串是否包含
    public static boolean containsIgnoreCase(String source, String target) {
        if (source == null || target == null) return false;
        return source.toLowerCase().contains(target.toLowerCase());
    }
}
