package com.xiaoyou.adsdkIntegration.demoapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.data.MainMenuItem;

public abstract class BaseMultiLevelMenuActivity
        extends AppCompatActivity {
    protected ListView listView;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);

        setupListViewContents(getListViewContents());
    }

    protected abstract MainMenuItem[] getListViewContents();

    private void setupListViewContents(final MainMenuItem[] items) {
        ArrayAdapter<MainMenuItem> listAdapter = new ArrayAdapter<MainMenuItem>(this, android.R.layout.simple_list_item_1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = convertView;
                if (row == null) {
                    LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                MainMenuItem item = items[position];

                TextView title = (TextView) row.findViewById(android.R.id.text1);
                title.setText(item.getTitle());

                return row;
            }
        };
        listView.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = items[position].getIntent();
                if (intent != null) startActivity(intent);
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }
}
