package com.example.fjobs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fjobs.R;
import com.example.fjobs.fragments.MapFragment;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.CvProfile;
import com.example.fjobs.models.JobDetail;
import com.example.fjobs.utils.ServerConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.ArrayList;
import java.util.List;

public class JobDetailFragment extends Fragment {
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
    private TextView infoRowExperienceLevelTitle, infoRowExperienceLevelValue; // Thêm mới
    private TextView infoRowQuantityTitle, infoRowQuantityValue;
    private TextView infoRowGenderTitle, infoRowGenderValue;
    private TextView infoRowPostDateTitle, infoRowPostDateValue;
    private TextView infoRowDeadlineTitle, infoRowDeadlineValue;

    // Thêm nút lưu công việc
    private Button btnSaveJob;
    private Button btnViewOnMap; // Nút xem trên bản đồ
    private boolean isJobSaved = false;
    private int savedJobId = -1;

    private int jobId;
    private JobDetail currentJob; // Biến để lưu trữ thông tin công việc hiện tại
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_job_detail, container, false);

        initViews(view);

        // Khởi tạo ApiService với đảm bảo rằng ApiClient đã được khởi tạo với context
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        if (retrofit != null) {
            apiService = retrofit.create(ApiService.class);
        } else {
            // Nếu chưa có retrofit, cần khởi tạo lại với context
            ApiClient.initialize(requireContext());
            apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        }

        // Lấy ID công việc từ arguments
        Bundle args = getArguments();
        if (args != null) {
            jobId = args.getInt("job_id", -1);
            if (jobId == -1) {
                Toast.makeText(requireContext(), "Lỗi: Không tìm thấy ID công việc", Toast.LENGTH_SHORT).show();
                return view;
            }
        }

        loadJobDetails();
        setupClickListeners(view);
        checkJobSavedStatus();
        updateApplyButtonState(); // Cập nhật trạng thái nút ứng tuyển khi tạo fragment

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật trạng thái nút khi quay lại từ màn hình đăng nhập
        if (isUserLoggedIn()) {
            checkJobSavedStatus(); // Kiểm tra lại trạng thái lưu công việc sau khi đăng nhập
        } else {
            // Nếu không đăng nhập, đảm bảo rằng nút lưu và ứng tuyển không khả dụng
            updateSaveButtonState();
        }
        updateApplyButtonState(); // Cập nhật trạng thái nút ứng tuyển
    }

    private void initViews(View view) {
        // Các view cũ
        tvJobTitleDetail = view.findViewById(R.id.tv_job_title_detail);
        tvJobSalaryDetail = view.findViewById(R.id.tv_job_salary_detail);
        tvJobDeadlineDetail = view.findViewById(R.id.tv_job_deadline_detail);
        tvJobDescriptionDetail = view.findViewById(R.id.tv_job_description_detail);
        btnApplyJob = view.findViewById(R.id.btn_apply_job);
        btnSaveJob = view.findViewById(R.id.btn_save_job); // Thêm nút lưu công việc
        btnViewOnMap = view.findViewById(R.id.btn_view_on_map); // Thêm nút xem trên bản đồ

        // Các view mới
        ivCompanyLogoDetail = view.findViewById(R.id.iv_company_logo_detail);
        tvCompanyNameDetail = view.findViewById(R.id.tv_company_name_detail);
        listRequirements = view.findViewById(R.id.list_requirements);
        listBenefits = view.findViewById(R.id.list_benefits);

        // Các view cho hàng tiêu đề-giá trị - truy cập qua các layout include
        LinearLayout infoRowPosition = view.findViewById(R.id.info_row_position);
        infoRowPositionTitle = infoRowPosition.findViewById(R.id.text1);
        infoRowPositionValue = infoRowPosition.findViewById(R.id.text2);

        LinearLayout infoRowSalary = view.findViewById(R.id.info_row_salary);
        infoRowSalaryTitle = infoRowSalary.findViewById(R.id.text1);
        infoRowSalaryValue = infoRowSalary.findViewById(R.id.text2);

        LinearLayout infoRowForm = view.findViewById(R.id.info_row_form);
        infoRowFormTitle = infoRowForm.findViewById(R.id.text1);
        infoRowFormValue = infoRowForm.findViewById(R.id.text2);

        LinearLayout infoRowExperienceLevel = view.findViewById(R.id.info_row_experience_level);
        infoRowExperienceLevelTitle = infoRowExperienceLevel.findViewById(R.id.text1);
        infoRowExperienceLevelValue = infoRowExperienceLevel.findViewById(R.id.text2);

        LinearLayout infoRowQuantity = view.findViewById(R.id.info_row_quantity);
        infoRowQuantityTitle = infoRowQuantity.findViewById(R.id.text1);
        infoRowQuantityValue = infoRowQuantity.findViewById(R.id.text2);

        LinearLayout infoRowGender = view.findViewById(R.id.info_row_gender);
        infoRowGenderTitle = infoRowGender.findViewById(R.id.text1);
        infoRowGenderValue = infoRowGender.findViewById(R.id.text2);

        LinearLayout infoRowPostDate = view.findViewById(R.id.info_row_postdate);
        infoRowPostDateTitle = infoRowPostDate.findViewById(R.id.text1);
        infoRowPostDateValue = infoRowPostDate.findViewById(R.id.text2);

        LinearLayout infoRowDeadline = view.findViewById(R.id.info_row_deadline);
        infoRowDeadlineTitle = infoRowDeadline.findViewById(R.id.text1);
        infoRowDeadlineValue = infoRowDeadline.findViewById(R.id.text2);
    }

    private void setupClickListeners(View view) {
        btnApplyJob.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                applyForJob();
            } else {
                redirectToLogin();
            }
        });

        btnSaveJob.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                // Thêm cơ chế chống nhấn nhanh
                v.setEnabled(false);
                v.postDelayed(() -> v.setEnabled(true), 500); // Kích hoạt lại nút sau 500ms

                if (isJobSaved) {
                    unsaveJob();
                } else {
                    saveJob();
                }
            } else {
                redirectToLogin();
            }
        });

        // View on map button
        btnViewOnMap.setOnClickListener(v -> {
            // Kiểm tra xem công việc có tọa độ không trước khi mở bản đồ
            if (currentJob != null && currentJob.getKinhDo() != null && currentJob.getViDo() != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("job_id", currentJob.getMaCongViec());
                bundle.putString("job_title", currentJob.getTieuDe());
                bundle.putDouble("latitude", currentJob.getViDo().doubleValue());
                bundle.putDouble("longitude", currentJob.getKinhDo().doubleValue());

                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundle);

                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, mapFragment)
                        .addToBackStack(null)
                        .commit();
                }
            } else {
                // Nếu công việc không có tọa độ, thử lấy tọa độ từ công ty
                if (currentJob != null && currentJob.getCompany() != null &&
                    currentJob.getCompany().getKinhDo() != null && currentJob.getCompany().getViDo() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("job_id", currentJob.getMaCongViec());
                    bundle.putString("job_title", currentJob.getTieuDe());
                    bundle.putDouble("latitude", currentJob.getCompany().getViDo().doubleValue());
                    bundle.putDouble("longitude", currentJob.getCompany().getKinhDo().doubleValue());

                    MapFragment mapFragment = new MapFragment();
                    mapFragment.setArguments(bundle);

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, mapFragment)
                            .addToBackStack(null)
                            .commit();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không có thông tin vị trí cho công việc này", Toast.LENGTH_SHORT).show();
                }
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
                                Toast.makeText(requireContext(), "Dữ liệu công việc không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Dữ liệu công việc không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể tải chi tiết công việc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

            // Xử lý tọa độ công việc nếu có
            if (map.containsKey("kinhDo") && map.get("kinhDo") != null) {
                Object kinhDoObj = map.get("kinhDo");
                if (kinhDoObj instanceof Double) {
                    job.setKinhDo(java.math.BigDecimal.valueOf((Double) kinhDoObj));
                } else if (kinhDoObj instanceof Integer) {
                    job.setKinhDo(java.math.BigDecimal.valueOf((Integer) kinhDoObj));
                } else if (kinhDoObj instanceof String) {
                    job.setKinhDo(new java.math.BigDecimal((String) kinhDoObj));
                } else {
                    job.setKinhDo(java.math.BigDecimal.valueOf(Double.parseDouble(kinhDoObj.toString())));
                }
            }

            if (map.containsKey("viDo") && map.get("viDo") != null) {
                Object viDoObj = map.get("viDo");
                if (viDoObj instanceof Double) {
                    job.setViDo(java.math.BigDecimal.valueOf((Double) viDoObj));
                } else if (viDoObj instanceof Integer) {
                    job.setViDo(java.math.BigDecimal.valueOf((Integer) viDoObj));
                } else if (viDoObj instanceof String) {
                    job.setViDo(new java.math.BigDecimal((String) viDoObj));
                } else {
                    job.setViDo(java.math.BigDecimal.valueOf(Double.parseDouble(viDoObj.toString())));
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

            // Xử lý tọa độ công ty nếu có
            if (map.containsKey("kinhDo") && map.get("kinhDo") != null) {
                Object kinhDoObj = map.get("kinhDo");
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

            if (map.containsKey("viDo") && map.get("viDo") != null) {
                Object viDoObj = map.get("viDo");
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

    private void displayJobDetails(JobDetail job) {
        // Cập nhật biến currentJob
        this.currentJob = job;

        // Hiển thị thông tin công việc
        tvJobTitleDetail.setText(job.getTieuDe());
        
        if (job.getLuong() != null) {
            String salaryText = String.format("%,d VNĐ", job.getLuong());
            if (job.getLoaiLuong() != null && !job.getLoaiLuong().isEmpty()) {
                salaryText += " (" + job.getLoaiLuong() + ")";
            }
            tvJobSalaryDetail.setText(salaryText);
        } else {
            tvJobSalaryDetail.setText("Thương lượng");
        }

        // Hiển thị hạn nộp
        if (job.getNgayKetThucTuyenDung() != null) {
            tvJobDeadlineDetail.setText("Hạn nộp: " + job.getNgayKetThucTuyenDung());
        } else {
            tvJobDeadlineDetail.setText("Hạn nộp: Không xác định");
        }

        // Hiển thị mô tả công việc
        if (job.getChiTiet() != null) {
            tvJobDescriptionDetail.setText(job.getChiTiet());
        } else {
            tvJobDescriptionDetail.setText("Chưa có mô tả chi tiết.");
        }

        // Hiển thị thông tin công ty
        if (job.getCompany() != null) {
            tvCompanyNameDetail.setText(job.getCompany().getTenCongTy());

            // Load logo công ty
            if (job.getCompany().getHinhAnhCty() != null && !job.getCompany().getHinhAnhCty().isEmpty()) {
                String logoUrl = ServerConfig.getBaseUrl() + job.getCompany().getHinhAnhCty();
                Glide.with(this)
                    .load(logoUrl)
                    .placeholder(R.drawable.ic_boss)
                    .error(R.drawable.ic_boss)
                    .into(ivCompanyLogoDetail);
            } else {
                ivCompanyLogoDetail.setImageResource(R.drawable.ic_boss);
            }
        }

        // Hiển thị thông tin bổ sung
        displayAdditionalInfo(job);

        // Hiển thị yêu cầu công việc
        displayJobRequirements(job);

        // Hiển thị quyền lợi
        displayJobBenefits(job);
    }

    private void displayAdditionalInfo(JobDetail job) {
        // Cập nhật các hàng thông tin
        infoRowPositionValue.setText(job.getJobPosition() != null ? job.getJobPosition().getTenViTri() : "N/A");
        infoRowSalaryValue.setText(job.getLuong() != null ? String.format("%,d VNĐ", job.getLuong()) : "Thương lượng");
        infoRowFormValue.setText(job.getCompany() != null ? job.getCompany().getTenCongTy() : "N/A");
        infoRowExperienceLevelValue.setText(job.getExperienceLevel() != null ? job.getExperienceLevel().getTenCapDo() : "N/A");
        infoRowQuantityValue.setText(job.getSoLuongTuyen() != null ? job.getSoLuongTuyen().toString() : "N/A");
        infoRowGenderValue.setText(job.getGioiTinhYeuCau() != null ? job.getGioiTinhYeuCau() : "N/A");
        infoRowPostDateValue.setText(job.getNgayDang() != null ? job.getNgayDang() : "N/A");
        infoRowDeadlineValue.setText(job.getNgayKetThucTuyenDung() != null ? job.getNgayKetThucTuyenDung() : "N/A");
    }

    private void displayJobRequirements(JobDetail job) {
        // Xóa nội dung cũ
        listRequirements.removeAllViews();

        // Thêm yêu cầu công việc nếu có
        if (job.getYeuCauCongViec() != null && !job.getYeuCauCongViec().isEmpty()) {
            String[] requirements = job.getYeuCauCongViec().split("\n");
            for (String requirement : requirements) {
                if (!requirement.trim().isEmpty()) {
                    TextView reqView = createInfoTextView(requirement.trim());
                    listRequirements.addView(reqView);
                }
            }
        } else {
            TextView reqView = createInfoTextView("Không có yêu cầu cụ thể được liệt kê.");
            listRequirements.addView(reqView);
        }
    }

    private void displayJobBenefits(JobDetail job) {
        // Xóa nội dung cũ
        listBenefits.removeAllViews();

        // Thêm quyền lợi nếu có
        if (job.getQuyenLoi() != null && !job.getQuyenLoi().isEmpty()) {
            String[] benefits = job.getQuyenLoi().split("\n");
            for (String benefit : benefits) {
                if (!benefit.trim().isEmpty()) {
                    TextView benefitView = createInfoTextView(benefit.trim());
                    listBenefits.addView(benefitView);
                }
            }
        } else {
            TextView benefitView = createInfoTextView("Không có quyền lợi cụ thể được liệt kê.");
            listBenefits.addView(benefitView);
        }
    }

    private TextView createInfoTextView(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText("• " + text);
        textView.setTextSize(14);
        textView.setPadding(16, 8, 16, 8);
        return textView;
    }

    private void applyForJob() {
        // Lấy danh sách hồ sơ CV của người dùng
        ApiService apiService = ApiClient.getApiService();

        Call<ApiResponse> call = apiService.getMyCvProfiles();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API sang danh sách CvProfile
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawList = (List<?>) apiResponse.getData();
                            List<CvProfile> cvProfiles = new ArrayList<>();

                            for (Object obj : rawList) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    CvProfile profile = convertMapToCvProfile(map);
                                    if (profile != null) {
                                        cvProfiles.add(profile);
                                    }
                                }
                            }

                            // Hiển thị dialog chọn hồ sơ CV
                            showCvProfileSelectionDialog(cvProfiles);
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi khi tải danh sách hồ sơ";
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể kết nối đến máy chủ để tải danh sách hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCvProfileSelectionDialog(List<CvProfile> cvProfiles) {
        if (cvProfiles.isEmpty()) {
            // Nếu không có hồ sơ CV nào, hỏi người dùng có muốn tạo mới không
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Chưa có hồ sơ CV")
                    .setMessage("Bạn chưa có hồ sơ CV nào. Bạn có muốn tạo mới không?")
                    .setPositiveButton("Tạo mới", (dialog, which) -> {
                        // Điều hướng đến màn hình tạo hồ sơ CV
                        if (getActivity() != null) {
                            // Tạo bundle để truyền thông tin công việc cho quá trình ứng tuyển
                            Bundle bundle = new Bundle();
                            bundle.putString("mode", "create");
                            bundle.putInt("job_id_to_apply", jobId); // Truyền ID công việc để quay lại sau khi tạo hồ sơ

                            CreateEditCvProfileFragment createEditCvProfileFragment = new CreateEditCvProfileFragment();
                            createEditCvProfileFragment.setArguments(bundle);

                            getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_frame, createEditCvProfileFragment)
                                .addToBackStack(null)
                                .commit();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return;
        }

        // Tạo danh sách tên hồ sơ để hiển thị trong dialog
        String[] profileNames = new String[cvProfiles.size()];
        for (int i = 0; i < cvProfiles.size(); i++) {
            profileNames[i] = cvProfiles.get(i).getTenHoSo();
        }

        // Hiển thị dialog chọn hồ sơ
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Chọn hồ sơ để ứng tuyển")
                .setItems(profileNames, (dialog, which) -> {
                    // Người dùng đã chọn hồ sơ, tiến hành ứng tuyển với hồ sơ đó
                    CvProfile selectedProfile = cvProfiles.get(which);
                    applyForJobWithCvProfile(selectedProfile.getMaHoSoCv());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void applyForJobWithCvProfile(int cvProfileId) {
        // Gọi API ứng tuyển với hồ sơ cụ thể
        ApiService apiService = ApiClient.getApiService();

        // Tạo request object
        ApiService.AppliedJobWithCvProfileRequest request = new ApiService.AppliedJobWithCvProfileRequest();
        request.setJobDetailId(jobId);
        request.setCvProfileId(cvProfileId);

        Call<ApiResponse> call = apiService.applyForJobWithCvProfile(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Ứng tuyển thành công!", Toast.LENGTH_SHORT).show();
                        updateApplyButtonState(); // Cập nhật trạng thái nút
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Ứng tuyển thất bại";
                        Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể ứng tuyển", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CvProfile convertMapToCvProfile(java.util.Map<String, Object> map) {
        try {
            CvProfile profile = new CvProfile();

            if (map.containsKey("maHoSoCv")) {
                Object idObj = map.get("maHoSoCv");
                if (idObj instanceof Integer) {
                    profile.setMaHoSoCv((Integer) idObj);
                } else if (idObj instanceof Double) {
                    profile.setMaHoSoCv(((Double) idObj).intValue());
                } else {
                    profile.setMaHoSoCv(Integer.parseInt(idObj.toString()));
                }
            }

            if (map.containsKey("maNguoiTimViec")) {
                Object userIdObj = map.get("maNguoiTimViec");
                if (userIdObj != null) {
                    if (userIdObj instanceof Integer) {
                        profile.setMaNguoiTimViec((Integer) userIdObj);
                    } else if (userIdObj instanceof Double) {
                        profile.setMaNguoiTimViec(((Double) userIdObj).intValue());
                    } else {
                        profile.setMaNguoiTimViec(Integer.parseInt(userIdObj.toString()));
                    }
                }
            }

            if (map.containsKey("tenHoSo") && map.get("tenHoSo") != null) {
                profile.setTenHoSo(map.get("tenHoSo").toString());
            }

            if (map.containsKey("moTa") && map.get("moTa") != null) {
                profile.setMoTa(map.get("moTa").toString());
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
                Object expObj = map.get("tongNamKinhNghiem");
                if (expObj instanceof Double) {
                    profile.setTongNamKinhNghiem(java.math.BigDecimal.valueOf((Double) expObj));
                } else if (expObj instanceof Integer) {
                    profile.setTongNamKinhNghiem(java.math.BigDecimal.valueOf((Integer) expObj));
                } else {
                    profile.setTongNamKinhNghiem(new java.math.BigDecimal(expObj.toString()));
                }
            }

            if (map.containsKey("gioiThieuBanThan") && map.get("gioiThieuBanThan") != null) {
                profile.setGioiThieuBanThan(map.get("gioiThieuBanThan").toString());
            }

            if (map.containsKey("urlCv") && map.get("urlCv") != null) {
                profile.setUrlCv(map.get("urlCv").toString());
            }

            if (map.containsKey("congKhai")) {
                Object publicObj = map.get("congKhai");
                if (publicObj instanceof Boolean) {
                    profile.setCongKhai((Boolean) publicObj);
                } else {
                    profile.setCongKhai(Boolean.parseBoolean(publicObj.toString()));
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

            if (map.containsKey("mucLuongMongMuon")) {
                Object salaryObj = map.get("mucLuongMongMuon");
                if (salaryObj != null) {
                    if (salaryObj instanceof Integer) {
                        profile.setMucLuongMongMuon((Integer) salaryObj);
                    } else if (salaryObj instanceof Double) {
                        profile.setMucLuongMongMuon(((Double) salaryObj).intValue());
                    } else {
                        profile.setMucLuongMongMuon(Integer.parseInt(salaryObj.toString()));
                    }
                }
            }

            if (map.containsKey("ngayTao") && map.get("ngayTao") != null) {
                profile.setNgayTao(map.get("ngayTao").toString());
            }

            if (map.containsKey("ngayCapNhat") && map.get("ngayCapNhat") != null) {
                profile.setNgayCapNhat(map.get("ngayCapNhat").toString());
            }

            if (map.containsKey("laMacDinh")) {
                Object defaultObj = map.get("laMacDinh");
                if (defaultObj instanceof Boolean) {
                    profile.setLaMacDinh((Boolean) defaultObj);
                } else {
                    profile.setLaMacDinh(Boolean.parseBoolean(defaultObj.toString()));
                }
            }

            return profile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveJob() {
        // Gọi API lưu công việc
        ApiService apiService = ApiClient.getApiService();
        
        // Tạo request object
        ApiService.SaveJobRequest request = new ApiService.SaveJobRequest();
        request.setJobDetailId(jobId);
        
        Call<ApiResponse> call = apiService.saveJob(request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Lưu công việc thành công!", Toast.LENGTH_SHORT).show();
                        isJobSaved = true;
                        updateSaveButtonState(); // Cập nhật trạng thái nút
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lưu công việc thất bại";
                        Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể lưu công việc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unsaveJob() {
        // Gọi API hủy lưu công việc
        ApiService apiService = ApiClient.getApiService();
        
        Call<ApiResponse> call = apiService.unsaveJob(jobId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(requireContext(), "Hủy lưu công việc thành công!", Toast.LENGTH_SHORT).show();
                        isJobSaved = false;
                        updateSaveButtonState(); // Cập nhật trạng thái nút
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Hủy lưu công việc thất bại";
                        Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể hủy lưu công việc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkJobSavedStatus() {
        // Kiểm tra trạng thái lưu công việc
        ApiService apiService = ApiClient.getApiService();
        
        Call<ApiResponse> call = apiService.checkIfJobSaved(jobId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Dữ liệu trả về là boolean cho biết công việc đã được lưu hay chưa
                        if (apiResponse.getData() instanceof Boolean) {
                            isJobSaved = (Boolean) apiResponse.getData();
                            updateSaveButtonState();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Nếu kiểm tra thất bại, không làm gì cả (giữ nguyên trạng thái hiện tại)
            }
        });
    }

    private void updateApplyButtonState() {
        // Kiểm tra xem người dùng đã ứng tuyển chưa
        // Trong phiên bản đơn giản này, chúng ta có thể gọi API để kiểm tra trạng thái ứng tuyển
        // hoặc lưu trạng thái cục bộ sau khi ứng tuyển thành công
        if (isUserLoggedIn()) {
            btnApplyJob.setEnabled(true);
            btnApplyJob.setText("Ứng tuyển");
            // Trong phiên bản đầy đủ, bạn có thể kiểm tra trạng thái ứng tuyển thực tế từ API
        } else {
            btnApplyJob.setEnabled(false);
            btnApplyJob.setText("Đăng nhập để ứng tuyển");
        }
    }

    private void updateSaveButtonState() {
        if (isUserLoggedIn()) {
            btnSaveJob.setEnabled(true);
            if (isJobSaved) {
                btnSaveJob.setText("Đã lưu");
            } else {
                btnSaveJob.setText("Lưu công việc");
            }
        } else {
            btnSaveJob.setEnabled(false);
            btnSaveJob.setText("Đăng nhập để lưu");
        }
    }

    private boolean isUserLoggedIn() {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        // Trong phiên bản đầy đủ, bạn sẽ kiểm tra token trong SharedPreferences
        com.example.fjobs.utils.SessionManager sessionManager = new com.example.fjobs.utils.SessionManager(requireContext());
        return sessionManager.isLoggedIn();
    }

    private void redirectToLogin() {
        // Chuyển hướng đến màn hình đăng nhập
        // Trong Fragment, bạn cần sử dụng Activity để chuyển hướng
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), com.example.fjobs.activities.LoginActivity.class);
            startActivity(intent);
        }
    }
}