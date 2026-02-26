package com.example.fjobs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class AdvancedSearchFragment extends Fragment {

    private Spinner spinnerWorkField;
    private Spinner spinnerWorkDiscipline;
    private Spinner spinnerJobPosition;
    private Spinner spinnerExperienceLevel;
    private Spinner spinnerWorkType;
    private EditText editTextKeyword; // Đổi từ Spinner sang EditText
    private Button btnSearchAdvanced;
    private Button btnClearFilters;
    private RecyclerView recyclerView;
    private TextView tvResultsTitle;
    private View dividerSearchResults;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_advanced_search, container, false);

        initViews(view);
        setupRecyclerView(view); // Thiết lập RecyclerView
        initApiService();
        setupSpinners();
        setupClickListeners();

        // Khởi tạo dữ liệu mặc định cho các spinner
        initDefaultSpinnerData();

        // Load dữ liệu từ API cho các dropdown
        loadDropdownDataFromApi();

        // Kiểm tra nếu có work_field_id từ bundle (click từ trang chủ)
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("work_field_id")) {
            int workFieldId = bundle.getInt("work_field_id", -1);
            String workFieldName = bundle.getString("work_field_name", "");
            
            if (workFieldId != -1) {
                // Lưu ID để sử dụng sau khi load dữ liệu
                selectedWorkFieldId = workFieldId;
                // Set từ khóa tìm kiếm theo tên lĩnh vực
                if (editTextKeyword != null) {
                    editTextKeyword.setText(workFieldName);
                }
            }
        }

        return view;
    }

    private void initViews(View view) {
        spinnerWorkField = view.findViewById(R.id.spinner_work_field);
        spinnerWorkDiscipline = view.findViewById(R.id.spinner_work_discipline);
        spinnerJobPosition = view.findViewById(R.id.spinner_job_position);
        spinnerExperienceLevel = view.findViewById(R.id.spinner_experience_level);
        spinnerWorkType = view.findViewById(R.id.spinner_work_type);
        editTextKeyword = view.findViewById(R.id.edit_text_keyword); // Đây là EditText, không phải Spinner
        btnSearchAdvanced = view.findViewById(R.id.btn_search_advanced);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);
        recyclerView = view.findViewById(R.id.rv_search_results); // RecyclerView mới trong layout
        tvResultsTitle = view.findViewById(R.id.tv_results_title); // Tiêu đề kết quả
        dividerSearchResults = view.findViewById(R.id.divider_search_results); // Divider ngăn cách
    }

    private void setupRecyclerView(View view) {
        if (recyclerView != null) {
            jobList = new ArrayList<>();
            jobAdapter = new HorizontalJobAdapter(jobList, requireContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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
        ProgressBar progressBar = getView().findViewById(R.id.progress_bar_search);
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
                ProgressBar progressBar = getView().findViewById(R.id.progress_bar_search);
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

                            // Hiển thị RecyclerView, divider và tiêu đề
                            if (recyclerView != null) {
                                recyclerView.setVisibility(jobs.isEmpty() ? View.GONE : View.VISIBLE);
                            }
                            
                            // Hiển thị divider và tiêu đề kết quả khi có kết quả
                            if (dividerSearchResults != null) {
                                dividerSearchResults.setVisibility(jobs.isEmpty() ? View.GONE : View.VISIBLE);
                            }
                            if (tvResultsTitle != null) {
                                tvResultsTitle.setVisibility(jobs.isEmpty() ? View.GONE : View.VISIBLE);
                                if (!jobs.isEmpty()) {
                                    tvResultsTitle.setText("KẾT QUẢ TÌM KIẾM (" + jobs.size() + " công việc)");
                                }
                            }

                            // Hiển thị thông báo
                            if (jobs.isEmpty()) {
                                Toast.makeText(requireContext(), "Không tìm thấy công việc phù hợp", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Tìm thấy " + jobs.size() + " công việc", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ẩn ProgressBar
                ProgressBar progressBar = getView().findViewById(R.id.progress_bar_search);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateWorkDisciplinesSpinner(Integer workFieldId) {
        // Gọi API để lấy danh sách ngành nghề theo lĩnh vực
        Call<ApiResponse> call = apiService.getWorkDisciplinesByField(workFieldId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawData = (List<?>) apiResponse.getData();
                            List<WorkDiscipline> disciplines = new ArrayList<>();

                            // Thêm tùy chọn "Tất cả ngành nghề" vào đầu danh sách
                            disciplines.add(new WorkDiscipline(0, "Tất cả ngành nghề", null));

                            for (Object obj : rawData) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    WorkDiscipline discipline = convertMapToWorkDiscipline(map);
                                    if (discipline != null) {
                                        disciplines.add(discipline);
                                    }
                                }
                            }

                            workDisciplines = disciplines;
                            updateWorkDisciplineSpinner();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Trong trường hợp lỗi, vẫn giữ nguyên danh sách hiện tại
                updateWorkDisciplineSpinner();
            }
        });
    }

    private void updateJobPositionsSpinner(Integer disciplineId) {
        // Gọi API để lấy danh sách vị trí công việc theo ngành nghề
        Call<ApiResponse> call = apiService.getJobPositionsByDiscipline(disciplineId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawData = (List<?>) apiResponse.getData();
                            List<JobPosition> positions = new ArrayList<>();

                            // Thêm tùy chọn "Tất cả vị trí" vào đầu danh sách
                            positions.add(new JobPosition(0, "Tất cả vị trí", null));

                            for (Object obj : rawData) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    JobPosition position = convertMapToJobPosition(map);
                                    if (position != null) {
                                        positions.add(position);
                                    }
                                }
                            }

                            jobPositions = positions;
                            updateJobPositionSpinner();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Trong trường hợp lỗi, vẫn giữ nguyên danh sách hiện tại
                updateJobPositionSpinner();
            }
        });
    }

    private void updateWorkFieldSpinner() {
        if (workFields != null) {
            List<String> fieldNames = new ArrayList<>();
            fieldNames.add("Tất cả lĩnh vực"); // Thêm tùy chọn "Tất cả" vào đầu danh sách
            for (WorkField field : workFields) {
                fieldNames.add(field.getTenLinhVuc());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, fieldNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWorkField.setAdapter(adapter);
        }
    }

    private void updateWorkDisciplineSpinner() {
        if (workDisciplines != null) {
            List<String> disciplineNames = new ArrayList<>();
            for (WorkDiscipline discipline : workDisciplines) {
                disciplineNames.add(discipline.getTenNganh());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, disciplineNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWorkDiscipline.setAdapter(adapter);
        }
    }

    private void updateJobPositionSpinner() {
        if (jobPositions != null) {
            List<String> positionNames = new ArrayList<>();
            for (JobPosition position : jobPositions) {
                positionNames.add(position.getTenViTri());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, positionNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerJobPosition.setAdapter(adapter);
        }
    }

    private void updateExperienceLevelSpinner() {
        if (experienceLevels != null) {
            List<String> levelNames = new ArrayList<>();
            levelNames.add("Tất cả cấp độ"); // Thêm tùy chọn "Tất cả" vào đầu danh sách
            for (ExperienceLevel level : experienceLevels) {
                levelNames.add(level.getTenCapDo());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, levelNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerExperienceLevel.setAdapter(adapter);
        }
    }

    private void updateWorkTypeSpinner() {
        if (workTypes != null) {
            List<String> typeNames = new ArrayList<>();
            typeNames.add("Tất cả hình thức"); // Thêm tùy chọn "Tất cả" vào đầu danh sách
            for (WorkType type : workTypes) {
                typeNames.add(type.getTenHinhThuc());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, typeNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWorkType.setAdapter(adapter);
        }
    }

    private void loadDropdownDataFromApi() {
        // Load danh sách lĩnh vực
        Call<ApiResponse> workFieldCall = apiService.getAllWorkFields();
        workFieldCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawData = (List<?>) apiResponse.getData();
                            List<WorkField> fields = new ArrayList<>();
                            for (Object obj : rawData) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    WorkField field = convertMapToWorkField(map);
                                    if (field != null) {
                                        fields.add(field);
                                    }
                                }
                            }
                            workFields = fields;
                            updateWorkFieldSpinner();
                            
                            // Sau khi cập nhật spinner, chọn lĩnh vực đã lưu từ bundle
                            if (selectedWorkFieldId != null) {
                                selectWorkFieldById(selectedWorkFieldId);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Trong trường hợp lỗi, vẫn giữ nguyên danh sách hiện tại
                updateWorkFieldSpinner();
            }
        });

        // Load danh sách cấp độ kinh nghiệm
        Call<ApiResponse> experienceLevelCall = apiService.getAllExperienceLevels();
        experienceLevelCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawData = (List<?>) apiResponse.getData();
                            List<ExperienceLevel> levels = new ArrayList<>();
                            for (Object obj : rawData) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    ExperienceLevel level = convertMapToExperienceLevel(map);
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
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Trong trường hợp lỗi, vẫn giữ nguyên danh sách hiện tại
                updateExperienceLevelSpinner();
            }
        });

        // Load danh sách hình thức làm việc
        Call<ApiResponse> workTypeCall = apiService.getAllWorkTypes();
        workTypeCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawData = (List<?>) apiResponse.getData();
                            List<WorkType> types = new ArrayList<>();
                            for (Object obj : rawData) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    WorkType type = convertMapToWorkType(map);
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
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Trong trường hợp lỗi, vẫn giữ nguyên danh sách hiện tại
                updateWorkTypeSpinner();
            }
        });
    }

    private com.example.fjobs.models.WorkType convertMapToWorkType(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.WorkType workType = new com.example.fjobs.models.WorkType();

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

    private void clearFilters() {
        // Reset tất cả các spinner về vị trí đầu tiên (Tất cả)
        spinnerWorkField.setSelection(0);
        spinnerWorkDiscipline.setSelection(0);
        spinnerJobPosition.setSelection(0);
        spinnerExperienceLevel.setSelection(0);
        spinnerWorkType.setSelection(0);

        // Xóa nội dung ô tìm kiếm
        editTextKeyword.setText("");

        // Reset các biến lưu trữ lựa chọn
        selectedWorkFieldId = null;
        selectedWorkDisciplineId = null;
        selectedJobPositionId = null;
        selectedExperienceLevelId = null;
        selectedWorkTypeId = null;
        selectedKeyword = "";

        // Ẩn RecyclerView, divider và tiêu đề kết quả
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (dividerSearchResults != null) {
            dividerSearchResults.setVisibility(View.GONE);
        }
        if (tvResultsTitle != null) {
            tvResultsTitle.setVisibility(View.GONE);
        }
    }

    /**
     * Chọn lĩnh vực theo ID và thực hiện tìm kiếm
     */
    private void selectWorkFieldById(int workFieldId) {
        if (workFields != null && spinnerWorkField != null) {
            // Tìm vị trí của lĩnh vực trong danh sách (cộng 1 vì vị trí 0 là "Tất cả")
            int position = 0;
            for (int i = 0; i < workFields.size(); i++) {
                if (workFields.get(i).getMaLinhVuc() == workFieldId) {
                    position = i + 1; // +1 vì có mục "Tất cả" ở đầu
                    break;
                }
            }
            
            if (position > 0) {
                // Chọn vị trí trong spinner
                spinnerWorkField.setSelection(position);
                
                // Tự động thực hiện tìm kiếm sau khi chọn
                // Đợi một chút để spinner cập nhật xong
                spinnerWorkField.postDelayed(() -> {
                    performAdvancedSearch();
                }, 300);
            }
        }
    }
}