package com.example.fjobs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fjobs.R;
import com.example.fjobs.adapters.MessageAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.Message;
import com.example.fjobs.utils.ChatUtils;
import com.example.fjobs.utils.SessionManager;
import com.example.fjobs.utils.WebSocketManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity implements WebSocketManager.WebSocketListener {

    private RecyclerView recyclerMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText etMessage;
    private Button btnSend;
    private ImageButton btnBack;
    private TextView tvToolbarTitle;
    private ApiService apiService;
    private SessionManager sessionManager;
    private WebSocketManager webSocketManager;

    private int otherUserId;
    private String otherUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        initViews();
        getIncomingData();
        setupRecyclerView();
        setupWebSocket();
        loadConversation();
        setupClickListeners();
    }

    private void initViews() {
        recyclerMessages = findViewById(R.id.recycler_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);
        webSocketManager = WebSocketManager.getInstance();
    }

    private void getIncomingData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            otherUserId = bundle.getInt("OTHER_USER_ID", -1);
            otherUserName = bundle.getString("OTHER_USER_NAME", "Người dùng");
        }

        tvToolbarTitle.setText(otherUserName);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        int currentUserId = sessionManager.getUserId();
        messageAdapter = new MessageAdapter(messageList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Tương đương với android:stackFromEnd="true"
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(messageAdapter);
    }

    private void setupWebSocket() {
        webSocketManager.setWebSocketListener(this);
        String token = sessionManager.getToken();
        if (!token.isEmpty()) {
            webSocketManager.connect(token);
        } else {
            Toast.makeText(this, "Không thể kết nối WebSocket: chưa có token", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadConversation() {
        Call<ApiResponse> call = apiService.getConversation(otherUserId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Deserialize dữ liệu từ JSON sang List<Message>
                    Object data = response.body().getData();
                    List<Message> messages = null;
                    if (data instanceof List) {
                        List<Object> rawList = (List<Object>) data;
                        messages = new java.util.ArrayList<>();
                        for (Object item : rawList) {
                            // Chuyển đổi từng item trong list sang Message
                            String jsonItem = new com.google.gson.Gson().toJson(item);
                            Message message = new com.google.gson.Gson().fromJson(jsonItem, Message.class);
                            messages.add(message);
                        }
                    }
                    if (messages != null) {
                        messageList.clear();
                        messageList.addAll(messages);
                        messageAdapter.notifyDataSetChanged();

                        // Cuộn đến tin nhắn cuối cùng
                        if (!messageList.isEmpty()) {
                            recyclerMessages.scrollToPosition(messageList.size() - 1);
                        }
                    }
                } else {
                    Toast.makeText(ChatDetailActivity.this, "Không thể tải lịch sử trò chuyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            webSocketManager.disconnect();
            finish();
        });

        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String messageContent = etMessage.getText().toString().trim();
        if (messageContent.isEmpty()) {
            return;
        }

        // Tạo đối tượng tin nhắn để hiển thị ngay lập tức
        int currentUserId = sessionManager.getUserId();
        Message newMessage = new Message(currentUserId, otherUserId, messageContent);

        // Thêm vào danh sách và cập nhật giao diện
        messageList.add(newMessage);
        messageAdapter.addMessage(newMessage);

        // Cuộn đến tin nhắn mới
        recyclerMessages.scrollToPosition(messageList.size() - 1);

        // Xóa nội dung ô nhập
        etMessage.setText("");

        // Gửi tin nhắn qua WebSocket nếu kết nối
        if (webSocketManager.isConnected()) {
            String chatMessageJson = createChatMessageJson(currentUserId, otherUserId, messageContent);
            webSocketManager.sendMessage(chatMessageJson);
        } else {
            // Nếu không có WebSocket, gửi qua HTTP API
            sendHttpMessage(currentUserId, otherUserId, messageContent);
        }
    }

    private void sendHttpMessage(int senderId, int receiverId, String content) {
        // Tạo Map để chứa dữ liệu tin nhắn
        java.util.Map<String, Object> messageData = new java.util.HashMap<>();
        messageData.put("receiverId", receiverId);
        messageData.put("content", content);

        Call<ApiResponse> call = apiService.sendMessage(messageData);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ChatDetailActivity.this, "Tin nhắn đã được gửi thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatDetailActivity.this, "Không thể gửi tin nhắn qua API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, "Lỗi gửi tin nhắn: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String createChatMessageJson(int senderId, int receiverId, String content) {
        // Sử dụng ChatUtils để tạo JSON theo định dạng mà backend ChatController hỗ trợ
        return ChatUtils.createChatMessageJson(senderId, receiverId, content);
    }

    @Override
    public void onConnected() {
        runOnUiThread(() -> Toast.makeText(this, "Kết nối chat thời gian thực đã sẵn sàng", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDisconnected() {
        runOnUiThread(() -> Toast.makeText(this, "Mất kết nối chat thời gian thực", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onMessageReceived(String message) {
        // Xử lý tin nhắn nhận được từ WebSocket
        runOnUiThread(() -> {
            // Parse tin nhắn JSON nhận được
            Message parsedMessage = ChatUtils.parseChatMessage(message);

            if (parsedMessage != null) {
                // Kiểm tra xem tin nhắn có phải từ người đang trò chuyện không
                if (parsedMessage.getSenderId() == otherUserId || parsedMessage.getReceiverId() == sessionManager.getUserId()) {
                    // Reload lại cuộc trò chuyện để cập nhật tất cả tin nhắn mới
                    // (cách này đơn giản và hiệu quả hơn là thêm từng tin nhắn riêng lẻ)
                    loadConversation();
                    Toast.makeText(this, "Nhận được tin nhắn mới", Toast.LENGTH_SHORT).show();
                } else if (parsedMessage.getSenderId() == sessionManager.getUserId()) {
                    // Đây là tin nhắn đã gửi thành công
                    Toast.makeText(this, "Tin nhắn đã được gửi thành công", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Nếu không parse được tin nhắn, vẫn reload để đảm bảo đồng bộ
                loadConversation();
            }
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, "Lỗi kết nối chat: " + error, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketManager.isConnected()) {
            webSocketManager.disconnect();
        }
    }
}