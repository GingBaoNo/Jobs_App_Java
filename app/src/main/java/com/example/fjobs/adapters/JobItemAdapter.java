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

public class JobItemAdapter extends RecyclerView.Adapter<JobItemAdapter.JobItemViewHolder> {
    private List<JobDetail> jobList;
    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(JobDetail job);
    }

    public JobItemAdapter(List<JobDetail> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job, parent, false);
        return new JobItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobItemViewHolder holder, int position) {
        if (position < jobList.size()) {
            JobDetail job = jobList.get(position);
            holder.bind(job);
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public class JobItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCompanyLogo;
        private TextView tvJobTitle;
        private TextView tvCompanyName;
        private TextView tvStatusBadge;
        private TextView tvSalary;
        private TextView tvShortDescription;
        private TextView btnTagApproved;
        private TextView btnTagOpen;
        private Button btnViewDetails;

        public JobItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCompanyLogo = itemView.findViewById(R.id.iv_company_logo);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvSalary = itemView.findViewById(R.id.tv_salary);
            tvShortDescription = itemView.findViewById(R.id.tv_short_description);
            btnTagApproved = itemView.findViewById(R.id.btn_tag_approved);
            btnTagOpen = itemView.findViewById(R.id.btn_tag_open);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);

            btnViewDetails.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    JobDetail job = jobList.get(position);
                    listener.onJobClick(job);
                }
            });

            // Thêm click listener cho toàn bộ item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    JobDetail job = jobList.get(position);
                    listener.onJobClick(job);
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
                    String logoUrl = "http://192.168.1.8:8080" + job.getCompany().getHinhAnhCty();
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
            if (job.getLuong() != null && job.getLuong() > 0) {
                String salaryText = String.format("%,d", job.getLuong()) + " VNĐ";
                if (job.getLoaiLuong() != null && !job.getLoaiLuong().isEmpty()) {
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

            // Set approval status
            String approvalStatus = job.getTrangThaiDuyet();
            btnTagApproved.setText(approvalStatus.equals("Đã duyệt") ? "Đã duyệt" : "Chờ duyệt");
            btnTagApproved.setVisibility(approvalStatus.equals("Đã duyệt") ? View.VISIBLE : View.GONE);

            // Set the open tag text
            btnTagOpen.setText(job.getTrangThaiTinTuyen());
        }
    }
}