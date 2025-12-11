package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Message {
    @SerializedName("maTinNhan")
    private int messageId;

    @SerializedName("maNguoiGui")
    private int senderId;

    @SerializedName("maNguoiNhan")
    private int receiverId;

    @SerializedName("noiDung")
    private String content;

    @SerializedName("daDoc")
    private boolean read;

    @SerializedName("thoiGianGui")
    private String sendTimeString;

    private Date sendTime; // Được parse từ sendTimeString khi cần

    @SerializedName("sender")
    private User sender;

    @SerializedName("receiver")
    private User receiver;

    // Constructors
    public Message() {}

    public Message(int senderId, int receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.read = false;
        Date currentTime = new Date();
        this.sendTime = currentTime;
        // Cập nhật chuỗi thời gian từ Date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.getDefault());
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        this.sendTimeString = sdf.format(currentTime);
    }

    // Getters and setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getSendTime() {
        if (sendTime == null && sendTimeString != null) {
            try {
                // Thử các định dạng ngày giờ khác nhau
                String[] formats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss.SSS"
                };

                for (String format : formats) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, java.util.Locale.getDefault());
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                        sendTime = sdf.parse(sendTimeString);
                        break;
                    } catch (java.text.ParseException e) {
                        // Thử định dạng tiếp theo
                        continue;
                    }
                }

                // Nếu tất cả định dạng đều thất bại, thử parse với ISO8601 nếu có
                if (sendTime == null) {
                    // Xử lý trường hợp có timezone
                    try {
                        sendTime = java.text.DateFormat.getDateTimeInstance().parse(sendTimeString);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
        // Nếu có Date, cập nhật lại chuỗi để đảm bảo đồng bộ
        if (sendTime != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            this.sendTimeString = sdf.format(sendTime);
        }
    }

    public String getSendTimeString() {
        return sendTimeString;
    }

    public void setSendTimeString(String sendTimeString) {
        this.sendTimeString = sendTimeString;
        // Khi có chuỗi mới, đặt lại Date để parse lại khi cần
        this.sendTime = null;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getDisplayTime() {
        Date time = getSendTime(); // Sử dụng getter để đảm bảo parse đúng
        if (time == null) {
            return "";
        }

        // Format thời gian để hiển thị (giờ:phút)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        return sdf.format(time);
    }
}