package com.xiaoyou.adsdkIntegration.demoapp.data.kwai;

import android.content.Context;

public class Config {

    private static Config instance;
    private final Context context;

    private Config(Context context) {
        this.context = context.getApplicationContext();
    }

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
     * 获取激励广告的 tagId
     */
    public String getRewardTagId() {
        // 这里可以从资源文件读取，或者写死测试ID
        return "8999996001";
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
