package com.example.fjobs.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fjobs.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Collections;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Cấu hình OSM
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osm_preferences", 0));

        View view = inflater.inflate(R.layout.activity_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Kiểm tra quyền truy cập vị trí
        if (checkLocationPermission()) {
            setupLocationOverlay();
        } else {
            requestLocationPermission();
        }

        // Nhận dữ liệu từ arguments
        Bundle args = getArguments();
        if (args != null) {
            double latitude = args.getDouble("latitude", 0.0);
            double longitude = args.getDouble("longitude", 0.0);
            String companyName = args.getString("company_name");
            String jobTitle = args.getString("job_title");
            int companyId = args.getInt("company_id", -1);
            int jobId = args.getInt("job_id", -1);

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

            // Không xử lý danh sách vị trí vì đã được gán null trong Activity gốc
        } else {
            // Thiết lập vị trí mặc định (Hà Nội)
            GeoPoint startPoint = new GeoPoint(21.0278, 105.8342);
            mapView.getController().setCenter(startPoint);
            mapView.getController().setZoom(12);
        }

        return view;
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (getParentFragment() != null) {
            getParentFragment().requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (getActivity() != null) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationOverlay();
            } else {
                Toast.makeText(requireContext(), "Quyền truy cập vị trí bị từ chối", Toast.LENGTH_SHORT).show();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}