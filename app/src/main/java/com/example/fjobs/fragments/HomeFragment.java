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
import com.example.fjobs.adapters.LatestJobAdapter;
import com.example.fjobs.adapters.CategoryJobAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.Company;
import com.example.fjobs.models.JobDetail;
import com.example.fjobs.models.WorkField;
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
    private RecyclerView rvLatestJobs;
    private RecyclerView rvPopularCategories;
    private HorizontalJobAdapter horizontalJobAdapter;
    private FeaturedCompanyAdapter featuredCompanyAdapter;
    private LatestJobAdapter latestJobAdapter;
    private CategoryJobAdapter categoryJobAdapter;
    private List<JobDetail> featuredJobList;
    private List<Company> featuredCompanyList;
    private List<JobDetail> latestJobList;
    private List<WorkField> popularCategoryList;
    private TextView tvFeaturedJobsTitle;
    private TextView tvFeaturedCompaniesTitle;
    private TextView tvTotalJobs;
    private TextView tvTotalCompanies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupRecyclerViews();
        loadFeaturedJobs();
        loadFeaturedCompanies();
        loadLatestJobs();
        loadPopularCategories();
        loadMarketStatistics();
        setupSearchFunctionality();

        return view;
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.edit_text_search);
        searchButton = view.findViewById(R.id.button_search);
        rvFeaturedJobs = view.findViewById(R.id.rv_featured_jobs);
        rvFeaturedCompanies = view.findViewById(R.id.rv_featured_companies);
        rvLatestJobs = view.findViewById(R.id.rv_latest_jobs);
        rvPopularCategories = view.findViewById(R.id.rv_popular_categories);
        tvFeaturedJobsTitle = view.findViewById(R.id.tv_featured_jobs_title);
        tvFeaturedCompaniesTitle = view.findViewById(R.id.tv_featured_companies_title);
        tvTotalJobs = view.findViewById(R.id.tv_total_jobs);
        tvTotalCompanies = view.findViewById(R.id.tv_total_companies);
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

        // Thiết lập RecyclerView cho công việc mới nhất
        latestJobList = new ArrayList<>();
        latestJobAdapter = new LatestJobAdapter(latestJobList, getContext());
        rvLatestJobs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvLatestJobs.setAdapter(latestJobAdapter);

        // Thiết lập RecyclerView cho danh mục công việc phổ biến
        popularCategoryList = new ArrayList<>();
        categoryJobAdapter = new CategoryJobAdapter(popularCategoryList, getContext());
        rvPopularCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvPopularCategories.setAdapter(categoryJobAdapter);
    }

    private void setupSearchFunctionality() {
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    navigateToSearchResults(query);
                }
            });
        }

        // Thêm sự kiện Enter trên EditText để tìm kiếm
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    navigateToSearchResults(query);
                }
                return true;
            }
            return false;
        });
    }

    private void navigateToSearchResults(String keyword) {
        // Tạo bundle để truyền từ khóa tìm kiếm
        Bundle bundle = new Bundle();
        bundle.putString("keyword", keyword);

        // Tạo và chuyển sang SearchResultFragment
        SearchResultFragment searchResultFragment = new SearchResultFragment();
        searchResultFragment.setArguments(bundle);

        // Dùng FragmentTransaction để chuyển fragment
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, searchResultFragment)
                .addToBackStack("search_results") // Thêm vào stack để có thể quay lại
                .commit();
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

            // Xử lý vị trí công việc nếu có
            if (map.containsKey("jobPosition") && map.get("jobPosition") != null) {
                java.util.Map<String, Object> jobPositionMap = (java.util.Map<String, Object>) map.get("jobPosition");
                if (jobPositionMap != null) {
                    com.example.fjobs.models.JobPosition jobPosition = convertMapToJobPosition(jobPositionMap);
                    job.setJobPosition(jobPosition);
                }
            }

            // Xử lý cấp độ kinh nghiệm nếu có
            if (map.containsKey("experienceLevel") && map.get("experienceLevel") != null) {
                java.util.Map<String, Object> experienceLevelMap = (java.util.Map<String, Object>) map.get("experienceLevel");
                if (experienceLevelMap != null) {
                    com.example.fjobs.models.ExperienceLevel experienceLevel = convertMapToExperienceLevel(experienceLevelMap);
                    job.setExperienceLevel(experienceLevel);
                }
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

            if (map.containsKey("emailCty") && map.get("emailCty") != null) {
                company.setEmailCty(map.get("emailCty").toString());
            }
            if (map.containsKey("soDienThoaiCty") && map.get("soDienThoaiCty") != null) {
                company.setSoDienThoaiCty(map.get("soDienThoaiCty").toString());
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

    private com.example.fjobs.models.JobPosition convertMapToJobPosition(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.JobPosition jobPosition = new com.example.fjobs.models.JobPosition();

            if (map.containsKey("maViTri") && map.get("maViTri") != null) {
                Object maViTriObj = map.get("maViTri");
                if (maViTriObj instanceof Integer) {
                    jobPosition.setMaViTri((Integer) maViTriObj);
                } else if (maViTriObj instanceof Double) {
                    jobPosition.setMaViTri(((Double) maViTriObj).intValue());
                } else {
                    jobPosition.setMaViTri(Integer.parseInt(maViTriObj.toString()));
                }
            }

            if (map.containsKey("tenViTri") && map.get("tenViTri") != null) {
                jobPosition.setTenViTri(map.get("tenViTri").toString());
            }

            // Xử lý workDiscipline nếu có
            if (map.containsKey("workDiscipline") && map.get("workDiscipline") != null) {
                java.util.Map<String, Object> workDisciplineMap = (java.util.Map<String, Object>) map.get("workDiscipline");
                if (workDisciplineMap != null) {
                    com.example.fjobs.models.WorkDiscipline workDiscipline = convertMapToWorkDiscipline(workDisciplineMap);
                    jobPosition.setWorkDiscipline(workDiscipline);
                }
            }

            return jobPosition;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private com.example.fjobs.models.ExperienceLevel convertMapToExperienceLevel(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.ExperienceLevel experienceLevel = new com.example.fjobs.models.ExperienceLevel();

            if (map.containsKey("maCapDo") && map.get("maCapDo") != null) {
                Object maCapDoObj = map.get("maCapDo");
                if (maCapDoObj instanceof Integer) {
                    experienceLevel.setMaCapDo((Integer) maCapDoObj);
                } else if (maCapDoObj instanceof Double) {
                    experienceLevel.setMaCapDo(((Double) maCapDoObj).intValue());
                } else {
                    experienceLevel.setMaCapDo(Integer.parseInt(maCapDoObj.toString()));
                }
            }

            if (map.containsKey("tenCapDo") && map.get("tenCapDo") != null) {
                experienceLevel.setTenCapDo(map.get("tenCapDo").toString());
            }

            return experienceLevel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private com.example.fjobs.models.WorkDiscipline convertMapToWorkDiscipline(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.WorkDiscipline workDiscipline = new com.example.fjobs.models.WorkDiscipline();

            if (map.containsKey("maNganh") && map.get("maNganh") != null) {
                Object maNganhObj = map.get("maNganh");
                if (maNganhObj instanceof Integer) {
                    workDiscipline.setMaNganh((Integer) maNganhObj);
                } else if (maNganhObj instanceof Double) {
                    workDiscipline.setMaNganh(((Double) maNganhObj).intValue());
                } else {
                    workDiscipline.setMaNganh(Integer.parseInt(maNganhObj.toString()));
                }
            }

            if (map.containsKey("tenNganh") && map.get("tenNganh") != null) {
                workDiscipline.setTenNganh(map.get("tenNganh").toString());
            }

            // Xử lý workField nếu có
            if (map.containsKey("workField") && map.get("workField") != null) {
                java.util.Map<String, Object> workFieldMap = (java.util.Map<String, Object>) map.get("workField");
                if (workFieldMap != null) {
                    com.example.fjobs.models.WorkField workField = convertMapToWorkField(workFieldMap);
                    workDiscipline.setWorkField(workField);
                }
            }

            return workDiscipline;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private com.example.fjobs.models.WorkField convertMapToWorkField(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.WorkField workField = new com.example.fjobs.models.WorkField();

            if (map.containsKey("maLinhVuc") && map.get("maLinhVuc") != null) {
                Object maLinhVucObj = map.get("maLinhVuc");
                if (maLinhVucObj instanceof Integer) {
                    workField.setMaLinhVuc((Integer) maLinhVucObj);
                } else if (maLinhVucObj instanceof Double) {
                    workField.setMaLinhVuc(((Double) maLinhVucObj).intValue());
                } else {
                    workField.setMaLinhVuc(Integer.parseInt(maLinhVucObj.toString()));
                }
            }

            if (map.containsKey("tenLinhVuc") && map.get("tenLinhVuc") != null) {
                workField.setTenLinhVuc(map.get("tenLinhVuc").toString());
            }

            return workField;
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

        // Tạo vị trí công việc và cấp độ kinh nghiệm mẫu
        com.example.fjobs.models.JobPosition jobPosition1 = new com.example.fjobs.models.JobPosition();
        jobPosition1.setTenViTri("Senior Android Developer");
        job1.setJobPosition(jobPosition1);

        com.example.fjobs.models.ExperienceLevel expLevel1 = new com.example.fjobs.models.ExperienceLevel();
        expLevel1.setTenCapDo("5 năm kinh nghiệm");
        job1.setExperienceLevel(expLevel1);

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

        com.example.fjobs.models.JobPosition jobPosition2 = new com.example.fjobs.models.JobPosition();
        jobPosition2.setTenViTri("Frontend Developer");
        job2.setJobPosition(jobPosition2);

        com.example.fjobs.models.ExperienceLevel expLevel2 = new com.example.fjobs.models.ExperienceLevel();
        expLevel2.setTenCapDo("2 năm kinh nghiệm");
        job2.setExperienceLevel(expLevel2);

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

        com.example.fjobs.models.JobPosition jobPosition3 = new com.example.fjobs.models.JobPosition();
        jobPosition3.setTenViTri("Senior DevOps Engineer");
        job3.setJobPosition(jobPosition3);

        com.example.fjobs.models.ExperienceLevel expLevel3 = new com.example.fjobs.models.ExperienceLevel();
        expLevel3.setTenCapDo("Trên 3 năm kinh nghiệm");
        job3.setExperienceLevel(expLevel3);

        featuredJobList.clear();
        featuredJobList.add(job1);
        featuredJobList.add(job2);
        featuredJobList.add(job3);
        horizontalJobAdapter.notifyDataSetChanged();
    }

    private void loadLatestJobs() {
        // Gọi API để lấy danh sách công việc mới nhất
        Call<ApiResponse> call = ApiClient.getRetrofitInstance().create(ApiService.class).getLatestJobs();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("HomeFragment", "Latest jobs response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeFragment", "Latest jobs response body: " + response.body().toString());
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

                        latestJobList.clear();
                        latestJobList.addAll(jobs);
                        latestJobAdapter.notifyDataSetChanged();

                        // Kiểm tra nếu không có dữ liệu sau khi xử lý
                        if (jobs.isEmpty()) {
                            Log.w("HomeFragment", "No latest jobs received from API, using sample data");
                            loadSampleLatestJobs();
                        } else {
                            Log.d("HomeFragment", "Loaded " + jobs.size() + " latest jobs from API");
                        }
                    } else {
                        // Nếu không có dữ liệu thì dùng dữ liệu mẫu
                        Log.w("HomeFragment", "No latest jobs data in response, using sample data");
                        loadSampleLatestJobs();
                    }
                } else {
                    // Trong trường hợp phản hồi không thành công, dùng dữ liệu mẫu
                    Log.e("HomeFragment", "Failed to load latest jobs: " + response.code() + " - " + (response.body() != null ? response.body().getMessage() : "No body"));
                    loadSampleLatestJobs();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Khi không thể kết nối API, dùng dữ liệu mẫu
                Log.e("HomeFragment", "Error loading latest jobs", t);
                loadSampleLatestJobs();
            }
        });
    }

    private void loadSampleLatestJobs() {
        // Dữ liệu mẫu cho mục đích demo
        JobDetail job1 = new JobDetail();
        job1.setTieuDe("Lập trình viên Java");
        job1.setLuong(20000000);
        job1.setLoaiLuong("Tháng");
        job1.setChiTiet("Phát triển ứng dụng backend với Java Spring Boot...");
        job1.setNgayDang("2024-10-15");
        job1.setTrangThaiDuyet("Đã duyệt");
        job1.setTrangThaiTinTuyen("Mở");

        // Tạo công ty mẫu
        com.example.fjobs.models.Company company1 = new com.example.fjobs.models.Company();
        company1.setTenCongTy("Công ty FPT");
        job1.setCompany(company1);

        JobDetail job2 = new JobDetail();
        job2.setTieuDe("Nhân viên Marketing");
        job2.setLuong(15000000);
        job2.setLoaiLuong("Tháng");
        job2.setChiTiet("Thực hiện chiến dịch marketing online và offline...");
        job2.setNgayDang("2024-10-14");
        job2.setTrangThaiDuyet("Đã duyệt");
        job2.setTrangThaiTinTuyen("Mở");

        com.example.fjobs.models.Company company2 = new com.example.fjobs.models.Company();
        company2.setTenCongTy("Công ty VinGroup");
        job2.setCompany(company2);

        JobDetail job3 = new JobDetail();
        job3.setTieuDe("Kỹ sư AI");
        job3.setLuong(35000000);
        job3.setLoaiLuong("Tháng");
        job3.setChiTiet("Phát triển mô hình học máy và trí tuệ nhân tạo...");
        job3.setNgayDang("2024-10-13");
        job3.setTrangThaiDuyet("Đã duyệt");
        job3.setTrangThaiTinTuyen("Mở");

        com.example.fjobs.models.Company company3 = new com.example.fjobs.models.Company();
        company3.setTenCongTy("Công ty Samsung");
        job3.setCompany(company3);

        latestJobList.clear();
        latestJobList.add(job1);
        latestJobList.add(job2);
        latestJobList.add(job3);
        latestJobAdapter.notifyDataSetChanged();
    }

    private void loadPopularCategories() {
        // Gọi API để lấy danh sách lĩnh vực công việc phổ biến
        Call<ApiResponse> call = ApiClient.getRetrofitInstance().create(ApiService.class).getPopularWorkFields();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("HomeFragment", "Popular categories response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeFragment", "Popular categories response body: " + response.body().toString());
                    Object data = response.body().getData();
                    if (data != null) {
                        List<WorkField> categories = new ArrayList<>();

                        // Chuyển đổi dữ liệu từ Object sang WorkField
                        if (data instanceof List) {
                            List<?> rawData = (List<?>) data;
                            for (Object item : rawData) {
                                if (item instanceof java.util.Map) {
                                    WorkField category = convertMapToWorkField((java.util.Map<String, Object>) item);
                                    if (category != null) {
                                        categories.add(category);
                                    }
                                } else if (item instanceof WorkField) {
                                    categories.add((WorkField) item);
                                }
                            }
                        }

                        popularCategoryList.clear();
                        popularCategoryList.addAll(categories);
                        categoryJobAdapter.notifyDataSetChanged();

                        // Kiểm tra nếu không có dữ liệu sau khi xử lý
                        if (categories.isEmpty()) {
                            Log.w("HomeFragment", "No popular categories received from API, using sample data");
                            loadSampleCategories();
                        } else {
                            Log.d("HomeFragment", "Loaded " + categories.size() + " popular categories from API");
                        }
                    } else {
                        // Nếu không có dữ liệu thì dùng dữ liệu mẫu
                        Log.w("HomeFragment", "No popular categories data in response, using sample data");
                        loadSampleCategories();
                    }
                } else {
                    // Trong trường hợp phản hồi không thành công, dùng dữ liệu mẫu
                    Log.e("HomeFragment", "Failed to load popular categories: " + response.code() + " - " + (response.body() != null ? response.body().getMessage() : "No body"));
                    loadSampleCategories();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Khi không thể kết nối API, dùng dữ liệu mẫu
                Log.e("HomeFragment", "Error loading popular categories", t);
                loadSampleCategories();
            }
        });
    }

    private void loadSampleCategories() {
        // Dữ liệu mẫu cho lĩnh vực công việc
        WorkField field1 = new WorkField();
        field1.setMaLinhVuc(1);
        field1.setTenLinhVuc("Công nghệ thông tin");

        WorkField field2 = new WorkField();
        field2.setMaLinhVuc(2);
        field2.setTenLinhVuc("Marketing");

        WorkField field3 = new WorkField();
        field3.setMaLinhVuc(3);
        field3.setTenLinhVuc("Tài chính");

        WorkField field4 = new WorkField();
        field4.setMaLinhVuc(4);
        field4.setTenLinhVuc("Nhân sự");

        popularCategoryList.clear();
        popularCategoryList.add(field1);
        popularCategoryList.add(field2);
        popularCategoryList.add(field3);
        popularCategoryList.add(field4);
        categoryJobAdapter.notifyDataSetChanged();
    }

    private void loadMarketStatistics() {
        // Gọi API để lấy thống kê thị trường
        Call<ApiResponse> call = ApiClient.getRetrofitInstance().create(ApiService.class).getMarketStatistics();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("HomeFragment", "Market statistics response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeFragment", "Market statistics response body: " + response.body().toString());
                    Object data = response.body().getData();
                    if (data != null) {
                        // Giả sử dữ liệu trả về là một Map chứa các thống kê
                        if (data instanceof java.util.Map) {
                            java.util.Map<String, Object> stats = (java.util.Map<String, Object>) data;

                            // Cập nhật số lượng công việc
                            if (stats.containsKey("totalJobs")) {
                                Object totalJobsObj = stats.get("totalJobs");
                                int totalJobs = 0;
                                if (totalJobsObj instanceof Integer) {
                                    totalJobs = (Integer) totalJobsObj;
                                } else if (totalJobsObj instanceof Double) {
                                    totalJobs = ((Double) totalJobsObj).intValue();
                                } else {
                                    totalJobs = Integer.parseInt(totalJobsObj.toString());
                                }
                                tvTotalJobs.setText(String.valueOf(totalJobs));
                            }

                            // Cập nhật số lượng công ty
                            if (stats.containsKey("totalCompanies")) {
                                Object totalCompaniesObj = stats.get("totalCompanies");
                                int totalCompanies = 0;
                                if (totalCompaniesObj instanceof Integer) {
                                    totalCompanies = (Integer) totalCompaniesObj;
                                } else if (totalCompaniesObj instanceof Double) {
                                    totalCompanies = ((Double) totalCompaniesObj).intValue();
                                } else {
                                    totalCompanies = Integer.parseInt(totalCompaniesObj.toString());
                                }
                                tvTotalCompanies.setText(String.valueOf(totalCompanies));
                            }
                        }
                    }
                } else {
                    // Trong trường hợp phản hồi không thành công, sử dụng dữ liệu mẫu
                    Log.e("HomeFragment", "Failed to load market statistics: " + response.code() + " - " + (response.body() != null ? response.body().getMessage() : "No body"));
                    loadSampleStatistics();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Khi không thể kết nối API, sử dụng dữ liệu mẫu
                Log.e("HomeFragment", "Error loading market statistics", t);
                loadSampleStatistics();
            }
        });
    }

    private void loadSampleStatistics() {
        // Dữ liệu mẫu cho thống kê thị trường
        tvTotalJobs.setText("1,234");
        tvTotalCompanies.setText("567");
    }
}