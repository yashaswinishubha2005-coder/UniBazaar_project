package com.example.unibazaar;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSend;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String currentUserId;
    private String otherUserId;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = currentUser.getUid();
        otherUserId = getIntent().getStringExtra("otherUserId");
        if (TextUtils.isEmpty(otherUserId)) {
            Toast.makeText(this, "Unable to open chat for this listing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (currentUserId.compareTo(otherUserId) < 0) {
            chatId = currentUserId + "_" + otherUserId;
        } else {
            chatId = otherUserId + "_" + currentUserId;
        }

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList, currentUserId);

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void sendMessage() {
        String text = editTextMessage.getText().toString().trim();

        if (TextUtils.isEmpty(text)) {
            editTextMessage.setError("Enter message");
            return;
        }

        Message message = new Message(
                currentUserId,
                otherUserId,
                text,
                System.currentTimeMillis()
        );

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    editTextMessage.setText("");
                    saveChatListForBothUsers(text);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ChatActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void saveChatListForBothUsers(String lastMessage) {
        db.collection("users").document(currentUserId).get().addOnSuccessListener(currentDoc -> {
            String currentEmailValue = currentDoc.getString("email");
            if (TextUtils.isEmpty(currentEmailValue) && mAuth.getCurrentUser() != null) {
                currentEmailValue = mAuth.getCurrentUser().getEmail();
            }
            if (TextUtils.isEmpty(currentEmailValue)) {
                currentEmailValue = "UniBazaar User";
            }
            final String currentEmail = currentEmailValue;

            db.collection("users").document(otherUserId).get().addOnSuccessListener(otherDoc -> {
                String otherEmail = otherDoc.getString("email");
                if (TextUtils.isEmpty(otherEmail)) {
                    otherEmail = "UniBazaar User";
                }

                Map<String, Object> currentUserChat = new HashMap<>();
                currentUserChat.put("otherUserId", otherUserId);
                currentUserChat.put("otherUserEmail", otherEmail);
                currentUserChat.put("lastMessage", lastMessage);

                Map<String, Object> otherUserChat = new HashMap<>();
                otherUserChat.put("otherUserId", currentUserId);
                otherUserChat.put("otherUserEmail", currentEmail);
                otherUserChat.put("lastMessage", lastMessage);

                db.collection("users")
                        .document(currentUserId)
                        .collection("chat_list")
                        .document(otherUserId)
                        .set(currentUserChat);

                db.collection("users")
                        .document(otherUserId)
                        .collection("chat_list")
                        .document(currentUserId)
                        .set(otherUserChat);
            });
        });
    }

    private void loadMessages() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(ChatActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    messageList.clear();

                    if (value != null) {
                        messageList.addAll(value.toObjects(Message.class));
                    }

                    messageAdapter.notifyDataSetChanged();

                    if (!messageList.isEmpty()) {
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                    }
                });
    }
}