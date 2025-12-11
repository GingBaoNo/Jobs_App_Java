package com.example.fjobs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fjobs.R;
import com.example.fjobs.models.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private int currentUserId;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public MessageAdapter(List<Message> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (messages != null && position >= 0 && position < messages.size()) {
            Message message = messages.get(position);
            if (message != null) {
                if (holder instanceof SentMessageViewHolder) {
                    ((SentMessageViewHolder) holder).bind(message);
                } else if (holder instanceof ReceivedMessageViewHolder) {
                    ((ReceivedMessageViewHolder) holder).bind(message);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages != null && position >= 0 && position < messages.size()) {
            Message message = messages.get(position);
            if (message != null && message.getSenderId() == currentUserId) {
                return VIEW_TYPE_SENT;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }
        return VIEW_TYPE_RECEIVED; // Mặc định là tin nhắn nhận được
    }

    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvTime;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

        public void bind(Message message) {
            tvMessage.setText(message.getContent());
            tvTime.setText(message.getDisplayTime());
        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvTime;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
        }

        public void bind(Message message) {
            tvMessage.setText(message.getContent());
            tvTime.setText(message.getDisplayTime());
        }
    }
}