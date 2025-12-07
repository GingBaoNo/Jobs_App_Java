package com.example.fjobs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fjobs.activities.JobDetailActivity;
import com.example.fjobs.R;
import com.example.fjobs.models.JobDetail;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvStatusBadge;
        private TextView tvSalary;
        private TextView tvShortDescription;
        private TextView tvLocation;
        private TextView tvApplicationDeadline;
        private TextView tvPostingDate;
        private TextView btnTagApproved;
        private Button btnTagOpen;
        private Button btnViewDetails;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCompanyLogo = itemView.findViewById(R.id.iv_company_logo);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            tvShortDescription = itemView.findViewById(R.id.tv_short_description);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvApplicationDeadline = itemView.findViewById(R.id.tv_application_deadline);
            tvPostingDate = itemView.findViewById(R.id.tv_posting_date);
            btnTagApproved = itemView.findViewById(R.id.btn_tag_approved);
            btnTagOpen = itemView.findViewById(R.id.btn_tag_open);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);

            btnViewDetails.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    JobDetail job = jobList.get(position);
                    Intent intent = new Intent(context, JobDetailActivity.class);
                    intent.putExtra("job_id", job.getMaCongViec());
                    context.startActivity(intent);
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
                    String logoUrl = "http://172.24.134.32:8080" + job.getCompany().getHinhAnhCty();
                    Glide.with(itemView.getContext())
                        .load(logoUrl)
                        .placeholder(R.drawable.ic_boss)
                        .error(R.drawable.ic_boss)
                        .into(ivCompanyLogo);
                } else {
                    ivCompanyLogo.setImageResource(R.drawable.ic_boss);
                }
            }

            // Set job status
            String status = job.getTrangThaiTinTuyen();
            tvStatusBadge.setText(status);

            // Set salary
            if (job.getLuong() > 0) {
                String salaryText = String.format("%,d", job.getLuong()) + " VNĐ";
                if (job.getLoaiLuong() != null) {
                    salaryText += " (" + job.getLoaiLuong() + ")";
                }
                tvSalary.setText(salaryText);
            } else {
                tvSalary.setText("Thương lượng");
            }

            // Set short description from job detail
            if (job.getChiTiet() != null && job.getChiTiet().length() > 100) {
                tvShortDescription.setText(job.getChiTiet().substring(0, 100) + "...");
            } else {
                tvShortDescription.setText(job.getChiTiet());
            }

            // Set location (we might need to get this from another source)
            tvLocation.setText("Hà Nội"); // Temporary - should be retrieved from job location data

            // Set posting date
            if (job.getNgayDang() != null) {
                tvPostingDate.setText(job.getNgayDang().substring(0, 10)); // Lấy phần ngày từ chuỗi datetime
            } else {
                tvPostingDate.setText("N/A");
            }

            // Set application deadline
            if (job.getNgayKetThucTuyenDung() != null) {
                tvApplicationDeadline.setText(job.getNgayKetThucTuyenDung());
            } else {
                tvApplicationDeadline.setText("N/A");
            }

            // Set approval status
            String approvalStatus = job.getTrangThaiDuyet();
            btnTagApproved.setText(approvalStatus.equals("Đã duyệt") ? "Đã duyệt" : "Chờ duyệt");

            // Set the open tag text
            btnTagOpen.setText(job.getTrangThaiTinTuyen());
        }
    }
}