package com.example.fjobs.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.fjobs.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Collections;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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

        // Kiểm tra quyền truy cập vị trí
        if (checkLocationPermission()) {
            setupLocationOverlay();
        } else {
            requestLocationPermission();
        }

        // Nhận dữ liệu từ intent
        Intent intent = getIntent();
        if (intent != null) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            String companyName = intent.getStringExtra("company_name");
            String jobTitle = intent.getStringExtra("job_title");
            int companyId = intent.getIntExtra("company_id", -1);
            int jobId = intent.getIntExtra("job_id", -1);

            if (latitude != 0.0 && longitude != 0.0) {
                if (jobTitle != null) {
                    // Đây là công việc
                    addJobMarker(latitude, longitude, jobTitle, "Vị trí công việc");
                    // Di chuyển đến vị trí công việc
                    moveToLocation(latitude, longitude, 15);
                } else if (companyName != null) {
                    // Đây là công ty
                    addCompanyMarker(latitude, longitude, companyName, "Vị trí công ty");
                    // Di chuyển đến vị trí công ty
                    moveToLocation(latitude, longitude, 15);
                } else {
                    // Không có thông tin cụ thể, chỉ di chuyển đến vị trí
                    GeoPoint point = new GeoPoint(latitude, longitude);
                    mapView.getController().setCenter(point);
                    mapView.getController().setZoom(15);
                }
            } else {
                // Thiết lập vị trí mặc định (Hà Nội)
                GeoPoint startPoint = new GeoPoint(21.0278, 105.8342);
                mapView.getController().setCenter(startPoint);
                mapView.getController().setZoom(12);
            }

            // Kiểm tra nếu có danh sách vị trí được truyền vào
            // ArrayList<Double> latitudes = intent.getDoubleArrayListExtra("latitudes");
            // ArrayList<Double> longitudes = intent.getDoubleArrayListExtra("longitudes");
            // ArrayList<String> titles = intent.getStringArrayListExtra("titles");
            // ArrayList<String> snippets = intent.getStringArrayListExtra("snippets");
            // ArrayList<String> types = intent.getStringArrayListExtra("types"); // "company" hoặc "job"

            // Hiện tại không sử dụng tính năng truyền nhiều vị trí qua Intent
            ArrayList<Double> latitudes = null;
            ArrayList<Double> longitudes = null;
            ArrayList<String> titles = null;
            ArrayList<String> snippets = null;
            ArrayList<String> types = null;

            // Không xử lý danh sách vị trí vì đã được gán null
            // if (latitudes != null && longitudes != null && titles != null &&
            //     latitudes.size() == longitudes.size() && latitudes.size() == titles.size()) {
            //
            //     for (int i = 0; i < latitudes.size(); i++) {
            //         double lat = latitudes.get(i);
            //         double lng = longitudes.get(i);
            //         String title = titles.get(i);
            //         String snippet = snippets != null && i < snippets.size() ? snippets.get(i) : "";
            //         String type = types != null && i < types.size() ? types.get(i) : "company";
            //
            //         if ("job".equals(type)) {
            //             addJobMarker(lat, lng, title, snippet);
            //         } else {
            //             addCompanyMarker(lat, lng, title, snippet);
            //         }
            //     }
            //
            //     // Tự động zoom để hiển thị tất cả các marker
            //     if (!latitudes.isEmpty()) {
            //         // Tính toán bounds để zoom vừa đủ
            //         double minLat = Collections.min(latitudes);
            //         double maxLat = Collections.max(latitudes);
            //         double minLng = Collections.min(longitudes);
            //         double maxLng = Collections.max(longitudes);
            //
            //         // Tính trung bình để đặt tâm
            //         double centerLat = (minLat + maxLat) / 2;
            //         double centerLng = (minLng + maxLng) / 2;
            //
            //         GeoPoint centerPoint = new GeoPoint(centerLat, centerLng);
            //         mapView.getController().setCenter(centerPoint);
            //
            //         // Tính toán mức zoom phù hợp
            //         double latDiff = Math.abs(maxLat - minLat);
            //         double lngDiff = Math.abs(maxLng - minLng);
            //         double maxDiff = Math.max(latDiff, lngDiff);
            //
            //         int zoomLevel = (int) (15 - Math.log(maxDiff) / Math.log(2));
            //         if (zoomLevel < 5) zoomLevel = 5;
            //         if (zoomLevel > 18) zoomLevel = 18;
            //
            //         mapView.getController().setZoom(zoomLevel);
            //     }
            // }
        } else {
            // Thiết lập vị trí mặc định (Hà Nội)
            GeoPoint startPoint = new GeoPoint(21.0278, 105.8342);
            mapView.getController().setCenter(startPoint);
            mapView.getController().setZoom(12);
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void setupLocationOverlay() {
        locationOverlay = new MyLocationNewOverlay(mapView);
        mapView.getOverlays().add(locationOverlay);
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
    }

    // Thêm marker cho công ty
    public void addCompanyMarker(double latitude, double longitude, String title, String snippet) {
        GeoPoint companyPoint = new GeoPoint(latitude, longitude);
        Marker companyMarker = new Marker(mapView);
        companyMarker.setPosition(companyPoint);
        companyMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        companyMarker.setTitle(title);
        companyMarker.setSnippet(snippet);

        mapView.getOverlays().add(companyMarker);
        mapView.invalidate();
    }

    // Thêm marker cho công việc
    public void addJobMarker(double latitude, double longitude, String title, String snippet) {
        GeoPoint jobPoint = new GeoPoint(latitude, longitude);
        Marker jobMarker = new Marker(mapView);
        jobMarker.setPosition(jobPoint);
        jobMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        jobMarker.setTitle(title);
        jobMarker.setSnippet(snippet);

        mapView.getOverlays().add(jobMarker);
        mapView.invalidate();
    }

    // Di chuyển đến vị trí cụ thể
    public void moveToLocation(double latitude, double longitude, int zoomLevel) {
        GeoPoint point = new GeoPoint(latitude, longitude);
        mapView.getController().animateTo(point);
        mapView.getController().setZoom(zoomLevel);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationOverlay();
            } else {
                Toast.makeText(this, "Quyền truy cập vị trí bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
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