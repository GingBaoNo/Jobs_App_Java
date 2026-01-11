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
import com.example.fjobs.models.SavedJob;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SavedJobsAdapter extends RecyclerView.Adapter<SavedJobsAdapter.SavedJobViewHolder> {
    private List<SavedJob> savedJobs;
    private Context context;
    private ApiService apiService;

    public SavedJobsAdapter(Context context, List<SavedJob> savedJobs) {
        this.context = context;
        this.savedJobs = savedJobs;

        // Khởi tạo ApiService với đảm bảo rằng ApiClient đã được khởi tạo với context
        ApiClient.initialize(context);
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        this.apiService = retrofit.create(ApiService.class);
    }

    @NonNull
    @Override
    public SavedJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_job, parent, false);
        return new SavedJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedJobViewHolder holder, int position) {
        SavedJob savedJob = savedJobs.get(position);
        holder.bind(savedJob);
    }

    @Override
    public int getItemCount() {
        return savedJobs.size();
    }

    public void updateData(List<SavedJob> newSavedJobs) {
        this.savedJobs = newSavedJobs;
        notifyDataSetChanged();
    }

    public class SavedJobViewHolder extends RecyclerView.ViewHolder {
        private TextView tvJobTitle, tvCompanyName, tvSalary, tvSaveDate;
        private Button btnViewJob, btnUnsaveJob;

        public SavedJobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            tvSaveDate = itemView.findViewById(R.id.tv_save_date);
            btnViewJob = itemView.findViewById(R.id.btn_view_job);
            btnUnsaveJob = itemView.findViewById(R.id.btn_unsave_job);
        }

        public void bind(SavedJob savedJob) {
            if (savedJob.getJobDetail() != null) {
                tvJobTitle.setText(savedJob.getJobDetail().getTieuDe() != null ?
                    savedJob.getJobDetail().getTieuDe() : "Không xác định");
                tvCompanyName.setText(savedJob.getJobDetail().getCompany() != null &&
                    savedJob.getJobDetail().getCompany().getTenCongTy() != null ?
                    savedJob.getJobDetail().getCompany().getTenCongTy() : "N/A");

                if (savedJob.getJobDetail().getLuong() != null) {
                    tvSalary.setText(String.format("%,d VNĐ", savedJob.getJobDetail().getLuong()));
                } else {
                    tvSalary.setText("Thương lượng");
                }

                // Xử lý ngày lưu
                if (savedJob.getNgayLuu() != null) {
                    String dateStr = savedJob.getNgayLuu();
                    // Nếu có cả thời gian, chỉ lấy ngày (YYYY-MM-DD)
                    if (dateStr.length() > 10) {
                        dateStr = dateStr.substring(0, 10);
                    }
                    tvSaveDate.setText("Đã lưu: " + dateStr);
                } else {
                    tvSaveDate.setText("Đã lưu: N/A");
                }
            } else {
                // Nếu không có jobDetail, có thể lấy từ savedJob object
                tvJobTitle.setText("Công việc không xác định");
                tvCompanyName.setText("N/A");
                tvSalary.setText("N/A");
                tvSaveDate.setText("N/A");
            }

            // Xử lý sự kiện xem chi tiết công việc
            btnViewJob.setOnClickListener(v -> {
                if (savedJob.getJobDetail() != null) {
                    Intent intent = new Intent(context, JobDetailActivity.class);
                    intent.putExtra("job_id", savedJob.getJobDetail().getMaCongViec());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Không thể mở chi tiết công việc", Toast.LENGTH_SHORT).show();
                }
            });

            // Xử lý sự kiện hủy lưu công việc
            btnUnsaveJob.setOnClickListener(v -> {
                unsaveJob(savedJob);
            });
        }

        private void unsaveJob(SavedJob savedJob) {
            if (savedJob.getJobDetail() != null && savedJob.getJobDetail().getMaCongViec() != null) {
                int jobId = savedJob.getJobDetail().getMaCongViec();

                Call<ApiResponse> call = apiService.unsaveJob(jobId);
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            if (apiResponse.isSuccess()) {
                                savedJobs.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                Toast.makeText(context, "Đã bỏ lưu công việc", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Không thể hủy lưu công việc", Toast.LENGTH_SHORT).show();
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
}