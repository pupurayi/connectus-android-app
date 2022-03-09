package com.connectus.mobile.ui.offering;

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
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OfferingsRecyclerAdapter extends RecyclerView.Adapter<OfferingsRecyclerAdapter.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");


    private Context context;
    private final List<OfferingDto> notifications;

    public OfferingsRecyclerAdapter(Context context, List<OfferingDto> notifications) {
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
        OfferingDto offeringDto = notifications.get(position);
        holder.textViewNotificationTitle.setText(offeringDto.getName());
        holder.textViewNotificationMessage.setText(offeringDto.getDescription());
        holder.textViewSentTime.setText(offeringDto.getCreated());
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
