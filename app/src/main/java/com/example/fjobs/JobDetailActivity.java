package com.example.fjobs;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.JobDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import java.util.ArrayList;
import java.util.List;

public class JobDetailActivity extends AppCompatActivity {
    // Các view cũ
    private TextView tvJobTitleDetail, tvJobSalaryDetail, tvJobDeadlineDetail, tvJobDescriptionDetail;
    private Button btnApplyJob;
    private ImageView ivCompanyLogoDetail;
    private TextView tvCompanyNameDetail;

    // Các view mới cho thông tin công việc
    private LinearLayout listRequirements;
    private LinearLayout listBenefits;

    // Các view cho hàng tiêu đề-giá trị
    private TextView infoRowPositionTitle, infoRowPositionValue;
    private TextView infoRowSalaryTitle, infoRowSalaryValue;
    private TextView infoRowFormTitle, infoRowFormValue;
    private TextView infoRowQuantityTitle, infoRowQuantityValue;
    private TextView infoRowGenderTitle, infoRowGenderValue;
    private TextView infoRowPostDateTitle, infoRowPostDateValue;
    private TextView infoRowDeadlineTitle, infoRowDeadlineValue;

    // Thêm nút lưu công việc
    private Button btnSaveJob;
    private boolean isJobSaved = false;
    private int savedJobId = -1;

    private int jobId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        initViews();

        // Khởi tạo ApiService với đảm bảo rằng ApiClient đã được khởi tạo với context
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        if (retrofit != null) {
            apiService = retrofit.create(ApiService.class);
        } else {
            // Nếu chưa có retrofit, cần khởi tạo lại với context
            ApiClient.initialize(this);
            apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        }

        // Lấy ID công việc từ intent
        jobId = getIntent().getIntExtra("job_id", -1);
        if (jobId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadJobDetails();
        setupClickListeners();
        checkJobSavedStatus();
    }

    private void initViews() {
        // Các view cũ
        tvJobTitleDetail = findViewById(R.id.tv_job_title_detail);
        tvJobSalaryDetail = findViewById(R.id.tv_job_salary_detail);
        tvJobDeadlineDetail = findViewById(R.id.tv_job_deadline_detail);
        tvJobDescriptionDetail = findViewById(R.id.tv_job_description_detail);
        btnApplyJob = findViewById(R.id.btn_apply_job);
        btnSaveJob = findViewById(R.id.btn_save_job); // Thêm nút lưu công việc

        // Các view mới
        ivCompanyLogoDetail = findViewById(R.id.iv_company_logo_detail);
        tvCompanyNameDetail = findViewById(R.id.tv_company_name_detail);
        listRequirements = findViewById(R.id.list_requirements);
        listBenefits = findViewById(R.id.list_benefits);

        // Các view cho hàng tiêu đề-giá trị - truy cập qua các layout include
        LinearLayout infoRowPosition = findViewById(R.id.info_row_position);
        infoRowPositionTitle = infoRowPosition.findViewById(R.id.text1);
        infoRowPositionValue = infoRowPosition.findViewById(R.id.text2);

        LinearLayout infoRowSalary = findViewById(R.id.info_row_salary);
        infoRowSalaryTitle = infoRowSalary.findViewById(R.id.text1);
        infoRowSalaryValue = infoRowSalary.findViewById(R.id.text2);

        LinearLayout infoRowForm = findViewById(R.id.info_row_form);
        infoRowFormTitle = infoRowForm.findViewById(R.id.text1);
        infoRowFormValue = infoRowForm.findViewById(R.id.text2);

        LinearLayout infoRowQuantity = findViewById(R.id.info_row_quantity);
        infoRowQuantityTitle = infoRowQuantity.findViewById(R.id.text1);
        infoRowQuantityValue = infoRowQuantity.findViewById(R.id.text2);

        LinearLayout infoRowGender = findViewById(R.id.info_row_gender);
        infoRowGenderTitle = infoRowGender.findViewById(R.id.text1);
        infoRowGenderValue = infoRowGender.findViewById(R.id.text2);

        LinearLayout infoRowPostDate = findViewById(R.id.info_row_postdate);
        infoRowPostDateTitle = infoRowPostDate.findViewById(R.id.text1);
        infoRowPostDateValue = infoRowPostDate.findViewById(R.id.text2);

        LinearLayout infoRowDeadline = findViewById(R.id.info_row_deadline);
        infoRowDeadlineTitle = infoRowDeadline.findViewById(R.id.text1);
        infoRowDeadlineValue = infoRowDeadline.findViewById(R.id.text2);
    }

