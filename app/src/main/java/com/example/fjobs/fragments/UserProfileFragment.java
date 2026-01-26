package com.example.fjobs.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.utils.ServerConfig;
import com.example.fjobs.activities.MainActivity;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.Profile;
import com.example.fjobs.utils.Constants;
import com.example.fjobs.utils.ImageUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.File;
import java.io.IOException;

public class UserProfileFragment extends Fragment {

    private CircleImageView imgAvatar;
    private TextView tvFullName, tvCurrentStatus, tvEmail, tvPhone, tvGender, tvDob, tvEducation;
    private TextView tvBasicFullName, tvIntroduction, tvCareerGoal, tvDesiredPosition, tvDesiredSalary;
    private TextView tvDesiredSalaryType, tvWorkType, tvWorkTime, tvWorkScheduleType, tvWorkExperience, tvTotalExperience;
    private TextView tvCvStatus;
    private Button btnEditProfile, btnDownloadCv, btnEditBasicInfo, btnEditCareerGoal, btnEditDesiredPosition, btnEditWorkExperience, btnViewCv;

    private ApiService apiService;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cvPickerLauncher;
    private Profile currentProfile; // Thêm biến lưu trữ profile hiện tại

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        initViews(view);
        initLaunchers();

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

    private void initLaunchers() {
        // Khởi tạo launcher để chọn ảnh đại diện
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            updateAvatar(selectedImageUri);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Lỗi khi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );

        // Khởi tạo launcher để chọn file CV
        cvPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedCvUri = result.getData().getData();
                    if (selectedCvUri != null) {
                        try {
                            updateCv(selectedCvUri);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Lỗi khi xử lý CV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        );
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
        tvCvStatus = view.findViewById(R.id.tv_cv_status);
        btnViewCv = view.findViewById(R.id.btn_view_cv);
    }

