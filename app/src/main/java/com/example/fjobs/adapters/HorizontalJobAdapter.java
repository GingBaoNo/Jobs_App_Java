package com.example.fjobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fjobs.activities.JobDetailActivity;
import com.example.fjobs.R;
import com.example.fjobs.models.JobDetail;
import java.util.List;

public class HorizontalJobAdapter extends RecyclerView.Adapter<HorizontalJobAdapter.JobViewHolder> {
    private List<JobDetail> jobList;
    private Context context;

    public HorizontalJobAdapter(List<JobDetail> jobList, Context context) {
        this.jobList = jobList;
        this.context = context;
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

            // Ẩn bookmark trong adapter này vì không sử dụng chức năng lưu
            ivBookmark.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    JobDetail job = jobList.get(position);
                    if (job != null && job.getMaCongViec() != null) {
                        Intent intent = new Intent(context, JobDetailActivity.class);
                        intent.putExtra("job_id", job.getMaCongViec());
                        context.startActivity(intent);
                    }
                }
            });
        }

        public void bind(JobDetail job) {
            // Set job title
            tvJobTitle.setText(job.getTieuDe());

            // Set company name
            if (job.getCompany() != null) {
                tvCompanyName.setText(job.getCompany().getTenCongTy());

                // Load company logo
                if (job.getCompany().getHinhAnhCty() != null && !job.getCompany().getHinhAnhCty().isEmpty()) {
                    String logoUrl = "http://192.168.102.19:8080" + job.getCompany().getHinhAnhCty();
                    Glide.with(itemView.getContext())
                        .load(logoUrl)
                        .placeholder(R.drawable.logotimviec)
                        .error(R.drawable.logotimviec)
                        .into(ivCompanyLogo);
                } else {
                    ivCompanyLogo.setImageResource(R.drawable.logotimviec);
                }
            }

            // Set category tag (work field or discipline)
            if (job.getWorkField() != null) {
                tvCategoryTag.setText(job.getWorkField().getTenLinhVuc());
            } else if (job.getJobPosition() != null && job.getJobPosition().getWorkDiscipline() != null) {
                tvCategoryTag.setText(job.getJobPosition().getWorkDiscipline().getTenNganh());
            } else {
                tvCategoryTag.setText("Nhiều lĩnh vực");
            }

            // Set location tag
            if (job.getCompany() != null && job.getCompany().getDiaChi() != null) {
                tvLocationTag.setText(getCityFromAddress(job.getCompany().getDiaChi()));
            } else {
                tvLocationTag.setText("Nhiều địa điểm");
            }

            // Set posting date
            if (job.getNgayDang() != null) {
                tvTimeAgo.setText(formatTimeAgo(job.getNgayDang()));
            } else {
                tvTimeAgo.setText("Mới đăng");
            }

            // Set salary
            if (job.getLuong() != null && job.getLuong() > 0) {
                String salaryText = formatSalary(job.getLuong());
                if (job.getLoaiLuong() != null && !job.getLoaiLuong().isEmpty()) {
                    salaryText += "/" + job.getLoaiLuong();
                }
                tvSalary.setText(salaryText);
            } else {
                tvSalary.setText("Thương lượng");
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
    }
}