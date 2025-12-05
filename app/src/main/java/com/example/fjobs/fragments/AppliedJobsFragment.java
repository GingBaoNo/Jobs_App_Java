package com.example.fjobs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fjobs.R;
import com.example.fjobs.adapters.AppliedJobsAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.AppliedJob;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AppliedJobsFragment extends Fragment {
    private RecyclerView rvAppliedJobs;
    private AppliedJobsAdapter adapter;
    private List<AppliedJob> appliedJobsList;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_applied_jobs, container, false);

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

        loadAppliedJobs();

        return view;
    }

    private void initViews(View view) {
        rvAppliedJobs = view.findViewById(R.id.rv_applied_jobs);
        appliedJobsList = new ArrayList<>();
        adapter = new AppliedJobsAdapter(requireContext(), appliedJobsList);

        rvAppliedJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvAppliedJobs.setAdapter(adapter);
    }

    private void loadAppliedJobs() {
        Call<ApiResponse> call = apiService.getMyApplications();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawData = (List<?>) apiResponse.getData();
                            List<AppliedJob> loadedAppliedJobs = new ArrayList<>();

                            for (Object obj : rawData) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    AppliedJob appliedJob = convertMapToAppliedJob(map);
                                    if (appliedJob != null) {
                                        loadedAppliedJobs.add(appliedJob);
                                    }
                                }
                            }

                            appliedJobsList.clear();
                            appliedJobsList.addAll(loadedAppliedJobs);
                            adapter.updateData(appliedJobsList);
                        }
                    } else {
                        Toast.makeText(requireContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Không thể tải danh sách công việc đã ứng tuyển", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AppliedJob convertMapToAppliedJob(java.util.Map<String, Object> map) {
        try {
            AppliedJob appliedJob = new AppliedJob();

            // Chuyển đổi maUngTuyen
            if (map.containsKey("maUngTuyen")) {
                Object maUngTuyenObj = map.get("maUngTuyen");
                if (maUngTuyenObj instanceof Integer) {
                    appliedJob.setMaUngTuyen((Integer) maUngTuyenObj);
                } else if (maUngTuyenObj instanceof Double) {
                    appliedJob.setMaUngTuyen(((Double) maUngTuyenObj).intValue());
                } else {
                    appliedJob.setMaUngTuyen(Integer.parseInt(maUngTuyenObj.toString()));
                }
            }

            // Chuyển đổi employee (người ứng tuyển) - bỏ qua nếu không cần thiết
            if (map.containsKey("employee") && map.get("employee") != null) {
                java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) map.get("employee");
                com.example.fjobs.models.User user = convertMapToUser(userMap);
                appliedJob.setEmployee(user);
            }

            // Chuyển đổi jobDetail (công việc)
            if (map.containsKey("jobDetail") && map.get("jobDetail") != null) {
                java.util.Map<String, Object> jobMap = (java.util.Map<String, Object>) map.get("jobDetail");
                com.example.fjobs.models.JobDetail jobDetail = convertMapToJobDetail(jobMap);
                appliedJob.setJobDetail(jobDetail);
            }

            // Chuyển đổi các trường khác
            if (map.containsKey("trangThaiUngTuyen") && map.get("trangThaiUngTuyen") != null) {
                appliedJob.setTrangThaiUngTuyen(map.get("trangThaiUngTuyen").toString());
            }

            if (map.containsKey("danhGiaNtd") && map.get("danhGiaNtd") != null) {
                Object danhGiaNtdObj = map.get("danhGiaNtd");
                if (danhGiaNtdObj instanceof Integer) {
                    appliedJob.setDanhGiaNtd((Integer) danhGiaNtdObj);
                } else if (danhGiaNtdObj instanceof Double) {
                    appliedJob.setDanhGiaNtd(((Double) danhGiaNtdObj).intValue());
                } else {
                    appliedJob.setDanhGiaNtd(Integer.parseInt(danhGiaNtdObj.toString()));
                }
            }

            if (map.containsKey("ngayUngTuyen") && map.get("ngayUngTuyen") != null) {
                appliedJob.setNgayUngTuyen(map.get("ngayUngTuyen").toString());
            }

            if (map.containsKey("urlCvUngTuyen") && map.get("urlCvUngTuyen") != null) {
                appliedJob.setUrlCvUngTuyen(map.get("urlCvUngTuyen").toString());
            }

            return appliedJob;
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

            if (map.containsKey("lienHe") && map.get("lienHe") != null) {
                user.setLienHe(map.get("lienHe").toString());
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

            if (map.containsKey("lienHeCty") && map.get("lienHeCty") != null) {
                company.setLienHeCty(map.get("lienHeCty").toString());
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