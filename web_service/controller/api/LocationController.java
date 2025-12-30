package com.example.demo.controller.api;

import com.example.demo.service.CompanyService;
import com.example.demo.utils.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RestController
@RequestMapping("/api/v1/location")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LocationController {

    @Autowired
    private CompanyService companyService;

    // API để lấy tọa độ từ địa chỉ sử dụng Google Maps Geocoding API
    @GetMapping("/geocode")
    public ResponseEntity<?> getCoordinates(@RequestParam String address) {
        try {
            // Mã hóa địa chỉ để đưa vào URL
            String encodedAddress = URLEncoder.encode(address, "UTF-8");
            
            // Sử dụng Google Maps Geocoding API (bạn cần có API key)
            // Trong ví dụ này, tôi sẽ mô phỏng việc lấy tọa độ
            // Trong thực tế, bạn cần thay thế bằng API key thực tế của bạn
            String googleApiKey = System.getenv("GOOGLE_MAPS_API_KEY"); // Lấy từ biến môi trường
            
            if (googleApiKey == null || googleApiKey.isEmpty()) {
                // Trong trường hợp không có API key, trả về tọa độ mặc định cho Việt Nam
                return ApiResponseUtil.success("Lấy tọa độ thành công (mock data)", 
                    java.util.Map.of("lat", 10.8231, "lng", 106.6297, "formatted_address", address));
            }
            
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodedAddress + "&key=" + googleApiKey + "&language=vi";
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Trong thực tế, bạn sẽ phân tích JSON phản hồi từ Google
            // Ở đây tôi sẽ trả về một phản hồi mẫu
            return ApiResponseUtil.success("Lấy tọa độ thành công", response.toString());
            
        } catch (Exception e) {
            return ApiResponseUtil.error("Lỗi khi lấy tọa độ: " + e.getMessage());
        }
    }

    // API để lấy địa chỉ từ tọa độ (Reverse Geocoding)
    @GetMapping("/reverse-geocode")
    public ResponseEntity<?> getAddress(@RequestParam double lat, @RequestParam double lng) {
        try {
            // Trong ví dụ này, tôi sẽ mô phỏng việc lấy địa chỉ từ tọa độ
            // Trong thực tế, bạn cần sử dụng Google Maps Reverse Geocoding API
            String googleApiKey = System.getenv("GOOGLE_MAPS_API_KEY"); // Lấy từ biến môi trường
            
            if (googleApiKey == null || googleApiKey.isEmpty()) {
                // Trả về địa chỉ mặc định
                return ApiResponseUtil.success("Lấy địa chỉ thành công (mock data)", 
                    java.util.Map.of("address", "Địa chỉ tại tọa độ (" + lat + ", " + lng + ")", "lat", lat, "lng", lng));
            }
            
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + googleApiKey + "&language=vi";
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            return ApiResponseUtil.success("Lấy địa chỉ thành công", response.toString());
            
        } catch (Exception e) {
            return ApiResponseUtil.error("Lỗi khi lấy địa chỉ: " + e.getMessage());
        }
    }

    // API để cập nhật tọa độ cho công ty
    @PutMapping("/company/{companyId}/coordinates")
    public ResponseEntity<?> updateCompanyCoordinates(
            @PathVariable Integer companyId,
            @RequestParam Double kinhDo,
            @RequestParam Double viDo) {
        try {
            java.math.BigDecimal kinhDoDecimal = kinhDo != null ? java.math.BigDecimal.valueOf(kinhDo) : null;
            java.math.BigDecimal viDoDecimal = viDo != null ? java.math.BigDecimal.valueOf(viDo) : null;
            
            // Gọi service để cập nhật tọa độ cho công ty
            com.example.demo.entity.Company updatedCompany = companyService.updateCompanyCoordinates(companyId, kinhDoDecimal, viDoDecimal);
            
            return ApiResponseUtil.success("Cập nhật tọa độ công ty thành công", updatedCompany);
        } catch (Exception e) {
            return ApiResponseUtil.error("Lỗi khi cập nhật tọa độ công ty: " + e.getMessage());
        }
    }
}