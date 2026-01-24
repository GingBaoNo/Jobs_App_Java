package com.example.fjobs.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.CvProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class CvProfileAdapter extends RecyclerView.Adapter<CvProfileAdapter.CvProfileViewHolder> {

    private List<CvProfile> cvProfiles;
    private OnCvProfileActionListener listener;
    private ApiService apiService;

    public interface OnCvProfileActionListener {
        void onEditCvProfile(CvProfile cvProfile);
        void onDeleteCvProfile(int position, CvProfile cvProfile);
        void onSetAsDefault(int cvProfileId);
    }

    public CvProfileAdapter(List<CvProfile> cvProfiles, OnCvProfileActionListener listener) {
        this.cvProfiles = cvProfiles;
        this.listener = listener;
        this.apiService = ApiClient.getApiService();
    }

    @NonNull
    @Override
    public CvProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cv_profile, parent, false);
        return new CvProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CvProfileViewHolder holder, int position) {
        CvProfile cvProfile = cvProfiles.get(position);
        holder.bind(cvProfile);
    }

    @Override
    public int getItemCount() {
        return cvProfiles != null ? cvProfiles.size() : 0;
    }

    public void updateCvProfiles(List<CvProfile> newCvProfiles) {
        this.cvProfiles = newCvProfiles;
        notifyDataSetChanged();
    }

    public class CvProfileViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCvProfileName;
        private TextView tvCvProfileDescription;
        private TextView tvCvProfilePosition;
        private CheckBox cbIsDefault;
        private Button btnEditCv;
        private Button btnDeleteCv;
        private Button btnSetDefault;

        public CvProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCvProfileName = itemView.findViewById(R.id.tv_cv_profile_name);
            tvCvProfileDescription = itemView.findViewById(R.id.tv_cv_profile_description);
            tvCvProfilePosition = itemView.findViewById(R.id.tv_cv_profile_position);
            cbIsDefault = itemView.findViewById(R.id.cb_is_default);
            btnEditCv = itemView.findViewById(R.id.btn_edit_cv);
            btnDeleteCv = itemView.findViewById(R.id.btn_delete_cv);
            btnSetDefault = itemView.findViewById(R.id.btn_set_default);
        }

        public void bind(CvProfile cvProfile) {
            tvCvProfileName.setText(cvProfile.getTenHoSo());
            tvCvProfileDescription.setText(cvProfile.getMoTa() != null ? cvProfile.getMoTa() : "Không có mô tả");
            tvCvProfilePosition.setText(cvProfile.getViTriMongMuon() != null ? cvProfile.getViTriMongMuon() : "Chưa cập nhật vị trí");

            boolean isDefault = cvProfile.getLaMacDinh() != null && cvProfile.getLaMacDinh();
            cbIsDefault.setChecked(isDefault);
            btnSetDefault.setVisibility(isDefault ? View.GONE : View.VISIBLE);

            btnEditCv.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditCvProfile(cvProfile);
                }
            });

            btnDeleteCv.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteCvProfile(getAdapterPosition(), cvProfile);
                }
            });

            btnSetDefault.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSetAsDefault(cvProfile.getMaHoSoCv());
                }
            });
        }
    }
}