package com.xiaoyou.adsdkIntegration.demoapp.ads.topon;

import android.app.Activity;

import com.anythink.core.api.ATAdInfo;
import com.anythink.core.api.AdError;
import com.anythink.rewardvideo.api.ATRewardVideoAd;
import com.anythink.rewardvideo.api.ATRewardVideoListener;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.utils.AdContentAnalyzer;
import com.xiaoyou.adsdkIntegration.demoapp.utils.LogUtil;
import com.xiaoyou.adsdkIntegration.demoapp.utils.Notify;

public class TopOn_RewardedAdLoader implements AdLoader {

    private static final String TAG = "topon reward";

    private static Activity activity;
    private final String placementId = "n683fba683e000";
    private ATRewardVideoAd mRewardVideoAd;

    public TopOn_RewardedAdLoader(Activity activity) {
        this.activity = activity;
        initAd(); // 自动预加载
    }

    private static void notify(CharSequence text) {
        Notify.notify(TAG, activity, text);
    }

    private void initAd() {
        // 加载广告
        mRewardVideoAd = new ATRewardVideoAd(activity, placementId);

        mRewardVideoAd.load();

        // 注册回调
        mRewardVideoAd.setAdListener(new ATRewardVideoListener() {

            @Override
            public void onRewardedVideoAdLoaded() {
                TopOn_RewardedAdLoader.notify("TopOn 激励视频加载完成");
                // mRewardVideoAd.load(); // 自动预加载
            }

            @Override
            public void onRewardedVideoAdFailed(AdError adError) {
                // 注意：禁止在此回调中执行广告的加载方法进行重试，否则会引起很多无用请求且可能会导致应用卡顿
                // AdError，请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_test?id=aderror
                TopOn_RewardedAdLoader.notify("TopOn 激励视频加载失败，错误码：" + adError.getCode() + "\n" + "报错详情：" + adError.printStackTrace() + "\n");
                // TopOn_RewardedAdLoader.notify("平台报错码：" + adError.getPlatformCode() + "\n" + "报错信息：" + adError.getPlatformMSG());
                // TopOn_RewardedAdLoader.notify(adError.printStackTrace());

            }

            @Override
            public void onRewardedVideoAdPlayStart(ATAdInfo atAdInfo) {

                System.out.println("xxxxxxxxxxxxxxxxx");
                // ATAdInfo可区分广告平台以及获取广告平台的广告位ID等
                // 请参考 https://docs.toponad.com/#/zh-cn/android/android_doc/android_sdk_callback_access?id=callback_info

                // 建议在此回调中调用load进行广告的加载，方便下一次广告的展示（不需要调用isAdReady()）
                mRewardVideoAd.load();

                LogUtil.i("Showing ad from: " + atAdInfo.getNetworkName());
                AdContentAnalyzer.getAdContent(atAdInfo);
            }

            @Override
            public void onRewardedVideoAdPlayEnd(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayFailed(AdError adError, ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdClosed(ATAdInfo atAdInfo) {

            }

            @Override
            public void onRewardedVideoAdPlayClicked(ATAdInfo atAdInfo) {

            }

            @Override
            public void onReward(ATAdInfo atAdInfo) {

            }
        });

        // 也可以加 setAdDownloadListener、setAdSourceStatusListener 等
    }

    @Override
    public void loadAd() {
        initAd();
    }

    @Override
    public void showAd() {
        if (mRewardVideoAd != null) {
            if (mRewardVideoAd.isAdReady()) {
                mRewardVideoAd.show(activity);
            } else {
                TopOn_RewardedAdLoader.notify("TopOn 激励广告还没准备好");
                initAd();
            }
        }
    }
}
