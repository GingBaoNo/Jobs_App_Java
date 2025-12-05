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
        private TextView tvJobTitle, tvCompanyName, tvSalary, tvStatus, tvApplyDate, tvCvInfo;
        private Button btnCancelApplication;

        public AppliedJobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
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
                tvCompanyName.setText(appliedJob.getJobDetail().getCompany() != null &&
                    appliedJob.getJobDetail().getCompany().getTenCongTy() != null ?
                    appliedJob.getJobDetail().getCompany().getTenCongTy() : "N/A");

                if (appliedJob.getJobDetail().getLuong() != null) {
                    tvSalary.setText(String.format("%,d VNĐ", appliedJob.getJobDetail().getLuong()));
                } else {
                    tvSalary.setText("Thương lượng");
                }
            } else {
                tvJobTitle.setText("Công việc không xác định");
                tvCompanyName.setText("N/A");
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
            if (appliedJob.getUrlCvUngTuyen() != null && !appliedJob.getUrlCvUngTuyen().isEmpty()) {
                tvCvInfo.setText("CV: Đã nộp");
            } else {
                tvCvInfo.setText("CV: Chưa nộp");
            }

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
            Call<ApiResponse> call = apiService.cancelApplication(applicationId);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if (apiResponse.isSuccess()) {
                            appliedJobs.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            Toast.makeText(context, "Hủy ứng tuyển thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Không thể hủy ứng tuyển", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}