package com.example.fjobs;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectionChecker {

    private static final String TAG = "ConnectionTest";
    private Context context;

    public ConnectionChecker(Context context) {
        this.context = context;
    }

    public void testConnection() {
        Log.d(TAG, "Bắt đầu kiểm tra kết nối API...");

        // 1. Gửi yêu cầu kiểm tra trạng thái tới Spring Boot API
        RetrofitClient.getInstance().getStatusApi().checkServerStatus().enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    // ✅ KẾT NỐI API THÀNH CÔNG (HTTP 200 OK)
                    String message = "Kết nối API (Spring Boot) thành công! Phản hồi: " + response.body();
                    Log.d(TAG, message);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    // Nếu các API dữ liệu chính cũng hoạt động, thì kết nối SQL Server gián tiếp là thành công.
                } else {
                    // ❌ LỖI PHẢN HỒI SERVER (ví dụ: 404, 500)
                    String errorMessage = "Lỗi phản hồi Server: Mã " + response.code() + ". Có thể API không hoạt động.";
                    Log.e(TAG, errorMessage);
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // ❌ LỖI KẾT NỐI MẠNG (ví dụ: mất mạng, sai BASE_URL, Server ngoại tuyến)
                String failureMessage = "Thất bại kết nối mạng: " + t.getMessage() + ". Kiểm tra IP/Port hoặc kết nối Server.";
                Log.e(TAG, failureMessage);
                Toast.makeText(context, failureMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
