package com.example.unibazaar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatUserViewHolder> {

    private Context context;
    private List<ChatUser> chatUserList;

    public ChatListAdapter(Context context, List<ChatUser> chatUserList) {
        this.context = context;
        this.chatUserList = chatUserList;
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_user, parent, false);
        return new ChatUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        ChatUser chatUser = chatUserList.get(position);

        holder.textViewEmail.setText(chatUser.getOtherUserEmail());
        holder.textViewLastMessage.setText(chatUser.getLastMessage());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("otherUserId", chatUser.getOtherUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }

    public static class ChatUserViewHolder extends RecyclerView.ViewHolder {

        TextView textViewEmail, textViewLastMessage;

        public ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewEmail = itemView.findViewById(R.id.textViewChatUserEmail);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
        }
    }
}
