package com.example.fjobs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.Profile;
import com.example.fjobs.api.ConnectionChecker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    // Basic Information
    private EditText etFullName, etIntroduction, etPhone, etDob;
    private Spinner spnGender, spnEducation;
    private EditText etCareerGoal, etDesiredPosition, etDesiredSalary;
    private Spinner spnDesiredSalaryType, spnWorkType, spnWorkTime, spnWorkScheduleType;
    private EditText etWorkExperience;
    private SeekBar sbTotalExperience;
    private TextView tvTotalExperienceValue;

    private Button btnUpdate, btnCancel;
    private ApiService apiService;
    private Profile currentProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_profile, container, false);

        initViews(view);
        setupClickListeners();
        setupSpinners();
        loadCurrentProfile();

        return view;
    }

    private void initViews(View view) {
        // Basic Information
        etFullName = view.findViewById(R.id.et_full_name);
        etIntroduction = view.findViewById(R.id.et_introduction);
        etPhone = view.findViewById(R.id.et_phone);
        etDob = view.findViewById(R.id.et_dob);
        spnGender = view.findViewById(R.id.spn_gender);
        spnEducation = view.findViewById(R.id.spn_education);

        // Career Goal
        etCareerGoal = view.findViewById(R.id.et_career_goal);

        // Desired Position
        etDesiredPosition = view.findViewById(R.id.et_desired_position);
        etDesiredSalary = view.findViewById(R.id.et_desired_salary);
        spnDesiredSalaryType = view.findViewById(R.id.spn_desired_salary_type);
        spnWorkType = view.findViewById(R.id.spn_work_type);
        spnWorkTime = view.findViewById(R.id.spn_work_time);
        spnWorkScheduleType = view.findViewById(R.id.spn_work_schedule_type);

        // Work Experience
        etWorkExperience = view.findViewById(R.id.et_work_experience);
        sbTotalExperience = view.findViewById(R.id.sb_total_experience);
        tvTotalExperienceValue = view.findViewById(R.id.tv_total_experience_value);

        btnUpdate = view.findViewById(R.id.btn_update_profile);
        btnCancel = view.findViewById(R.id.btn_cancel_profile);

        // Initialize API service
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Setup SeekBar
        sbTotalExperience.setMax(50); // Max 50 years
        sbTotalExperience.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTotalExperienceValue.setText(progress + " năm");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupClickListeners() {
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnCancel.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });
    }

    private void setupSpinners() {
        // Setup gender spinner
        String[] genders = {"Nam", "Nữ", "Khác"};
        android.widget.ArrayAdapter<String> genderAdapter = new android.widget.ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGender.setAdapter(genderAdapter);

        // Setup education spinner
        String[] educations = {"Trung học", "Trung cấp", "Cao đẳng", "Đại học", "Thạc sĩ", "Tiến sĩ", "Khác"};
        android.widget.ArrayAdapter<String> educationAdapter = new android.widget.ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, educations);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEducation.setAdapter(educationAdapter);

        // Setup salary type spinner
        String[] salaryTypes = {"Theo tháng", "Theo năm", "Theo giờ", "Theo dự án"};
        android.widget.ArrayAdapter<String> salaryTypeAdapter = new android.widget.ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, salaryTypes);
        salaryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDesiredSalaryType.setAdapter(salaryTypeAdapter);

        // Setup work type spinner
        String[] workTypes = {"Toàn thời gian", "Bán thời gian", "Làm việc từ xa", "Làm việc bán thời gian", "Thực tập"};
        android.widget.ArrayAdapter<String> workTypeAdapter = new android.widget.ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, workTypes);
        workTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWorkType.setAdapter(workTypeAdapter);

        // Setup work time spinner
        String[] workTimes = {"Sáng (8h-17h)", "Ca đêm", "Ca gãy", "Linh động", "Theo ca"};
        android.widget.ArrayAdapter<String> workTimeAdapter = new android.widget.ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, workTimes);
        workTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWorkTime.setAdapter(workTimeAdapter);

        // Setup work schedule type spinner
        String[] workScheduleTypes = {"5 ngày/tuần", "6 ngày/tuần", "7 ngày/tuần", "Linh hoạt"};
        android.widget.ArrayAdapter<String> workScheduleTypeAdapter = new android.widget.ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, workScheduleTypes);
        workScheduleTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWorkScheduleType.setAdapter(workScheduleTypeAdapter);
    }

    private void loadCurrentProfile() {
        if (!ConnectionChecker.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Không có kết nối internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ApiResponse> call = apiService.getMyProfile();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        if (apiResponse.getData() instanceof java.util.Map) {
                            java.util.Map<String, Object> profileMap = (java.util.Map<String, Object>) apiResponse.getData();
                            currentProfile = convertMapToProfile(profileMap);

                            if (currentProfile != null) {
                                populateFields();
                            } else {
                                Toast.makeText(requireContext(), "Dữ liệu hồ sơ không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Dữ liệu không đúng định dạng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Không thể tải hồ sơ";
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int statusCode = response.code();
                    String errorMessage = "Lỗi API: " + statusCode;
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Profile convertMapToProfile(java.util.Map<String, Object> map) {
        try {
            Profile profile = new Profile();

            if (map.containsKey("maHoSo")) {
                Object maHoSoObj = map.get("maHoSo");
                if (maHoSoObj instanceof Integer) {
                    profile.setMaHoSo((Integer) maHoSoObj);
                } else if (maHoSoObj instanceof Double) {
                    profile.setMaHoSo(((Double) maHoSoObj).intValue());
                } else {
                    profile.setMaHoSo(Integer.parseInt(maHoSoObj.toString()));
                }
            }

            if (map.containsKey("hoTen") && map.get("hoTen") != null) {
                profile.setHoTen(map.get("hoTen").toString());
            }

            if (map.containsKey("gioiThieuBanThan") && map.get("gioiThieuBanThan") != null) {
                profile.setGioiThieuBanThan(map.get("gioiThieuBanThan").toString());
            }

            if (map.containsKey("soDienThoai") && map.get("soDienThoai") != null) {
                profile.setSoDienThoai(map.get("soDienThoai").toString());
            }

            if (map.containsKey("ngaySinh") && map.get("ngaySinh") != null) {
                profile.setNgaySinh(map.get("ngaySinh").toString());
            }

            if (map.containsKey("gioiTinh") && map.get("gioiTinh") != null) {
                profile.setGioiTinh(map.get("gioiTinh").toString());
            }

            if (map.containsKey("trinhDoHocVan") && map.get("trinhDoHocVan") != null) {
                profile.setTrinhDoHocVan(map.get("trinhDoHocVan").toString());
            }

            if (map.containsKey("thoiGianMongMuon") && map.get("thoiGianMongMuon") != null) {
                profile.setThoiGianMongMuon(map.get("thoiGianMongMuon").toString());
            }

            if (map.containsKey("viTriMongMuon") && map.get("viTriMongMuon") != null) {
                profile.setViTriMongMuon(map.get("viTriMongMuon").toString());
            }

            if (map.containsKey("mucLuongMongMuon") && map.get("mucLuongMongMuon") != null) {
                Object mucLuongMongMuonObj = map.get("mucLuongMongMuon");
                if (mucLuongMongMuonObj instanceof Integer) {
                    profile.setMucLuongMongMuon((Integer) mucLuongMongMuonObj);
                } else if (mucLuongMongMuonObj instanceof Double) {
                    profile.setMucLuongMongMuon(((Double) mucLuongMongMuonObj).intValue());
                } else {
                    profile.setMucLuongMongMuon(Integer.parseInt(mucLuongMongMuonObj.toString()));
                }
            }

            if (map.containsKey("loaiLuongMongMuon") && map.get("loaiLuongMongMuon") != null) {
                profile.setLoaiLuongMongMuon(map.get("loaiLuongMongMuon").toString());
            }

            if (map.containsKey("hinhThucLamViec") && map.get("hinhThucLamViec") != null) {
                profile.setHinhThucLamViec(map.get("hinhThucLamViec").toString());
            }

            if (map.containsKey("loaiThoiGianLamViec") && map.get("loaiThoiGianLamViec") != null) {
                profile.setLoaiThoiGianLamViec(map.get("loaiThoiGianLamViec").toString());
            }

            if (map.containsKey("kinhNghiem") && map.get("kinhNghiem") != null) {
                profile.setKinhNghiem(map.get("kinhNghiem").toString());
            }

            if (map.containsKey("tongNamKinhNghiem") && map.get("tongNamKinhNghiem") != null) {
                Object tongNamKinhNghiemObj = map.get("tongNamKinhNghiem");
                if (tongNamKinhNghiemObj instanceof Double) {
                    profile.setTongNamKinhNghiem(((Double) tongNamKinhNghiemObj).floatValue());
                } else if (tongNamKinhNghiemObj instanceof Integer) {
                    profile.setTongNamKinhNghiem(((Integer) tongNamKinhNghiemObj).floatValue());
                } else {
                    profile.setTongNamKinhNghiem(Float.parseFloat(tongNamKinhNghiemObj.toString()));
                }
            }

            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateFields() {
        // Basic Information
        if (currentProfile.getHoTen() != null) etFullName.setText(currentProfile.getHoTen());
        if (currentProfile.getGioiThieuBanThan() != null) etIntroduction.setText(currentProfile.getGioiThieuBanThan());
        if (currentProfile.getSoDienThoai() != null) etPhone.setText(currentProfile.getSoDienThoai());
        if (currentProfile.getNgaySinh() != null) etDob.setText(currentProfile.getNgaySinh());
        if (currentProfile.getGioiTinh() != null) setSpinnerSelection(spnGender, currentProfile.getGioiTinh());
        if (currentProfile.getTrinhDoHocVan() != null) setSpinnerSelection(spnEducation, currentProfile.getTrinhDoHocVan());

        // Career Goal
        if (currentProfile.getThoiGianMongMuon() != null) etCareerGoal.setText(currentProfile.getThoiGianMongMuon());

        // Desired Position
        if (currentProfile.getViTriMongMuon() != null) etDesiredPosition.setText(currentProfile.getViTriMongMuon());
        if (currentProfile.getMucLuongMongMuon() != null) etDesiredSalary.setText(String.valueOf(currentProfile.getMucLuongMongMuon()));
        if (currentProfile.getLoaiLuongMongMuon() != null) setSpinnerSelection(spnDesiredSalaryType, currentProfile.getLoaiLuongMongMuon());
        if (currentProfile.getHinhThucLamViec() != null) setSpinnerSelection(spnWorkType, currentProfile.getHinhThucLamViec());
        if (currentProfile.getThoiGianMongMuon() != null) setSpinnerSelection(spnWorkTime, currentProfile.getThoiGianMongMuon());
        if (currentProfile.getLoaiThoiGianLamViec() != null) setSpinnerSelection(spnWorkScheduleType, currentProfile.getLoaiThoiGianLamViec());

        // Work Experience
        if (currentProfile.getKinhNghiem() != null) etWorkExperience.setText(currentProfile.getKinhNghiem());
        if (currentProfile.getTongNamKinhNghiem() != null) {
            int experience = Math.round(currentProfile.getTongNamKinhNghiem());
            sbTotalExperience.setProgress(experience);
            tvTotalExperienceValue.setText(experience + " năm");
        } else {
            sbTotalExperience.setProgress(0);
            tvTotalExperienceValue.setText("0 năm");
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null) {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateProfile() {
        // Validate required fields
        String fullName = etFullName.getText().toString().trim();
        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return;
        }

        if (!ConnectionChecker.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Không có kết nối internet", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create profile object with updated values
        Profile updatedProfile = new Profile();
        updatedProfile.setMaHoSo(currentProfile.getMaHoSo());
        updatedProfile.setHoTen(fullName);
        updatedProfile.setGioiThieuBanThan(etIntroduction.getText().toString().trim());
        updatedProfile.setSoDienThoai(etPhone.getText().toString().trim());
        updatedProfile.setNgaySinh(etDob.getText().toString().trim());
        updatedProfile.setGioiTinh(spnGender.getSelectedItem().toString());
        updatedProfile.setTrinhDoHocVan(spnEducation.getSelectedItem().toString());
        updatedProfile.setThoiGianMongMuon(etCareerGoal.getText().toString().trim());
        updatedProfile.setViTriMongMuon(etDesiredPosition.getText().toString().trim());

        String desiredSalaryStr = etDesiredSalary.getText().toString().trim();
        if (!desiredSalaryStr.isEmpty()) {
            try {
                updatedProfile.setMucLuongMongMuon(Integer.parseInt(desiredSalaryStr));
            } catch (NumberFormatException e) {
                etDesiredSalary.setError("Mức lương không hợp lệ");
                etDesiredSalary.requestFocus();
                return;
            }
        }

        updatedProfile.setLoaiLuongMongMuon(spnDesiredSalaryType.getSelectedItem().toString());
        updatedProfile.setHinhThucLamViec(spnWorkType.getSelectedItem().toString());
        updatedProfile.setLoaiThoiGianLamViec(spnWorkScheduleType.getSelectedItem().toString());
        updatedProfile.setKinhNghiem(etWorkExperience.getText().toString().trim());
        updatedProfile.setTongNamKinhNghiem((float) sbTotalExperience.getProgress());

        Call<ApiResponse> call = apiService.updateMyProfile(updatedProfile);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            getActivity().setResult(android.app.Activity.RESULT_OK);
                        }
                        if (getFragmentManager() != null) {
                            getFragmentManager().popBackStack();
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Cập nhật thất bại";
                        Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int statusCode = response.code();
                    String errorMessage = "Lỗi API: " + statusCode;
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}