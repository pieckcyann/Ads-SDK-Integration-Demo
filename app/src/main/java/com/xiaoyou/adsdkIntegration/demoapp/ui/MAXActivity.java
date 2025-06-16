package com.xiaoyou.adsdkIntegration.demoapp.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.sdk.AppLovinSdk;
import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.MAX_InterstitialAdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.ads.max.MAX_RewardedAdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdMenuItem;
import com.xiaoyou.adsdkIntegration.demoapp.ui.base.BaseGridCardAdapter;

import java.util.ArrayList;
import java.util.List;

public class MAXActivity extends AppCompatActivity implements BaseGridCardAdapter.OnAdActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);
        setTitle("MAX Ads");

        final BaseGridCardAdapter adapter = new BaseGridCardAdapter(generateMaxAdListItems(), this, this);
        final RecyclerView recyclerView = findViewById(R.id.grid_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 每行两个
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Check that SDK key is present in Android Manifest
        checkSdkKey();
    }

    private List<AdMenuItem> generateMaxAdListItems() {
        List<AdMenuItem> maxAdItems = new ArrayList<>();

        maxAdItems.add(new AdMenuItem("Interstitials", new MAX_InterstitialAdLoader(this)));
        maxAdItems.add(new AdMenuItem("Rewarded", new MAX_RewardedAdLoader(this)));
        // maxAdItems.add(new AdMenuItem("App Open Ads", new MAX_AppOpenAdLoader(this)));
        // maxAdItems.add(new AdMenuItem("Banners", new MAX_BannerAdLoader(this)));

        return maxAdItems;
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

    @Override
    public void onLoadClicked(AdLoader itemAdLoad) {
        itemAdLoad.loadAd();
    }

    @Override
    public void onShowClicked(AdLoader itemAdLoad) {
        itemAdLoad.showAd();
    }
}