    private void setupClickListeners() {
        // Gán sự kiện click cho ảnh đại diện để chọn ảnh mới
        imgAvatar.setOnClickListener(v -> openImagePicker());

        btnEditProfile.setOnClickListener(v -> {
            // Replace current fragment with EditProfileFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });

        btnDownloadCv.setOnClickListener(v -> {
            openCvPicker(); // Thay vì chỉ hiện toast, gọi hàm chọn file CV
        });

        // Chuyển các nút chỉnh sửa riêng lẻ sang EditProfileFragment
        btnEditBasicInfo.setOnClickListener(v -> {
            // Replace current fragment with EditProfileFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });

        btnEditCareerGoal.setOnClickListener(v -> {
            // Replace current fragment with EditProfileFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });

        btnEditDesiredPosition.setOnClickListener(v -> {
            // Replace current fragment with EditProfileFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });

        btnEditWorkExperience.setOnClickListener(v -> {
            // Replace current fragment with EditProfileFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
            }
        });

        btnViewCv.setOnClickListener(v -> {
            viewCv();
        });
    }

    private void viewCv() {
        // Mở URL CV trong trình duyệt hoặc hiển thị PDF nếu có thư viện hỗ trợ
        if (currentProfile != null && currentProfile.getUrlCv() != null && !currentProfile.getUrlCv().isEmpty()) {
            String cvUrl = ServerConfig.getBaseUrl() + currentProfile.getUrlCv();
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cvUrl));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể mở CV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Chưa có CV nào được upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void openCvPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Chỉ chọn file PDF
        cvPickerLauncher.launch(intent);
    }

    private void updateAvatar(Uri imageUri) {
        if (getContext() == null) return;

        Toast.makeText(getContext(), "Đang upload ảnh đại diện...", Toast.LENGTH_SHORT).show();

        try {
            File file = ImageUtils.uriToFile(requireContext(), imageUri);

            // Tạo request body
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", file.getName(), requestFile);

            // Gọi API để upload avatar
            Call<ApiResponse> call = apiService.uploadAvatar(body);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            // Cập nhật lại ảnh trên giao diện
                            String avatarUrl = apiResponse.getData().toString();
                            // Ghi log để kiểm tra URL trả về từ API
                            System.out.println("Avatar URL từ API: " + avatarUrl);

                            // Xử lý trường hợp avatarUrl không bắt đầu bằng dấu /
                            String imageUrl;
                            if (avatarUrl.startsWith("/")) {
                                imageUrl = ServerConfig.getBaseUrl() + avatarUrl;
                            } else {
                                imageUrl = ServerConfig.getBaseUrl() + "/" + avatarUrl;
                            }

                            // Ghi log để kiểm tra URL sẽ dùng để load ảnh
                            System.out.println("Image URL sẽ load: " + imageUrl);

                            try {
                                // Đảm bảo rằng imgAvatar đã có kích thước hợp lệ trước khi load ảnh
                                imgAvatar.post(() -> {
                                    if (imgAvatar.getWidth() > 0 && imgAvatar.getHeight() > 0) {
                                        Glide.with(requireContext())
                                                .load(imageUrl)
                                                .placeholder(R.drawable.ic_default_avatar)
                                                .error(R.drawable.ic_default_avatar)
                                                .into(imgAvatar);
                                    } else {
                                        // Nếu chưa có kích thước, đợi thêm một chút
                                        imgAvatar.post(() -> {
                                            Glide.with(requireContext())
                                                    .load(imageUrl)
                                                    .placeholder(R.drawable.ic_default_avatar)
                                                    .error(R.drawable.ic_default_avatar)
                                                    .into(imgAvatar);
                                        });
                                    }
                                });
                            } catch (Exception e) {
                                System.out.println("Lỗi load ảnh bằng Glide: " + e.getMessage());
                                // Fallback: sử dụng ảnh mặc định nếu có lỗi
                                imgAvatar.setImageResource(R.drawable.ic_default_avatar);
                            }

                            // Cập nhật avatar trong navigation header
                            if (getActivity() instanceof MainActivity) {
                                MainActivity mainActivity = (MainActivity) getActivity();
                                mainActivity.updateNavHeaderAvatar(avatarUrl);
                            }

                            Toast.makeText(getContext(), "Upload ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Upload thất bại";
                            Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Thêm thông tin lỗi chi tiết
                        int code = response.code();
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = "Không thể đọc lỗi phản hồi";
                        }
                        Toast.makeText(getContext(), "Upload thất bại, mã lỗi: " + code + ", " + errorBody, Toast.LENGTH_SHORT).show();
                        System.out.println("Upload lỗi chi tiết: " + code + " - " + errorBody);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Thêm thông tin lỗi chi tiết hơn
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Lỗi upload chi tiết: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        } catch (IOException e) {
            Toast.makeText(getContext(), "Lỗi khi xử lý file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCv(Uri cvUri) {
        if (getContext() == null) return;

        Toast.makeText(getContext(), "Đang upload CV...", Toast.LENGTH_SHORT).show();

        try {
            File file = ImageUtils.uriToFile(requireContext(), cvUri);

            // Tạo request body
            RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("cvFile", file.getName(), requestFile);

            // Gọi API để upload CV
            Call<ApiResponse> call = apiService.uploadCv(body);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                            String cvUrl = apiResponse.getData().toString();
                            System.out.println("CV URL từ API: " + cvUrl);
                            Toast.makeText(getContext(), "Upload CV thành công", Toast.LENGTH_SHORT).show();

                            // Cập nhật lại profile để hiển thị thông tin mới
                            if (currentProfile != null) {
                                currentProfile.setUrlCv(cvUrl);
                                updateCvInfo(currentProfile);
                            }
                        } else {
                            String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Upload CV thất bại";
                            Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Thêm thông tin lỗi chi tiết
                        int code = response.code();
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = "Không thể đọc lỗi phản hồi";
                        }
                        Toast.makeText(getContext(), "Upload CV thất bại, mã lỗi: " + code + ", " + errorBody, Toast.LENGTH_SHORT).show();
                        System.out.println("Upload CV lỗi chi tiết: " + code + " - " + errorBody);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Thêm thông tin lỗi chi tiết hơn
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Lỗi upload CV chi tiết: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        } catch (IOException e) {
            Toast.makeText(getContext(), "Lỗi khi xử lý file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

            // Xử lý thông tin người dùng nếu có trong phản hồi (để cập nhật email và số điện thoại vào SharedPreferences)
            if (map.containsKey("user") && map.get("user") instanceof java.util.Map) {
                java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) map.get("user");

                // Cập nhật email và số điện thoại vào SharedPreferences nếu có
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (userMap.containsKey("email") && userMap.get("email") != null) {
                    editor.putString(Constants.KEY_EMAIL, userMap.get("email").toString());
                }

                if (userMap.containsKey("soDienThoai") && userMap.get("soDienThoai") != null) {
                    // Nếu số điện thoại không có trong profile, sử dụng từ user
                    if (profile.getSoDienThoai() == null || profile.getSoDienThoai().isEmpty()) {
                        profile.setSoDienThoai(userMap.get("soDienThoai").toString());
                    }
                }

                editor.apply();
            }

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

            // Ưu tiên sử dụng số điện thoại từ phản hồi trực tiếp nếu có, nếu không thì từ user
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
            String avatarUrl = profile.getUrlAnhDaiDien();
            System.out.println("Avatar URL từ profile: " + avatarUrl);
            // Xử lý trường hợp avatarUrl không bắt đầu bằng dấu /
            String imageUrl;
            if (avatarUrl.startsWith("/")) {
                imageUrl = ServerConfig.getBaseUrl() + avatarUrl;
            } else {
                imageUrl = ServerConfig.getBaseUrl() + "/" + avatarUrl;
            }
            System.out.println("Image URL sẽ load: " + imageUrl);

            try {
                // Đảm bảo rằng imgAvatar đã có kích thước hợp lệ trước khi load ảnh
                imgAvatar.post(() -> {
                    if (imgAvatar.getWidth() > 0 && imgAvatar.getHeight() > 0) {
                        Glide.with(UserProfileFragment.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_default_avatar)
                                .error(R.drawable.ic_default_avatar)
                                .into(imgAvatar);
                    } else {
                        // Nếu chưa có kích thước, đợi thêm một chút
                        imgAvatar.post(() -> {
                            Glide.with(UserProfileFragment.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_default_avatar)
                                    .error(R.drawable.ic_default_avatar)
                                    .into(imgAvatar);
                        });
                    }
                });
            } catch (Exception e) {
                System.out.println("Lỗi load ảnh bằng Glide: " + e.getMessage());
                // Fallback: sử dụng ảnh mặc định nếu có lỗi
                imgAvatar.setImageResource(R.drawable.ic_default_avatar);
            }
        }

        // Hiển thị các thông tin khác
        // Lấy thông tin người dùng từ SharedPreferences vì API có thể không trả về thông tin user trong phản hồi hồ sơ
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
        String email = sharedPreferences.getString(Constants.KEY_EMAIL, "Chưa cập nhật");
        String username = sharedPreferences.getString(Constants.KEY_USERNAME, "Chưa đăng nhập");

        // Ưu tiên sử dụng email nếu có, nếu không thì dùng username
        tvEmail.setText(email != null && !email.equals("Chưa đăng nhập") ? email : username);

        // Cố gắng lấy số điện thoại từ profile, nếu không có thì để trống
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

        // Cập nhật thông tin CV
        updateCvInfo(profile);

        // Lưu profile hiện tại để sử dụng sau này
        currentProfile = profile;
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

    private void updateCvInfo(Profile profile) {
        // Cập nhật thông tin CV
        if (profile.getUrlCv() != null && !profile.getUrlCv().isEmpty()) {
            tvCvStatus.setText("Đã upload");
            tvCvStatus.setTextColor(requireContext().getResources().getColor(android.R.color.holo_green_dark));
            btnViewCv.setEnabled(true);
        } else {
            tvCvStatus.setText("Chưa upload");
            tvCvStatus.setTextColor(requireContext().getResources().getColor(android.R.color.darker_gray));
            btnViewCv.setEnabled(false);
        }
    }
}