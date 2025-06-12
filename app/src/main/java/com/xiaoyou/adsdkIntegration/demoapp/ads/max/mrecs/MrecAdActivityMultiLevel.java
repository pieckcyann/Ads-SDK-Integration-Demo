package com.xiaoyou.adsdkIntegration.demoapp.ads.max.mrecs;

import android.content.Intent;

import com.xiaoyou.adsdkIntegration.demoapp.data.MainMenuItem;
import com.xiaoyou.adsdkIntegration.demoapp.ui.BaseMultiLevelMenuActivity;

public class MrecAdActivityMultiLevel
        extends BaseMultiLevelMenuActivity {
    @Override
    protected MainMenuItem[] getListViewContents() {
        MainMenuItem[] result = {
                new MainMenuItem("Programmatic MRECs", new Intent(this, ProgrammaticMrecAdActivity.class)),
                new MainMenuItem("Layout Editor MRECs", new Intent(this, LayoutEditorMrecAdActivity.class)),
                new MainMenuItem("Recycler View MRECs", new Intent(this, RecyclerViewMrecAdActivity.class)),
        };
        return result;
    }
}
