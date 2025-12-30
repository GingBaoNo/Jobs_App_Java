package com.example.fjobs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fjobs.R;
import com.example.fjobs.api.ApiClient;
import com.example.fjobs.api.ApiService;
import com.example.fjobs.models.ApiResponse;
import com.example.fjobs.models.Company;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompaniesMapActivity extends AppCompatActivity {

    private MapView mapView;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Cấu hình OSM
        Configuration.getInstance().load(this, getSharedPreferences("osm_preferences", MODE_PRIVATE));
        
        setContentView(R.layout.activity_map);
        
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        
        loadCompaniesAndDisplayOnMap();
    }

    private void loadCompaniesAndDisplayOnMap() {
        Call<ApiResponse> call = apiService.getAllCompanies();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Chuyển đổi dữ liệu từ API sang danh sách Company
                        List<Company> companies = convertDataToCompanies(apiResponse.getData());
                        displayCompaniesOnMap(companies);
                    } else {
                        Toast.makeText(CompaniesMapActivity.this, "Không thể tải danh sách công ty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CompaniesMapActivity.this, "Không thể tải danh sách công ty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CompaniesMapActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Company> convertDataToCompanies(Object data) {
        List<Company> companies = new ArrayList<>();
        
        try {
            if (data instanceof List) {
                List<?> rawList = (List<?>) data;
                
                for (Object obj : rawList) {
                    if (obj instanceof java.util.Map) {
                        java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                        Company company = new Company();
                        
                        // Ánh xạ các trường từ Map sang Company
                        if (map.containsKey("maCongTy")) {
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
                        
                        if (map.containsKey("trangThai") && map.get("trangThai") != null) {
                            company.setTrangThai(map.get("trangThai").toString());
                        }
                        
                        // Ánh xạ tọa độ
                        if (map.containsKey("kinhDo") && map.get("kinhDo") != null) {
                            Object kinhDoObj = map.get("kinhDo");
                            if (kinhDoObj instanceof Double) {
                                company.setKinhDo(BigDecimal.valueOf((Double) kinhDoObj));
                            } else if (kinhDoObj instanceof Integer) {
                                company.setKinhDo(BigDecimal.valueOf((Integer) kinhDoObj));
                            } else if (kinhDoObj instanceof String) {
                                company.setKinhDo(new BigDecimal((String) kinhDoObj));
                            } else {
                                company.setKinhDo(BigDecimal.valueOf(Double.parseDouble(kinhDoObj.toString())));
                            }
                        }

                        if (map.containsKey("viDo") && map.get("viDo") != null) {
                            Object viDoObj = map.get("viDo");
                            if (viDoObj instanceof Double) {
                                company.setViDo(BigDecimal.valueOf((Double) viDoObj));
                            } else if (viDoObj instanceof Integer) {
                                company.setViDo(BigDecimal.valueOf((Integer) viDoObj));
                            } else if (viDoObj instanceof String) {
                                company.setViDo(new BigDecimal((String) viDoObj));
                            } else {
                                company.setViDo(BigDecimal.valueOf(Double.parseDouble(viDoObj.toString())));
                            }
                        }
                        
                        companies.add(company);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return companies;
    }

    private void displayCompaniesOnMap(List<Company> companies) {
        List<Double> latitudes = new ArrayList<>();
        List<Double> longitudes = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> snippets = new ArrayList<>();
        List<String> types = new ArrayList<>();
        
        for (Company company : companies) {
            // Chỉ thêm công ty có tọa độ vào bản đồ
            if (company.getKinhDo() != null && company.getViDo() != null) {
                double latitude = company.getViDo().doubleValue();
                double longitude = company.getKinhDo().doubleValue();
                
                latitudes.add(latitude);
                longitudes.add(longitude);
                titles.add(company.getTenCongTy());
                snippets.add(company.getDiaChi() != null ? company.getDiaChi() : "Vị trí công ty");
                types.add("company");
                
                // Thêm marker cho công ty
                addCompanyMarker(latitude, longitude, company.getTenCongTy(), 
                    company.getDiaChi() != null ? company.getDiaChi() : "Vị trí công ty");
            }
        }
        
        // Nếu có nhiều công ty, tự động zoom để hiển thị tất cả
        if (latitudes.size() > 1) {
            autoZoomToBoundaries(latitudes, longitudes);
        } else if (latitudes.size() == 1) {
            // Nếu chỉ có một công ty, zoom vào vị trí đó
            GeoPoint point = new GeoPoint(latitudes.get(0), longitudes.get(0));
            mapView.getController().animateTo(point);
            mapView.getController().setZoom(15);
        } else {
            // Nếu không có công ty nào có tọa độ, hiển thị vị trí mặc định
            GeoPoint startPoint = new GeoPoint(21.0278, 105.8342);
            mapView.getController().setCenter(startPoint);
            mapView.getController().setZoom(12);
            Toast.makeText(this, "Không có công ty nào có thông tin vị trí", Toast.LENGTH_SHORT).show();
        }
    }

    private void addCompanyMarker(double latitude, double longitude, String title, String snippet) {
        GeoPoint companyPoint = new GeoPoint(latitude, longitude);
        Marker companyMarker = new Marker(mapView);
        companyMarker.setPosition(companyPoint);
        companyMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        companyMarker.setTitle(title);
        companyMarker.setSnippet(snippet);
        
        // Thêm hành động khi click vào marker
        companyMarker.setOnMarkerClickListener((marker, mapView) -> {
            // Có thể mở chi tiết công ty khi click vào marker
            Intent intent = new Intent(CompaniesMapActivity.this, CompanyDetailActivity.class);
            // Tìm công ty tương ứng để truyền ID
            // Trong phiên bản đơn giản, chỉ hiển thị thông báo
            Toast.makeText(CompaniesMapActivity.this, "Công ty: " + title, Toast.LENGTH_SHORT).show();
            return false; // Trả về false để marker vẫn hiển thị thông tin
        });
        
        mapView.getOverlays().add(companyMarker);
        mapView.invalidate();
    }

    private void autoZoomToBoundaries(List<Double> latitudes, List<Double> longitudes) {
        if (latitudes.isEmpty() || longitudes.isEmpty()) return;

        // Tính toán bounds để zoom vừa đủ - sử dụng phương thức thủ công thay vì Collections.min/max
        double minLat = latitudes.get(0);
        double maxLat = latitudes.get(0);
        double minLng = longitudes.get(0);
        double maxLng = longitudes.get(0);

        for (double lat : latitudes) {
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
        }

        for (double lng : longitudes) {
            if (lng < minLng) minLng = lng;
            if (lng > maxLng) maxLng = lng;
        }

        // Tính trung bình để đặt tâm
        double centerLat = (minLat + maxLat) / 2;
        double centerLng = (minLng + maxLng) / 2;

        GeoPoint centerPoint = new GeoPoint(centerLat, centerLng);
        mapView.getController().setCenter(centerPoint);

        // Tính toán mức zoom phù hợp
        double latDiff = Math.abs(maxLat - minLat);
        double lngDiff = Math.abs(maxLng - minLng);
        double maxDiff = Math.max(latDiff, lngDiff);

        int zoomLevel = (int) (15 - Math.log(maxDiff) / Math.log(2));
        if (zoomLevel < 5) zoomLevel = 5;
        if (zoomLevel > 18) zoomLevel = 18;

        mapView.getController().setZoom(zoomLevel);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}