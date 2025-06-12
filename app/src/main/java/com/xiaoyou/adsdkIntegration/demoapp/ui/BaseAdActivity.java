package com.xiaoyou.adsdkIntegration.demoapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoyou.adsdkIntegration.demoapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Base for activities showing ads.
 * <p>
 * Created by Harry Arakkal on 2019-10-21.
 */
public abstract class BaseAdActivity
        extends AppCompatActivity {
    private final List<String> callbacks = new ArrayList<>();
    private CallbacksRecyclerViewAdapter callbacksAdapter;

    /**
     * Setup callbacks RecyclerView adapter and appearance.
     */
    protected void setupCallbacksRecyclerView() {
        callbacksAdapter = new CallbacksRecyclerViewAdapter(callbacks, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration decoration = new DividerItemDecoration(this, manager.getOrientation());

        RecyclerView callbacksRecyclerView = findViewById(R.id.callbacks_recycler_view);
        callbacksRecyclerView.setHasFixedSize(true);
        callbacksRecyclerView.setAdapter(callbacksAdapter);
        callbacksRecyclerView.setLayoutManager(manager);
        callbacksRecyclerView.addItemDecoration(decoration);
    }

    /**
     * Log ad callbacks in the RecyclerView.
     * Uses the name of the function that calls this one in the log.
     * 在 RecyclerView 中记录广告回调。
     * 使用在日志中调用此回调的函数的名称。
     */
    protected void logCallback() {
        String callbackName = new Throwable().getStackTrace()[1].getMethodName();
        callbacks.add(callbackName);

        callbacksAdapter.notifyItemInserted(callbacks.size() - 1);
    }

    protected void logAnonymousCallback() {
        String callbackName = new Throwable().getStackTrace()[2].getMethodName();
        callbacks.add(callbackName);

        callbacksAdapter.notifyItemInserted(callbacks.size() - 1);
    }
}
