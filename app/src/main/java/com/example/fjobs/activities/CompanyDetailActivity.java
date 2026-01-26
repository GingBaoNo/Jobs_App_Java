package com.example.fjobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.adapters.JobAdapter;
import com.example.fjobs.adapters.JobItemAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.Company;
import com.example.fjobs.utils.ServerConfig;
import com.example.fjobs.models.JobDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompanyDetailActivity extends AppCompatActivity {
    // Các thành phần mới từ layout mới
    private ImageView backButton, shareButton;
    private ImageView ivCompanyLogo;
    private TextView tvCompanyName, tvVerifiedBadge, tvCompanyLocation, tvCompanyDescription;
    private TextView tvCompanyPhone, tvCompanyPhone1, tvCompanyEmail, tvCompanyEmailDisplay;
    private TextView tvCompanyInfo;
    private TextView tvContactPerson, tvCompanyContact, tvTaxCode, tvCompanyAddress;
    private Button btnViewJobs, btnViewOnMap;

    // RecyclerView và Adapter cho danh sách công việc
    private RecyclerView rvCompanyJobs;
    private JobItemAdapter jobAdapter;
    private List<JobDetail> jobList;
    private int companyId;
    private Company currentCompany; // Biến để lưu trữ thông tin công ty hiện tại
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        initViews();
        setupClickListeners();
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Lấy ID công ty từ intent
        companyId = getIntent().getIntExtra("company_id", -1);
        if (companyId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID công ty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadCompanyDetails();
    }

    private void initViews() {
        // Toolbar components
        backButton = findViewById(R.id.back_button);
        shareButton = findViewById(R.id.share_button);

        // Company Hero Section
        ivCompanyLogo = findViewById(R.id.iv_company_logo);
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvVerifiedBadge = findViewById(R.id.tv_verified_badge);
        tvCompanyLocation = findViewById(R.id.tv_company_location);
        tvCompanyDescription = findViewById(R.id.tv_company_description);
        tvCompanyPhone = findViewById(R.id.tv_company_phone);
        tvCompanyPhone1 = findViewById(R.id.tv_company_phone_1);
        tvCompanyEmail = findViewById(R.id.tv_company_email);
        tvCompanyEmailDisplay = findViewById(R.id.tv_company_email_display);
        tvCompanyContact = findViewById(R.id.tv_company_contact);

        // Introduction section
        tvCompanyInfo = findViewById(R.id.tv_company_info);

        // Contact Information section
        tvContactPerson = findViewById(R.id.tv_contact_person);
        tvCompanyContact = findViewById(R.id.tv_company_contact);
        tvTaxCode = findViewById(R.id.tv_tax_code);
        tvCompanyAddress = findViewById(R.id.tv_company_address);

        // Footer button
        btnViewJobs = findViewById(R.id.sticky_footer);

        // View on map button
        btnViewOnMap = findViewById(R.id.btn_view_on_map);

        // RecyclerView for jobs
        rvCompanyJobs = findViewById(R.id.rv_company_jobs);

        jobList = new ArrayList<>();
        jobAdapter = new JobItemAdapter(jobList, job -> {
            // Click listener để chuyển đến trang chi tiết công việc khi click vào item
            Intent intent = new Intent(this, JobDetailActivity.class);
            intent.putExtra("job_id", job.getMaCongViec());
            startActivity(intent);
        });

        // LayoutManager cho RecyclerView (hiển thị danh sách công việc của công ty)
        rvCompanyJobs.setLayoutManager(new LinearLayoutManager(this));
        rvCompanyJobs.setAdapter(jobAdapter);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Share button
        shareButton.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng chia sẻ", Toast.LENGTH_SHORT).show();
            // Thêm chức năng chia sẻ nếu cần
        });

        // View jobs button
        btnViewJobs.setOnClickListener(v -> {
            Toast.makeText(this, "Hiển thị danh sách công việc đang tuyển", Toast.LENGTH_SHORT).show();
            // Có thể chuyển sang một activity mới để hiển thị danh sách công việc của công ty
        });

        // View on map button
        btnViewOnMap.setOnClickListener(v -> {
            // Kiểm tra xem công ty có tọa độ không trước khi mở bản đồ
            if (currentCompany != null && currentCompany.getKinhDo() != null && currentCompany.getViDo() != null) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("company_id", currentCompany.getMaCongTy());
                intent.putExtra("company_name", currentCompany.getTenCongTy());
                intent.putExtra("latitude", currentCompany.getViDo().doubleValue());
                intent.putExtra("longitude", currentCompany.getKinhDo().doubleValue());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không có thông tin vị trí cho công ty này", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCompanyDetails() {
        // Sử dụng endpoint mới để lấy cả công ty và công việc của công ty
        Call<ApiResponse> call = apiService.getJobsByCompanyId(companyId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Dữ liệu trả về là một Map chứa company và jobs
                        if (apiResponse.getData() instanceof Map) {
                            Map<String, Object> data = (Map<String, Object>) apiResponse.getData();

                            Company company = null;

                            // Lấy thông tin công ty
                            if (data.containsKey("company") && data.get("company") instanceof Map) {
                                Map<String, Object> companyData = (Map<String, Object>) data.get("company");
                                company = new Company();

                                if (companyData.get("maCongTy") != null) {
                                    company.setMaCongTy(((Number) companyData.get("maCongTy")).intValue());
                                }
                                if (companyData.get("tenCongTy") != null) {
                                    company.setTenCongTy((String) companyData.get("tenCongTy"));
                                }
                                if (companyData.get("tenNguoiDaiDien") != null) {
                                    company.setTenNguoiDaiDien((String) companyData.get("tenNguoiDaiDien"));
                                }
                                if (companyData.get("maSoThue") != null) {
                                    company.setMaSoThue((String) companyData.get("maSoThue"));
                                }
                                if (companyData.get("diaChi") != null) {
                                    company.setDiaChi((String) companyData.get("diaChi"));
                                }
                                if (companyData.get("emailCty") != null) {
                                    company.setEmailCty((String) companyData.get("emailCty"));
                                }
                                if (companyData.get("soDienThoaiCty") != null) {
                                    company.setSoDienThoaiCty((String) companyData.get("soDienThoaiCty"));
                                }
                                if (companyData.get("hinhAnhCty") != null) {
                                    company.setHinhAnhCty((String) companyData.get("hinhAnhCty"));
                                }
                                if (companyData.get("daXacThuc") != null) {
                                    company.setDaXacThuc((Boolean) companyData.get("daXacThuc"));
                                }
                                if (companyData.get("moTaCongTy") != null) {
                                    company.setMoTaCongTy((String) companyData.get("moTaCongTy"));
                                }
                                if (companyData.get("trangThai") != null) {
                                    company.setTrangThai((String) companyData.get("trangThai"));
                                }

                                // Ánh xạ tọa độ
                                if (companyData.get("kinhDo") != null) {
                                    Object kinhDoObj = companyData.get("kinhDo");
                                    if (kinhDoObj instanceof Double) {
                                        company.setKinhDo(java.math.BigDecimal.valueOf((Double) kinhDoObj));
                                    } else if (kinhDoObj instanceof Integer) {
                                        company.setKinhDo(java.math.BigDecimal.valueOf((Integer) kinhDoObj));
                                    } else if (kinhDoObj instanceof String) {
                                        company.setKinhDo(new java.math.BigDecimal((String) kinhDoObj));
                                    } else {
                                        company.setKinhDo(java.math.BigDecimal.valueOf(Double.parseDouble(kinhDoObj.toString())));
                                    }
                                }

                                if (companyData.get("viDo") != null) {
                                    Object viDoObj = companyData.get("viDo");
                                    if (viDoObj instanceof Double) {
                                        company.setViDo(java.math.BigDecimal.valueOf((Double) viDoObj));
                                    } else if (viDoObj instanceof Integer) {
                                        company.setViDo(java.math.BigDecimal.valueOf((Integer) viDoObj));
                                    } else if (viDoObj instanceof String) {
                                        company.setViDo(new java.math.BigDecimal((String) viDoObj));
                                    } else {
                                        company.setViDo(java.math.BigDecimal.valueOf(Double.parseDouble(viDoObj.toString())));
                                    }
                                }

                                displayCompanyDetails(company);
                                loadCompanyLogo(company.getHinhAnhCty());
                            }

                            // Lấy danh sách công việc
                            if (data.containsKey("jobs") && data.get("jobs") instanceof List) {
                                List<?> rawJobs = (List<?>) data.get("jobs");
                                jobList.clear();

                                for (Object obj : rawJobs) {
                                    if (obj instanceof Map) {
                                        Map<String, Object> jobData = (Map<String, Object>) obj;
                                        JobDetail job = new JobDetail();

                                        // Ánh xạ các trường từ Map sang JobDetail
                                        if (jobData.get("maCongViec") != null) {
                                            job.setMaCongViec(((Number) jobData.get("maCongViec")).intValue());
                                        }
                                        if (jobData.get("maCongTy") != null) {
                                            job.setMaCongTy(((Number) jobData.get("maCongTy")).intValue());
                                        }
                                        if (jobData.get("tieuDe") != null) {
                                            job.setTieuDe((String) jobData.get("tieuDe"));
                                        }
                                        if (jobData.get("luong") != null) {
                                            job.setLuong(((Number) jobData.get("luong")).intValue());
                                        }
                                        if (jobData.get("loaiLuong") != null) {
                                            job.setLoaiLuong((String) jobData.get("loaiLuong"));
                                        }
                                        if (jobData.get("chiTiet") != null) {
                                            job.setChiTiet((String) jobData.get("chiTiet"));
                                        }
                                        if (jobData.get("ngayKetThucTuyenDung") != null) {
                                            job.setNgayKetThucTuyenDung((String) jobData.get("ngayKetThucTuyenDung"));
                                        }
                                        if (jobData.get("ngayDang") != null) {
                                            job.setNgayDang((String) jobData.get("ngayDang"));
                                        }
                                        if (jobData.get("luotXem") != null) {
                                            job.setLuotXem(((Number) jobData.get("luotXem")).intValue());
                                        }
                                        if (jobData.get("trangThaiDuyet") != null) {
                                            job.setTrangThaiDuyet((String) jobData.get("trangThaiDuyet"));
                                        }
                                        if (jobData.get("trangThaiTinTuyen") != null) {
                                            job.setTrangThaiTinTuyen((String) jobData.get("trangThaiTinTuyen"));
                                        }

                                        // Add company information to the job if not already present
                                        if (job.getCompany() == null && company != null) {
                                            job.setCompany(new Company(
                                                company.getTenCongTy(),
                                                company.getTenNguoiDaiDien(),
                                                company.getMaSoThue(),
                                                company.getDiaChi(),
                                                company.getEmailCty(),
                                                company.getSoDienThoaiCty(),
                                                company.getHinhAnhCty()
                                            ));
                                            job.getCompany().setMaCongTy(company.getMaCongTy());
                                            job.getCompany().setDaXacThuc(company.isDaXacThuc());
                                        }

                                        jobList.add(job);
                                    }
                                }
                                jobAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    Toast.makeText(CompanyDetailActivity.this, "Không thể tải chi tiết công ty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CompanyDetailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCompanyDetails(Company company) {
        // Cập nhật biến currentCompany
        this.currentCompany = company;

        // Company Hero Section
        tvCompanyName.setText(company.getTenCongTy());

        // Hiển thị trạng thái xác thực
        if (company.isDaXacThuc()) {
            tvVerifiedBadge.setText("Đã xác thực");
            tvVerifiedBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.border_rounded_small));
        } else {
            tvVerifiedBadge.setText("Chưa xác thực");
            tvVerifiedBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.border_rounded_small));
        }

        // Địa chỉ công ty (nếu có)
        if (company.getDiaChi() != null && !company.getDiaChi().isEmpty()) {
            tvCompanyLocation.setText(company.getDiaChi());
        } else {
            tvCompanyLocation.setText("Chưa cập nhật địa chỉ");
        }

        // Mô tả công ty (sử dụng tên lĩnh vực hoặc thông tin chung nếu có)
        tvCompanyDescription.setText("Công ty chuyên nghiệp trong lĩnh vực của mình");

        // Thông tin liên hệ (sử dụng thông tin từ emailCty và soDienThoaiCty)
        if ((company.getEmailCty() != null && !company.getEmailCty().isEmpty()) ||
            (company.getSoDienThoaiCty() != null && !company.getSoDienThoaiCty().isEmpty())) {

            // Hiển thị email riêng biệt
            if (company.getEmailCty() != null && !company.getEmailCty().isEmpty()) {
                tvCompanyEmail.setText(company.getEmailCty());
                tvCompanyEmailDisplay.setText(company.getEmailCty());
            } else {
                tvCompanyEmail.setText("Chưa cập nhật");
                tvCompanyEmailDisplay.setText("Chưa cập nhật");
            }

            // Hiển thị số điện thoại riêng biệt
            if (company.getSoDienThoaiCty() != null && !company.getSoDienThoaiCty().isEmpty()) {
                tvCompanyPhone.setText(company.getSoDienThoaiCty());
                tvCompanyPhone1.setText(company.getSoDienThoaiCty());
            } else {
                tvCompanyPhone.setText("Chưa cập nhật");
                tvCompanyPhone1.setText("Chưa cập nhật");
            }

            // Hiển thị tổng hợp thông tin liên hệ
            StringBuilder contactInfo = new StringBuilder();
            if (company.getEmailCty() != null && !company.getEmailCty().isEmpty()) {
                contactInfo.append("Email: ").append(company.getEmailCty());
            }
            if (company.getSoDienThoaiCty() != null && !company.getSoDienThoaiCty().isEmpty()) {
                if (contactInfo.length() > 0) {
                    contactInfo.append("\n");
                }
                contactInfo.append("ĐT: ").append(company.getSoDienThoaiCty());
            }

            tvCompanyContact.setText(contactInfo.toString());
        } else {
            tvCompanyContact.setText("Chưa cập nhật");
            tvCompanyPhone.setText("Chưa cập nhật");
            tvCompanyPhone1.setText("Chưa cập nhật");
            tvCompanyEmail.setText("Chưa cập nhật");
            tvCompanyEmailDisplay.setText("Chưa cập nhật");
        }

        // Giới thiệu công ty - sử dụng trường moTaCongTy nếu có
        if (company.getMoTaCongTy() != null && !company.getMoTaCongTy().isEmpty()) {
            tvCompanyInfo.setText(company.getMoTaCongTy());
        } else {
            tvCompanyInfo.setText("Chưa có thông tin giới thiệu chi tiết.");
        }

        // Thông tin liên hệ chi tiết
        if (company.getTenNguoiDaiDien() != null && !company.getTenNguoiDaiDien().isEmpty()) {
            tvContactPerson.setText(company.getTenNguoiDaiDien());
        } else {
            tvContactPerson.setText("Chưa cập nhật");
        }

        if (company.getMaSoThue() != null && !company.getMaSoThue().isEmpty()) {
            tvTaxCode.setText(company.getMaSoThue());
        } else {
            tvTaxCode.setText("Chưa cập nhật");
        }

        if (company.getDiaChi() != null && !company.getDiaChi().isEmpty()) {
            tvCompanyAddress.setText(company.getDiaChi());
        } else {
            tvCompanyAddress.setText("Chưa cập nhật");
        }
    }

    private void loadCompanyLogo(String logoUrl) {
        if (logoUrl != null && !logoUrl.isEmpty()) {
            String fullLogoUrl = ServerConfig.getBaseUrl() + logoUrl;
            Glide.with(this)
                .load(fullLogoUrl)
                .placeholder(R.drawable.ic_boss) // Ảnh placeholder
                .error(R.drawable.ic_boss) // Ảnh khi có lỗi
                .into(ivCompanyLogo);
        } else {
            // Nếu không có logo, sử dụng ảnh mặc định
            ivCompanyLogo.setImageResource(R.drawable.ic_boss);
        }
    }
}