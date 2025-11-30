package com.example.fjobs.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fjobs.R;
import com.example.fjobs.adapters.FeaturedCompanyAdapter;
import com.example.fjobs.adapters.HorizontalJobAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.Company;
import com.example.fjobs.models.JobDetail;
import com.example.fjobs.models.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView rvFeaturedJobs;
    private RecyclerView rvFeaturedCompanies;
    private HorizontalJobAdapter horizontalJobAdapter;
    private FeaturedCompanyAdapter featuredCompanyAdapter;
    private List<JobDetail> featuredJobList;
    private List<Company> featuredCompanyList;
    private TextView tvFeaturedJobsTitle;
    private TextView tvFeaturedCompaniesTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerViews();
        loadFeaturedJobs();
        loadFeaturedCompanies();
        setupSearchFunctionality();

        return view;
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.edit_text_search);
        searchButton = view.findViewById(R.id.button_search);
        rvFeaturedJobs = view.findViewById(R.id.rv_featured_jobs);
        rvFeaturedCompanies = view.findViewById(R.id.rv_featured_companies);
        tvFeaturedJobsTitle = view.findViewById(R.id.tv_featured_jobs_title);
        tvFeaturedCompaniesTitle = view.findViewById(R.id.tv_featured_companies_title);
    }

    private void setupRecyclerViews() {
        // Thiết lập RecyclerView cho công việc nổi bật
        featuredJobList = new ArrayList<>();
        horizontalJobAdapter = new HorizontalJobAdapter(featuredJobList, getContext());
        rvFeaturedJobs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedJobs.setAdapter(horizontalJobAdapter);

        // Thiết lập RecyclerView cho công ty nổi bật
        featuredCompanyList = new ArrayList<>();
        featuredCompanyAdapter = new FeaturedCompanyAdapter(featuredCompanyList, requireContext());
        rvFeaturedCompanies.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedCompanies.setAdapter(featuredCompanyAdapter);
    }

    private void setupSearchFunctionality() {
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                String query = searchEditText.getText().toString().trim();
                // Perform search action based on query
                // This would typically involve navigating to a search results fragment
                // or calling an API to search for jobs/companies
            });
        }
    }

    private void loadFeaturedJobs() {
        // Gọi API để lấy danh sách công việc nổi bật
        Call<ApiResponse> call = ApiClient.getRetrofitInstance().create(ApiService.class).getFeaturedJobs();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("HomeFragment", "Response code: " + response.code() + ", URL: " + call.request().url());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeFragment", "Response body: " + response.body().toString());
                    // Trong cấu trúc ApiResponse hiện tại, ta cần kiểm tra xem dữ liệu có đúng định dạng không
                    Object data = response.body().getData();
                    if (data != null) {
                        List<JobDetail> jobs = new ArrayList<>();

                        // Chuyển đổi dữ liệu từ Object sang JobDetail
                        if (data instanceof List) {
                            List<?> rawData = (List<?>) data;
                            for (Object item : rawData) {
                                if (item instanceof java.util.Map) {
                                    JobDetail job = convertMapToJobDetail((java.util.Map<String, Object>) item);
                                    if (job != null) {
                                        jobs.add(job);
                                    }
                                } else if (item instanceof JobDetail) {
                                    jobs.add((JobDetail) item);
                                }
                            }
                        }

                        featuredJobList.clear();
                        featuredJobList.addAll(jobs);
                        horizontalJobAdapter.notifyDataSetChanged();

                        // Kiểm tra nếu không có dữ liệu sau khi xử lý
                        if (jobs.isEmpty()) {
                            Log.w("HomeFragment", "No featured jobs received from API, using sample data");
                            loadSampleData();
                        } else {
                            Log.d("HomeFragment", "Loaded " + jobs.size() + " featured jobs from API");
                        }
                    } else {
                        // Nếu không có dữ liệu thì dùng dữ liệu mẫu
                        Log.w("HomeFragment", "No data in response, using sample data");
                        loadSampleData();
                    }
                } else {
                    // Trong trường hợp phản hồi không thành công, sử dụng dữ liệu mẫu
                    Log.e("HomeFragment", "Failed to load featured jobs: " + response.code() + " - " + (response.body() != null ? response.body().getMessage() : "No body"));
                    loadSampleData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Khi không thể kết nối API, sử dụng dữ liệu mẫu
                Log.e("HomeFragment", "Error loading featured jobs", t);
                loadSampleData();
            }
        });
    }

    private void loadFeaturedCompanies() {
        // Gọi API để lấy danh sách công ty nổi bật
        Call<ApiResponse> call = ApiClient.getRetrofitInstance().create(ApiService.class).getFeaturedCompanies();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("HomeFragment", "Featured companies response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeFragment", "Featured companies response body: " + response.body().toString());
                    Object data = response.body().getData();
                    if (data != null) {
                        List<Company> companies = new ArrayList<>();

                        // Chuyển đổi dữ liệu từ Object sang Company
                        if (data instanceof List) {
                            List<?> rawData = (List<?>) data;
                            for (Object item : rawData) {
                                if (item instanceof java.util.Map) {
                                    Company company = convertMapToCompany((java.util.Map<String, Object>) item);
                                    if (company != null) {
                                        companies.add(company);
                                    }
                                } else if (item instanceof Company) {
                                    companies.add((Company) item);
                                }
                            }
                        }

                        featuredCompanyList.clear();
                        featuredCompanyList.addAll(companies);
                        Log.d("HomeFragment", "Before notifyDataSetChanged - companies list size: " + featuredCompanyList.size() + ", adapter count: " + featuredCompanyAdapter.getItemCount());
                        featuredCompanyAdapter.notifyDataSetChanged();
                        Log.d("HomeFragment", "After notifyDataSetChanged - companies list size: " + featuredCompanyList.size() + ", adapter count: " + featuredCompanyAdapter.getItemCount());

                        // Kiểm tra nếu không có dữ liệu sau khi xử lý
                        if (companies.isEmpty()) {
                            Log.w("HomeFragment", "No featured companies received from API, using sample data");
                            loadSampleCompanies(); // Thêm dữ liệu mẫu nếu không có dữ liệu từ API
                        } else {
                            Log.d("HomeFragment", "Loaded " + companies.size() + " featured companies from API");
                        }
                    } else {
                        // Nếu không có dữ liệu thì dùng dữ liệu mẫu
                        Log.w("HomeFragment", "No featured companies data in response, using sample data");
                        loadSampleCompanies();
                    }
                } else {
                    // Trong trường hợp phản hồi không thành công, dùng dữ liệu mẫu
                    Log.e("HomeFragment", "Failed to load featured companies: " + response.code() + " - " + (response.body() != null ? response.body().getMessage() : "No body"));
                    loadSampleCompanies();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Khi không thể kết nối API, dùng dữ liệu mẫu
                Log.e("HomeFragment", "Error loading featured companies", t);
                loadSampleCompanies();
            }
        });
    }

    private void loadSampleCompanies() {
        // Dữ liệu mẫu cho công ty nổi bật
        Company company1 = new Company();
        company1.setTenCongTy("Công ty TNHH ABC");
        company1.setDiaChi("Hà Nội");
        company1.setHinhAnhCty("/uploads/companies/logo_1.png"); // Đường dẫn mẫu
        company1.setDaXacThuc(true);

        Company company2 = new Company();
        company2.setTenCongTy("Công ty XYZ");
        company2.setDiaChi("TP. Hồ Chí Minh");
        company2.setHinhAnhCty("/uploads/companies/logo_2.png"); // Đường dẫn mẫu
        company2.setDaXacThuc(true);

        Company company3 = new Company();
        company3.setTenCongTy("Công ty Công nghệ 123");
        company3.setDiaChi("Đà Nẵng");
        company3.setHinhAnhCty("/uploads/companies/logo_3.png"); // Đường dẫn mẫu
        company3.setDaXacThuc(true);

        featuredCompanyList.clear();
        featuredCompanyList.add(company1);
        featuredCompanyList.add(company2);
        featuredCompanyList.add(company3);
        featuredCompanyAdapter.notifyDataSetChanged();

        Log.d("HomeFragment", "Loaded " + featuredCompanyList.size() + " sample featured companies");
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

            if (map.containsKey("tieuDe")) {
                job.setTieuDe(map.get("tieuDe").toString());
            }

            if (map.containsKey("luong")) {
                Object luongObj = map.get("luong");
                if (luongObj instanceof Integer) {
                    job.setLuong((Integer) luongObj);
                } else if (luongObj instanceof Double) {
                    job.setLuong(((Double) luongObj).intValue());
                } else {
                    job.setLuong(Integer.parseInt(luongObj.toString()));
                }
            }

            if (map.containsKey("loaiLuong")) {
                job.setLoaiLuong(map.get("loaiLuong").toString());
            }

            if (map.containsKey("chiTiet")) {
                job.setChiTiet(map.get("chiTiet").toString());
            }

            if (map.containsKey("ngayKetThucTuyenDung")) {
                job.setNgayKetThucTuyenDung(map.get("ngayKetThucTuyenDung").toString());
            }

            if (map.containsKey("ngayDang")) {
                job.setNgayDang(map.get("ngayDang").toString());
            }

            if (map.containsKey("luotXem")) {
                Object luotXemObj = map.get("luotXem");
                if (luotXemObj instanceof Integer) {
                    job.setLuotXem((Integer) luotXemObj);
                } else if (luotXemObj instanceof Double) {
                    job.setLuotXem(((Double) luotXemObj).intValue());
                } else {
                    job.setLuotXem(Integer.parseInt(luotXemObj.toString()));
                }
            }

            if (map.containsKey("trangThaiDuyet")) {
                job.setTrangThaiDuyet(map.get("trangThaiDuyet").toString());
            }

            if (map.containsKey("trangThaiTinTuyen")) {
                job.setTrangThaiTinTuyen(map.get("trangThaiTinTuyen").toString());
            }

            // Chuyển đổi thông tin công ty nếu có
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

    private void loadSampleData() {
        // Dữ liệu mẫu cho mục đích demo
        JobDetail job1 = new JobDetail();
        job1.setTieuDe("Lập trình viên Android");
        job1.setLuong(25000000);
        job1.setLoaiLuong("Tháng");
        job1.setChiTiet("Phát triển ứng dụng di động trên nền tảng Android sử dụng Java/Kotlin...");
        job1.setNgayKetThucTuyenDung("2024-12-31");
        job1.setTrangThaiDuyet("Đã duyệt");
        job1.setTrangThaiTinTuyen("Mở");

        // Tạo công ty mẫu
        com.example.fjobs.models.Company company1 = new com.example.fjobs.models.Company();
        company1.setTenCongTy("Công ty TNHH ABC");
        job1.setCompany(company1);

        JobDetail job2 = new JobDetail();
        job2.setTieuDe("Lập trình viên Frontend");
        job2.setLuong(22000000);
        job2.setLoaiLuong("Tháng");
        job2.setChiTiet("Phát triển giao diện người dùng cho các ứng dụng web sử dụng React, Vue...");
        job2.setNgayKetThucTuyenDung("2024-11-30");
        job2.setTrangThaiDuyet("Đã duyệt");
        job2.setTrangThaiTinTuyen("Mở");

        com.example.fjobs.models.Company company2 = new com.example.fjobs.models.Company();
        company2.setTenCongTy("Công ty XYZ");
        job2.setCompany(company2);

        JobDetail job3 = new JobDetail();
        job3.setTieuDe("Kỹ sư DevOps");
        job3.setLuong(30000000);
        job3.setLoaiLuong("Tháng");
        job3.setChiTiet("Quản lý hệ thống CI/CD, triển khai và giám sát hạ tầng cloud...");
        job3.setNgayKetThucTuyenDung("2024-12-15");
        job3.setTrangThaiDuyet("Đã duyệt");
        job3.setTrangThaiTinTuyen("Mở");

        com.example.fjobs.models.Company company3 = new com.example.fjobs.models.Company();
        company3.setTenCongTy("Công ty Công nghệ 123");
        job3.setCompany(company3);

        featuredJobList.clear();
        featuredJobList.add(job1);
        featuredJobList.add(job2);
        featuredJobList.add(job3);
        horizontalJobAdapter.notifyDataSetChanged();
    }
}