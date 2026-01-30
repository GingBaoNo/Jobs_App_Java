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
import com.example.fjobs.utils.ServerConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import de.hdodenhof.circleimageview.CircleImageView; // Import CircleImageView
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
        private CircleImageView ivAvatar; // CircleImageView cho avatar
        private TextView tvUsername;
        private TextView tvLastMessage;
        private TextView tvTime;
        private TextView tvUnreadCount;

        public ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivAvatar = itemView.findViewById(R.id.iv_avatar); // Ánh xạ CircleImageView
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

            // Load avatar từ URL nếu có, nếu không thì hiển thị placeholder/error drawable
            String avatarUrl = chatUser.getUser().getUrlAnhDaiDien(); // Giả sử có field này trong User
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                // Nếu URL là đường dẫn tương đối, thêm baseUrl
                if (avatarUrl.startsWith("/")) {
                    avatarUrl = ServerConfig.getBaseUrl() + avatarUrl;
                }
                Glide.with(itemView.getContext())
                    .load(avatarUrl)
                    .placeholder(R.drawable.default_avatar) // Ảnh mặc định nếu load lỗi hoặc chưa xong
                    .error(R.drawable.default_avatar)       // Ảnh mặc định nếu lỗi
                    .apply(RequestOptions.bitmapTransform(new CenterCrop()).transform(new RoundedCorners(50))) // Bo góc
                    .into(ivAvatar);
            } else {
                // Nếu không có URL, Glide sẽ tự động hiển thị placeholder/error drawable
                // Không cần xử lý TextView nữa
                Glide.with(itemView.getContext())
                    .load(R.drawable.default_avatar) // Load placeholder mặc định
                    .apply(RequestOptions.bitmapTransform(new CenterCrop()).transform(new RoundedCorners(50))) // Bo góc
                    .into(ivAvatar);
            }

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