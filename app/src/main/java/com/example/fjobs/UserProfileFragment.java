package com.example.fjobs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.Profile;
import com.example.fjobs.models.User;
import com.example.fjobs.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserProfileFragment extends Fragment {

    private ImageView imgAvatar;
    private TextView tvFullName, tvCurrentStatus, tvEmail, tvPhone, tvGender, tvDob, tvEducation;
    private TextView tvBasicFullName, tvIntroduction, tvCareerGoal, tvDesiredPosition, tvDesiredSalary;
    private TextView tvDesiredSalaryType, tvWorkType, tvWorkTime, tvWorkScheduleType, tvWorkExperience, tvTotalExperience;
    private Button btnEditProfile, btnDownloadCv, btnEditBasicInfo, btnEditCareerGoal, btnEditDesiredPosition, btnEditWorkExperience;

    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        initViews(view);

        // Sử dụng phương thức không tham số vì ApiClient đã được khởi tạo trong MainActivity
        // Nếu retrofit chưa được khởi tạo (trường hợp chưa đăng nhập), ta sẽ không thể lấy được apiService
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        if (retrofit != null) {
            apiService = retrofit.create(ApiService.class);
        } else {
            // Nếu chưa có retrofit, cần khởi tạo lại với context
            ApiClient.initialize(requireContext());
            apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        }

        loadUserProfile();

        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        // Ánh xạ các view từ layout
        imgAvatar = view.findViewById(R.id.img_avatar);
        tvFullName = view.findViewById(R.id.tv_full_name);
        tvCurrentStatus = view.findViewById(R.id.tv_current_status);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvGender = view.findViewById(R.id.tv_gender);
        tvDob = view.findViewById(R.id.tv_dob);
        tvEducation = view.findViewById(R.id.tv_education);

        tvBasicFullName = view.findViewById(R.id.tv_basic_full_name);
        tvIntroduction = view.findViewById(R.id.tv_introduction);
        tvCareerGoal = view.findViewById(R.id.tv_career_goal);
        tvDesiredPosition = view.findViewById(R.id.tv_desired_position);
        tvDesiredSalary = view.findViewById(R.id.tv_desired_salary);
        tvDesiredSalaryType = view.findViewById(R.id.tv_desired_salary_type);
        tvWorkType = view.findViewById(R.id.tv_work_type);
        tvWorkTime = view.findViewById(R.id.tv_work_time);
        tvWorkScheduleType = view.findViewById(R.id.tv_work_schedule_type);
        tvWorkExperience = view.findViewById(R.id.tv_work_experience);
        tvTotalExperience = view.findViewById(R.id.tv_total_experience);

        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnDownloadCv = view.findViewById(R.id.btn_download_cv);
        btnEditBasicInfo = view.findViewById(R.id.btn_edit_basic_info);
        btnEditCareerGoal = view.findViewById(R.id.btn_edit_career_goal);
        btnEditDesiredPosition = view.findViewById(R.id.btn_edit_desired_position);
        btnEditWorkExperience = view.findViewById(R.id.btn_edit_work_experience);
    }

    private void setupClickListeners() {
        // Các nút chỉnh sửa có thể mở activity chỉnh sửa
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng chỉnh sửa hồ sơ đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnDownloadCv.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng tải CV đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnEditBasicInfo.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa thông tin cơ bản", Toast.LENGTH_SHORT).show();
        });

        btnEditCareerGoal.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa mục tiêu nghề nghiệp", Toast.LENGTH_SHORT).show();
        });

        btnEditDesiredPosition.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa vị trí mong muốn", Toast.LENGTH_SHORT).show();
        });

        btnEditWorkExperience.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chỉnh sửa kinh nghiệm làm việc", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        Call<ApiResponse> call = apiService.getMyProfile();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Boolean success = apiResponse.isSuccess();

                    if (success != null && success && apiResponse.getData() != null) {
                        if (apiResponse.getData() instanceof java.util.Map) {
                            java.util.Map<String, Object> profileMap = (java.util.Map<String, Object>) apiResponse.getData();
                            Profile profile = convertMapToProfile(profileMap);

                            if (profile != null) {
                                displayProfileData(profile);
                            } else {
                                Toast.makeText(getContext(), "Dữ liệu hồ sơ không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Dữ liệu không đúng định dạng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Không thể tải hồ sơ";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else if (response.body() != null) {
                    // Trường hợp có body nhưng không thành công
                    ApiResponse apiResponse = response.body();
                    String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi hệ thống";
                    Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                } else {
                    // Trường hợp không có body hoặc xảy ra lỗi khác
                    int statusCode = response.code();
                    Toast.makeText(getContext(), "Lỗi API: " + statusCode, Toast.LENGTH_SHORT).show();
                    System.out.println("API Error Code: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Connection Error: " + t.getMessage());
                t.printStackTrace();
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

            // Không xử lý thông tin user nữa do API đã được cập nhật để tránh circular reference
            // Không có thông tin user trong response mới

            if (map.containsKey("urlAnhDaiDien") && map.get("urlAnhDaiDien") != null) {
                profile.setUrlAnhDaiDien(map.get("urlAnhDaiDien").toString());
            }

            if (map.containsKey("hoTen") && map.get("hoTen") != null) {
                profile.setHoTen(map.get("hoTen").toString());
            }

            if (map.containsKey("gioiTinh") && map.get("gioiTinh") != null) {
                profile.setGioiTinh(map.get("gioiTinh").toString());
            }

            if (map.containsKey("ngaySinh") && map.get("ngaySinh") != null) {
                profile.setNgaySinh(map.get("ngaySinh").toString());
            }

            if (map.containsKey("soDienThoai") && map.get("soDienThoai") != null) {
                profile.setSoDienThoai(map.get("soDienThoai").toString());
            }

            if (map.containsKey("trinhDoHocVan") && map.get("trinhDoHocVan") != null) {
                profile.setTrinhDoHocVan(map.get("trinhDoHocVan").toString());
            }

            if (map.containsKey("tinhTrangHocVan") && map.get("tinhTrangHocVan") != null) {
                profile.setTinhTrangHocVan(map.get("tinhTrangHocVan").toString());
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

            if (map.containsKey("gioiThieuBanThan") && map.get("gioiThieuBanThan") != null) {
                profile.setGioiThieuBanThan(map.get("gioiThieuBanThan").toString());
            }

            if (map.containsKey("urlCv") && map.get("urlCv") != null) {
                profile.setUrlCv(map.get("urlCv").toString());
            }

            if (map.containsKey("congKhai") && map.get("congKhai") != null) {
                Object congKhaiObj = map.get("congKhai");
                if (congKhaiObj instanceof Boolean) {
                    profile.setCongKhai((Boolean) congKhaiObj);
                } else {
                    profile.setCongKhai(Boolean.parseBoolean(congKhaiObj.toString()));
                }
            }

            if (map.containsKey("viTriMongMuon") && map.get("viTriMongMuon") != null) {
                profile.setViTriMongMuon(map.get("viTriMongMuon").toString());
            }

            if (map.containsKey("thoiGianMongMuon") && map.get("thoiGianMongMuon") != null) {
                profile.setThoiGianMongMuon(map.get("thoiGianMongMuon").toString());
            }

            if (map.containsKey("loaiThoiGianLamViec") && map.get("loaiThoiGianLamViec") != null) {
                profile.setLoaiThoiGianLamViec(map.get("loaiThoiGianLamViec").toString());
            }

            if (map.containsKey("hinhThucLamViec") && map.get("hinhThucLamViec") != null) {
                profile.setHinhThucLamViec(map.get("hinhThucLamViec").toString());
            }

            if (map.containsKey("loaiLuongMongMuon") && map.get("loaiLuongMongMuon") != null) {
                profile.setLoaiLuongMongMuon(map.get("loaiLuongMongMuon").toString());
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

            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void displayProfileData(Profile profile) {
        // Hiển thị thông tin cơ bản
        if (profile.getHoTen() != null) {
            tvFullName.setText(profile.getHoTen());
            tvBasicFullName.setText(profile.getHoTen());
        } else {
            tvFullName.setText("Chưa cập nhật");
            tvBasicFullName.setText("Chưa cập nhật");
        }

        // Hiển thị ảnh đại diện nếu có
        if (profile.getUrlAnhDaiDien() != null && !profile.getUrlAnhDaiDien().isEmpty()) {
            String imageUrl = "http://192.168.1.8:8080" + profile.getUrlAnhDaiDien();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .into(imgAvatar);
        }

        // Hiển thị các thông tin khác
        // Lấy email/tài khoản từ SharedPreferences vì API không trả về thông tin user nữa
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
        String username = sharedPreferences.getString(Constants.KEY_USERNAME, "Chưa đăng nhập");
        tvEmail.setText(username); // Trong hệ thống của bạn, username có thể là email

        tvPhone.setText(profile.getSoDienThoai() != null ? profile.getSoDienThoai() : "Chưa cập nhật");
        tvGender.setText(profile.getGioiTinh() != null ? profile.getGioiTinh() : "Chưa cập nhật");
        tvDob.setText(profile.getNgaySinh() != null ? profile.getNgaySinh() : "Chưa cập nhật");
        tvEducation.setText(profile.getTrinhDoHocVan() != null ? profile.getTrinhDoHocVan() : "Chưa cập nhật");

        // Hiển thị giới thiệu bản thân
        tvIntroduction.setText(profile.getGioiThieuBanThan() != null ? profile.getGioiThieuBanThan() : "Chưa cập nhật");

        // Hiển thị mục tiêu nghề nghiệp - sử dụng thời gian mong muốn vì không có trường riêng cho mục tiêu nghề nghiệp
        tvCareerGoal.setText(profile.getThoiGianMongMuon() != null ? profile.getThoiGianMongMuon() : "Chưa cập nhật");

        // Hiển thị vị trí mong muốn
        tvDesiredPosition.setText(profile.getViTriMongMuon() != null ? profile.getViTriMongMuon() : "Chưa cập nhật");

        // Hiển thị mức lương mong muốn
        if (profile.getMucLuongMongMuon() != null) {
            tvDesiredSalary.setText(String.format("%,d VNĐ", profile.getMucLuongMongMuon()));
        } else {
            tvDesiredSalary.setText("Chưa cập nhật");
        }

        // Hiển thị loại lương mong muốn
        tvDesiredSalaryType.setText(profile.getLoaiLuongMongMuon() != null ? profile.getLoaiLuongMongMuon() : "Chưa cập nhật");

        // Hiển thị hình thức làm việc
        tvWorkType.setText(profile.getHinhThucLamViec() != null ? profile.getHinhThucLamViec() : "Chưa cập nhật");

        // Hiển thị thời gian mong muốn
        tvWorkTime.setText(profile.getThoiGianMongMuon() != null ? profile.getThoiGianMongMuon() : "Chưa cập nhật");

        // Hiển thị loại thời gian làm việc
        tvWorkScheduleType.setText(profile.getLoaiThoiGianLamViec() != null ? profile.getLoaiThoiGianLamViec() : "Chưa cập nhật");

        // Hiển thị kinh nghiệm làm việc
        String kinhNghiem = profile.getKinhNghiem() != null ? profile.getKinhNghiem() : "Chưa cập nhật";
        tvWorkExperience.setText(kinhNghiem);

        // Hiển thị tổng số năm kinh nghiệm
        if (profile.getTongNamKinhNghiem() != null) {
            tvTotalExperience.setText(String.format("%.1f năm", profile.getTongNamKinhNghiem()));
        } else {
            tvTotalExperience.setText("0 năm");
        }

        // Cập nhật trạng thái hiện tại dựa trên thông tin hồ sơ
        updateCurrentStatus(profile);
    }

    private void updateCurrentStatus(Profile profile) {
        // Cập nhật trạng thái dựa trên mức độ hoàn thiện hồ sơ
        int completedFields = 0;
        int totalFields = 8; // Số trường thông tin quan trọng

        if (profile.getHoTen() != null && !profile.getHoTen().isEmpty()) completedFields++;
        if (profile.getSoDienThoai() != null && !profile.getSoDienThoai().isEmpty()) completedFields++;
        if (profile.getGioiTinh() != null && !profile.getGioiTinh().isEmpty()) completedFields++;
        if (profile.getNgaySinh() != null && !profile.getNgaySinh().isEmpty()) completedFields++;
        if (profile.getTrinhDoHocVan() != null && !profile.getTrinhDoHocVan().isEmpty()) completedFields++;
        if (profile.getViTriMongMuon() != null && !profile.getViTriMongMuon().isEmpty()) completedFields++;
        if (profile.getGioiThieuBanThan() != null && !profile.getGioiThieuBanThan().isEmpty()) completedFields++;
        if (profile.getUrlAnhDaiDien() != null && !profile.getUrlAnhDaiDien().isEmpty()) completedFields++;

        String status = String.format("Hồ sơ hoàn thiện: %d/%d", completedFields, totalFields);
        tvCurrentStatus.setText(status);
    }
}