package com.xiaoyou.adsdkIntegration.demoapp.ads.max.nativead;

import android.content.Intent;

import com.xiaoyou.adsdkIntegration.demoapp.data.MainMenuItem;
import com.xiaoyou.adsdkIntegration.demoapp.ui.BaseMultiLevelMenuActivity;

public class NativeAdActivityMultiLevel
        extends BaseMultiLevelMenuActivity {
    @Override
    protected MainMenuItem[] getListViewContents() {
        MainMenuItem[] result = {
                new MainMenuItem("Templates API", new Intent(this, TemplateNativeAdActivity.class)),
                new MainMenuItem("Manual API", new Intent(this, ManualNativeAdActivity.class)),
                new MainMenuItem("Manual Late Binding API", new Intent(this, ManualNativeLateBindingAdActivity.class)),
                new MainMenuItem("Recycler View Ad Placer", new Intent(this, RecyclerViewNativeAdActivity.class))
        };
        return result;
    }
}
