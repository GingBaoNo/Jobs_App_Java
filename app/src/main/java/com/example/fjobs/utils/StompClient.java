package com.example.fjobs.utils;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class StompClient {
    private static final String TAG = "StompClient";
    private WebSocketClient client;
    private String serverUrl;
    private StompListener listener;
    private boolean isConnected = false;
    private String subscriptionId = "sub-0";

    public interface StompListener {
        void onConnected();
        void onDisconnected();
        void onMessage(String destination, String message);
        void onError(String error);
    }

    public StompClient(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setListener(StompListener listener) {
        this.listener = listener;
    }

    public void connect(String token) {
        try {
            URI uri = new URI(serverUrl);
            Map<String, String> headers = new HashMap<>();
            
            if (token != null && !token.isEmpty()) {
                headers.put("Authorization", "Bearer " + token);
            }

            client = new WebSocketClient(uri, headers) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    Log.d(TAG, "WebSocket opened, sending STOMP CONNECT");
                    // Gửi STOMP CONNECT frame
                    sendStompFrame("CONNECT", 
                        "accept-version", "1.1,1.0",
                        "heart-beat", "10000,10000");
                }

                @Override
                public void onMessage(String message) {
                    Log.d(TAG, "Received STOMP message: " + message);
                    parseStompFrame(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(TAG, "STOMP closed - Code: " + code + ", Reason: " + reason);
                    isConnected = false;
                    if (listener != null) {
                        listener.onDisconnected();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e(TAG, "STOMP error: " + ex.getMessage());
                    isConnected = false;
                    if (listener != null) {
                        listener.onError(ex.getMessage());
                    }
                }
            };

            client.connect();
        } catch (URISyntaxException e) {
            Log.e(TAG, "Invalid URI: " + e.getMessage());
            if (listener != null) {
                listener.onError("Invalid URI: " + e.getMessage());
            }
        }
    }

    private void sendStompFrame(String command, String... headers) {
        if (client == null || !isConnected) {
            Log.e(TAG, "Cannot send STOMP frame - not connected");
            return;
        }

        StringBuilder frame = new StringBuilder();
        frame.append(command).append("\n");
        
        for (int i = 0; i < headers.length; i += 2) {
            if (i + 1 < headers.length) {
                frame.append(headers[i]).append(":").append(headers[i + 1]).append("\n");
            }
        }
        
        frame.append("\n").append("\u0000"); // Null byte terminator
        client.send(frame.toString());
        Log.d(TAG, "Sent STOMP frame: " + command);
    }

    public void subscribe(String destination) {
        subscriptionId = "sub-" + System.currentTimeMillis();
        sendStompFrame("SUBSCRIBE",
            "id", subscriptionId,
            "destination", destination);
        Log.d(TAG, "Subscribed to: " + destination);
    }

    public void send(String destination, String message) {
        sendStompFrame("SEND",
            "destination", destination,
            "content-type", "application/json");
        client.send(message + "\u0000");
        Log.d(TAG, "Sent message to: " + destination);
    }

    public void disconnect() {
        if (client != null && isConnected) {
            sendStompFrame("DISCONNECT");
            client.close();
            isConnected = false;
        }
    }

    private void parseStompFrame(String message) {
        if (message == null || message.isEmpty()) return;

        String[] parts = message.split("\n\n", 2);
        if (parts.length == 0) return;

        String[] lines = parts[0].split("\n");
        if (lines.length == 0) return;

        String command = lines[0].trim();
        Log.d(TAG, "STOMP command: " + command);

        switch (command) {
            case "CONNECTED":
                isConnected = true;
                Log.d(TAG, "STOMP connected successfully");
                if (listener != null) {
                    listener.onConnected();
                }
                break;
                
            case "MESSAGE":
                // Extract destination from headers
                String destination = "";
                for (String line : lines) {
                    if (line.startsWith("destination:")) {
                        destination = line.substring("destination:".length()).trim();
                        break;
                    }
                }
                
                // Get message body
                String body = parts.length > 1 ? parts[1] : "";
                // Remove null terminator if present
                if (body.endsWith("\u0000")) {
                    body = body.substring(0, body.length() - 1);
                }
                
                Log.d(TAG, "Received MESSAGE for destination: " + destination);
                if (listener != null) {
                    listener.onMessage(destination, body);
                }
                break;
                
            case "ERROR":
                Log.e(TAG, "STOMP error frame received");
                if (listener != null) {
                    listener.onError("STOMP ERROR");
                }
                break;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
