package com.xiaoyou.adsdkIntegration.demoapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoyou.adsdkIntegration.demoapp.data.IntentMenuItem;
import com.xiaoyou.adsdkIntegration.demoapp.ui.BIGOActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ui.KwaiActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ui.MAXActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ui.TopOnActivity;
import com.xiaoyou.adsdkIntegration.demoapp.ui.base.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * The main {@link android.app.Activity} of this app.
 */
public class MainActivity
        extends AppCompatActivity
        implements BaseRecyclerViewAdapter.OnMainListItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final BaseRecyclerViewAdapter adapter = new BaseRecyclerViewAdapter(generateMainListItems(), this, this);
        final RecyclerView recyclerView = findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // ★
        recyclerView.setAdapter(adapter);

    }


    // 创建各个平台的 Activity
    private List<IntentMenuItem> generateMainListItems() {
        List<IntentMenuItem> mainItem = new ArrayList<>();
        mainItem.add(new IntentMenuItem("MAX", new Intent(this, MAXActivity.class)));
        mainItem.add(new IntentMenuItem("TopOn", new Intent(this, TopOnActivity.class)));
        mainItem.add(new IntentMenuItem("Kwai", new Intent(this, KwaiActivity.class)));
        mainItem.add(new IntentMenuItem("Bigo", new Intent(this, BIGOActivity.class)));
        return mainItem;
    }

    @Override
    public void onItemClicked(IntentMenuItem item) {
        startActivity(item.getIntent());
    }
}
