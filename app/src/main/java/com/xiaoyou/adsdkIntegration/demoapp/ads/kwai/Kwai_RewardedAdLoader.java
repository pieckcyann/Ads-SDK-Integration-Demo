package com.xiaoyou.adsdkIntegration.demoapp.ads.kwai;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kwai.network.sdk.KwaiAdSDK;
import com.kwai.network.sdk.api.KwaiAdLoaderManager;
import com.kwai.network.sdk.constant.KwaiError;
import com.kwai.network.sdk.event.AllianceConstants;
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAd;
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdConfig;
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdRequest;
import com.kwai.network.sdk.loader.business.reward.interf.IKwaiRewardAdListener;
import com.kwai.network.sdk.loader.common.interf.AdLoadListener;
import com.kwai.network.sdk.loader.common.interf.IKwaiAdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.kwai.Config;

public class Kwai_RewardedAdLoader implements AdLoader {

    private static final String TAG = "KwaiRewardLoader";
    private final Context context;
    private final Activity activity;
    private KwaiRewardAd mKwaiRewardAd = null;
    private IKwaiAdLoader<KwaiRewardAdRequest> mKwaiRewardAdLoader = null;

    public Kwai_RewardedAdLoader(Activity activity) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    @Override
    /**
     * 请求激励广告 | Request for reward ads
     */
    public void loadAd() {
        String tagId = Config.getInstance(context).getRewardTagId();
        String floorPrice = Config.getInstance(context).getFloorPrice();
        String extInfo = Config.getInstance(context).getExtInfo();

        KwaiAdLoaderManager loaderManager = KwaiAdSDK.getKwaiAdLoaderManager();
        if (loaderManager != null) {
            mKwaiRewardAdLoader = loaderManager.buildRewardAdLoader(
                    new KwaiRewardAdConfig.Builder(new RewardAdLoadListener())
                            .withKwaiRewardAdListener(new RewardAdListener())
                            .build()
            );

            KwaiRewardAdRequest request = new KwaiRewardAdRequest(tagId);
            request.extParams.put(AllianceConstants.Request.BID_FLOOR_PRICE, floorPrice);
            request.extParams.put(AllianceConstants.Request.MEDIATION_EXT_INFO, extInfo);

            mKwaiRewardAdLoader.loadAd(request);
        } else {
            Log.w(TAG, "KwaiAdLoaderManager is null, SDK might not be initialized");
        }
    }

    @Override
    public void showAd() {
        if (mKwaiRewardAd != null && mKwaiRewardAd.isReady() && activity != null) {
            mKwaiRewardAd.show(activity);
        } else {
            Log.w(TAG, "RewardAd not ready to show");
        }
    }

    public void sendBidWin() {
        if (mKwaiRewardAd != null) {
            String minWinPrice = Config.getInstance(context).getMinWinPrice();
            mKwaiRewardAd.getBidController().sendBidWin(minWinPrice);
        }
    }

    public void sendBidLose() {
        if (mKwaiRewardAd != null) {
            String loseCode = Config.getInstance(context).getLoseCode();
            String winPrice = Config.getInstance(context).getWinAdPrice();
            mKwaiRewardAd.getBidController().sendBidLose(loseCode, winPrice);
        }
    }

    public void release() {
        if (mKwaiRewardAdLoader != null) {
            mKwaiRewardAdLoader.release();
            mKwaiRewardAdLoader = null;
        }
    }

    private static class RewardAdListener implements IKwaiRewardAdListener {

        @Override
        public void onAdShow() {
            Log.i(TAG, "onAdShow");
        }

        @Override
        public void onAdShowFailed(@NonNull KwaiError error) {
            Log.i(TAG, "onAdShowFailed code = " + error.getCode() + " msg = " + error.getMsg());
        }

        @Override
        public void onAdClick() {
            Log.i(TAG, "onAdClick");
        }

        @Override
        public void onAdPlayComplete() {
            Log.i(TAG, "onAdPlayComplete");
        }

        @Override
        public void onRewardEarned() {
            Log.i(TAG, "onAdEarned");
        }

        @Override
        public void onAdClose() {
            Log.i(TAG, "onAdClose");
        }
    }

    /// ///////////////////////////////// Callbacks ////////////////////////////////////////

    private class RewardAdLoadListener implements AdLoadListener<KwaiRewardAd> {

        @Override
        public void onAdLoadStart(@Nullable String trackId) {
            Log.i(TAG, "onAdLoadStart");
            Toast.makeText(context, "onAdLoadStart", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdLoadSuccess(@Nullable String trackId, @NonNull KwaiRewardAd kwaiRewardAd) {
            String msg = "onAdLoaded: " + kwaiRewardAd.getPrice();
            Log.i(TAG, msg);
            mKwaiRewardAd = kwaiRewardAd;
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdLoadFailed(@Nullable String trackId, @NonNull KwaiError kwaiError) {
            String msg = "onAdLoadFailed code = " + kwaiError.getCode() + " msg = " + kwaiError.getMsg();
            Log.i(TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
