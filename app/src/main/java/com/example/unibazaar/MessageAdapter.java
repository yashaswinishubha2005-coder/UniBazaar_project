package com.example.unibazaar;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserId;

    public MessageAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.textViewMessage.setText(message.getMessageText());

        if (message.getSenderId().equals(currentUserId)) {
            holder.messageContainer.setGravity(Gravity.END);
            holder.textViewMessage.setBackgroundResource(R.drawable.message_sent_background);
            holder.textViewMessage.setTextColor(context.getColor(R.color.white));
        } else {
            holder.messageContainer.setGravity(Gravity.START);
            holder.textViewMessage.setBackgroundResource(R.drawable.message_received_background);
            holder.textViewMessage.setTextColor(context.getColor(R.color.ub_deep));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout messageContainer;
        TextView textViewMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }
}
