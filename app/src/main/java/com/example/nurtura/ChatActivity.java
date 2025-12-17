package com.example.nurtura;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.adapter.ChatMessageAdapter;
import com.example.nurtura.auth.UserRepository;
import com.example.nurtura.model.ChatMessage;
import com.example.nurtura.repository.ChatRepository;
import com.example.nurtura.utils.WrapContentLinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChatMessages;
    private EditText etMessageInput;
    private FloatingActionButton btnSend;
    private ImageButton btnBack;
    private TextView txtChatUserName;
    private ImageView imgChatAvatar;

    private ChatRepository chatRepository;
    private UserRepository userRepository;
    private ChatMessageAdapter adapter;
    private String currentUserId;
    private String otherUserId;
    private String chatId;
    private String otherUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatRepository = new ChatRepository();
        currentUserId = FirebaseAuth.getInstance().getUid();
        userRepository = new UserRepository();

        chatId = getIntent().getStringExtra("CHAT_ID");
        otherUserId = getIntent().getStringExtra("OTHER_USER_ID");

        userRepository.getUserByUid(otherUserId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                Object name = userData.get("name");
                otherUserName = name != null ? name.toString() : "Unknown User";
                txtChatUserName.setText(otherUserName);
            }

            @Override
            public void onNotFound() {
                Toast.makeText(
                        ChatActivity.this,
                        "User not found",
                        Toast.LENGTH_SHORT
                ).show();

                txtChatUserName.setText("Unknown User");

                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ChatActivity", "Failed to load user", e);

                Toast.makeText(
                        ChatActivity.this,
                        "Failed to load user. Please try again.",
                        Toast.LENGTH_SHORT
                ).show();

                txtChatUserName.setText("Chat");

                finish();
            }
        });

        if (chatId == null || currentUserId == null) {
            Toast.makeText(this, "Error: Chat ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerChatMessages = findViewById(R.id.recyclerChatMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        txtChatUserName = findViewById(R.id.txtChatUserName);
        imgChatAvatar = findViewById(R.id.imgChatAvatar);

        btnBack.setOnClickListener(v -> finish());

        setupChatAdapter();

        btnSend.setOnClickListener(v -> {
            String text = etMessageInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                sendMessageHelper(text);
            }
        });
    }

    private void setupChatAdapter() {
        Query query = chatRepository.getMessagesQuery(chatId);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();
        adapter = new ChatMessageAdapter(options, currentUserId);

        WrapContentLinearLayoutManager manager = new WrapContentLinearLayoutManager(this);
        manager.setStackFromEnd(true);

        recyclerChatMessages.setLayoutManager(manager);
        recyclerChatMessages.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = adapter.getItemCount();
                int lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition();

                // If the user is close to the bottom of the list or the list is loaded for the first time
                // scroll to the bottom to show the new message
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerChatMessages.scrollToPosition(positionStart);
                }
            }
        });
    }

    private void sendMessageHelper(String text) {
        chatRepository.sendMessage(chatId, currentUserId, text, "text");

        etMessageInput.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}