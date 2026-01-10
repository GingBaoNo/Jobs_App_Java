package com.example.fjobs.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fjobs.R;
import com.example.fjobs.adapters.SavedJobsAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.SavedJob;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SavedJobsActivity extends AppCompatActivity {
    private RecyclerView rvSavedJobs;
    private SavedJobsAdapter adapter;
    private List<SavedJob> savedJobsList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_jobs);

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

        loadSavedJobs();
    }

    private void initViews() {
        rvSavedJobs = findViewById(R.id.rv_saved_jobs);
        savedJobsList = new ArrayList<>();
        adapter = new SavedJobsAdapter(this, savedJobsList);
        
        rvSavedJobs.setLayoutManager(new LinearLayoutManager(this));
        rvSavedJobs.setAdapter(adapter);
    }

    private void loadSavedJobs() {
        Call<ApiResponse> call = apiService.getMySavedJobs();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawData = (List<?>) apiResponse.getData();
                            List<SavedJob> loadedSavedJobs = new ArrayList<>();
                            
                            for (Object obj : rawData) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    SavedJob savedJob = convertMapToSavedJob(map);
                                    if (savedJob != null) {
                                        loadedSavedJobs.add(savedJob);
                                    }
                                }
                            }
                            
                            savedJobsList.clear();
                            savedJobsList.addAll(loadedSavedJobs);
                            adapter.updateData(savedJobsList);
                        }
                    } else {
                        Toast.makeText(SavedJobsActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SavedJobsActivity.this, "Không thể tải danh sách công việc đã lưu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(SavedJobsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SavedJob convertMapToSavedJob(java.util.Map<String, Object> map) {
        try {
            SavedJob savedJob = new SavedJob();
            
            // Chuyển đổi maCvDaLuu
            if (map.containsKey("maCvDaLuu")) {
                Object maCvDaLuuObj = map.get("maCvDaLuu");
                if (maCvDaLuuObj instanceof Integer) {
                    savedJob.setMaCvDaLuu((Integer) maCvDaLuuObj);
                } else if (maCvDaLuuObj instanceof Double) {
                    savedJob.setMaCvDaLuu(((Double) maCvDaLuuObj).intValue());
                } else {
                    savedJob.setMaCvDaLuu(Integer.parseInt(maCvDaLuuObj.toString()));
                }
            }
            
            // Chuyển đổi user
            if (map.containsKey("user") && map.get("user") != null) {
                java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) map.get("user");
                com.example.fjobs.models.User user = convertMapToUser(userMap);
                savedJob.setUser(user);
            }
            
            // Chuyển đổi jobDetail (công việc)
            if (map.containsKey("jobDetail") && map.get("jobDetail") != null) {
                java.util.Map<String, Object> jobMap = (java.util.Map<String, Object>) map.get("jobDetail");
                com.example.fjobs.models.JobDetail jobDetail = convertMapToJobDetail(jobMap);
                savedJob.setJobDetail(jobDetail);
            }
            
            // Chuyển đổi ngày lưu
            if (map.containsKey("ngayLuu") && map.get("ngayLuu") != null) {
                savedJob.setNgayLuu(map.get("ngayLuu").toString());
            }
            
            return savedJob;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private com.example.fjobs.models.User convertMapToUser(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.User user = new com.example.fjobs.models.User();
            
            if (map.containsKey("maNguoiDung")) {
                Object maNguoiDungObj = map.get("maNguoiDung");
                if (maNguoiDungObj instanceof Integer) {
                    user.setMaNguoiDung((Integer) maNguoiDungObj);
                } else if (maNguoiDungObj instanceof Double) {
                    user.setMaNguoiDung(((Double) maNguoiDungObj).intValue());
                } else {
                    user.setMaNguoiDung(Integer.parseInt(maNguoiDungObj.toString()));
                }
            }
            
            if (map.containsKey("taiKhoan") && map.get("taiKhoan") != null) {
                user.setTaiKhoan(map.get("taiKhoan").toString());
            }
            
            if (map.containsKey("tenHienThi") && map.get("tenHienThi") != null) {
                user.setTenHienThi(map.get("tenHienThi").toString());
            }
            
            if (map.containsKey("email") && map.get("email") != null) {
                user.setEmail(map.get("email").toString());
            }
            if (map.containsKey("soDienThoai") && map.get("soDienThoai") != null) {
                user.setSoDienThoai(map.get("soDienThoai").toString());
            }
            
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private com.example.fjobs.models.JobDetail convertMapToJobDetail(java.util.Map<String, Object> map) {
        try {
            com.example.fjobs.models.JobDetail job = new com.example.fjobs.models.JobDetail();
            
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
            
            return company;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}