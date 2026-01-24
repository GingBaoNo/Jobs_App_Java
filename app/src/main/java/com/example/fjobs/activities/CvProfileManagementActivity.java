package com.example.fjobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fjobs.R;
import com.example.fjobs.adapters.CvProfileAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.CvProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class CvProfileManagementActivity extends AppCompatActivity implements CvProfileAdapter.OnCvProfileActionListener {

    private RecyclerView rvCvProfiles;
    private EditText etSearchCv;
    private Button btnAddCv;
    private CvProfileAdapter cvProfileAdapter;
    private List<CvProfile> cvProfiles;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cv_profile_management);

        initViews();
        setupRecyclerView();
        apiService = ApiClient.getApiService();
        loadCvProfiles();
        setupClickListeners();
    }

    private void initViews() {
        rvCvProfiles = findViewById(R.id.rv_cv_profiles);
        etSearchCv = findViewById(R.id.et_search_cv);
        btnAddCv = findViewById(R.id.btn_add_cv);
    }

    private void setupRecyclerView() {
        cvProfiles = new ArrayList<>();
        cvProfileAdapter = new CvProfileAdapter(cvProfiles, this);
        rvCvProfiles.setLayoutManager(new LinearLayoutManager(this));
        rvCvProfiles.setAdapter(cvProfileAdapter);
    }

    private void setupClickListeners() {
        btnAddCv.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEditCvProfileActivity.class);
            intent.putExtra("mode", "create");
            startActivity(intent);
        });
    }

    private void loadCvProfiles() {
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
                            List<CvProfile> loadedProfiles = new ArrayList<>();
                            
                            for (Object obj : rawList) {
                                if (obj instanceof java.util.Map) {
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    CvProfile profile = convertMapToCvProfile(map);
                                    if (profile != null) {
                                        loadedProfiles.add(profile);
                                    }
                                }
                            }
                            
                            cvProfiles.clear();
                            cvProfiles.addAll(loadedProfiles);
                            cvProfileAdapter.notifyDataSetChanged();
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Lỗi khi tải danh sách hồ sơ";
                        Toast.makeText(CvProfileManagementActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CvProfileManagementActivity.this, "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CvProfileManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onEditCvProfile(CvProfile cvProfile) {
        Intent intent = new Intent(this, CreateEditCvProfileActivity.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("cv_profile", cvProfile);
        startActivity(intent);
    }

    @Override
    public void onDeleteCvProfile(int position, CvProfile cvProfile) {
        // Xác nhận xóa
        new android.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa hồ sơ '" + cvProfile.getTenHoSo() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCvProfile(cvProfile.getMaHoSoCv(), position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCvProfile(int cvProfileId, int position) {
        Call<ApiResponse> call = apiService.deleteCvProfile(cvProfileId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        cvProfiles.remove(position);
                        cvProfileAdapter.notifyItemRemoved(position);
                        Toast.makeText(CvProfileManagementActivity.this, "Xóa hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Xóa hồ sơ thất bại";
                        Toast.makeText(CvProfileManagementActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CvProfileManagementActivity.this, "Không thể xóa hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CvProfileManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSetAsDefault(int cvProfileId) {
        Call<ApiResponse> call = apiService.setCvProfileAsDefault(cvProfileId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Cập nhật lại danh sách hồ sơ
                        loadCvProfiles();
                        Toast.makeText(CvProfileManagementActivity.this, "Đặt làm hồ sơ mặc định thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Đặt hồ sơ mặc định thất bại";
                        Toast.makeText(CvProfileManagementActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CvProfileManagementActivity.this, "Không thể đặt hồ sơ mặc định", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CvProfileManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách hồ sơ mỗi khi quay lại activity
        loadCvProfiles();
    }
}