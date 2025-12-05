package com.example.fjobs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fjobs.activities.CompanyDetailActivity;
import com.example.fjobs.R;
import com.example.fjobs.adapters.CompanyAdapter;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.Company;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class CompaniesFragment extends Fragment implements CompanyAdapter.OnCompanyClickListener {
    private RecyclerView rvCompanies;
    private CompanyAdapter companyAdapter;
    private List<Company> companyList;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_companies, container, false);

        initViews(view);
        setupRecyclerView();
        loadCompanies();

        return view;
    }

    private void initViews(View view) {
        rvCompanies = view.findViewById(R.id.rv_companies);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    private void setupRecyclerView() {
        companyList = new ArrayList<>();
        companyAdapter = new CompanyAdapter(companyList, this);
        rvCompanies.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCompanies.setAdapter(companyAdapter);
    }

    private void loadCompanies() {
        Call<ApiResponse> call = apiService.getAllCompanies();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API sang List<Company>
                        // Dữ liệu từ API thường được deserialize thành LinkedTreeMap
                        // nên cần xử lý chuyển đổi đúng cách
                        if (apiResponse.getData() instanceof List) {
                            List<?> rawList = (List<?>) apiResponse.getData();
                            List<Company> companies = new ArrayList<>();

                            for (Object obj : rawList) {
                                if (obj instanceof java.util.Map) {
                                    // Chuyển đổi Map sang Company
                                    java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                                    Company company = new Company();

                                    // Ánh xạ các trường từ Map sang Company
                                    if (map.get("maCongTy") != null) {
                                        company.setMaCongTy(((Number) map.get("maCongTy")).intValue());
                                    }
                                    if (map.get("tenCongTy") != null) {
                                        company.setTenCongTy((String) map.get("tenCongTy"));
                                    }
                                    if (map.get("tenNguoiDaiDien") != null) {
                                        company.setTenNguoiDaiDien((String) map.get("tenNguoiDaiDien"));
                                    }
                                    if (map.get("maSoThue") != null) {
                                        company.setMaSoThue((String) map.get("maSoThue"));
                                    }
                                    if (map.get("diaChi") != null) {
                                        company.setDiaChi((String) map.get("diaChi"));
                                    }
                                    if (map.get("lienHeCty") != null) {
                                        company.setLienHeCty((String) map.get("lienHeCty"));
                                    }
                                    if (map.get("hinhAnhCty") != null) {
                                        company.setHinhAnhCty((String) map.get("hinhAnhCty"));
                                    }
                                    if (map.get("daXacThuc") != null) {
                                        company.setDaXacThuc((Boolean) map.get("daXacThuc"));
                                    }

                                    companies.add(company);
                                }
                            }

                            companyList.clear();
                            companyList.addAll(companies);
                            companyAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }

    @Override
    public void onCompanyClick(Company company) {
        // Chuyển đến màn hình chi tiết công ty
        Intent intent = new Intent(getContext(), CompanyDetailActivity.class);
        intent.putExtra("company_id", company.getMaCongTy());
        startActivity(intent);
    }
}