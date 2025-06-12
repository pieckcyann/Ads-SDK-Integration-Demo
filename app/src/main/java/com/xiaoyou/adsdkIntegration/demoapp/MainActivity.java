package com.xiaoyou.adsdkIntegration.demoapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.applovin.sdk.AppLovinSdk;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.AppOpenAdActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.InterstitialAdActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.RewardedAdActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.banner.BannerAdActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.mrecs.MrecAdActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.nativead.NativeAdActivity;
import com.xiaoyou.adsdkIntegration.demoapp.data.MainMenuItem;
import com.xiaoyou.adsdkIntegration.demoapp.ui.MainExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main {@link android.app.Activity} of this app.
 */
public class MainActivity
        extends AppCompatActivity
        implements MainExpandableListAdapter.OnMainListItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final MainExpandableListAdapter adapter = new MainExpandableListAdapter(generateMainListItems(), this, this);
        final ExpandableListView expandableListView = findViewById(R.id.expandable_list_view);
        expandableListView.setAdapter(adapter);

        // Check that SDK key is present in Android Manifest
        checkSdkKey();
    }

    private void checkSdkKey() {
        final String sdkKey = AppLovinSdk.getInstance(getApplicationContext()).getSdkKey();
        if ("YOUR_SDK_KEY".equalsIgnoreCase(sdkKey)) {
            new AlertDialog.Builder(this)
                    .setTitle("ERROR")
                    .setMessage("Please update your sdk key in the manifest file.")
                    .setCancelable(false)
                    .setNeutralButton("OK", null)
                    .show();
        }
    }

    // 创建下拉列表
    private Map<String, List<MainMenuItem>> generateMainListItems() {
        // 标题 -> 多个广告类型
        Map<String, List<MainMenuItem>> childMap = new HashMap<>();

        // 给 MAX 组添加子项
        List<MainMenuItem> maxChildren = new ArrayList<>();
        maxChildren.add(new MainMenuItem("Interstitials", new Intent(this, InterstitialAdActivity.class)));
        maxChildren.add(new MainMenuItem("App Open Ads", new Intent(this, AppOpenAdActivity.class)));
        maxChildren.add(new MainMenuItem("Rewarded", new Intent(this, RewardedAdActivity.class)));
        maxChildren.add(new MainMenuItem("Banners", new Intent(this, BannerAdActivity.class)));
        maxChildren.add(new MainMenuItem("MRECs", new Intent(this, MrecAdActivity.class)));
        maxChildren.add(new MainMenuItem("Native Ads", new Intent(this, NativeAdActivity.class)));

        childMap.put("MAX", maxChildren);


        // 给 MAX 组添加子项
        List<MainMenuItem> topOnChildren = new ArrayList<>();
        topOnChildren.add(new MainMenuItem("Interstitials", new Intent(this, InterstitialAdActivity.class)));
        topOnChildren.add(new MainMenuItem("Interstitials", new Intent(this, InterstitialAdActivity.class)));

        childMap.put("TopOn", topOnChildren);


        return childMap;
    }

    @Override
    public void onItemClicked(MainMenuItem item) {
        if (item.getIntent() != null) {
            startActivity(item.getIntent());
        } else if (item.getRunnable() != null) {
            item.getRunnable().run();
        }
    }

}
