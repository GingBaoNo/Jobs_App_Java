package com.example.fjobs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.example.fjobs.models.JobDetail;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultFragment extends Fragment {

    private EditText editTextSearchResults;
    private ImageButton buttonSearchResults;
    private RecyclerView rvSearchResults;
    private TextView tvSearchResultsTitle;
    
    private HorizontalJobAdapter searchResultsAdapter;
    private List<JobDetail> searchResultsList;
    private ApiService apiService;
    
    private String currentSearchKeyword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        initViews(view);
        setupRecyclerView();
        initApiService();
        
        // Lấy từ khóa tìm kiếm từ bundle nếu có
        Bundle bundle = getArguments();
        if (bundle != null) {
            String keyword = bundle.getString("keyword");
            if (keyword != null && !keyword.isEmpty()) {
                currentSearchKeyword = keyword;
                editTextSearchResults.setText(keyword);
                performSearch(keyword);
            }
        }

        setupSearchFunctionality();

        return view;
    }

    private void initViews(View view) {
        editTextSearchResults = view.findViewById(R.id.edit_text_search_results);
        buttonSearchResults = view.findViewById(R.id.button_search_results);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        tvSearchResultsTitle = view.findViewById(R.id.tv_search_results_title);
    }

    private void setupRecyclerView() {
        searchResultsList = new ArrayList<>();
        searchResultsAdapter = new HorizontalJobAdapter(searchResultsList, getContext());
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearchResults.setAdapter(searchResultsAdapter);
    }

    private void initApiService() {
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    private void setupSearchFunctionality() {
        buttonSearchResults.setOnClickListener(v -> {
            String keyword = editTextSearchResults.getText().toString().trim();
            if (!keyword.isEmpty()) {
                performSearch(keyword);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });

        // Thêm sự kiện Enter trên EditText để tìm kiếm
        editTextSearchResults.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String keyword = editTextSearchResults.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    performSearch(keyword);
                }
                return true;
            }
            return false;
        });
    }

    private void performSearch(String keyword) {
        currentSearchKeyword = keyword;
        tvSearchResultsTitle.setText("Kết quả tìm kiếm cho: \"" + keyword + "\"");
        System.out.println("Gọi API tìm kiếm với từ khóa: " + keyword);

        // Gọi API không áp dụng điều kiện trạng thái
        Call<ApiResponse> call = apiService.searchJobsNoStatus(keyword);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                System.out.println("Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    System.out.println("API Response isSuccess: " + apiResponse.isSuccess());
                    System.out.println("API Response data: " + apiResponse.getData());

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API
                        Object data = apiResponse.getData();
                        List<JobDetail> searchResults = new ArrayList<>();

                        if (data instanceof List) {
                            List<?> rawData = (List<?>) data;
                            System.out.println("Số lượng kết quả nhận được: " + rawData.size());

                            for (Object item : rawData) {
                                System.out.println("Xử lý item: " + item);
                                if (item instanceof java.util.Map) {
                                    JobDetail job = convertMapToJobDetail((java.util.Map<String, Object>) item);
                                    if (job != null) {
                                        System.out.println("Tìm thấy công việc: " + job.getTieuDe());
                                        searchResults.add(job);
                                    }
                                } else if (item instanceof JobDetail) {
                                    System.out.println("Tìm thấy công việc (JobDetail): " + ((JobDetail) item).getTieuDe());
                                    searchResults.add((JobDetail) item);
                                }
                            }
                        }

                        // Cập nhật danh sách kết quả tìm kiếm
                        searchResultsList.clear();
                        searchResultsList.addAll(searchResults);
                        searchResultsAdapter.notifyDataSetChanged();

                        // Hiển thị thông báo nếu không có kết quả
                        if (searchResults.isEmpty()) {
                            tvSearchResultsTitle.setText("Không tìm thấy kết quả cho: \"" + keyword + "\"");
                        } else {
                            tvSearchResultsTitle.setText("Kết quả tìm kiếm cho: \"" + keyword + "\" (" + searchResults.size() + " kết quả)");
                        }
                    } else {
                        String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Tìm kiếm thất bại";
                        System.out.println("Lỗi API: " + message);
                        Toast.makeText(getContext(), "Lỗi: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    System.out.println("Tìm kiếm thất bại, response code: " + response.code());
                    Toast.makeText(getContext(), "Tìm kiếm thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                System.out.println("Lỗi kết nối: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    // Phương thức để thực hiện tìm kiếm từ bên ngoài
    public void searchForKeyword(String keyword) {
        editTextSearchResults.setText(keyword);
        performSearch(keyword);
    }
}