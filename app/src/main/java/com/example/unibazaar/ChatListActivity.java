package com.example.unibazaar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChatUsers;
    private TextView textViewEmptyState;
    private List<ChatUser> chatUserList;
    private ChatListAdapter chatListAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerViewChatUsers = findViewById(R.id.recyclerViewChatUsers);
        textViewEmptyState = findViewById(R.id.textViewEmptyState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        chatUserList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(this, chatUserList);

        recyclerViewChatUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChatUsers.setAdapter(chatListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadChatUsers();
    }

    private void loadChatUsers() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("chat_list")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    chatUserList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ChatUser chatUser = document.toObject(ChatUser.class);
                        chatUserList.add(chatUser);
                    }

                    chatListAdapter.notifyDataSetChanged();
                    updateEmptyState();
                })
                .addOnFailureListener(e -> {
                    updateEmptyState();
                    Toast.makeText(ChatListActivity.this,
                            "Failed to load chats: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateEmptyState() {
        if (chatUserList.isEmpty()) {
            textViewEmptyState.setVisibility(View.VISIBLE);
            recyclerViewChatUsers.setVisibility(View.GONE);
        } else {
            textViewEmptyState.setVisibility(View.GONE);
            recyclerViewChatUsers.setVisibility(View.VISIBLE);
        }
    }
}