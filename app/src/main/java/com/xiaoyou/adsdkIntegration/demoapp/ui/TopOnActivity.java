package com.xiaoyou.adsdkIntegration.demoapp.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.ads.topon.TopOn_AppOpenAdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.ads.topon.TopOn_InterstitialLoader;
import com.xiaoyou.adsdkIntegration.demoapp.ads.topon.TopOn_RewardedAdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdMenuItem;
import com.xiaoyou.adsdkIntegration.demoapp.ui.base.BaseGridCardAdapter;

import java.util.ArrayList;
import java.util.List;

public class TopOnActivity extends AppCompatActivity implements BaseGridCardAdapter.OnAdActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform);
        setTitle("TopOn Ads");

        final BaseGridCardAdapter adapter = new BaseGridCardAdapter(generateTopOnAdListItems(), this, this);
        final RecyclerView recyclerView = findViewById(R.id.grid_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2); // 每行两个
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<AdMenuItem> generateTopOnAdListItems() {
        List<AdMenuItem> topOnAdItems = new ArrayList<>();

        topOnAdItems.add(new AdMenuItem("Interstitials", new TopOn_InterstitialLoader(this)));
        topOnAdItems.add(new AdMenuItem("App Open Ads", new TopOn_AppOpenAdLoader(this)));
        topOnAdItems.add(new AdMenuItem("Rewarded", new TopOn_RewardedAdLoader(this)));

        return topOnAdItems;
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
