/*
package com.applovin.enterprise.apps.demoapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.enterprise.apps.demoapp.R;
import com.applovin.enterprise.apps.demoapp.data.main.ListItem;
import com.applovin.enterprise.apps.demoapp.data.main.MainMenuItem;
import com.applovin.enterprise.apps.demoapp.data.main.SectionHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


// A {@link RecyclerView.Adapter} used to show a list of items on the main screen..
public class MainRecyclerViewAdapter
        extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
    private final List<ListItem> listItems;
    private final OnMainListItemClickListener clickListener;
    private final LayoutInflater inflater;
    public MainRecyclerViewAdapter(final Map<String, List<MainMenuItem>> groupedItems, final OnMainListItemClickListener clickListener, final Context context) {
        this.listItems = new ArrayList<>();
        this.clickListener = clickListener;
        this.inflater = ContextCompat.getSystemService(context, LayoutInflater.class);

        // 展开 Map
        for (Map.Entry<String, List<MainMenuItem>> entry : groupedItems.entrySet()) {
            // 先加组头
            listItems.add(new SectionHeader(entry.getKey()));

            // 加组内所有子项
            listItems.addAll(entry.getValue());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        @LayoutRes final int viewId;
        if (viewType == ListItem.TYPE_SECTION_HEADER) {
            viewId = R.layout.expandable_list_parent_item;
        } else if (viewType == ListItem.TYPE_AD_ITEM) {
            viewId = R.layout.expandable_list_child_item;
        } else {
            viewId = View.NO_ID;
        }

        return new ViewHolder(inflater.inflate(viewId, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ListItem item = listItems.get(position);
        if (item.getType() == ListItem.TYPE_SECTION_HEADER) {
            holder.title.setText(((SectionHeader) item).getTitle());
        } else if (item.getType() == ListItem.TYPE_AD_ITEM) {
            holder.title.setText(((MainMenuItem) item).getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return listItems.get(position).getType();
    }

    public interface OnMainListItemClickListener {
        void onItemClicked(final ListItem item);
    }

    class ViewHolder
            extends RecyclerView.ViewHolder {
        private final TextView title;

        ViewHolder(@NonNull final View itemView, int viewType) {
            super(itemView);

            title = itemView.findViewById(R.id.title);

            if (viewType == ListItem.TYPE_AD_ITEM) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final ListItem item = listItems.get(getAdapterPosition());
                        clickListener.onItemClicked(item);
                    }
                });
            }
        }
    }
}
*/
