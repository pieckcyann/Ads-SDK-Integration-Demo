package com.xiaoyou.adsdkIntegration.demoapp.ads.max.banner;

import android.content.Intent;

import com.xiaoyou.adsdkIntegration.demoapp.data.MainMenuItem;
import com.xiaoyou.adsdkIntegration.demoapp.ui.BaseMenuActivity;

public class BannerAdActivity
        extends BaseMenuActivity {
    @Override
    protected MainMenuItem[] getListViewContents() {
        MainMenuItem[] result = {
                new MainMenuItem("Programmatic Banners", new Intent(this, ProgrammaticBannerAdActivity.class)),
                new MainMenuItem("Layout Editor Banners", new Intent(this, LayoutEditorBannerAdActivity.class)),
        };
        return result;
    }
}
