package com.example.fjobs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fjobs.R;
import com.example.fjobs.models.ChatUser;
import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder> {

    private List<ChatUser> chatUsers;
    private OnChatUserClickListener listener;

    public interface OnChatUserClickListener {
        void onChatUserClick(ChatUser chatUser);
    }

    public ChatUserAdapter(List<ChatUser> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public void setOnChatUserClickListener(OnChatUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_user, parent, false);
        return new ChatUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        ChatUser chatUser = chatUsers.get(position);
        holder.bind(chatUser);
    }

    @Override
    public int getItemCount() {
        return chatUsers != null ? chatUsers.size() : 0;
    }

    public void updateChatUsers(List<ChatUser> newChatUsers) {
        this.chatUsers = newChatUsers;
        notifyDataSetChanged();
    }

    public class ChatUserViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView tvAvatarInitial;
        private TextView tvUsername;
        private TextView tvLastMessage;
        private TextView tvTime;
        private TextView tvUnreadCount;

        public ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvAvatarInitial = itemView.findViewById(R.id.tv_avatar_initial);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvUnreadCount = itemView.findViewById(R.id.tv_unread_count);
        }

        public void bind(ChatUser chatUser) {
            // Thiết lập tên người dùng
            String displayName = chatUser.getUser().getTenHienThi() != null ? 
                chatUser.getUser().getTenHienThi() : chatUser.getUser().getTaiKhoan();
            tvUsername.setText(displayName);

            // Lấy ký tự đầu tiên để hiển thị trên avatar
            String firstChar = "?";
            if (displayName != null && !displayName.isEmpty()) {
                firstChar = String.valueOf(displayName.charAt(0)).toUpperCase();
            }
            tvAvatarInitial.setText(firstChar);

            // Thiết lập tin nhắn cuối cùng
            if (chatUser.getLastMessage() != null && !chatUser.getLastMessage().isEmpty()) {
                tvLastMessage.setText(chatUser.getLastMessage());
            } else {
                tvLastMessage.setText("Bắt đầu cuộc trò chuyện");
            }

            // Thiết lập thời gian
            if (chatUser.getLastMessageTime() != null) {
                tvTime.setText(chatUser.getDisplayTime());
            } else {
                tvTime.setText("");
            }

            // Thiết lập số tin nhắn chưa đọc
            if (chatUser.isUnread()) {
                tvUnreadCount.setVisibility(View.VISIBLE);
                tvUnreadCount.setText("1"); // Trong thực tế, có thể cần số lượng cụ thể
            } else {
                tvUnreadCount.setVisibility(View.GONE);
            }

            // Xử lý click item
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChatUserClick(chatUser);
                }
            });
        }
    }
}