package com.example.fjobs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import com.example.fjobs.R;
import com.example.fjobs.fragments.ChatDetailFragment;
import com.example.fjobs.adapters.ChatUserAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ChatUser;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map;

public class ChatFragment extends Fragment implements ChatUserAdapter.OnChatUserClickListener {

    private RecyclerView recyclerChatList;
    private ChatUserAdapter chatUserAdapter;
    private List<ChatUser> chatUserList;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyTitle, tvEmptySubtitle;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initViews(view);
        setupRecyclerView();
        loadData();

        return view;
    }

    private void initViews(View view) {
        recyclerChatList = view.findViewById(R.id.recycler_chat_list);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        tvEmptyTitle = view.findViewById(R.id.tv_empty_title);
        tvEmptySubtitle = view.findViewById(R.id.tv_empty_subtitle);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(requireContext());
    }

    private void setupRecyclerView() {
        chatUserList = new ArrayList<>();
        chatUserAdapter = new ChatUserAdapter(chatUserList);
        chatUserAdapter.setOnChatUserClickListener(this);

        recyclerChatList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerChatList.setAdapter(chatUserAdapter);
    }

    private void loadData() {
        showLoading(true);

        // Xác định vai trò người dùng để gọi API phù hợp
        String userRole = sessionManager.getUserRole();

        if ("ADMIN".equals(userRole)) {
            // Admin: Lấy danh sách tất cả người dùng có thể trò chuyện
            Call<ApiResponse> call = apiService.getChatUsersForAdmin();
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    showLoading(false);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Xử lý dữ liệu trả về
                        List<ChatUser> chatUsers = (List<ChatUser>) response.body().getData();
                        if (chatUsers != null && !chatUsers.isEmpty()) {
                            chatUserList.clear();
                            chatUserList.addAll(chatUsers);
                            chatUserAdapter.notifyDataSetChanged();
                            showEmptyState(false);
                        } else {
                            showEmptyState(true);
                        }
                    } else {
                        showEmptyState(true);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    showLoading(false);
                    showEmptyState(true);
                }
            });
        } else if ("NTD".equals(userRole)) {
            // Nhà tuyển dụng: Lấy danh sách admin và ứng viên đã ứng tuyển
            loadEmployerChats();
        } else if ("NV".equals(userRole)) {
            // Người tìm việc: Lấy danh sách admin và NTD mà họ đã ứng tuyển
            Call<ApiResponse> call = apiService.getAvailableChatsForApplicant();
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    showLoading(false);
                    android.util.Log.d("ChatFragment", "Response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        android.util.Log.d("ChatFragment", "Response body: " + response.body().getMessage());
                        android.util.Log.d("ChatFragment", "Response success: " + response.body().isSuccess());

                        if (response.body().isSuccess()) {
                            // Xử lý dữ liệu trả về
                            Object data = response.body().getData();
                            android.util.Log.d("ChatFragment", "Raw data type: " + (data != null ? data.getClass().getName() : "null"));

                            // Deserialize dữ liệu từ JSON sang List<ChatUser>
                            List<ChatUser> chatUsers;
                            if (data instanceof List) {
                                List<Map<String, Object>> rawList = (List<Map<String, Object>>) data;
                                chatUsers = new java.util.ArrayList<>();
                                for (Map<String, Object> item : rawList) {
                                    // Chuyển đổi từng item trong list sang ChatUser
                                    ChatUser chatUser = new com.google.gson.Gson().fromJson(
                                        new com.google.gson.Gson().toJson(item),
                                        ChatUser.class
                                    );
                                    chatUsers.add(chatUser);
                                }
                            } else {
                                chatUsers = null;
                            }
                            if (chatUsers != null) {
                                android.util.Log.d("ChatFragment", "Number of chat users: " + chatUsers.size());

                                for (int i = 0; i < chatUsers.size(); i++) {
                                    ChatUser chatUser = chatUsers.get(i);
                                    if (chatUser != null && chatUser.getUser() != null) {
                                        android.util.Log.d("ChatFragment", "ChatUser " + i + ": " +
                                            chatUser.getUser().getTaiKhoan() +
                                            ", last message: " + chatUser.getLastMessage());
                                    } else {
                                        android.util.Log.d("ChatFragment", "ChatUser " + i + " is null or has no user");
                                    }
                                }

                                chatUserList.clear();
                                chatUserList.addAll(chatUsers);
                                chatUserAdapter.notifyDataSetChanged();
                                if (chatUsers.isEmpty()) {
                                    showEmptyState(true);
                                    tvEmptyTitle.setText("Chưa có cuộc trò chuyện");
                                    tvEmptySubtitle.setText("Bạn sẽ thấy người bạn có thể trò chuyện ở đây sau khi ứng tuyển");
                                } else {
                                    showEmptyState(false);
                                    android.util.Log.d("ChatFragment", "Chat users displayed successfully");
                                }
                            } else {
                                android.util.Log.d("ChatFragment", "Chat users list is null");
                                showEmptyState(true);
                            }
                        } else {
                            android.util.Log.d("ChatFragment", "Response not successful: " + response.body().getMessage());
                            showEmptyState(true);
                        }
                    } else {
                        android.util.Log.d("ChatFragment", "Response not successful - Code: " + response.code());
                        showEmptyState(true);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    showLoading(false);
                    showEmptyState(true);
                    tvEmptyTitle.setText("Lỗi kết nối");
                    tvEmptySubtitle.setText("Không thể tải danh sách trò chuyện. Vui lòng thử lại sau.");
                    android.util.Log.e("ChatFragment", "API call failed: " + t.getMessage(), t);
                }
            });
        } else {
            // Vai trò không xác định
            showLoading(false);
            showEmptyState(true);
        }
    }

    private void loadEmployerChats() {
        // Gọi cả hai API cho nhà tuyển dụng và kết hợp kết quả
        Call<ApiResponse> adminsCall = apiService.getAdminsForEmployer();
        Call<ApiResponse> applicantsCall = apiService.getApplicantsForEmployer();

        // Gọi API lấy danh sách admin
        adminsCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                List<ChatUser> combinedChatUsers = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ChatUser> adminUsers = (List<ChatUser>) response.body().getData();
                    if (adminUsers != null) {
                        combinedChatUsers.addAll(adminUsers);
                    }
                }

                // Sau khi có danh sách admin, tiếp tục gọi API lấy danh sách ứng viên
                applicantsCall.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<ChatUser> applicantUsers = (List<ChatUser>) response.body().getData();
                            if (applicantUsers != null) {
                                combinedChatUsers.addAll(applicantUsers);
                            }
                        }

                        if (!combinedChatUsers.isEmpty()) {
                            chatUserList.clear();
                            chatUserList.addAll(combinedChatUsers);
                            chatUserAdapter.notifyDataSetChanged();
                            showEmptyState(false);
                        } else {
                            showEmptyState(true);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                        showLoading(false);
                        showEmptyState(true);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                showLoading(false);
                showEmptyState(true);
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerChatList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyState(boolean show) {
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerChatList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onChatUserClick(ChatUser chatUser) {
        // Chuyển sang fragment chat detail
        Bundle bundle = new Bundle();
        bundle.putInt("OTHER_USER_ID", chatUser.getUser().getMaNguoiDung());
        bundle.putString("OTHER_USER_NAME", chatUser.getUser().getTenHienThi() != null ?
            chatUser.getUser().getTenHienThi() : chatUser.getUser().getTaiKhoan());

        ChatDetailFragment chatDetailFragment = new ChatDetailFragment();
        chatDetailFragment.setArguments(bundle);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, chatDetailFragment)
                .addToBackStack(null)
                .commit();
        }
    }

    // Cập nhật lại dữ liệu khi quay lại fragment
    @Override
    public void onResume() {
        super.onResume();
        if (chatUserList != null) {
            loadData();
        }
    }
}