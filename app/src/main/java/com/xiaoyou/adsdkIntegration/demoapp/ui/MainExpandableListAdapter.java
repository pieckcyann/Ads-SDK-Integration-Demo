package com.xiaoyou.adsdkIntegration.demoapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.data.MainMenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> groupTitles;
    private final Map<String, List<MainMenuItem>> childMap;
    private final OnMainListItemClickListener clickListener;

    public MainExpandableListAdapter(
            Map<String, List<MainMenuItem>> groupedItems,
            Context context,
            OnMainListItemClickListener clickListener
    ) {
        this.context = context;
        this.clickListener = clickListener;
        this.childMap = groupedItems;
        this.groupTitles = new ArrayList<>(groupedItems.keySet());
    }

    @Override
    public int getGroupCount() {
        System.out.println(groupTitles.size());
        return groupTitles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String group = groupTitles.get(groupPosition);
        return childMap.get(group).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupTitles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String group = groupTitles.get(groupPosition);
        return childMap.get(group).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // 父项布局：用 SectionHeader 的样式
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_parent_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.parent_title);
        textView.setText(groupTitles.get(groupPosition));

        ImageView arrow = convertView.findViewById(R.id.item_parent_arrow_image);

        if (isExpanded) {
            arrow.setRotation(90); // 箭头向下
        } else {
            arrow.setRotation(0); // 箭头向右
        }

        return convertView;
    }

    // 子项布局：用 MainMenuItem 的样式
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_list_child_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.child_title);
        MainMenuItem item = (MainMenuItem) getChild(groupPosition, childPosition);
        textView.setText(item.getTitle());

        // 模拟 clickListener.onItemClicked(item)
        convertView.setOnClickListener(v -> {

            if (clickListener != null) {
                clickListener.onItemClicked(item);
            }

            if (item.getIntent() != null) {
                context.startActivity(item.getIntent());
            } else if (item.getRunnable() != null) {
                item.getRunnable().run();
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    // 点击回调接口
    public interface OnMainListItemClickListener {
        void onItemClicked(final MainMenuItem item);
    }
}
