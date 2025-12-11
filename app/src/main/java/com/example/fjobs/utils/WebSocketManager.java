package com.example.fjobs.utils;

import android.util.Log;
import androidx.annotation.NonNull;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private WebSocketClient webSocketClient;
    private static WebSocketManager instance;
    private WebSocketListener listener;
    private String serverUrl;
    private boolean isConnected = false;

    public interface WebSocketListener {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(String message);
        void onError(String error);
    }

    private WebSocketManager() {
        // Cập nhật URL WebSocket theo cấu hình của bạn
        // Nếu gặp lỗi HTTP 302, có thể server yêu cầu sử dụng WSS (WebSocket Secure) thay vì WS
        this.serverUrl = "ws://192.168.102.19:8080/ws";
    }

    // Thêm phương thức để thiết lập URL WebSocket tùy chỉnh
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // Trả về URL hiện tại để kiểm tra
    public String getServerUrl() {
        return serverUrl;
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void setWebSocketListener(WebSocketListener listener) {
        this.listener = listener;
    }

    public void connect(String token) {
        if (isConnected) {
            Log.d(TAG, "WebSocket đã kết nối rồi");
            return;
        }

        try {
            URI uri = new URI(serverUrl);
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "WebSocket kết nối thành công");
                    isConnected = true;
                    if (listener != null) {
                        listener.onConnected();
                    }
                    // Gửi token xác thực
                    send("AUTH:" + token);
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "Nhận được tin nhắn: " + message);
                    if (listener != null) {
                        listener.onMessageReceived(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket đóng kết nối - Mã: " + code + ", Lý do: " + reason + ", Remote: " + remote);
                    isConnected = false;
                    if (listener != null) {
                        listener.onDisconnected();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "Lỗi WebSocket: " + ex.getMessage());
                    // Ghi log chi tiết nếu có lỗi 302
                    if (ex.getMessage() != null && ex.getMessage().contains("302")) {
                        Log.e(TAG, "Lỗi chuyển hướng HTTP 302 - Kiểm tra lại URL WebSocket hoặc cấu hình server");
                    }
                    isConnected = false;
                    if (listener != null) {
                        listener.onError(ex.getMessage());
                    }
                }
            };

            webSocketClient.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "URI không hợp lệ: " + e.getMessage());
            if (listener != null) {
                listener.onError("URI không hợp lệ: " + e.getMessage());
            }
        }
    }

    public void disconnect() {
        if (webSocketClient != null && isConnected) {
            webSocketClient.close();
            isConnected = false;
        }
    }

    public void sendMessage(String message) {
        if (webSocketClient != null && isConnected) {
            webSocketClient.send(message);
        } else {
            Log.e(TAG, "WebSocket chưa kết nối");
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}