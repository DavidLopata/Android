package com.example.campusapp.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.campusapp.R;
import com.example.campusapp.data.LocationRepository;
import com.example.campusapp.model.LocationItem;
import com.example.campusapp.ui.detail.DetailActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String ARG_LOCATION_ID = "location_id";
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private GoogleMap googleMap;
    private final Map<Integer, Marker> markerMap = new HashMap<>();
    private Integer pendingFocusLocationId = null;

    public static MapFragment newInstance(@Nullable Integer locationId) {
        MapFragment fragment = new MapFragment();
        if (locationId != null) {
            Bundle args = new Bundle();
            args.putInt(ARG_LOCATION_ID, locationId);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        if (getArguments() != null && getArguments().containsKey(ARG_LOCATION_ID)) {
            pendingFocusLocationId = getArguments().getInt(ARG_LOCATION_ID);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setPadding(0, 72, 0, 0);
        applyMapStyle();
        addMarkers();
        enableUserLocation();

        if (pendingFocusLocationId != null) {
            focusOnLocation(pendingFocusLocationId);
            pendingFocusLocationId = null;
        }

        googleMap.setOnMarkerClickListener(marker -> {
            Integer locationId = (Integer) marker.getTag();
            if (locationId != null) {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("location_id", locationId);
                startActivity(intent);
            }
            return true;
        });
        googleMap.setOnInfoWindowClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof Integer) {
                int locationId = (Integer) tag;

                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("location_id", locationId);
                startActivity(intent);
            }
        });


    }

    private void addMarkers() {

        for (LocationItem location : LocationRepository.getLocations()) {
            LatLng latLng = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
            );

            Marker marker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .title(location.getName())
                            .snippet("Tap for details")
            );
            marker.setTag(location.getId());
            markerMap.put(location.getId(), marker);
        }

        if (!LocationRepository.getLocations().isEmpty()) {
            LocationItem first = LocationRepository.getLocations().get(0);
            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(first.getLatitude(), first.getLongitude()),
                            15f
                    )
            );
        }
    }
    private void applyMapStyle() {
        int nightModeFlags =
                getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;

        boolean isDark =
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        int styleRes = isDark
                ? R.raw.map_style_dark
                : R.raw.map_style_light;

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        requireContext(), styleRes
                )
        );
    }
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
        }
    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && googleMap != null) {

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
    }

    public void focusOnLocation(int locationId) {
        if (googleMap == null) {
            pendingFocusLocationId = locationId;
            return;
        }

        Marker marker = markerMap.get(locationId);
        if (marker != null) {
            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17f)
            );
            marker.showInfoWindow();
        }
    }
}
