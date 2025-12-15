package com.example.fjobs.activities;

import android.os.Bundle;
import android.util.Log;
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
import com.example.fjobs.models.StandardChatMessage;
import com.example.fjobs.utils.ChatUtils;
import com.example.fjobs.utils.SessionManager;
import com.example.fjobs.utils.WebSocketManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Looper;

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

    // Thêm các trường cho việc kiểm tra tin nhắn mới định kỳ
    private Handler handler;
    private Runnable refreshRunnable;
    private static final int REFRESH_INTERVAL = 5000; // 5 giây
    private boolean isCheckingForNewMessages = false;

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
        setupMessageRefresh();
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
                    // Deserialize dữ liệu từ JSON sang List<StandardChatMessage>
                    Object data = response.body().getData();
                    List<StandardChatMessage> standardMessages = null;
                    if (data instanceof List) {
                        List<Object> rawList = (List<Object>) data;
                        standardMessages = new java.util.ArrayList<>();
                        for (Object item : rawList) {
                            // Chuyển đổi từng item trong list sang StandardChatMessage
                            String jsonItem = new com.google.gson.Gson().toJson(item);
                            StandardChatMessage standardMessage = new com.google.gson.Gson().fromJson(jsonItem, StandardChatMessage.class);

                            // Đảm bảo thông tin người gửi và người nhận được xử lý đúng
                            if (standardMessage != null) {
                                standardMessages.add(standardMessage);
                            }
                        }
                    }
                    if (standardMessages != null) {
                        messageList.clear();
                        // Chuyển đổi StandardChatMessage sang Message để hiển thị
                        for (StandardChatMessage standardMsg : standardMessages) {
                            Message message = new Message();
                            message.setMessageId(standardMsg.getMessageId() != null ? standardMsg.getMessageId() : 0);
                            message.setSenderId(standardMsg.getSenderId() != null ? standardMsg.getSenderId() : 0);
                            message.setReceiverId(standardMsg.getReceiverId() != null ? standardMsg.getReceiverId() : 0);
                            message.setContent(standardMsg.getContent());
                            message.setRead(standardMsg.getIsRead() != null ? standardMsg.getIsRead() : false);
                            message.setSendTimeString(standardMsg.getSendTimeString());

                            messageList.add(message);
                        }
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

        int currentUserId = sessionManager.getUserId();

        // Gửi tin nhắn qua WebSocket nếu kết nối
        if (webSocketManager.isConnected()) {
            String chatMessageJson = createChatMessageJson(currentUserId, otherUserId, messageContent);
            webSocketManager.sendMessage(chatMessageJson);

            // Thêm tin nhắn vào giao diện ngay khi gửi qua WebSocket
            // Không thêm vào giao diện ngay ở đây để tránh hiển thị kép
            // Mà chỉ thêm khi nhận được phản hồi từ WebSocket hoặc sau khi gửi HTTP
        } else {
            // Nếu không có WebSocket, gửi qua HTTP API
            sendHttpMessage(currentUserId, otherUserId, messageContent);
        }

        // Xóa nội dung ô nhập
        etMessage.setText("");
    }

    private void sendHttpMessage(int senderId, int receiverId, String content) {
        // Tạo StandardChatMessage để gửi
        StandardChatMessage standardMsg = new StandardChatMessage();
        standardMsg.setSenderId(senderId);
        standardMsg.setReceiverId(receiverId);
        standardMsg.setContent(content);
        standardMsg.setType("CHAT");

        Call<ApiResponse> call = apiService.sendStandardMessage(standardMsg);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ChatDetailActivity.this, "Tin nhắn đã được gửi thành công", Toast.LENGTH_SHORT).show();
                    // Sau khi gửi thành công, tải lại cuộc trò chuyện để cập nhật giao diện
                    // Backend sẽ gửi thông báo reload qua WebSocket, nhưng cũng tải lại ngay để đảm bảo
                    loadConversation();
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
            try {
                // Parse tin nhắn JSON nhận được như StandardChatMessage
                com.google.gson.Gson gson = new com.google.gson.Gson();
                StandardChatMessage standardMsg = gson.fromJson(message, StandardChatMessage.class);

                if (standardMsg != null) {
                    // Kiểm tra xem tin nhắn có phải từ người đang trò chuyện hoặc là phản hồi cho tin nhắn đã gửi
                    if (standardMsg.getSenderId() == otherUserId || standardMsg.getReceiverId() == otherUserId) {
                        // Reload lại cuộc trò chuyện để cập nhật tất cả tin nhắn mới
                        // (cách này đơn giản và hiệu quả hơn là thêm từng tin nhắn riêng lẻ)
                        loadConversation();
                    }

                    // Thông báo người dùng tùy theo loại tin nhắn
                    if (standardMsg.getSenderId() == sessionManager.getUserId()) {
                        // Đây là tin nhắn đã gửi thành công
                        Toast.makeText(this, "Tin nhắn đã được gửi thành công", Toast.LENGTH_SHORT).show();
                    } else if (standardMsg.getSenderId() == otherUserId) {
                        // Đây là tin nhắn nhận được từ người khác
                        Toast.makeText(this, "Nhận được tin nhắn mới", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Nếu không parse được tin nhắn như StandardChatMessage, thử parse như Message cũ
                    Message parsedMessage = ChatUtils.parseChatMessage(message);
                    if (parsedMessage != null) {
                        if (parsedMessage.getSenderId() == otherUserId || parsedMessage.getReceiverId() == otherUserId) {
                            loadConversation();
                        }

                        if (parsedMessage.getSenderId() == sessionManager.getUserId()) {
                            Toast.makeText(this, "Tin nhắn đã được gửi thành công", Toast.LENGTH_SHORT).show();
                        } else if (parsedMessage.getSenderId() == otherUserId) {
                            Toast.makeText(this, "Nhận được tin nhắn mới", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Nếu không parse được tin nhắn, vẫn reload để đảm bảo đồng bộ
                        loadConversation();
                    }
                }
            } catch (Exception e) {
                // Nếu có lỗi parse, vẫn reload để đảm bảo đồng bộ
                loadConversation();
            }
        });
    }

    @Override
    public void onReloadNotification(String reason) {
        // Xử lý thông báo reload từ server
        runOnUiThread(() -> {
            if ("new_message".equals(reason)) {
                // Tải lại cuộc trò chuyện để cập nhật tin nhắn mới
                loadConversation();
                Log.d("ChatDetailActivity", "Đã nhận thông báo reload và tải lại cuộc trò chuyện");
            }
        });
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, "Lỗi kết nối chat: " + error, Toast.LENGTH_SHORT).show());
    }

    private void setupMessageRefresh() {
        handler = new Handler(Looper.getMainLooper());

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // Kiểm tra xem có nên tiếp tục kiểm tra không
                if (isCheckingForNewMessages) {
                    // Gọi lại API để tải cuộc trò chuyện mới
                    // Chỉ thực hiện nếu không có kết nối WebSocket đang hoạt động
                    // hoặc nếu có kết nối WebSocket nhưng không nhận được thông báo mới trong thời gian dài
                    if (!webSocketManager.isConnected()) {
                        loadConversation();
                    }
                    // Tiếp tục chu kỳ kiểm tra
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            }
        };

        // Bắt đầu kiểm tra
        isCheckingForNewMessages = true;
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Dừng kiểm tra khi activity bị pause
        isCheckingForNewMessages = false;
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tiếp tục kiểm tra khi activity trở lại
        if (!isCheckingForNewMessages) {
            isCheckingForNewMessages = true;
            if (handler != null && refreshRunnable != null) {
                handler.postDelayed(refreshRunnable, REFRESH_INTERVAL);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCheckingForNewMessages = false;
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
        if (webSocketManager.isConnected()) {
            webSocketManager.disconnect();
        }
    }
}