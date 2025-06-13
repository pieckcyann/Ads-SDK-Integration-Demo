package com.xiaoyou.adsdkIntegration.demoapp.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.data.IntentMenuItem;

import java.util.List;


// A {@link RecyclerView.Adapter} used to show a list of items on the main screen..
public class BaseRecyclerViewAdapter
        extends RecyclerView.Adapter<BaseRecyclerViewAdapter.ViewHolder> {
    private final List<IntentMenuItem> listItems;
    private final OnMainListItemClickListener clickListener;
    private final LayoutInflater inflater;

    public BaseRecyclerViewAdapter(final List<IntentMenuItem> listItems, final OnMainListItemClickListener clickListener, final Context context) {
        this.listItems = listItems;
        this.clickListener = clickListener;
        this.inflater = ContextCompat.getSystemService(context, LayoutInflater.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_recycler_view, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.title.setText(listItems.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public interface OnMainListItemClickListener {
        void onItemClicked(final IntentMenuItem item);
    }

    class ViewHolder
            extends RecyclerView.ViewHolder {
        private final TextView title;

        ViewHolder(@NonNull final View itemView, int viewType) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final IntentMenuItem item = listItems.get(getAdapterPosition());
                    clickListener.onItemClicked(item);
                }
            });
        }
    }
}
