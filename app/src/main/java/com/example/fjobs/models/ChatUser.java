package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class ChatUser {
    @SerializedName("user")
    private User user;

    @SerializedName("lastMessage")
    private String lastMessage;

    @SerializedName("lastMessageTime")
    private String lastMessageTimeString;

    private transient Date parsedLastMessageTime; // This will be populated by the getter when needed

    @SerializedName("unread")
    private boolean unread;

    // Constructors
    public ChatUser() {}

    public ChatUser(User user, String lastMessage, Date lastMessageTime, boolean unread) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.parsedLastMessageTime = lastMessageTime;
        this.unread = unread;
    }

    // Getters and setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTime() {
        if (parsedLastMessageTime == null && lastMessageTimeString != null) {
            try {
                // Chuyển đổi từ string sang date
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.getDefault());
                parsedLastMessageTime = sdf.parse(lastMessageTimeString);
            } catch (Exception e) {
                e.printStackTrace();
                // Thử định dạng khác nếu định dạng đầu tiên không thành công
                try {
                    java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", java.util.Locale.getDefault());
                    parsedLastMessageTime = sdf2.parse(lastMessageTimeString);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    // Thử định dạng cuối cùng
                    try {
                        java.text.SimpleDateFormat sdf3 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                        parsedLastMessageTime = sdf3.parse(lastMessageTimeString);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
        return parsedLastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.parsedLastMessageTime = lastMessageTime;
    }

    public String getLastMessageTimeString() {
        return lastMessageTimeString;
    }

    public void setLastMessageTimeString(String lastMessageTimeString) {
        this.lastMessageTimeString = lastMessageTimeString;
        // Khi có string mới, xóa date để khi getter sẽ parse lại
        this.parsedLastMessageTime = null;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public String getDisplayTime() {
        if (parsedLastMessageTime == null) {
            return "";
        }

        // Format thời gian để hiển thị (ví dụ: HH:mm nếu cùng ngày, dd/MM nếu khác ngày)
        // Logic đơn giản: nếu cùng ngày thì hiển thị giờ phút, nếu không thì hiển thị ngày/tháng
        java.text.SimpleDateFormat sdf;
        java.util.Calendar today = java.util.Calendar.getInstance();
        java.util.Calendar lastMsg = java.util.Calendar.getInstance();
        lastMsg.setTime(parsedLastMessageTime);

        if (today.get(java.util.Calendar.YEAR) == lastMsg.get(java.util.Calendar.YEAR) &&
            today.get(java.util.Calendar.DAY_OF_YEAR) == lastMsg.get(java.util.Calendar.DAY_OF_YEAR)) {
            sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        } else {
            sdf = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
        }

        return sdf.format(parsedLastMessageTime);
    }
}