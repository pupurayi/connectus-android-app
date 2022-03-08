package com.connectus.mobile.ui.siba.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.DetailedChatMessage;

import java.util.List;


public class SibaMessagesRecyclerAdapter extends RecyclerView.Adapter {
    private static final String TAG = SibaMessagesRecyclerAdapter.class.getSimpleName();

    private static final int INCOMING = 0;
    private static final int OUTGOING = 1;

    private Context context;
    private final List<DetailedChatMessage> detailedChatMessages;
    private final ProfileDTO profileDTO;

    public SibaMessagesRecyclerAdapter(Context context, List<DetailedChatMessage> detailedChatMessages, ProfileDTO profileDTO) {
        this.context = context;
        this.detailedChatMessages = detailedChatMessages;
        this.profileDTO = profileDTO;
    }

    @Override
    public int getItemCount() {
        return detailedChatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        DetailedChatMessage message = (DetailedChatMessage) detailedChatMessages.get(position);
        if (profileDTO.getProfileId().equals(message.getSender().getProfileId())) {
            // If the current user is the sender of the message
            return OUTGOING;
        } else {
            // If some other user sent the message
            return INCOMING;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == OUTGOING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outgoing_message_list, parent, false);
            return new OutgoingMessageViewHolder(view);
        } else if (viewType == INCOMING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incoming_message_list, parent, false);
            return new IncomingMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DetailedChatMessage detailedChatMessage = (DetailedChatMessage) detailedChatMessages.get(position);
        switch (holder.getItemViewType()) {
            case OUTGOING:
                ((OutgoingMessageViewHolder) holder).bind(detailedChatMessage);
                break;
            case INCOMING:
                ((IncomingMessageViewHolder) holder).bind(detailedChatMessage);
                break;
        }
    }

    class OutgoingMessageViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDate, textViewMessage, textViewTimestamp;

        public OutgoingMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = (TextView) itemView.findViewById(R.id.text_view_outgoing_date);
            textViewMessage = (TextView) itemView.findViewById(R.id.text_view_outgoing_message);
            textViewTimestamp = (TextView) itemView.findViewById(R.id.text_view_outgoing_timestamp);
        }

        void bind(DetailedChatMessage detailedChatMessage) {
            textViewDate.setText(Common.getFormattedTime("MMM dd", detailedChatMessage.getCreatedAt()));
            textViewMessage.setText(detailedChatMessage.getMessage());
            textViewTimestamp.setText(Common.getFormattedTime("HH:mm", detailedChatMessage.getCreatedAt()));
        }
    }

    class IncomingMessageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewIncomingMessageAvatar;
        TextView textViewDate, textViewFirstName, textViewMessage, textViewTimestamp;

        public IncomingMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = (TextView) itemView.findViewById(R.id.text_view_incoming_date);
            imageViewIncomingMessageAvatar = (ImageView) itemView.findViewById(R.id.image_view_incoming_message_avatar);
            textViewFirstName = (TextView) itemView.findViewById(R.id.text_view_incoming_message_first_name);
            textViewMessage = (TextView) itemView.findViewById(R.id.text_view_incoming_message);
            textViewTimestamp = (TextView) itemView.findViewById(R.id.text_view_incoming_message_timestamp);
        }

        void bind(DetailedChatMessage detailedChatMessage) {
            textViewDate.setText(Common.getFormattedTime("MMM dd", detailedChatMessage.getCreatedAt()));
            Common.loadAvatar(true, imageViewIncomingMessageAvatar, detailedChatMessage.getSender().getUserId());
            String firstName = String.valueOf(detailedChatMessage.getSender().getNickName());
            textViewFirstName.setText(firstName);
            textViewMessage.setText(detailedChatMessage.getMessage());
            textViewTimestamp.setText(Common.getFormattedTime("HH:mm", detailedChatMessage.getCreatedAt()));
        }
    }
}


