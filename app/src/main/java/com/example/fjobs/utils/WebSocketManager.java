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
    private boolean hasAttemptedConnection = false; // Theo dõi xem đã cố gắng kết nối hay chưa
    private String lastToken; // Lưu token để thử lại kết nối

    public interface WebSocketListener {
        void onConnected();
        void onDisconnected();
        void onMessageReceived(String message);
        void onReloadNotification(String reason);
        void onError(String error);
    }

    private WebSocketManager() {
        // Cập nhật URL WebSocket theo cấu hình của bạn
        // Nếu gặp lỗi HTTP 302, có thể server yêu cầu sử dụng WSS (WebSocket Secure) thay vì WS
        // hoặc endpoint chưa được cấu hình đúng trên backend
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

        // Lưu token để sử dụng cho việc thử lại kết nối
        this.lastToken = token;

        try {
            // Cố gắng xây dựng URI với token xác thực nếu cần thiết
            String fullUrl = serverUrl;
            // Một số server yêu cầu token trong URL, nhưng nếu không thì có thể gửi sau
            URI uri = new URI(fullUrl);
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "WebSocket kết nối thành công");
                    isConnected = true;
                    hasAttemptedConnection = true;
                    if (listener != null) {
                        listener.onConnected();
                    }
                    // Gửi token xác thực sau khi kết nối thành công
                    if (token != null && !token.isEmpty()) {
                        send("AUTH:" + token);
                        Log.d(TAG, "Gửi token xác thực: " + token);
                    }
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "Nhận được tin nhắn: " + message);
                    if (listener != null) {
                        // Kiểm tra nếu đây là thông báo reload
                        if (message.startsWith("USER_UPDATE:")) {
                            String reason = message.substring("USER_UPDATE:".length());
                            listener.onReloadNotification(reason);
                        } else {
                            listener.onMessageReceived(message);
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "WebSocket đóng kết nối - Mã: " + code + ", Lý do: " + reason + ", Remote: " + remote);
                    isConnected = false;
                    // Chỉ thông báo nếu không phải do lỗi kết nối ban đầu (HTTP 302)
                    // để tránh thông báo lỗi ngay khi vào chat
                    if (code != 1002 || !reason.toLowerCase().contains("302")) {
                        if (listener != null) {
                            listener.onDisconnected();
                        }
                    } else {
                        // Trong trường hợp lỗi 302, không thông báo ngay mà chỉ ghi log
                        Log.d(TAG, "Không thông báo ngắt kết nối do lỗi 302 - sử dụng fallback HTTP");
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "Lỗi WebSocket: " + ex.getMessage(), ex);
                    // Ghi log chi tiết nếu có lỗi 302
                    if (ex.getMessage() != null && (ex.getMessage().contains("302") ||
                        ex.getMessage().toLowerCase().contains("redirect"))) {
                        Log.e(TAG, "Lỗi chuyển hướng HTTP 302 - Có thể server yêu cầu xác thực HTTP trước khi kết nối WebSocket");
                        Log.e(TAG, "Đảm bảo bạn sử dụng đúng endpoint WebSocket thuần, không phải STOMP endpoint");
                        Log.e(TAG, "Backend hiện tại có thể đang sử dụng STOMP tại endpoint '" + serverUrl + "'");

                        // Kiểm tra nếu endpoint sử dụng STOMP (có thể cần endpoint khác cho WebSocket thuần)
                        if (serverUrl.contains("/ws") && serverUrl.contains("192.168.102.19")) {
                            Log.e(TAG, "Backend có vẻ đang sử dụng STOMP. Endpoint WebSocket thuần có thể là /websocket hoặc endpoint khác.");
                        }

                        // Không gọi listener.onError cho lỗi 302 để tránh thông báo cho người dùng
                        // vì ứng dụng đã có cơ chế HTTP fallback
                        return;
                    }
                    isConnected = false;
                    if (listener != null) {
                        listener.onError(ex.getMessage());
                    }
                }
            };

            // Thiết lập header tùy chọn nếu cần thiết để hỗ trợ xác thực
            webSocketClient.setConnectionLostTimeout(0); // Không tự ngắt kết nối

            // Thêm header Authorization nếu có token
            if (token != null && !token.isEmpty()) {
                webSocketClient.addHeader("Authorization", "Bearer " + token);
                Log.d(TAG, "Thêm header Authorization cho WebSocket");
            }

            webSocketClient.addHeader("Connection", "Upgrade");
            webSocketClient.addHeader("Upgrade", "websocket");
            webSocketClient.addHeader("Sec-WebSocket-Version", "13");

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

    /**
     * Thử kết nối lại WebSocket với token đã lưu
     */
    public void reconnect() {
        if (lastToken != null) {
            Log.d(TAG, "Thử kết nối lại WebSocket...");
            connect(lastToken);
        } else {
            Log.w(TAG, "Không thể kết nối lại - không có token được lưu");
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