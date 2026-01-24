package com.example.fjobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fjobs.activities.JobDetailActivity;
import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.AppliedJob;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AppliedJobsAdapter extends RecyclerView.Adapter<AppliedJobsAdapter.AppliedJobViewHolder> {
    private List<AppliedJob> appliedJobs;
    private Context context;
    private ApiService apiService;

    public AppliedJobsAdapter(Context context, List<AppliedJob> appliedJobs) {
        this.context = context;
        this.appliedJobs = appliedJobs;

        // Khởi tạo ApiService với đảm bảo rằng ApiClient đã được khởi tạo với context
        ApiClient.initialize(context);
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        this.apiService = retrofit.create(ApiService.class);
    }

    @NonNull
    @Override
    public AppliedJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applied_job, parent, false);
        return new AppliedJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppliedJobViewHolder holder, int position) {
        AppliedJob appliedJob = appliedJobs.get(position);
        holder.bind(appliedJob);
    }

    @Override
    public int getItemCount() {
        return appliedJobs.size();
    }

    public void updateData(List<AppliedJob> newAppliedJobs) {
        this.appliedJobs = newAppliedJobs;
        notifyDataSetChanged();
    }

    public class AppliedJobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJobTitle, tvSalary, tvStatus, tvApplyDate, tvCvInfo;
        private Button btnCancelApplication;

        public AppliedJobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvApplyDate = itemView.findViewById(R.id.tv_apply_date);
            tvCvInfo = itemView.findViewById(R.id.tv_cv_info);
            btnCancelApplication = itemView.findViewById(R.id.btn_cancel_application);
        }

        public void bind(AppliedJob appliedJob) {
            if (appliedJob.getJobDetail() != null) {
                tvJobTitle.setText(appliedJob.getJobDetail().getTieuDe() != null ?
                    appliedJob.getJobDetail().getTieuDe() : "Không xác định");

                if (appliedJob.getJobDetail().getLuong() != null) {
                    tvSalary.setText(String.format("%,d VNĐ", appliedJob.getJobDetail().getLuong()));
                } else {
                    tvSalary.setText("Thương lượng");
                }
            } else {
                tvJobTitle.setText("Công việc không xác định");
                tvSalary.setText("N/A");
            }

            tvStatus.setText(getStatusText(appliedJob.getTrangThaiUngTuyen()));
            setColorForStatus(appliedJob.getTrangThaiUngTuyen(), tvStatus);

            if (appliedJob.getNgayUngTuyen() != null) {
                String dateStr = appliedJob.getNgayUngTuyen();
                // Nếu có cả thời gian, chỉ lấy ngày (YYYY-MM-DD)
                if (dateStr.length() > 10) {
                    dateStr = dateStr.substring(0, 10);
                }
                tvApplyDate.setText("Ngày ứng tuyển: " + dateStr);
            } else {
                tvApplyDate.setText("Ngày ứng tuyển: N/A");
            }

            // Hiển thị thông tin CV đã ứng tuyển
            if (appliedJob.getCvProfile() != null) {
                // Ứng viên đã sử dụng hồ sơ CV cụ thể để ứng tuyển
                String cvName = appliedJob.getCvProfile().getTenHoSo();
                if (cvName != null && !cvName.isEmpty()) {
                    tvCvInfo.setText("CV: " + cvName);
                } else {
                    tvCvInfo.setText("CV: Hồ sơ #" + appliedJob.getCvProfile().getMaHoSoCv());
                }
            } else if (appliedJob.getUrlCvUngTuyen() != null && !appliedJob.getUrlCvUngTuyen().isEmpty()) {
                // Ứng viên đã nộp CV trực tiếp (trường hợp cũ)
                tvCvInfo.setText("CV: Đã nộp");
            } else {
                tvCvInfo.setText("CV: Chưa nộp");
            }

            // Xử lý sự kiện click vào item để chuyển sang trang chi tiết công việc
            itemView.setOnClickListener(v -> {
                if (appliedJob.getJobDetail() != null) {
                    // Chuyển sang trang chi tiết công việc
                    Intent intent = new Intent(context, JobDetailActivity.class);
                    intent.putExtra("job_id", appliedJob.getJobDetail().getMaCongViec());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Không thể mở chi tiết công việc", Toast.LENGTH_SHORT).show();
                }
            });

            // Xử lý sự kiện hủy ứng tuyển
            btnCancelApplication.setOnClickListener(v -> {
                cancelApplication(appliedJob.getMaUngTuyen());
            });
        }

        private String getStatusText(String status) {
            switch (status) {
                case "Đã gửi":
                    return "Đã gửi";
                case "Đã xem":
                    return "Đã xem";
                case "Phỏng vấn":
                    return "Phỏng vấn";
                case "Tuyển dụng":
                    return "Tuyển dụng";
                case "Từ chối":
                    return "Từ chối";
                default:
                    return status;
            }
        }

        private void setColorForStatus(String status, TextView statusView) {
            switch (status) {
                case "Đã gửi":
                    statusView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    break;
                case "Đã xem":
                    statusView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
                    break;
                case "Phỏng vấn":
                    statusView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
                    break;
                case "Tuyển dụng":
                    statusView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                    break;
                case "Từ chối":
                    statusView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                    break;
                default:
                    statusView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    break;
            }
        }

        private void cancelApplication(int applicationId) {
            // Thêm cơ chế chống nhấn nhanh
            btnCancelApplication.setEnabled(false);
            String originalText = btnCancelApplication.getText().toString();
            btnCancelApplication.setText("Đang hủy...");

            Call<ApiResponse> call = apiService.cancelApplication(applicationId);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    // Luôn kích hoạt lại nút sau khi nhận phản hồi
                    btnCancelApplication.setEnabled(true);
                    btnCancelApplication.setText(originalText);

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                // Lấy vị trí hiện tại để đảm bảo xóa đúng item
                                int position = getAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    appliedJobs.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Hủy ứng tuyển thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Nếu không lấy được vị trí, thử làm mới toàn bộ danh sách
                                    Toast.makeText(context, "Hủy ứng tuyển thành công, vui lòng làm mới danh sách", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Hủy ứng tuyển thất bại";
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Phản hồi thành công nhưng không có body
                            // Có thể API trả về mã trạng thái 200 nhưng không có nội dung
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                appliedJobs.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Hủy ứng tuyển thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Hủy ứng tuyển thành công, vui lòng làm mới danh sách", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        // Xử lý lỗi HTTP (mã trạng thái không phải 2xx)
                        int statusCode = response.code();
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = "Lỗi không xác định";
                        }
                        Toast.makeText(context, "Lỗi " + statusCode + ": Không thể hủy ứng tuyển", Toast.LENGTH_LONG).show();
                        System.out.println("API Error: " + errorBody);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Kích hoạt lại nút khi có lỗi kết nối
                    btnCancelApplication.setEnabled(true);
                    btnCancelApplication.setText(originalText);
                    Toast.makeText(context, "Lỗi kết nối mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    System.out.println("Connection Error: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        }
    }
}