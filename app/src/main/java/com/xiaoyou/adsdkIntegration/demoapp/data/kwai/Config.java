package com.xiaoyou.adsdkIntegration.demoapp.data.kwai;

import android.content.Context;

/**
 * config对象用于存储各种回调实例，比如广告加载的成功或者失败，以及广告发生的各种行为。同时是构建广告加载器Loader的必传对象。
 * <a href="https://docs.qingque.cn/d/home/eZQB8993uGF9aJ0W06Tp-DUXd#section=h.1quofsnqmn5y">原文链接</a>
 */
public class Config {

    private static Config instance;
    private final Context context;

    private Config(Context context) {
        this.context = context.getApplicationContext();
    }

    // 单例模式
    public static Config getInstance(Context context) {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取激励广告 tagId
     */
    public String getRewardTagId() {
        return "8999996001";
    }


    /**
     * 获取插屏告 tagId
     */
    public String getInterstitialTagId() {
        return "8999996002";
    }

    /**
     * 获取底价 floorPrice
     */
    public String getFloorPrice() {
        // 例子，默认0
        return "0";
    }

    /**
     * 获取扩展信息 extInfo，JSON字符串或自定义格式
     */
    public String getExtInfo() {
        // 示例返回空字符串
        return "";
    }

    /**
     * 竞价中，最低赢价
     */
    public String getMinWinPrice() {
        return "0.01";
    }

    /**
     * 竞价失败原因代码
     */
    public String getLoseCode() {
        return "1001";
    }

    /**
     * 竞价胜出广告价格
     */
    public String getWinAdPrice() {
        return "0.02";
    }

    // 你还可以根据需求，扩展更多配置接口

}
