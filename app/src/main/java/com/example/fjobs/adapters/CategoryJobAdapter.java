package com.example.fjobs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.activities.MainActivity;
import com.example.fjobs.fragments.SearchResultFragment;
import com.example.fjobs.models.WorkField;

import android.os.Bundle;
import java.util.List;

public class CategoryJobAdapter extends RecyclerView.Adapter<CategoryJobAdapter.CategoryViewHolder> {
    private List<WorkField> categoryList;
    private Context context;

    public CategoryJobAdapter(List<WorkField> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_listing, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (position < categoryList.size()) {
            WorkField category = categoryList.get(position);
            holder.bind(category);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategoryIcon;
        private TextView tvCategoryTitle;
        private TextView tvCategoryDescription;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Sử dụng các view hiện có từ item_job_listing
            // Chúng ta sẽ tái sử dụng layout hiện có và tùy chỉnh để phù hợp với danh mục
            ivCategoryIcon = itemView.findViewById(R.id.iv_company_logo);
            tvCategoryTitle = itemView.findViewById(R.id.tv_job_title);
            tvCategoryDescription = itemView.findViewById(R.id.tv_company_name);

            // Ẩn các thành phần không cần thiết cho danh mục
            itemView.findViewById(R.id.iv_bookmark).setVisibility(View.GONE);
            itemView.findViewById(R.id.tv_category_tag).setVisibility(View.GONE);
            itemView.findViewById(R.id.tv_location_tag).setVisibility(View.GONE);
            itemView.findViewById(R.id.tv_time_ago).setVisibility(View.GONE);
            itemView.findViewById(R.id.tv_salary).setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    WorkField category = categoryList.get(position);
                    if (category != null && category.getMaLinhVuc() != null) {
                        // Điều hướng đến trang tìm kiếm với bộ lọc lĩnh vực
                        if (context instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity) context;
                            Bundle bundle = new Bundle();
                            bundle.putInt("work_field_id", category.getMaLinhVuc());
                            bundle.putString("work_field_name", category.getTenLinhVuc());

                            SearchResultFragment searchResultFragment = new SearchResultFragment();
                            searchResultFragment.setArguments(bundle);

                            mainActivity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, searchResultFragment)
                                .addToBackStack(null)
                                .commit();
                        }
                    }
                }
            });
        }

        public void bind(WorkField category) {
            // Set category title
            tvCategoryTitle.setText(category.getTenLinhVuc());
            
            // Set description
            tvCategoryDescription.setText("Các công việc trong lĩnh vực " + category.getTenLinhVuc());
            
            // Set icon (using a default icon that exists in the project)
            ivCategoryIcon.setImageResource(R.drawable.logotimviec); // using existing logo as placeholder
        }
    }
}