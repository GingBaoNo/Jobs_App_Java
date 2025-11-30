package com.example.fjobs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.models.Company;
import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {
    private List<Company> companyList;
    private OnCompanyClickListener listener;

    public interface OnCompanyClickListener {
        void onCompanyClick(Company company);
    }

    public CompanyAdapter(List<Company> companyList, OnCompanyClickListener listener) {
        this.companyList = companyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_featured_company, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        Company company = companyList.get(position);
        holder.bind(company);
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public class CompanyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCompanyLogoCard;
        private ImageView ivVerifiedLargeCorner;
        private TextView tvCompanyNameCard;
        private TextView tvLocationCard;
        private TextView tvVerifiedBadge;
        private Button btnDetails;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCompanyLogoCard = itemView.findViewById(R.id.iv_company_logo_card);
            ivVerifiedLargeCorner = itemView.findViewById(R.id.iv_verified_large_corner);
            tvCompanyNameCard = itemView.findViewById(R.id.tv_company_name_card);
            tvLocationCard = itemView.findViewById(R.id.tv_location_card);
            tvVerifiedBadge = itemView.findViewById(R.id.tv_verified_badge);
            btnDetails = itemView.findViewById(R.id.btn_details);

            // Thêm click listener cho nút "Chi tiết"
            btnDetails.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCompanyClick(companyList.get(position));
                }
            });

            // Thêm click listener cho toàn bộ item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCompanyClick(companyList.get(position));
                }
            });
        }

        public void bind(Company company) {
            // Cập nhật tên công ty
            tvCompanyNameCard.setText(company.getTenCongTy());

            // Cập nhật địa điểm - sử dụng địa chỉ từ công ty nếu có
            if (company.getDiaChi() != null && !company.getDiaChi().isEmpty()) {
                tvLocationCard.setText(company.getDiaChi());
            } else {
                tvLocationCard.setText("Địa điểm chưa cập nhật");
            }

            // Cập nhật trạng thái xác thực
            if (company.isDaXacThuc()) {
                tvVerifiedBadge.setText("Đã xác thực");
                tvVerifiedBadge.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.border_rounded_small));
                ivVerifiedLargeCorner.setVisibility(View.VISIBLE);
                tvVerifiedBadge.setVisibility(View.VISIBLE);
            } else {
                tvVerifiedBadge.setText("Chưa xác thực");
                tvVerifiedBadge.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.border_rounded_small));
                ivVerifiedLargeCorner.setVisibility(View.GONE);
                tvVerifiedBadge.setVisibility(View.VISIBLE);
            }

            // Cập nhật logo công ty nếu có
            if (company.getHinhAnhCty() != null && !company.getHinhAnhCty().isEmpty()) {
                String logoUrl = "http://192.168.1.8:8080" + company.getHinhAnhCty(); // Điều chỉnh URL theo server của bạn
                Glide.with(itemView.getContext())
                    .load(logoUrl)
                    .placeholder(R.drawable.ic_boss) // Ảnh placeholder khi đang load
                    .error(R.drawable.ic_boss) // Ảnh khi có lỗi
                    .into(ivCompanyLogoCard);
            } else {
                // Nếu không có logo, sử dụng ảnh mặc định
                ivCompanyLogoCard.setImageResource(R.drawable.ic_boss);
            }
        }
    }
}