    private void setupClickListeners() {
        btnApplyJob.setOnClickListener(v -> {
            applyForJob();
        });

        btnSaveJob.setOnClickListener(v -> {
            if (isJobSaved) {
                unsaveJob();
            } else {
                saveJob();
            }
        });
    }

    private void loadJobDetails() {
        Call<ApiResponse> call = apiService.getJobById(jobId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API sang JobDetail
                        // Dữ liệu từ API thường được deserialize thành LinkedTreeMap
                        // nên cần xử lý chuyển đổi đúng cách
                        if (apiResponse.getData() instanceof java.util.Map) {
                            java.util.Map<String, Object> map = (java.util.Map<String, Object>) apiResponse.getData();
                            JobDetail job = convertMapToJobDetail(map);

                            if (job != null) {
                                displayJobDetails(job);
                            } else {
                                Toast.makeText(JobDetailActivity.this, "Dữ liệu công việc không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(JobDetailActivity.this, "Dữ liệu công việc không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(JobDetailActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobDetailActivity.this, "Không thể tải chi tiết công việc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JobDetail convertMapToJobDetail(java.util.Map<String, Object> map) {
        try {
            JobDetail job = new JobDetail();

            // Chuyển đổi các trường dữ liệu
            if (map.containsKey("maCongViec")) {
                Object maCongViecObj = map.get("maCongViec");
                if (maCongViecObj instanceof Integer) {
                    job.setMaCongViec((Integer) maCongViecObj);
                } else if (maCongViecObj instanceof Double) {
                    job.setMaCongViec(((Double) maCongViecObj).intValue());
                } else {
                    job.setMaCongViec(Integer.parseInt(maCongViecObj.toString()));
                }
            }

            if (map.containsKey("tieuDe") && map.get("tieuDe") != null) {
                job.setTieuDe(map.get("tieuDe").toString());
            }

            if (map.containsKey("luong") && map.get("luong") != null) {
                Object luongObj = map.get("luong");
                if (luongObj instanceof Integer) {
                    job.setLuong((Integer) luongObj);
                } else if (luongObj instanceof Double) {
                    job.setLuong(((Double) luongObj).intValue());
                } else {
                    job.setLuong(Integer.parseInt(luongObj.toString()));
                }
            }

            if (map.containsKey("loaiLuong") && map.get("loaiLuong") != null) {
                job.setLoaiLuong(map.get("loaiLuong").toString());
            }

            if (map.containsKey("chiTiet") && map.get("chiTiet") != null) {
                job.setChiTiet(map.get("chiTiet").toString());
            }

            if (map.containsKey("ngayKetThucTuyenDung") && map.get("ngayKetThucTuyenDung") != null) {
                job.setNgayKetThucTuyenDung(map.get("ngayKetThucTuyenDung").toString());
            }

            if (map.containsKey("ngayDang") && map.get("ngayDang") != null) {
                job.setNgayDang(map.get("ngayDang").toString());
            }

            if (map.containsKey("luotXem") && map.get("luotXem") != null) {
                Object luotXemObj = map.get("luotXem");
                if (luotXemObj instanceof Integer) {
                    job.setLuotXem((Integer) luotXemObj);
                } else if (luotXemObj instanceof Double) {
                    job.setLuotXem(((Double) luotXemObj).intValue());
                } else {
                    job.setLuotXem(Integer.parseInt(luotXemObj.toString()));
                }
            }

            if (map.containsKey("trangThaiDuyet") && map.get("trangThaiDuyet") != null) {
                job.setTrangThaiDuyet(map.get("trangThaiDuyet").toString());
            }

            if (map.containsKey("trangThaiTinTuyen") && map.get("trangThaiTinTuyen") != null) {
                job.setTrangThaiTinTuyen(map.get("trangThaiTinTuyen").toString());
            }

            // Xử lý các trường khác nếu có trong dữ liệu trả về từ API
            if (map.containsKey("soLuongTuyen") && map.get("soLuongTuyen") != null) {
                Object soLuongTuyenObj = map.get("soLuongTuyen");
                if (soLuongTuyenObj instanceof Integer) {
                    job.setSoLuongTuyen((Integer) soLuongTuyenObj);
                } else if (soLuongTuyenObj instanceof Double) {
                    job.setSoLuongTuyen(((Double) soLuongTuyenObj).intValue());
                } else {
                    job.setSoLuongTuyen(Integer.parseInt(soLuongTuyenObj.toString()));
                }
            }

            if (map.containsKey("gioiTinhYeuCau") && map.get("gioiTinhYeuCau") != null) {
                job.setGioiTinhYeuCau(map.get("gioiTinhYeuCau").toString());
            }

            if (map.containsKey("gioBatDau") && map.get("gioBatDau") != null) {
                job.setGioBatDau(map.get("gioBatDau").toString());
            }

            if (map.containsKey("gioKetThuc") && map.get("gioKetThuc") != null) {
                job.setGioKetThuc(map.get("gioKetThuc").toString());
            }

            if (map.containsKey("coTheThuongLuongGio") && map.get("coTheThuongLuongGio") != null) {
                job.setCoTheThuongLuongGio(map.get("coTheThuongLuongGio").toString());
            }

            if (map.containsKey("ngayLamViec") && map.get("ngayLamViec") != null) {
                job.setNgayLamViec(map.get("ngayLamViec").toString());
            }

            if (map.containsKey("thoiHanLamViec") && map.get("thoiHanLamViec") != null) {
                job.setThoiHanLamViec(map.get("thoiHanLamViec").toString());
            }

            if (map.containsKey("coTheThuongLuongNgay") && map.get("coTheThuongLuongNgay") != null) {
                job.setCoTheThuongLuongNgay(map.get("coTheThuongLuongNgay").toString());
            }

            if (map.containsKey("yeuCauCongViec") && map.get("yeuCauCongViec") != null) {
                job.setYeuCauCongViec(map.get("yeuCauCongViec").toString());
            }

            if (map.containsKey("quyenLoi") && map.get("quyenLoi") != null) {
                job.setQuyenLoi(map.get("quyenLoi").toString());
            }

            // Xử lý thông tin công ty nếu có
            if (map.containsKey("company") && map.get("company") != null) {
                java.util.Map<String, Object> companyMap = (java.util.Map<String, Object>) map.get("company");
                com.example.fjobs.models.Company company = convertMapToCompany(companyMap);
                job.setCompany(company);
            }

            return job;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private com.example.fjobs.models.Company convertMapToCompany(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.Company company = new com.example.fjobs.models.Company();

            if (map.containsKey("maCongTy") && map.get("maCongTy") != null) {
                Object maCongTyObj = map.get("maCongTy");
                if (maCongTyObj instanceof Integer) {
                    company.setMaCongTy((Integer) maCongTyObj);
                } else if (maCongTyObj instanceof Double) {
                    company.setMaCongTy(((Double) maCongTyObj).intValue());
                } else {
                    company.setMaCongTy(Integer.parseInt(maCongTyObj.toString()));
                }
            }

            if (map.containsKey("tenCongTy") && map.get("tenCongTy") != null) {
                company.setTenCongTy(map.get("tenCongTy").toString());
            }

            if (map.containsKey("tenNguoiDaiDien") && map.get("tenNguoiDaiDien") != null) {
                company.setTenNguoiDaiDien(map.get("tenNguoiDaiDien").toString());
            }

            if (map.containsKey("maSoThue") && map.get("maSoThue") != null) {
                company.setMaSoThue(map.get("maSoThue").toString());
            }

            if (map.containsKey("diaChi") && map.get("diaChi") != null) {
                company.setDiaChi(map.get("diaChi").toString());
            }

            if (map.containsKey("lienHeCty") && map.get("lienHeCty") != null) {
                company.setLienHeCty(map.get("lienHeCty").toString());
            }

            if (map.containsKey("hinhAnhCty") && map.get("hinhAnhCty") != null) {
                company.setHinhAnhCty(map.get("hinhAnhCty").toString());
            }

            if (map.containsKey("daXacThuc") && map.get("daXacThuc") != null) {
                Object daXacThucObj = map.get("daXacThuc");
                if (daXacThucObj instanceof Boolean) {
                    company.setDaXacThuc((Boolean) daXacThucObj);
                } else {
                    company.setDaXacThuc(Boolean.parseBoolean(daXacThucObj.toString()));
                }
            }

            return company;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayJobDetails(JobDetail job) {
        // Hiển thị thông tin cơ bản
        tvJobTitleDetail.setText(job.getTieuDe());
        tvJobSalaryDetail.setText(String.format("%,d VNĐ", job.getLuong()));
        tvJobDeadlineDetail.setText(job.getNgayKetThucTuyenDung());
        tvJobDescriptionDetail.setText(job.getChiTiet());

        // Hiển thị thông tin công ty
        if (job.getCompany() != null) {
            tvCompanyNameDetail.setText(job.getCompany().getTenCongTy());

            // Load logo công ty nếu có
            if (job.getCompany().getHinhAnhCty() != null && !job.getCompany().getHinhAnhCty().isEmpty()) {
                String logoUrl = "http://192.168.1.8:8080" + job.getCompany().getHinhAnhCty();
                Glide.with(this)
                    .load(logoUrl)
                    .placeholder(R.drawable.ic_boss)
                    .error(R.drawable.ic_boss)
                    .into(ivCompanyLogoDetail);
            }
        }

        // Hiển thị thông tin chi tiết công việc
        infoRowPositionTitle.setText("Vị trí");
        infoRowPositionValue.setText(job.getTieuDe());

        infoRowSalaryTitle.setText("Mức lương");
        infoRowSalaryValue.setText(String.format("%,d VNĐ", job.getLuong()));

        infoRowFormTitle.setText("Hình thức");
        infoRowFormValue.setText(job.getLoaiLuong() != null ? job.getLoaiLuong() : "Toàn thời gian");

        infoRowQuantityTitle.setText("Số lượng");
        infoRowQuantityValue.setText(job.getSoLuongTuyen() > 0 ? String.valueOf(job.getSoLuongTuyen()) + " người" : "N/A");

        infoRowGenderTitle.setText("Giới tính");
        infoRowGenderValue.setText(job.getGioiTinhYeuCau() != null ? job.getGioiTinhYeuCau() : "Không yêu cầu");

        infoRowPostDateTitle.setText("Ngày đăng");
        infoRowPostDateValue.setText(job.getNgayDang() != null ? job.getNgayDang().substring(0, 10) : "N/A");

        infoRowDeadlineTitle.setText("Hạn nộp");
        infoRowDeadlineValue.setText(job.getNgayKetThucTuyenDung());

        // Thêm yêu cầu công việc từ dữ liệu thực tế
        if (job.getYeuCauCongViec() != null && !job.getYeuCauCongViec().trim().isEmpty()) {
            // Xử lý chuỗi yêu cầu công việc - có thể là chuỗi JSON hoặc văn bản thường
            String yeuCauCongViec = job.getYeuCauCongViec();

            // Nếu là chuỗi JSON hoặc có định dạng đặc biệt, có thể cần xử lý thêm
            // Ở đây tôi sẽ hiển thị từng dòng nếu có dấu xuống dòng
            String[] lines = yeuCauCongViec.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    addRequirementItem(line.trim());
                }
            }
        } else {
            // Nếu không có dữ liệu yêu cầu công việc, sử dụng dữ liệu mẫu
            if (job.getSoLuongTuyen() > 0) {
                addRequirementItem("Số lượng tuyển: " + job.getSoLuongTuyen() + " người");
            }
            if (job.getNgayLamViec() != null && !job.getNgayLamViec().isEmpty()) {
                addRequirementItem("Ngày làm việc: " + job.getNgayLamViec());
            }
            if (job.getThoiHanLamViec() != null && !job.getThoiHanLamViec().isEmpty()) {
                addRequirementItem("Thời hạn làm việc: " + job.getThoiHanLamViec());
            }
            if (job.getGioBatDau() != null && job.getGioKetThuc() != null) {
                addRequirementItem("Giờ làm: " + job.getGioBatDau() + " - " + job.getGioKetThuc());
            }
        }

        // Thêm quyền lợi từ dữ liệu thực tế
        if (job.getQuyenLoi() != null && !job.getQuyenLoi().trim().isEmpty()) {
            // Xử lý chuỗi quyền lợi - có thể là chuỗi JSON hoặc văn bản thường
            String quyenLoi = job.getQuyenLoi();

            // Nếu là chuỗi JSON hoặc có định dạng đặc biệt, có thể cần xử lý thêm
            // Ở đây tôi sẽ hiển thị từng dòng nếu có dấu xuống dòng
            String[] lines = quyenLoi.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    addBenefitItem(line.trim());
                }
            }
        } else {
            // Nếu không có dữ liệu quyền lợi, sử dụng dữ liệu mẫu
            addBenefitItem("Môi trường làm việc chuyên nghiệp");
            addBenefitItem("Cơ hội thăng tiến rõ ràng");
            addBenefitItem("Các chế độ phúc lợi khác");
        }
    }

    private void addRequirementItem(String text) {
        LinearLayout requirementLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_icon_text_row, null);
        TextView requirementTextView = requirementLayout.findViewById(R.id.rowText);
        requirementTextView.setText(text);
        listRequirements.addView(requirementLayout);
    }

    private void addBenefitItem(String text) {
        LinearLayout benefitLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_benefit_row, null);
        TextView benefitTextView = benefitLayout.findViewById(R.id.rowText);
        benefitTextView.setText(text);
        listBenefits.addView(benefitLayout);
    }

    // Phương thức ứng tuyển công việc
    private void applyForJob() {
        ApiService.AppliedJobRequest request = new ApiService.AppliedJobRequest();
        request.setJobDetailId(Integer.valueOf(jobId)); // Đảm bảo chuyển đổi sang Integer

        Call<ApiResponse> call = apiService.applyForJob(request);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Boolean success = apiResponse.isSuccess();
                    if (success != null && success) {
                        Toast.makeText(JobDetailActivity.this, "Ứng tuyển thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Ứng tuyển thất bại";
                        Toast.makeText(JobDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Thử in ra mã lỗi cụ thể để debug
                    int statusCode = response.code();
                    String errorBody = "Lỗi ứng tuyển: " + statusCode;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (Exception e) {
                        System.out.println("Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(JobDetailActivity.this, "Lỗi API: " + statusCode + " - " + errorBody, Toast.LENGTH_SHORT).show();
                    System.out.println("API Error: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Connection Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    // Phương thức lưu công việc
    private void saveJob() {
        ApiService.SaveJobRequest request = new ApiService.SaveJobRequest();
        request.setJobDetailId(Integer.valueOf(jobId)); // Đảm bảo chuyển đổi sang Integer

        Call<ApiResponse> call = apiService.saveJob(request);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Boolean success = apiResponse.isSuccess();
                    if (success != null && success) {
                        isJobSaved = true;
                        updateSaveButtonState();
                        Toast.makeText(JobDetailActivity.this, "Đã lưu công việc!", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lưu công việc thất bại";
                        Toast.makeText(JobDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Thử in ra mã lỗi cụ thể để debug
                    int statusCode = response.code();
                    String errorBody = "Lỗi lưu công việc: " + statusCode;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (Exception e) {
                        System.out.println("Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(JobDetailActivity.this, "Lỗi API: " + statusCode + " - " + errorBody, Toast.LENGTH_SHORT).show();
                    System.out.println("API Error: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Connection Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    // Phương thức hủy lưu công việc
    private void unsaveJob() {
        ApiService.UnsaveJobRequest request = new ApiService.UnsaveJobRequest();
        request.setJobDetailId(Integer.valueOf(jobId)); // Đảm bảo chuyển đổi sang Integer

        Call<ApiResponse> call = apiService.unsaveJob(request);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Boolean success = apiResponse.isSuccess();
                    if (success != null && success) {
                        isJobSaved = false;
                        updateSaveButtonState();
                        Toast.makeText(JobDetailActivity.this, "Đã bỏ lưu công việc!", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Hủy lưu công việc thất bại";
                        Toast.makeText(JobDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Thử in ra mã lỗi cụ thể để debug
                    int statusCode = response.code();
                    String errorBody = "Lỗi hủy lưu công việc: " + statusCode;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (Exception e) {
                        System.out.println("Error reading error body: " + e.getMessage());
                    }
                    Toast.makeText(JobDetailActivity.this, "Lỗi API: " + statusCode + " - " + errorBody, Toast.LENGTH_SHORT).show();
                    System.out.println("API Error: " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(JobDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Connection Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    // Phương thức kiểm tra trạng thái lưu công việc
    private void checkJobSavedStatus() {
        Call<ApiResponse> call = apiService.checkIfJobSaved(jobId);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Boolean success = apiResponse.isSuccess();
                    if (success != null && success && apiResponse.getData() instanceof java.util.Map) {
                        java.util.Map<String, Object> data = (java.util.Map<String, Object>) apiResponse.getData();
                        Object isSavedObj = data.get("isSaved");
                        if (isSavedObj instanceof Boolean) {
                            isJobSaved = (Boolean) isSavedObj;
                        } else if (isSavedObj instanceof String) {
                            isJobSaved = Boolean.parseBoolean((String) isSavedObj);
                        }
                        if (data.get("savedJobId") != null) {
                            Object savedJobIdObj = data.get("savedJobId");
                            if (savedJobIdObj instanceof Integer) {
                                savedJobId = (Integer) savedJobIdObj;
                            } else if (savedJobIdObj instanceof Double) {
                                savedJobId = ((Double) savedJobIdObj).intValue();
                            } else {
                                savedJobId = Integer.parseInt(savedJobIdObj.toString());
                            }
                        }
                        updateSaveButtonState();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Không làm gì nếu kiểm tra trạng thái thất bại
            }
        });
    }

    // Phương thức cập nhật trạng thái nút lưu
    private void updateSaveButtonState() {
        if (isJobSaved) {
            btnSaveJob.setText("Đã lưu");
            btnSaveJob.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#E53935"))); // Đỏ
        } else {
            btnSaveJob.setText("Lưu công việc");
            btnSaveJob.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#4CAF50"))); // Xanh
        }
    }
}