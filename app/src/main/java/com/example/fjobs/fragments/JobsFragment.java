package com.example.fjobs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fjobs.activities.JobDetailActivity;
import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.JobDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class JobsFragment extends Fragment implements com.example.fjobs.adapters.JobAdapter.OnJobClickListener {
    private RecyclerView rvJobs;
    private com.example.fjobs.adapters.JobAdapter jobAdapter;
    private List<JobDetail> jobList;
    private ApiService apiService;
    private Button btnAdvancedSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);

        initViews(view);
        setupRecyclerView();
        loadJobs();

        return view;
    }

    private void initViews(View view) {
        rvJobs = view.findViewById(R.id.rv_jobs);
        btnAdvancedSearch = view.findViewById(R.id.btn_advanced_search);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Xử lý sự kiện click cho nút tìm kiếm nâng cao
        btnAdvancedSearch.setOnClickListener(v -> {
            // Chuyển sang AdvancedSearchActivity
            Intent intent = new Intent(getActivity(), com.example.fjobs.activities.AdvancedSearchActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        jobList = new ArrayList<>();
        jobAdapter = new com.example.fjobs.adapters.JobAdapter(jobList, this); // Truyền listener là fragment hiện tại
        rvJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        rvJobs.setAdapter(jobAdapter);
    }

    private void loadJobs() {
        Call<ApiResponse> call = apiService.getAllJobs();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("JobsFragment", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("JobsFragment", "Response body: " + response.body().toString());
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API sang List<JobDetail>
                        // Dữ liệu từ API thường được deserialize thành LinkedTreeMap
                        // nên cần xử lý chuyển đổi đúng cách
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawList = (List<?>) apiResponse.getData();
                            List<JobDetail> jobs = new ArrayList<>();

                            for (Object obj : rawList) {
                                if (obj instanceof java.util.Map) {
                                    // Chuyển đổi Map sang JobDetail
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    JobDetail job = convertMapToJobDetail(map);

                                    if (job != null) {
                                        jobs.add(job);
                                    } else {
                                        Log.e("JobsFragment", "Failed to convert map to JobDetail: " + map);
                                    }
                                } else if (obj instanceof JobDetail) {
                                    jobs.add((JobDetail) obj);
                                }
                            }

                            jobList.clear();
                            jobList.addAll(jobs);
                            jobAdapter.notifyDataSetChanged();

                            Log.d("JobsFragment", "Loaded " + jobs.size() + " jobs from API");
                        } else {
                            Log.e("JobsFragment", "Data is not a list: " + apiResponse.getData().getClass().getName());
                        }
                    } else {
                        Log.e("JobsFragment", "API response not successful or no data: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e("JobsFragment", "Failed to load jobs: " + response.code() + " - " + (response.body() != null ? response.body().getMessage() : "No body"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("JobsFragment", "Error loading jobs", t);
            }
        });
    }

    @Override
    public void onJobClick(JobDetail job) {
        // Chuyển đến màn hình chi tiết công việc
        Intent intent = new Intent(getContext(), JobDetailActivity.class);
        intent.putExtra("job_id", job.getMaCongViec());
        startActivity(intent);
    }

    /**
     * Converts a Map representation of a job to a JobDetail object
     */
    private JobDetail convertMapToJobDetail(java.util.Map<String, Object> map) {
        try {
            JobDetail job = new JobDetail();

            // Handle maCongViec
            if (map.containsKey("maCongViec") && map.get("maCongViec") != null) {
                Object maCongViecObj = map.get("maCongViec");
                if (maCongViecObj instanceof Integer) {
                    job.setMaCongViec((Integer) maCongViecObj);
                } else if (maCongViecObj instanceof Double) {
                    job.setMaCongViec(((Double) maCongViecObj).intValue());
                } else if (maCongViecObj instanceof Float) {
                    job.setMaCongViec(((Float) maCongViecObj).intValue());
                } else {
                    job.setMaCongViec(Integer.parseInt(maCongViecObj.toString()));
                }
            }

            // Handle maCongTy
            if (map.containsKey("maCongTy") && map.get("maCongTy") != null) {
                Object maCongTyObj = map.get("maCongTy");
                if (maCongTyObj instanceof Integer) {
                    job.setMaCongTy((Integer) maCongTyObj);
                } else if (maCongTyObj instanceof Double) {
                    job.setMaCongTy(((Double) maCongTyObj).intValue());
                } else if (maCongTyObj instanceof Float) {
                    job.setMaCongTy(((Float) maCongTyObj).intValue());
                } else {
                    job.setMaCongTy(Integer.parseInt(maCongTyObj.toString()));
                }
            }

            // Handle tieuDe (job title)
            if (map.containsKey("tieuDe") && map.get("tieuDe") != null) {
                job.setTieuDe(map.get("tieuDe").toString());
            }

            // Handle luong (salary)
            if (map.containsKey("luong") && map.get("luong") != null) {
                Object luongObj = map.get("luong");
                if (luongObj instanceof Integer) {
                    job.setLuong((Integer) luongObj);
                } else if (luongObj instanceof Double) {
                    job.setLuong(((Double) luongObj).intValue());
                } else if (luongObj instanceof Float) {
                    job.setLuong(((Float) luongObj).intValue());
                } else {
                    job.setLuong(Integer.parseInt(luongObj.toString()));
                }
            }

            // Handle loaiLuong
            if (map.containsKey("loaiLuong") && map.get("loaiLuong") != null) {
                job.setLoaiLuong(map.get("loaiLuong").toString());
            }

            // Handle chiTiet
            if (map.containsKey("chiTiet") && map.get("chiTiet") != null) {
                job.setChiTiet(map.get("chiTiet").toString());
            }

            // Handle ngayKetThucTuyenDung
            if (map.containsKey("ngayKetThucTuyenDung") && map.get("ngayKetThucTuyenDung") != null) {
                job.setNgayKetThucTuyenDung(map.get("ngayKetThucTuyenDung").toString());
            }

            // Handle ngayDang
            if (map.containsKey("ngayDang") && map.get("ngayDang") != null) {
                job.setNgayDang(map.get("ngayDang").toString());
            }

            // Handle luotXem
            if (map.containsKey("luotXem") && map.get("luotXem") != null) {
                Object luotXemObj = map.get("luotXem");
                if (luotXemObj instanceof Integer) {
                    job.setLuotXem((Integer) luotXemObj);
                } else if (luotXemObj instanceof Double) {
                    job.setLuotXem(((Double) luotXemObj).intValue());
                } else if (luotXemObj instanceof Float) {
                    job.setLuotXem(((Float) luotXemObj).intValue());
                } else {
                    job.setLuotXem(Integer.parseInt(luotXemObj.toString()));
                }
            }

            // Handle trangThaiDuyet
            if (map.containsKey("trangThaiDuyet") && map.get("trangThaiDuyet") != null) {
                job.setTrangThaiDuyet(map.get("trangThaiDuyet").toString());
            }

            // Handle trangThaiTinTuyen
            if (map.containsKey("trangThaiTinTuyen") && map.get("trangThaiTinTuyen") != null) {
                job.setTrangThaiTinTuyen(map.get("trangThaiTinTuyen").toString());
            }

            // Handle company information if present
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
            Log.e("JobsFragment", "Error converting map to JobDetail", e);
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
                } else if (maCongTyObj instanceof Float) {
                    company.setMaCongTy(((Float) maCongTyObj).intValue());
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
            Log.e("JobsFragment", "Error converting map to Company", e);
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
                } else if (maViTriObj instanceof Float) {
                    jobPosition.setMaViTri(((Float) maViTriObj).intValue());
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
            Log.e("JobsFragment", "Error converting map to JobPosition", e);
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
                } else if (maCapDoObj instanceof Float) {
                    experienceLevel.setMaCapDo(((Float) maCapDoObj).intValue());
                } else {
                    experienceLevel.setMaCapDo(Integer.parseInt(maCapDoObj.toString()));
                }
            }

            if (map.containsKey("tenCapDo") && map.get("tenCapDo") != null) {
                experienceLevel.setTenCapDo(map.get("tenCapDo").toString());
            }

            return experienceLevel;
        } catch (Exception e) {
            Log.e("JobsFragment", "Error converting map to ExperienceLevel", e);
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
                } else if (maNganhObj instanceof Float) {
                    workDiscipline.setMaNganh(((Float) maNganhObj).intValue());
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
            Log.e("JobsFragment", "Error converting map to WorkDiscipline", e);
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
                } else if (maLinhVucObj instanceof Float) {
                    workField.setMaLinhVuc(((Float) maLinhVucObj).intValue());
                } else {
                    workField.setMaLinhVuc(Integer.parseInt(maLinhVucObj.toString()));
                }
            }

            if (map.containsKey("tenLinhVuc") && map.get("tenLinhVuc") != null) {
                workField.setTenLinhVuc(map.get("tenLinhVuc").toString());
            }

            return workField;
        } catch (Exception e) {
            Log.e("JobsFragment", "Error converting map to WorkField", e);
            return null;
        }
    }
}