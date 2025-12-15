package com.example.fjobs.utils;

import com.example.fjobs.models.Message;
import com.example.fjobs.models.StandardChatMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatUtils {
    
    public static Message parseChatMessage(String jsonMessage) {
        try {
            // Thử parse như StandardChatMessage đầu tiên
            com.google.gson.Gson gson = new com.google.gson.Gson();
            StandardChatMessage standardMsg = gson.fromJson(jsonMessage, StandardChatMessage.class);

            if (standardMsg != null) {
                // Tạo Message object từ StandardChatMessage
                Message message = new Message();
                message.setContent(standardMsg.getContent());
                message.setSenderId(standardMsg.getSenderId() != null ? standardMsg.getSenderId() : 0);
                message.setReceiverId(standardMsg.getReceiverId() != null ? standardMsg.getReceiverId() : 0);

                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Nếu không parse được như StandardChatMessage, thử phương pháp cũ với JSONObject
        try {
            JSONObject jsonObject = new JSONObject(jsonMessage);

            String type = jsonObject.optString("type", "");
            String content = jsonObject.optString("content", "");
            String sender = jsonObject.optString("sender", "");
            String receiver = jsonObject.optString("receiver", "");

            // Tạo Message object
            Message message = new Message();
            message.setContent(content);

            try {
                message.setSenderId(Integer.parseInt(sender));
                message.setReceiverId(Integer.parseInt(receiver));
            } catch (NumberFormatException e) {
                // Nếu không parse được ID, có thể là username, bỏ qua hoặc xử lý theo cách khác
                // Trong trường hợp này, chúng ta sẽ bỏ qua
            }

            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String createChatMessageJson(int senderId, int receiverId, String content) {
        StandardChatMessage standardMsg = new StandardChatMessage();
        standardMsg.setSenderId(senderId);
        standardMsg.setReceiverId(receiverId);
        standardMsg.setContent(content);
        standardMsg.setType("CHAT");

        // Sử dụng Gson để tạo JSON đúng cấu trúc StandardChatMessage
        com.google.gson.Gson gson = new com.google.gson.Gson();
        return gson.toJson(standardMsg);
    }
}