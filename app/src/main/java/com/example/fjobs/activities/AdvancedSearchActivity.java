package com.example.fjobs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fjobs.R;
import com.example.fjobs.adapters.HorizontalJobAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.ExperienceLevel;
import com.example.fjobs.models.JobDetail;
import com.example.fjobs.models.JobPosition;
import com.example.fjobs.models.WorkDiscipline;
import com.example.fjobs.models.WorkField;
import com.example.fjobs.models.WorkType;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdvancedSearchActivity extends AppCompatActivity {

    private Spinner spinnerWorkField;
    private Spinner spinnerWorkDiscipline;
    private Spinner spinnerJobPosition;
    private Spinner spinnerExperienceLevel;
    private Spinner spinnerWorkType;
    private EditText editTextKeyword; // Đổi từ Spinner sang EditText
    private Button btnSearchAdvanced;
    private Button btnClearFilters;
    private RecyclerView recyclerView;
    private HorizontalJobAdapter jobAdapter;
    private List<JobDetail> jobList;

    // Các danh sách dữ liệu cho dropdown
    private List<WorkField> workFields;
    private List<WorkDiscipline> workDisciplines;
    private List<JobPosition> jobPositions;
    private List<ExperienceLevel> experienceLevels;
    private List<WorkType> workTypes;

    private ApiService apiService;

    // Biến lưu trữ lựa chọn
    private Integer selectedWorkFieldId;
    private Integer selectedWorkDisciplineId;
    private Integer selectedJobPositionId;
    private Integer selectedExperienceLevelId;
    private Integer selectedWorkTypeId;
    private String selectedKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        initViews();
        setupRecyclerView(); // Thiết lập RecyclerView
        initApiService();
        setupSpinners();
        setupClickListeners();

        // Khởi tạo dữ liệu mặc định cho các spinner
        initDefaultSpinnerData();

        // Load dữ liệu từ API cho các dropdown
        loadDropdownDataFromApi();
    }

    private void initViews() {
        spinnerWorkField = findViewById(R.id.spinner_work_field);
        spinnerWorkDiscipline = findViewById(R.id.spinner_work_discipline);
        spinnerJobPosition = findViewById(R.id.spinner_job_position);
        spinnerExperienceLevel = findViewById(R.id.spinner_experience_level);
        spinnerWorkType = findViewById(R.id.spinner_work_type);
        editTextKeyword = findViewById(R.id.edit_text_keyword); // Đây là EditText, không phải Spinner
        btnSearchAdvanced = findViewById(R.id.btn_search_advanced);
        btnClearFilters = findViewById(R.id.btn_clear_filters);
        recyclerView = findViewById(R.id.rv_search_results); // RecyclerView mới trong layout
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            jobList = new ArrayList<>();
            jobAdapter = new HorizontalJobAdapter(jobList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(jobAdapter);
            recyclerView.setVisibility(View.GONE); // Ban đầu ẩn RecyclerView
        }
    }

    private void initApiService() {
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    private void initDefaultSpinnerData() {
        // Khởi tạo các danh sách nếu chưa có để đảm bảo spinner có thể hiển thị ít nhất tùy chọn "Tất cả"
        if (workFields == null) {
            workFields = new ArrayList<>();
        }

        if (workDisciplines == null) {
            workDisciplines = new ArrayList<>();
            workDisciplines.add(new WorkDiscipline(0, "Tất cả ngành nghề", null));
        }

        if (jobPositions == null) {
            jobPositions = new ArrayList<>();
            jobPositions.add(new JobPosition(0, "Tất cả vị trí", null));
        }

        if (experienceLevels == null) {
            experienceLevels = new ArrayList<>();
        }

        if (workTypes == null) {
            workTypes = new ArrayList<>();
        }

        // Cập nhật các spinner với tùy chọn mặc định
        updateWorkFieldSpinner();
        updateWorkDisciplineSpinner();
        updateJobPositionSpinner();
        updateExperienceLevelSpinner();
        updateWorkTypeSpinner();
    }

    private void setupSpinners() {
        // Thiết lập listeners cho các spinner
        spinnerWorkField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Trường hợp "Tất cả lĩnh vực" được chọn
                    selectedWorkFieldId = null;
                    // Khi chọn "Tất cả lĩnh vực", hiển thị tất cả ngành nghề
                    updateWorkDisciplinesSpinner(null);
                } else {
                    // Các lĩnh vực cụ thể được chọn
                    if (workFields != null && position > 0 && workFields.size() >= position) {
                        selectedWorkFieldId = workFields.get(position - 1).getMaLinhVuc(); // Lấy ID lĩnh vực thực tế
                        updateWorkDisciplinesSpinner(selectedWorkFieldId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedWorkFieldId = null;
            }
        });

        spinnerWorkDiscipline.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Trường hợp "Tất cả ngành nghề" được chọn
                    selectedWorkDisciplineId = null;
                    // Khi chọn "Tất cả ngành nghề", nên hiển thị tất cả vị trí công việc
                    updateJobPositionsSpinner(null);
                } else {
                    // Các ngành nghề cụ thể được chọn
                    // workDisciplines bao gồm mục "Tất cả ngành nghề" ở vị trí 0, nên cần lấy phần tử tại vị trí position
                    if (workDisciplines != null && position > 0 && position < workDisciplines.size()) {
                        selectedWorkDisciplineId = workDisciplines.get(position).getMaNganh(); // Lấy ID ngành nghề thực tế
                        updateJobPositionsSpinner(selectedWorkDisciplineId);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedWorkDisciplineId = null;
            }
        });

        spinnerJobPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Trường hợp "Tất cả vị trí" được chọn
                    selectedJobPositionId = null;
                } else {
                    // Các vị trí công việc cụ thể được chọn
                    if (jobPositions != null && position < jobPositions.size()) {
                        selectedJobPositionId = jobPositions.get(position).getMaViTri(); // Lấy ID vị trí thực tế
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedJobPositionId = null;
            }
        });

        spinnerExperienceLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedExperienceLevelId = null;
                } else {
                    if (experienceLevels != null && position < experienceLevels.size()) {
                        selectedExperienceLevelId = experienceLevels.get(position).getMaCapDo(); // Lấy ID cấp độ thực tế
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedExperienceLevelId = null;
            }
        });

        spinnerWorkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedWorkTypeId = null;
                } else {
                    if (workTypes != null && position < workTypes.size()) {
                        selectedWorkTypeId = workTypes.get(position).getMaHinhThuc(); // Lấy ID hình thức thực tế
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedWorkTypeId = null;
            }
        });
    }

    private void setupClickListeners() {
        btnSearchAdvanced.setOnClickListener(v -> performAdvancedSearch());

        btnClearFilters.setOnClickListener(v -> clearFilters());
    }

    private void performAdvancedSearch() {
        // Hiển thị ProgressBar và ẩn RecyclerView
        ProgressBar progressBar = findViewById(R.id.progress_bar_search);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }

        // Lấy từ khóa từ EditText
        if (editTextKeyword != null) {
            selectedKeyword = editTextKeyword.getText().toString().trim();
        }

        // Gọi API tìm kiếm nâng cao mới (truyền null cho các tham số mức lương theo yêu cầu)
        Call<ApiResponse> call = apiService.searchJobsAdvanced(
                selectedKeyword.isEmpty() ? null : selectedKeyword,
                selectedWorkFieldId,
                selectedWorkDisciplineId,
                selectedJobPositionId,
                selectedExperienceLevelId,
                selectedWorkTypeId,
                null, // minSalary - không sử dụng theo yêu cầu
                null, // maxSalary - không sử dụng theo yêu cầu
                0, // page mặc định
                10 // size mặc định
        );

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Ẩn ProgressBar
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Xử lý dữ liệu trả về
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawList = (List<?>) apiResponse.getData();
                            List<JobDetail> jobs = new ArrayList<>();

                            for (Object item : rawList) {
                                if (item instanceof JobDetail) {
                                    jobs.add((JobDetail) item);
                                } else if (item instanceof java.util.Map) {
                                    // Chuyển đổi từ Map sang JobDetail nếu cần
                                    JobDetail job = convertMapToJobDetail((java.util.Map<String, Object>) item);
                                    if (job != null) {
                                        jobs.add(job);
                                    }
                                }
                            }

                            // Cập nhật danh sách công việc
                            jobList.clear();
                            jobList.addAll(jobs);
                            if (jobAdapter != null) {
                                jobAdapter.notifyDataSetChanged();
                            }

                            // Hiển thị RecyclerView và cập nhật giao diện
                            if (recyclerView != null) {
                                recyclerView.setVisibility(jobs.isEmpty() ? View.GONE : View.VISIBLE);
                            }

                            // Hiển thị thông báo
                            if (jobs.isEmpty()) {
                                Toast.makeText(AdvancedSearchActivity.this, "Không tìm thấy công việc phù hợp", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AdvancedSearchActivity.this, "Tìm thấy " + jobs.size() + " công việc", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(AdvancedSearchActivity.this, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdvancedSearchActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ẩn ProgressBar
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(AdvancedSearchActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private JobDetail convertMapToJobDetail(java.util.Map<String, Object> map) {
        try {
            JobDetail job = new JobDetail();

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

            // Xử lý thông tin công ty nếu có
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
                company.setTenCongTy((String) map.get("tenCongTy"));
            }

            if (map.containsKey("tenNguoiDaiDien") && map.get("tenNguoiDaiDien") != null) {
                company.setTenNguoiDaiDien((String) map.get("tenNguoiDaiDien"));
            }

            if (map.containsKey("maSoThue") && map.get("maSoThue") != null) {
                company.setMaSoThue((String) map.get("maSoThue"));
            }

            if (map.containsKey("diaChi") && map.get("diaChi") != null) {
                company.setDiaChi((String) map.get("diaChi"));
            }

            if (map.containsKey("emailCty") && map.get("emailCty") != null) {
                company.setEmailCty((String) map.get("emailCty"));
            }
            if (map.containsKey("soDienThoaiCty") && map.get("soDienThoaiCty") != null) {
                company.setSoDienThoaiCty((String) map.get("soDienThoaiCty"));
            }

            if (map.containsKey("hinhAnhCty") && map.get("hinhAnhCty") != null) {
                company.setHinhAnhCty((String) map.get("hinhAnhCty"));
            }

            if (map.containsKey("daXacThuc") && map.get("daXacThuc") != null) {
                Object daXacThucObj = map.get("daXacThuc");
                if (daXacThucObj instanceof Boolean) {
                    company.setDaXacThuc((Boolean) daXacThucObj);
                } else {
                    company.setDaXacThuc(Boolean.parseBoolean(daXacThucObj.toString()));
                }
            }

            if (map.containsKey("moTaCongTy") && map.get("moTaCongTy") != null) {
                company.setMoTaCongTy((String) map.get("moTaCongTy"));
            }

            if (map.containsKey("trangThai") && map.get("trangThai") != null) {
                company.setTrangThai((String) map.get("trangThai"));
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
                jobPosition.setTenViTri((String) map.get("tenViTri"));
            }

            if (map.containsKey("workDiscipline") && map.get("workDiscipline") != null) {
                java.util.Map<String, Object> workDisciplineMap = (java.util.Map<String, Object>) map.get("workDiscipline");
                com.example.fjobs.models.WorkDiscipline workDiscipline = convertMapToWorkDiscipline(workDisciplineMap);
                jobPosition.setWorkDiscipline(workDiscipline);
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
                experienceLevel.setTenCapDo((String) map.get("tenCapDo"));
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
                workDiscipline.setTenNganh((String) map.get("tenNganh"));
            }

            if (map.containsKey("workField") && map.get("workField") != null) {
                java.util.Map<String, Object> workFieldMap = (java.util.Map<String, Object>) map.get("workField");
                com.example.fjobs.models.WorkField workField = convertMapToWorkField(workFieldMap);
                workDiscipline.setWorkField(workField);
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
                workField.setTenLinhVuc((String) map.get("tenLinhVuc"));
            }

            return workField;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void clearFilters() {
        // Reset tất cả các spinner về vị trí đầu tiên (tùy chọn "Tất cả")
        if (spinnerWorkField.getAdapter() != null && spinnerWorkField.getAdapter().getCount() > 0) {
            spinnerWorkField.setSelection(0);
        }

        if (spinnerWorkDiscipline.getAdapter() != null && spinnerWorkDiscipline.getAdapter().getCount() > 0) {
            spinnerWorkDiscipline.setSelection(0);
        }

        if (spinnerJobPosition.getAdapter() != null && spinnerJobPosition.getAdapter().getCount() > 0) {
            spinnerJobPosition.setSelection(0);
        }

        if (spinnerExperienceLevel.getAdapter() != null && spinnerExperienceLevel.getAdapter().getCount() > 0) {
            spinnerExperienceLevel.setSelection(0);
        }

        if (spinnerWorkType.getAdapter() != null && spinnerWorkType.getAdapter().getCount() > 0) {
            spinnerWorkType.setSelection(0);
        }

        // Xóa từ khóa tìm kiếm
        if (editTextKeyword != null) {
            editTextKeyword.setText("");
        }

        // Reset các biến lưu trữ lựa chọn
        selectedWorkFieldId = null;
        selectedWorkDisciplineId = null;
        selectedJobPositionId = null;
        selectedExperienceLevelId = null;
        selectedWorkTypeId = null;
        selectedKeyword = "";

        // Xóa danh sách kết quả
        if (jobList != null) {
            jobList.clear();
            if (jobAdapter != null) {
                jobAdapter.notifyDataSetChanged();
            }
        }

        // Ẩn RecyclerView
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }

        Toast.makeText(this, "Đã xóa bộ lọc", Toast.LENGTH_SHORT).show();
    }

    private void loadDropdownDataFromApi() {
        // Load danh sách lĩnh vực
        loadWorkFields();

        // Load danh sách cấp độ kinh nghiệm
        loadExperienceLevels();

        // Load danh sách hình thức làm việc
        loadWorkTypes();

        // Cập nhật các spinner con với tùy chọn "Tất cả" ngay từ đầu
        // để người dùng có thể thấy các tùy chọn mặc định
        updateWorkDisciplineSpinner();
        updateJobPositionSpinner();
    }

    private void loadWorkFields() {
        Call<ApiResponse> call = apiService.getAllWorkFields(); // Giả định endpoint này tồn tại
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Object data = response.body().getData();
                    if (data instanceof List) {
                        List<WorkField> fields = new ArrayList<>();
                        for (Object item : (List<?>) data) {
                            if (item instanceof WorkField) {
                                fields.add((WorkField) item);
                            } else if (item instanceof java.util.Map) {
                                WorkField field = convertMapToWorkField((java.util.Map<String, Object>) item);
                                if (field != null) {
                                    fields.add(field);
                                }
                            }
                        }
                        workFields = fields;
                        updateWorkFieldSpinner();
                    } else {
                        // Nếu API trả về thành công nhưng không có dữ liệu, sử dụng dữ liệu mẫu
                        loadSampleData();
                    }
                } else {
                    // Nếu phản hồi không thành công, sử dụng dữ liệu mẫu
                    loadSampleData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Nếu không thể load từ API, sử dụng dữ liệu mẫu
                loadSampleData();
            }
        });
    }

    private void loadExperienceLevels() {
        Call<ApiResponse> call = apiService.getAllExperienceLevels(); // Giả định endpoint này tồn tại
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Object data = response.body().getData();
                    if (data instanceof List) {
                        List<ExperienceLevel> levels = new ArrayList<>();
                        for (Object item : (List<?>) data) {
                            if (item instanceof ExperienceLevel) {
                                levels.add((ExperienceLevel) item);
                            } else if (item instanceof java.util.Map) {
                                ExperienceLevel level = convertMapToExperienceLevel((java.util.Map<String, Object>) item);
                                if (level != null) {
                                    levels.add(level);
                                }
                            }
                        }
                        experienceLevels = levels;
                        updateExperienceLevelSpinner();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Nếu không thể load từ API, sử dụng dữ liệu mẫu nếu chưa có
                if (workFields == null || workFields.isEmpty()) {
                    loadSampleData();
                }
            }
        });
    }

    private void loadWorkTypes() {
        Call<ApiResponse> call = apiService.getAllWorkTypes(); // Giả định endpoint này tồn tại
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Object data = response.body().getData();
                    if (data instanceof List) {
                        List<WorkType> types = new ArrayList<>();
                        for (Object item : (List<?>) data) {
                            if (item instanceof WorkType) {
                                types.add((WorkType) item);
                            } else if (item instanceof java.util.Map) {
                                WorkType type = convertMapToWorkType((java.util.Map<String, Object>) item);
                                if (type != null) {
                                    types.add(type);
                                }
                            }
                        }
                        workTypes = types;
                        updateWorkTypeSpinner();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Nếu không thể load từ API, sử dụng dữ liệu mẫu nếu chưa có
                if (workFields == null || workFields.isEmpty()) {
                    loadSampleData();
                }
            }
        });
    }

    private void updateWorkFieldSpinner() {
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("Tất cả lĩnh vực");
        if (workFields != null) {
            for (WorkField field : workFields) {
                fieldNames.add(field.getTenLinhVuc());
            }
        }
        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldNames);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkField.setAdapter(fieldAdapter);
    }

    private void updateWorkDisciplinesSpinner(Integer workFieldId) {
        // Xóa lựa chọn ở các spinner phụ thuộc
        selectedWorkDisciplineId = null;
        selectedJobPositionId = null;

        if (workFieldId != null) {
            Call<ApiResponse> call = apiService.getWorkDisciplinesByField(workFieldId);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Object data = response.body().getData();
                        if (data instanceof List) {
                            List<WorkDiscipline> disciplines = new ArrayList<>();
                            for (Object item : (List<?>) data) {
                                if (item instanceof WorkDiscipline) {
                                    disciplines.add((WorkDiscipline) item);
                                } else if (item instanceof java.util.Map) {
                                    WorkDiscipline discipline = convertMapToWorkDiscipline((java.util.Map<String, Object>) item);
                                    if (discipline != null) {
                                        disciplines.add(discipline);
                                    }
                                }
                            }

                            // Cập nhật danh sách ngành nghề và spinner
                            workDisciplines = new ArrayList<>();
                            workDisciplines.add(new WorkDiscipline(0, "Tất cả ngành nghề", null)); // Thêm tùy chọn tất cả
                            workDisciplines.addAll(disciplines);

                            updateWorkDisciplineSpinner();
                            updateJobPositionSpinner(); // Cập nhật spinner vị trí công việc sau khi cập nhật ngành nghề
                        }
                    } else {
                        // Nếu không thể load từ API, sử dụng danh sách hiện tại
                        updateWorkDisciplineSpinner();
                        updateJobPositionSpinner();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Nếu không thể load từ API, sử dụng danh sách hiện tại
                    updateWorkDisciplineSpinner();
                    updateJobPositionSpinner();
                }
            });
        } else {
            // Nếu không có ID lĩnh vực (tức là chọn "Tất cả lĩnh vực"), hiển thị tất cả ngành nghề
            workDisciplines = new ArrayList<>();
            workDisciplines.add(new WorkDiscipline(0, "Tất cả ngành nghề", null));
            updateWorkDisciplineSpinner();
            updateJobPositionSpinner();
        }
    }

    private void updateWorkDisciplineSpinner() {
        List<String> disciplineNames = new ArrayList<>();
        disciplineNames.add("Tất cả ngành nghề");
        if (workDisciplines != null) {
            for (WorkDiscipline discipline : workDisciplines) {
                // Chỉ thêm nếu không phải là mục "Tất cả ngành nghề" (maNganh = 0) hoặc nếu không có mục đó
                if (discipline.getMaNganh() != null && discipline.getMaNganh() != 0) {
                    disciplineNames.add(discipline.getTenNganh());
                }
            }
        }
        ArrayAdapter<String> disciplineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, disciplineNames);
        disciplineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkDiscipline.setAdapter(disciplineAdapter);
    }

    private void updateJobPositionsSpinner(Integer disciplineId) {
        // Xóa lựa chọn ở spinner vị trí công việc
        selectedJobPositionId = null;

        // Hiển thị "Đang tải..." hoặc làm trống spinner để người dùng biết đang cập nhật
        List<String> positionNames = new ArrayList<>();
        positionNames.add("Tất cả vị trí");
        // Nếu đang tải dữ liệu theo ngành cụ thể, có thể thêm thông báo
        if (disciplineId != null && disciplineId != 0) {
            positionNames.add("Đang tải...");
        }

        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, positionNames);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobPosition.setAdapter(positionAdapter);

        if (disciplineId != null && disciplineId != 0) { // Không cập nhật nếu là tùy chọn "Tất cả ngành nghề"
            Call<ApiResponse> call = apiService.getJobPositionsByDiscipline(disciplineId);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Object data = response.body().getData();
                        if (data instanceof List) {
                            List<JobPosition> positions = new ArrayList<>();
                            for (Object item : (List<?>) data) {
                                if (item instanceof JobPosition) {
                                    positions.add((JobPosition) item);
                                } else if (item instanceof java.util.Map) {
                                    JobPosition position = convertMapToJobPosition((java.util.Map<String, Object>) item);
                                    if (position != null) {
                                        positions.add(position);
                                    }
                                }
                            }

                            // Cập nhật danh sách vị trí công việc và spinner
                            jobPositions = new ArrayList<>();
                            jobPositions.add(new JobPosition(0, "Tất cả vị trí", null)); // Thêm tùy chọn tất cả
                            jobPositions.addAll(positions);

                            // Cập nhật lại spinner với dữ liệu mới
                            updateJobPositionSpinner();
                        } else {
                            // Nếu không có dữ liệu, vẫn cập nhật với danh sách rỗng
                            jobPositions = new ArrayList<>();
                            jobPositions.add(new JobPosition(0, "Tất cả vị trí", null));
                            updateJobPositionSpinner();
                        }
                    } else {
                        // Nếu phản hồi không thành công, vẫn cập nhật với danh sách rỗng
                        jobPositions = new ArrayList<>();
                        jobPositions.add(new JobPosition(0, "Tất cả vị trí", null));
                        updateJobPositionSpinner();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    // Nếu gọi API thất bại, vẫn cập nhật với danh sách rỗng
                    jobPositions = new ArrayList<>();
                    jobPositions.add(new JobPosition(0, "Tất cả vị trí", null));
                    updateJobPositionSpinner();
                }
            });
        } else {
            // Nếu không có ID ngành nghề hoặc là tùy chọn "Tất cả ngành nghề", hiển thị tất cả vị trí
            jobPositions = new ArrayList<>();
            jobPositions.add(new JobPosition(0, "Tất cả vị trí", null));
            updateJobPositionSpinner();
        }
    }

    private void updateJobPositionSpinner() {
        List<String> positionNames = new ArrayList<>();
        positionNames.add("Tất cả vị trí");
        if (jobPositions != null) {
            for (JobPosition position : jobPositions) {
                // Chỉ thêm nếu không phải là mục "Tất cả vị trí" hoặc nếu không có mục đó
                if (position.getMaViTri() != null && position.getMaViTri() != 0) {
                    positionNames.add(position.getTenViTri());
                }
            }
        }
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, positionNames);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobPosition.setAdapter(positionAdapter);
    }

    private void updateExperienceLevelSpinner() {
        List<String> levelNames = new ArrayList<>();
        levelNames.add("Tất cả cấp độ");
        if (experienceLevels != null) {
            for (ExperienceLevel level : experienceLevels) {
                levelNames.add(level.getTenCapDo());
            }
        }
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelNames);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExperienceLevel.setAdapter(levelAdapter);
    }

    private void updateWorkTypeSpinner() {
        List<String> typeNames = new ArrayList<>();
        typeNames.add("Tất cả hình thức");
        if (workTypes != null) {
            for (WorkType type : workTypes) {
                typeNames.add(type.getTenHinhThuc());
            }
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeNames);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkType.setAdapter(typeAdapter);
    }

    private WorkType convertMapToWorkType(java.util.Map<String, Object> map) {
        try {
            WorkType workType = new WorkType();

            if (map.containsKey("maHinhThuc") && map.get("maHinhThuc") != null) {
                Object maHinhThucObj = map.get("maHinhThuc");
                if (maHinhThucObj instanceof Integer) {
                    workType.setMaHinhThuc((Integer) maHinhThucObj);
                } else if (maHinhThucObj instanceof Double) {
                    workType.setMaHinhThuc(((Double) maHinhThucObj).intValue());
                } else {
                    workType.setMaHinhThuc(Integer.parseInt(maHinhThucObj.toString()));
                }
            }

            if (map.containsKey("tenHinhThuc") && map.get("tenHinhThuc") != null) {
                workType.setTenHinhThuc((String) map.get("tenHinhThuc"));
            }

            return workType;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadSampleData() {
        // Dữ liệu mẫu cho các dropdown nếu không thể load từ API
        workFields = new ArrayList<>();
        workFields.add(new WorkField(1, "Công nghệ thông tin"));
        workFields.add(new WorkField(2, "Kinh doanh"));
        workFields.add(new WorkField(3, "Tài chính"));

        workDisciplines = new ArrayList<>();
        workDisciplines.add(new WorkDiscipline(1, "Lập trình phần mềm", workFields.get(0)));
        workDisciplines.add(new WorkDiscipline(2, "Thiết kế UX/UI", workFields.get(0)));
        workDisciplines.add(new WorkDiscipline(3, "Kinh doanh quốc tế", workFields.get(1)));
        workDisciplines.add(new WorkDiscipline(4, "Ngân hàng", workFields.get(2)));

        jobPositions = new ArrayList<>();
        jobPositions.add(new JobPosition(1, "Lập trình viên Frontend", workDisciplines.get(0)));
        jobPositions.add(new JobPosition(2, "Lập trình viên Backend", workDisciplines.get(0)));
        jobPositions.add(new JobPosition(3, "Thiết kế UI/UX", workDisciplines.get(1)));
        jobPositions.add(new JobPosition(4, "Chuyên viên kinh doanh", workDisciplines.get(2)));
        jobPositions.add(new JobPosition(5, "Chuyên viên tín dụng", workDisciplines.get(3)));

        experienceLevels = new ArrayList<>();
        experienceLevels.add(new ExperienceLevel(1, "Mới tốt nghiệp"));
        experienceLevels.add(new ExperienceLevel(2, "1-2 năm kinh nghiệm"));
        experienceLevels.add(new ExperienceLevel(3, "2-5 năm kinh nghiệm"));
        experienceLevels.add(new ExperienceLevel(4, "Trên 5 năm kinh nghiệm"));

        workTypes = new ArrayList<>();
        workTypes.add(new WorkType(1, "Toàn thời gian"));
        workTypes.add(new WorkType(2, "Bán thời gian"));
        workTypes.add(new WorkType(3, "Freelance"));
        workTypes.add(new WorkType(4, "Remote"));

        // Cập nhật các adapter cho spinner
        updateSpinnersWithData();
    }

    private void updateSpinnersWithData() {
        // Cập nhật spinner lĩnh vực
        List<String> fieldNames = new ArrayList<>();
        fieldNames.add("Tất cả lĩnh vực");
        for (WorkField field : workFields) {
            fieldNames.add(field.getTenLinhVuc());
        }
        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldNames);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkField.setAdapter(fieldAdapter);

        // Cập nhật spinner ngành nghề
        List<String> disciplineNames = new ArrayList<>();
        disciplineNames.add("Tất cả ngành nghề");
        for (WorkDiscipline discipline : workDisciplines) {
            disciplineNames.add(discipline.getTenNganh());
        }
        ArrayAdapter<String> disciplineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, disciplineNames);
        disciplineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkDiscipline.setAdapter(disciplineAdapter);

        // Cập nhật spinner vị trí công việc
        List<String> positionNames = new ArrayList<>();
        positionNames.add("Tất cả vị trí");
        for (JobPosition position : jobPositions) {
            positionNames.add(position.getTenViTri());
        }
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, positionNames);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobPosition.setAdapter(positionAdapter);

        // Cập nhật spinner cấp độ kinh nghiệm
        List<String> levelNames = new ArrayList<>();
        levelNames.add("Tất cả cấp độ");
        for (ExperienceLevel level : experienceLevels) {
            levelNames.add(level.getTenCapDo());
        }
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levelNames);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExperienceLevel.setAdapter(levelAdapter);

        // Cập nhật spinner hình thức làm việc
        List<String> typeName = new ArrayList<>();
        typeName.add("Tất cả hình thức");
        for (WorkType type : workTypes) {
            typeName.add(type.getTenHinhThuc());
        }
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeName);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkType.setAdapter(typeAdapter);
    }
}