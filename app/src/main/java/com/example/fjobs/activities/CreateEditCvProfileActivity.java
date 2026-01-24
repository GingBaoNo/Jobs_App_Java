package com.example.fjobs.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.CvProfile;
import com.example.fjobs.utils.FileUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEditCvProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_AVATAR = 1001;
    private static final int REQUEST_CODE_PICK_CV = 1002;
    private static final int REQUEST_CODE_PERMISSION = 1003;

    private EditText etCvProfileName, etCvProfileDescription, etFullName, etDateOfBirth,
            etPhone, etEducationLevel, etEducationStatus, etExperience, etTotalExperienceYears,
            etSelfIntroduction, etDesiredPosition, etDesiredTime, etWorkTimeType,
            etWorkForm, etExpectedSalaryType, etExpectedSalary;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private ImageView ivAvatarPreview;
    private TextView tvCvFilename;
    private CheckBox cbSetAsDefault;
    private Button btnUploadAvatar, btnUploadCv, btnSaveCv, btnCancel;

    private ApiService apiService;
    private String mode; // "create" or "edit"
    private CvProfile currentCvProfile;
    private String avatarFilePath;
    private String cvFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_cv_profile);

        initViews();
        setupClickListeners();
        apiService = ApiClient.getApiService();

        // Nhận dữ liệu từ intent
        mode = getIntent().getStringExtra("mode");
        if ("edit".equals(mode)) {
            currentCvProfile = getIntent().getParcelableExtra("cv_profile");
            if (currentCvProfile != null) {
                populateFormData();
            }
        }
    }

    private void initViews() {
        etCvProfileName = findViewById(R.id.et_cv_profile_name);
        etCvProfileDescription = findViewById(R.id.et_cv_profile_description);
        etFullName = findViewById(R.id.et_full_name);
        rgGender = findViewById(R.id.rg_gender);
        rbMale = findViewById(R.id.rb_male);
        rbFemale = findViewById(R.id.rb_female);
        etDateOfBirth = findViewById(R.id.et_date_of_birth);
        etPhone = findViewById(R.id.et_phone);
        etEducationLevel = findViewById(R.id.et_education_level);
        etEducationStatus = findViewById(R.id.et_education_status);
        etExperience = findViewById(R.id.et_experience);
        etTotalExperienceYears = findViewById(R.id.et_total_experience_years);
        etSelfIntroduction = findViewById(R.id.et_self_introduction);
        etDesiredPosition = findViewById(R.id.et_desired_position);
        etDesiredTime = findViewById(R.id.et_desired_time);
        etWorkTimeType = findViewById(R.id.et_work_time_type);
        etWorkForm = findViewById(R.id.et_work_form);
        etExpectedSalaryType = findViewById(R.id.et_expected_salary_type);
        etExpectedSalary = findViewById(R.id.et_expected_salary);
        ivAvatarPreview = findViewById(R.id.iv_avatar_preview);
        tvCvFilename = findViewById(R.id.tv_cv_filename);
        cbSetAsDefault = findViewById(R.id.cb_set_as_default);
        btnUploadAvatar = findViewById(R.id.btn_upload_avatar);
        btnUploadCv = findViewById(R.id.btn_upload_cv);
        btnSaveCv = findViewById(R.id.btn_save_cv);
        btnCancel = findViewById(R.id.btn_cancel);

        // Cập nhật tiêu đề
        TextView tvTitle = findViewById(R.id.tv_title);
        if ("edit".equals(mode)) {
            tvTitle.setText("Chỉnh sửa hồ sơ CV");
        } else {
            tvTitle.setText("Tạo hồ sơ CV mới");
        }
    }

    private void setupClickListeners() {
        etDateOfBirth.setOnClickListener(v -> showDatePicker());
        
        btnUploadAvatar.setOnClickListener(v -> {
            if (checkPermission()) {
                openImagePicker(REQUEST_CODE_PICK_AVATAR);
            } else {
                requestPermission();
            }
        });

        btnUploadCv.setOnClickListener(v -> {
            if (checkPermission()) {
                openDocumentPicker(REQUEST_CODE_PICK_CV);
            } else {
                requestPermission();
            }
        });

        btnSaveCv.setOnClickListener(v -> saveCvProfile());

        btnCancel.setOnClickListener(v -> finish());
    }

    private void populateFormData() {
        if (currentCvProfile != null) {
            etCvProfileName.setText(currentCvProfile.getTenHoSo());
            etCvProfileDescription.setText(currentCvProfile.getMoTa());
            etFullName.setText(currentCvProfile.getHoTen());
            
            // Thiết lập giới tính
            if ("Nam".equals(currentCvProfile.getGioiTinh()) || "Male".equalsIgnoreCase(currentCvProfile.getGioiTinh())) {
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }
            
            etDateOfBirth.setText(currentCvProfile.getNgaySinh());
            etPhone.setText(currentCvProfile.getSoDienThoai());
            etEducationLevel.setText(currentCvProfile.getTrinhDoHocVan());
            etEducationStatus.setText(currentCvProfile.getTinhTrangHocVan());
            etExperience.setText(currentCvProfile.getKinhNghiem());
            
            if (currentCvProfile.getTongNamKinhNghiem() != null) {
                etTotalExperienceYears.setText(currentCvProfile.getTongNamKinhNghiem().toString());
            }
            
            etSelfIntroduction.setText(currentCvProfile.getGioiThieuBanThan());
            etDesiredPosition.setText(currentCvProfile.getViTriMongMuon());
            etDesiredTime.setText(currentCvProfile.getThoiGianMongMuon());
            etWorkTimeType.setText(currentCvProfile.getLoaiThoiGianLamViec());
            etWorkForm.setText(currentCvProfile.getHinhThucLamViec());
            etExpectedSalaryType.setText(currentCvProfile.getLoaiLuongMongMuon());
            
            if (currentCvProfile.getMucLuongMongMuon() != null) {
                etExpectedSalary.setText(String.valueOf(currentCvProfile.getMucLuongMongMuon()));
            }
            
            // Hiển thị ảnh đại diện nếu có
            if (currentCvProfile.getUrlAnhDaiDien() != null && !currentCvProfile.getUrlAnhDaiDien().isEmpty()) {
                String imageUrl = "http://192.168.1.8:8080" + currentCvProfile.getUrlAnhDaiDien();
                Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_boss)
                    .error(R.drawable.ic_boss)
                    .into(ivAvatarPreview);
            }
            
            // Hiển thị tên file CV nếu có
            if (currentCvProfile.getUrlCv() != null && !currentCvProfile.getUrlCv().isEmpty()) {
                String fileName = new File(currentCvProfile.getUrlCv()).getName();
                tvCvFilename.setText(fileName);
            }
            
            // Thiết lập checkbox hồ sơ mặc định
            cbSetAsDefault.setChecked(currentCvProfile.getLaMacDinh() != null && currentCvProfile.getLaMacDinh());
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format ngày tháng
                String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", 
                    selectedYear, selectedMonth + 1, selectedDay);
                etDateOfBirth.setText(formattedDate);
            },
            year, month, day
        );

        datePickerDialog.show();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            REQUEST_CODE_PERMISSION);
    }

    private void openImagePicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    private void openDocumentPicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                if (requestCode == REQUEST_CODE_PICK_AVATAR) {
                    // Xử lý chọn ảnh đại diện
                    ivAvatarPreview.setImageURI(selectedFileUri);
                    avatarFilePath = FileUtils.getPathFromUri(this, selectedFileUri);
                } else if (requestCode == REQUEST_CODE_PICK_CV) {
                    // Xử lý chọn file CV
                    String fileName = FileUtils.getFileNameFromUri(this, selectedFileUri);
                    tvCvFilename.setText(fileName);
                    cvFilePath = FileUtils.getPathFromUri(this, selectedFileUri);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, có thể thực hiện hành động
            } else {
                Toast.makeText(this, "Cần quyền truy cập bộ nhớ để chọn ảnh và file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveCvProfile() {
        // Validate dữ liệu đầu vào
        if (!validateInput()) {
            return;
        }

        // Tạo đối tượng CvProfile từ dữ liệu nhập
        CvProfile cvProfile = new CvProfile();
        
        cvProfile.setTenHoSo(etCvProfileName.getText().toString().trim());
        cvProfile.setMoTa(etCvProfileDescription.getText().toString().trim());
        cvProfile.setHoTen(etFullName.getText().toString().trim());
        
        // Giới tính
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId == R.id.rb_male) {
            cvProfile.setGioiTinh("Nam");
        } else if (selectedGenderId == R.id.rb_female) {
            cvProfile.setGioiTinh("Nữ");
        }
        
        cvProfile.setNgaySinh(etDateOfBirth.getText().toString().trim());
        cvProfile.setSoDienThoai(etPhone.getText().toString().trim());
        cvProfile.setTrinhDoHocVan(etEducationLevel.getText().toString().trim());
        cvProfile.setTinhTrangHocVan(etEducationStatus.getText().toString().trim());
        cvProfile.setKinhNghiem(etExperience.getText().toString().trim());
        
        String totalExpStr = etTotalExperienceYears.getText().toString().trim();
        if (!totalExpStr.isEmpty()) {
            cvProfile.setTongNamKinhNghiem(java.math.BigDecimal.valueOf(Double.parseDouble(totalExpStr)));
        }
        
        cvProfile.setGioiThieuBanThan(etSelfIntroduction.getText().toString().trim());
        cvProfile.setViTriMongMuon(etDesiredPosition.getText().toString().trim());
        cvProfile.setThoiGianMongMuon(etDesiredTime.getText().toString().trim());
        cvProfile.setLoaiThoiGianLamViec(etWorkTimeType.getText().toString().trim());
        cvProfile.setHinhThucLamViec(etWorkForm.getText().toString().trim());
        cvProfile.setLoaiLuongMongMuon(etExpectedSalaryType.getText().toString().trim());
        
        String expectedSalaryStr = etExpectedSalary.getText().toString().trim();
        if (!expectedSalaryStr.isEmpty()) {
            cvProfile.setMucLuongMongMuon(Integer.parseInt(expectedSalaryStr));
        }
        
        cvProfile.setLaMacDinh(cbSetAsDefault.isChecked());

        if ("edit".equals(mode) && currentCvProfile != null) {
            // Chế độ chỉnh sửa
            updateCvProfile(cvProfile);
        } else {
            // Chế độ tạo mới
            createCvProfile(cvProfile);
        }
    }

    private void createCvProfile(CvProfile cvProfile) {
        Call<ApiResponse> call = apiService.createCvProfile(cvProfile);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Upload ảnh đại diện và CV nếu có
                        handleFileUploads((Integer) ((java.util.Map<String, Object>) apiResponse.getData()).get("maHoSoCv"));
                        Toast.makeText(CreateEditCvProfileActivity.this, "Tạo hồ sơ thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Tạo hồ sơ thất bại";
                        Toast.makeText(CreateEditCvProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateEditCvProfileActivity.this, "Không thể tạo hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CreateEditCvProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCvProfile(CvProfile cvProfile) {
        if (currentCvProfile == null) return;
        
        cvProfile.setMaHoSoCv(currentCvProfile.getMaHoSoCv());
        
        Call<ApiResponse> call = apiService.updateCvProfile(currentCvProfile.getMaHoSoCv(), cvProfile);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Upload ảnh đại diện và CV nếu có
                        handleFileUploads(currentCvProfile.getMaHoSoCv());
                        Toast.makeText(CreateEditCvProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Cập nhật hồ sơ thất bại";
                        Toast.makeText(CreateEditCvProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateEditCvProfileActivity.this, "Không thể cập nhật hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CreateEditCvProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFileUploads(Integer cvProfileId) {
        if (cvProfileId == null) return;
        
        // Upload ảnh đại diện nếu có
        if (avatarFilePath != null) {
            uploadAvatar(cvProfileId);
        }
        
        // Upload CV nếu có
        if (cvFilePath != null) {
            uploadCv(cvProfileId);
        }
    }

    private void uploadAvatar(Integer cvProfileId) {
        if (avatarFilePath == null) return;

        File file = new File(avatarFilePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<ApiResponse> call = apiService.uploadAvatar(multipartBody);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (!apiResponse.isSuccess()) {
                        Toast.makeText(CreateEditCvProfileActivity.this, "Upload ảnh đại diện thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CreateEditCvProfileActivity.this, "Lỗi upload ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadCv(Integer cvProfileId) {
        if (cvFilePath == null) return;

        File file = new File(cvFilePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<ApiResponse> call = apiService.uploadCv(multipartBody);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (!apiResponse.isSuccess()) {
                        Toast.makeText(CreateEditCvProfileActivity.this, "Upload CV thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CreateEditCvProfileActivity.this, "Lỗi upload CV: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput() {
        if (etCvProfileName.getText().toString().trim().isEmpty()) {
            etCvProfileName.setError("Vui lòng nhập tên hồ sơ");
            etCvProfileName.requestFocus();
            return false;
        }

        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return false;
        }

        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}