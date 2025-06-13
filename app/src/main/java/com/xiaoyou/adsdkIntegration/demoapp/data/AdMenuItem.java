package com.xiaoyou.adsdkIntegration.demoapp.data;

// 用于广告
public class AdMenuItem extends MenuItem {
    private final AdLoader adLoader;

    public AdMenuItem(String title, AdLoader adLoader) {
        super(title);
        this.adLoader = adLoader;
    }

    public AdLoader getAdLoader() {
        return adLoader;
    }

}