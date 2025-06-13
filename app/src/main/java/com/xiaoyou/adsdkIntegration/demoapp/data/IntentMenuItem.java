package com.xiaoyou.adsdkIntegration.demoapp.data;

import android.content.Intent;

public class IntentMenuItem extends MenuItem {
    private final Intent intent;

    public IntentMenuItem(String title, Intent intent) {
        super(title);
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }
}