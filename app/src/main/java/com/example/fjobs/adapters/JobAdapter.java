package com.example.fjobs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.models.JobDetail;
import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private List<JobDetail> jobList;
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(JobDetail job);
    }

    public JobAdapter(List<JobDetail> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
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
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvStatusBadge;
        private TextView tvSalary;
        private TextView tvShortDescription;
        private TextView tvExperienceLevel; // Thêm mới
        private TextView tvJobPosition;     // Thêm mới
        private TextView tvLocation;
        private TextView tvPostingDate;
        private TextView tvApplicationDeadline;
        private TextView btnTagApproved;
        private TextView btnTagOpen;
        private Button btnViewDetails;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCompanyLogo = itemView.findViewById(R.id.iv_company_logo);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            tvShortDescription = itemView.findViewById(R.id.tv_short_description);
            tvExperienceLevel = itemView.findViewById(R.id.tv_experience_level); // Ánh xạ mới
            tvJobPosition = itemView.findViewById(R.id.tv_job_position);         // Ánh xạ mới
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPostingDate = itemView.findViewById(R.id.tv_posting_date);
            tvApplicationDeadline = itemView.findViewById(R.id.tv_application_deadline);
            btnTagApproved = itemView.findViewById(R.id.btn_tag_approved);
            btnTagOpen = itemView.findViewById(R.id.btn_tag_open);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);

            btnViewDetails.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onJobClick(jobList.get(position));
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
                    String logoUrl = "http://192.168.1.8:8080" + job.getCompany().getHinhAnhCty(); // Điều chỉnh URL theo server của bạn
                    Glide.with(itemView.getContext())
                        .load(logoUrl)
                        .placeholder(R.drawable.ic_boss) // Ảnh placeholder khi đang load
                        .error(R.drawable.ic_boss) // Ảnh khi có lỗi
                        .into(ivCompanyLogo);
                } else {
                    // Nếu không có logo, sử dụng ảnh mặc định
                    ivCompanyLogo.setImageResource(R.drawable.ic_boss);
                }
            }

            // Cập nhật trạng thái công việc
            tvStatusBadge.setText(job.getTrangThaiTinTuyen());

            // Cập nhật mức lương
            if (job.getLuong() != null && job.getLuong() > 0) {
                String salaryText = String.format("%,d", job.getLuong()) + " VNĐ";
                if (job.getLoaiLuong() != null && !job.getLoaiLuong().isEmpty()) {
                    salaryText += " (" + job.getLoaiLuong() + ")";
                }
                tvSalary.setText(salaryText);
            } else {
                tvSalary.setText("Thương lượng");
            }

            // Cập nhật mô tả ngắn (chi tiết công việc)
            if (job.getChiTiet() != null && !job.getChiTiet().isEmpty()) {
                String shortDescription = job.getChiTiet();
                // Giới hạn độ dài mô tả để phù hợp với giao diện
                if (shortDescription.length() > 100) {
                    shortDescription = shortDescription.substring(0, 100) + "...";
                }
                tvShortDescription.setText(shortDescription);
            } else {
                tvShortDescription.setText("Không có mô tả");
            }

            // Cập nhật cấp độ kinh nghiệm
            if (job.getExperienceLevel() != null && job.getExperienceLevel().getTenCapDo() != null) {
                tvExperienceLevel.setText(job.getExperienceLevel().getTenCapDo());
                tvExperienceLevel.setVisibility(View.VISIBLE);
            } else {
                tvExperienceLevel.setText("Kinh nghiệm linh hoạt");
                tvExperienceLevel.setVisibility(View.VISIBLE); // Hoặc View.GONE nếu không muốn hiển thị
            }

            // Cập nhật vị trí công việc
            if (job.getJobPosition() != null && job.getJobPosition().getTenViTri() != null) {
                tvJobPosition.setText(job.getJobPosition().getTenViTri());
                tvJobPosition.setVisibility(View.VISIBLE);
            } else {
                tvJobPosition.setText("Vị trí linh hoạt");
                tvJobPosition.setVisibility(View.VISIBLE); // Hoặc View.GONE nếu không muốn hiển thị
            }

            // Cập nhật địa điểm (nếu có thông tin địa điểm trong tương lai có thể thêm)
            // Hiện tại chưa có thông tin địa điểm trong model JobDetail, nên tạm để mặc định
            tvLocation.setText("Hà Nội"); // Thay bằng thông tin thực tế khi có

            // Cập nhật ngày đăng
            if (job.getNgayDang() != null) {
                tvPostingDate.setText(job.getNgayDang());
            }

            // Cập nhật hạn nộp
            if (job.getNgayKetThucTuyenDung() != null) {
                tvApplicationDeadline.setText(job.getNgayKetThucTuyenDung());
            }

            // Cập nhật trạng thái đã duyệt
            String approvalStatus = job.getTrangThaiDuyet();
            btnTagApproved.setText(approvalStatus.equals("Đã duyệt") ? "Đã duyệt" : "Chờ duyệt");
            btnTagApproved.setVisibility(approvalStatus.equals("Đã duyệt") ? View.VISIBLE : View.GONE);

            // Cập nhật trạng thái mở
            String openStatus = job.getTrangThaiTinTuyen();
            btnTagOpen.setText(openStatus);
            btnTagOpen.setVisibility(View.VISIBLE); // Luôn hiển thị nhưng có thể ẩn nếu cần
        }
    }
}