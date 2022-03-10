package com.connectus.mobile.ui.goods_and_services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class GoodsAndServicesRecyclerAdapter extends RecyclerView.Adapter<com.connectus.mobile.ui.goods_and_services.GoodsAndServicesRecyclerAdapter.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");


    private Context context;
    private final List<GoodsAndServicesDto> notifications;

    public GoodsAndServicesRecyclerAdapter(Context context, List<GoodsAndServicesDto> notifications) {
        this.context = context;
        Collections.reverse(notifications);
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GoodsAndServicesDto new_goods_and_servicesDto = notifications.get(position);
        holder.textViewNotificationTitle.setText(new_goods_and_servicesDto.getName());
        holder.textViewNotificationMessage.setText(new_goods_and_servicesDto.getDescription());
        holder.textViewSentTime.setText(new_goods_and_servicesDto.getCreated());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewNotificationTitle, textViewNotificationMessage, textViewSentTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNotificationTitle = itemView.findViewById(R.id.text_view_notification_title);
            textViewNotificationMessage = itemView.findViewById(R.id.text_view_notification_message);
            textViewSentTime = itemView.findViewById(R.id.text_view_sent_time);
        }
    }
}

