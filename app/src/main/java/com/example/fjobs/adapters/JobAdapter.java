package com.example.fjobs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.JobDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<JobDetail> jobList;
    private OnJobClickListener listener;
    private Context context;
    private ApiService apiService;

    public interface OnJobClickListener {
        void onJobClick(JobDetail job);
    }

    public JobAdapter(Context context, List<JobDetail> jobList, OnJobClickListener listener) {
        this.context = context;
        this.jobList = jobList;
        this.listener = listener;
        this.apiService = ApiClient.getApiService();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_listing, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        if (position < jobList.size()) {
            JobDetail job = jobList.get(position);
            holder.bind(job);
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class JobViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCompanyLogo;
        private ImageView ivBookmark;
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvCategoryTag;
        private TextView tvLocationTag;
        private TextView tvTimeAgo;
        private TextView tvSalary;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCompanyLogo = itemView.findViewById(R.id.iv_company_logo);
            ivBookmark = itemView.findViewById(R.id.iv_bookmark);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvCategoryTag = itemView.findViewById(R.id.tv_category_tag);
            tvLocationTag = itemView.findViewById(R.id.tv_location_tag);
            tvTimeAgo = itemView.findViewById(R.id.tv_time_ago);
            tvSalary = itemView.findViewById(R.id.tv_salary);

            // Xử lý sự kiện click cho bookmark
            ivBookmark.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    toggleBookmark(position);
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onJobClick(jobList.get(position));
                }
            });
        }

        public void bind(JobDetail job) {
            // Cập nhật tiêu đề công việc
            tvJobTitle.setText(job.getTieuDe());

            // Cập nhật tên công ty nếu có thông tin công ty
            if (job.getCompany() != null) {
                tvCompanyName.setText(job.getCompany().getTenCongTy());

                // Hiển thị logo công ty nếu có
                if (job.getCompany().getHinhAnhCty() != null) {
                    String logoUrl = "http://192.168.102.19:8080" + job.getCompany().getHinhAnhCty(); // Điều chỉnh URL theo server của bạn
                    Glide.with(itemView.getContext())
                        .load(logoUrl)
                        .placeholder(R.drawable.logotimviec) // Ảnh placeholder khi đang load
                        .error(R.drawable.logotimviec) // Ảnh khi có lỗi
                        .into(ivCompanyLogo);
                } else {
                    // Nếu không có logo, sử dụng ảnh mặc định
                    ivCompanyLogo.setImageResource(R.drawable.logotimviec);
                }
            }

            // Cập nhật danh mục công việc (lĩnh vực hoặc ngành)
            if (job.getWorkField() != null) {
                tvCategoryTag.setText(job.getWorkField().getTenLinhVuc());
            } else if (job.getJobPosition() != null && job.getJobPosition().getWorkDiscipline() != null) {
                tvCategoryTag.setText(job.getJobPosition().getWorkDiscipline().getTenNganh());
            } else {
                tvCategoryTag.setText("Nhiều lĩnh vực");
            }

            // Cập nhật địa điểm công việc
            if (job.getCompany() != null && job.getCompany().getDiaChi() != null) {
                tvLocationTag.setText(getCityFromAddress(job.getCompany().getDiaChi()));
            } else {
                tvLocationTag.setText("Nhiều địa điểm");
            }

            // Cập nhật thời gian đăng (chuyển đổi từ ngày đăng nếu có)
            if (job.getNgayDang() != null) {
                tvTimeAgo.setText(formatTimeAgo(job.getNgayDang()));
            } else {
                tvTimeAgo.setText("Mới đăng");
            }

            // Cập nhật mức lương
            if (job.getLuong() != null && job.getLuong() > 0) {
                String salaryText = formatSalary(job.getLuong());
                if (job.getLoaiLuong() != null && !job.getLoaiLuong().isEmpty()) {
                    salaryText += "/" + job.getLoaiLuong();
                }
                tvSalary.setText(salaryText);
            } else {
                tvSalary.setText("Thương lượng");
            }

            // Cập nhật trạng thái bookmark
            updateBookmarkIcon(job.isSaved());
        }

        // Cập nhật icon bookmark dựa trên trạng thái
        private void updateBookmarkIcon(boolean isBookmarked) {
            if (isBookmarked) {
                ivBookmark.setImageResource(R.drawable.ic_bookmark2); // Trạng thái đã lưu
            } else {
                ivBookmark.setImageResource(R.drawable.ic_bookmark); // Trạng thái chưa lưu
            }
        }

        // Hàm hỗ trợ định dạng lương
        private String formatSalary(int salary) {
            if (salary >= 10000000) {
                double millions = salary / 1000000.0;
                return String.format("%.1f triệu", millions);
            } else {
                double thousands = salary / 1000.0;
                return String.format("%.0f nghìn", thousands);
            }
        }

        // Hàm hỗ trợ lấy tên thành phố từ địa chỉ
        private String getCityFromAddress(String address) {
            if (address == null || address.isEmpty()) {
                return "N/A";
            }

            // Một số thành phố phổ biến để tìm trong địa chỉ
            String[] cities = {"Hà Nội", "TP.HCM", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ", "Bình Dương", "Đồng Nai"};

            for (String city : cities) {
                if (address.toLowerCase().contains(city.toLowerCase())) {
                    if (city.equals("Hồ Chí Minh")) {
                        return "TP.HCM";
                    }
                    return city;
                }
            }

            // Nếu không tìm thấy thành phố cụ thể, trả về 2 từ cuối cùng của địa chỉ
            String[] parts = address.split(" ");
            if (parts.length >= 2) {
                return parts[parts.length - 2] + " " + parts[parts.length - 1];
            } else if (parts.length == 1) {
                return parts[0];
            } else {
                return "Nhiều địa điểm";
            }
        }

        // Hàm hỗ trợ định dạng thời gian đăng
        private String formatTimeAgo(String dateString) {
            try {
                // Giả sử dateString có định dạng "yyyy-MM-dd" hoặc "yyyy-MM-dd HH:mm:ss"
                // Trong thực tế, bạn có thể cần điều chỉnh để phù hợp với định dạng thực tế từ API
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(dateString);
                long dateInMillis = date.getTime();

                long now = System.currentTimeMillis();
                long diffInMillis = now - dateInMillis;

                long days = diffInMillis / (24 * 60 * 60 * 1000);

                if (days == 0) {
                    return "Hôm nay";
                } else if (days == 1) {
                    return "Hôm qua";
                } else if (days < 7) {
                    return days + " ngày trước";
                } else if (days < 30) {
                    long weeks = days / 7;
                    return weeks + " tuần trước";
                } else {
                    long months = days / 30;
                    return months + " tháng trước";
                }
            } catch (Exception e) {
                // Nếu không thể phân tích ngày, trả về giá trị mặc định
                return "Mới đăng";
            }
        }

        // Phương thức toggle bookmark
        private void toggleBookmark(int position) {
            JobDetail job = jobList.get(position);

            // Đảo ngược trạng thái bookmark
            boolean isBookmarked = job.isSaved();
            int jobId = job.getMaCongViec();

            if (isBookmarked) {
                // Gọi API hủy lưu việc làm
                unsaveJob(jobId, position);
            } else {
                // Gọi API lưu việc làm
                saveJob(jobId, position);
            }
        }

        // Gọi API lưu việc làm
        private void saveJob(int jobId, int position) {
            com.example.fjobs.api.ApiService.SaveJobRequest request = new com.example.fjobs.api.ApiService.SaveJobRequest();
            request.setJobDetailId(jobId);

            Call<ApiResponse> call = apiService.saveJob(request);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Cập nhật trạng thái trong model
                        JobDetail job = jobList.get(position);
                        job.setSaved(true);

                        // Cập nhật lại icon bookmark
                        updateBookmarkIcon(true);

                        Toast.makeText(context, "Đã lưu công việc", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lưu công việc thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối khi lưu công việc", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Gọi API hủy lưu việc làm
        private void unsaveJob(int jobId, int position) {
            com.example.fjobs.api.ApiService.UnsaveJobRequest request = new com.example.fjobs.api.ApiService.UnsaveJobRequest();
            request.setJobDetailId(jobId);

            Call<ApiResponse> call = apiService.unsaveJob(request);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Cập nhật trạng thái trong model
                        JobDetail job = jobList.get(position);
                        job.setSaved(false);

                        // Cập nhật lại icon bookmark
                        updateBookmarkIcon(false);

                        Toast.makeText(context, "Đã bỏ lưu công việc", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Bỏ lưu công việc thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối khi bỏ lưu công việc", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}