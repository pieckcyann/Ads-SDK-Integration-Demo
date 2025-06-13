package com.xiaoyou.adsdkIntegration.demoapp.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoyou.adsdkIntegration.demoapp.R;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdLoader;
import com.xiaoyou.adsdkIntegration.demoapp.data.AdMenuItem;

import java.util.List;

public class BaseGridCardAdapter extends RecyclerView.Adapter<BaseGridCardAdapter.ViewHolder> {

    private final List<AdMenuItem> cardItems;
    private final OnAdActionListener listener;
    private final LayoutInflater inflater;

    public BaseGridCardAdapter(List<AdMenuItem> cardItems, final BaseGridCardAdapter.OnAdActionListener listener, final Context context) {
        this.cardItems = cardItems;
        this.listener = listener;
        this.inflater = ContextCompat.getSystemService(context, LayoutInflater.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_grid_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdMenuItem curCardItems = cardItems.get(position);
        String title = curCardItems.getTitle();
        holder.titleText.setText(title);

        holder.loadAdButton.setOnClickListener(v -> listener.onLoadClicked(curCardItems.getAdLoader()));
        holder.showAdButton.setOnClickListener(v -> listener.onShowClicked(curCardItems.getAdLoader()));
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }


    public interface OnAdActionListener {
        void onLoadClicked(final AdLoader itemAdLoad);

        void onShowClicked(final AdLoader itemAdLoad);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        Button loadAdButton;
        Button showAdButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.title_text);
            loadAdButton = itemView.findViewById(R.id.load_ad_button);
            showAdButton = itemView.findViewById(R.id.show_ad_button);
        }
    }
}
