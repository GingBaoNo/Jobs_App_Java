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
    private TextView tvCompanyPhone, tvCompanyEmail;
    private TextView tvCompanyInfo;
    private TextView tvContactPerson, tvCompanyContact, tvTaxCode, tvCompanyAddress;
    private Button btnViewJobs;

    // RecyclerView và Adapter cho danh sách công việc
    private RecyclerView rvCompanyJobs;
    private JobItemAdapter jobAdapter;
    private List<JobDetail> jobList;
    private int companyId;
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
        tvCompanyEmail = findViewById(R.id.tv_company_email);

        // Introduction section
        tvCompanyInfo = findViewById(R.id.tv_company_info);

        // Contact Information section
        tvContactPerson = findViewById(R.id.tv_contact_person);
        tvCompanyContact = findViewById(R.id.tv_company_contact);
        tvTaxCode = findViewById(R.id.tv_tax_code);
        tvCompanyAddress = findViewById(R.id.tv_company_address);

        // Footer button
        btnViewJobs = findViewById(R.id.sticky_footer);

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
                                if (companyData.get("lienHeCty") != null) {
                                    company.setLienHeCty((String) companyData.get("lienHeCty"));
                                }
                                if (companyData.get("hinhAnhCty") != null) {
                                    company.setHinhAnhCty((String) companyData.get("hinhAnhCty"));
                                }
                                if (companyData.get("daXacThuc") != null) {
                                    company.setDaXacThuc((Boolean) companyData.get("daXacThuc"));
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
                                                company.getLienHeCty(),
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

        // Thông tin liên hệ (sử dụng thông tin từ lienHeCty - có thể chứa cả số điện thoại và email)
        if (company.getLienHeCty() != null && !company.getLienHeCty().isEmpty()) {
            // Giả sử lienHeCty chứa cả số điện thoại và email, tách ra nếu cần
            tvCompanyContact.setText(company.getLienHeCty());

            // Cố gắng phân tích để tách riêng số điện thoại và email nếu có thể
            String contactInfo = company.getLienHeCty();
            if (contactInfo.contains("@")) {
                // Đây là email
                tvCompanyEmail.setText(contactInfo);
            } else {
                // Đây là số điện thoại
                tvCompanyPhone.setText(contactInfo);
            }
        } else {
            tvCompanyContact.setText("Chưa cập nhật");
            tvCompanyPhone.setText("Chưa cập nhật");
            tvCompanyEmail.setText("Chưa cập nhật");
        }

        // Giới thiệu công ty (có thể từ một trường cụ thể nếu có trong API)
        tvCompanyInfo.setText("Chưa có thông tin giới thiệu chi tiết. Đây là phần mô tả công ty.");

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
            String fullLogoUrl = "http://172.24.134.32:8080" + logoUrl;
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