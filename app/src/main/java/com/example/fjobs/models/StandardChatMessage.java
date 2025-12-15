package com.example.fjobs.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class StandardChatMessage {
    @SerializedName("messageId")
    private Integer messageId;

    @SerializedName("senderId")
    private Integer senderId;

    @SerializedName("senderUsername")
    private String senderUsername;

    @SerializedName("senderDisplayName")
    private String senderDisplayName;

    @SerializedName("receiverId")
    private Integer receiverId;

    @SerializedName("receiverUsername")
    private String receiverUsername;

    @SerializedName("receiverDisplayName")
    private String receiverDisplayName;

    @SerializedName("content")
    private String content;

    @SerializedName("sendTime")  // Trường này nhận dữ liệu thời gian từ JSON (tương ứng với LocalDateTime trong backend)
    private String sendTimeJson; // Trường này nhận dữ liệu JSON từ backend, sẽ là chuỗi ISO8601

    private Date parsedSendTime; // Được parse từ sendTimeJson khi cần (đổi tên để tránh xung đột)

    @SerializedName("isRead")
    private Boolean isRead;

    @SerializedName("type")
    private String type; // CHAT, JOIN, LEAVE

    public StandardChatMessage() {}

    public StandardChatMessage(Integer senderId, String senderUsername, String senderDisplayName,
                              Integer receiverId, String receiverUsername, String receiverDisplayName,
                              String content, String type) {
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderDisplayName = senderDisplayName;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
        this.receiverDisplayName = receiverDisplayName;
        this.content = content;
        this.type = type;
    }

    // Getters and Setters
    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverDisplayName() {
        return receiverDisplayName;
    }

    public void setReceiverDisplayName(String receiverDisplayName) {
        this.receiverDisplayName = receiverDisplayName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTimeString() {
        return sendTimeJson;
    }

    public void setSendTimeString(String sendTimeString) {
        this.sendTimeJson = sendTimeString;
        // Khi có chuỗi mới, đặt lại Date để parse lại khi cần
        this.parsedSendTime = null;
    }

    public Date getSendTime() {
        if (parsedSendTime == null && sendTimeJson != null) {
            try {
                // Thử các định dạng ngày giờ khác nhau (backend trả về LocalDateTime theo định dạng ISO8601)
                String[] formats = {
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    "MMM dd, yyyy h:mm:ss a"  // Định dạng có thể từ Java toString()
                };

                for (String format : formats) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, java.util.Locale.getDefault());
                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                        parsedSendTime = sdf.parse(sendTimeJson);
                        break;
                    } catch (java.text.ParseException e) {
                        // Thử định dạng tiếp theo
                        continue;
                    }
                }

                // Nếu tất cả định dạng đều thất bại, thử parse với ISO8601 nếu có
                if (parsedSendTime == null) {
                    try {
                        parsedSendTime = java.text.DateFormat.getDateTimeInstance().parse(sendTimeJson);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parsedSendTime;
    }

    public void setSendTime(Date sendTime) {
        this.parsedSendTime = sendTime;
        // Nếu có Date, cập nhật lại chuỗi để đảm bảo đồng bộ
        if (sendTime != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            this.sendTimeJson = sdf.format(sendTime);
        }
    }

    public String getSendTimeJson() {
        return sendTimeJson;
    }

    public void setSendTimeJson(String sendTimeJson) {
        this.sendTimeJson = sendTimeJson;
        // Khi có chuỗi mới, đặt lại Date để parse lại khi cần
        this.parsedSendTime = null;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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