package com.example.campusapp.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.campusapp.R;
import com.example.campusapp.ui.fragments.FavoritesFragment;
import com.example.campusapp.ui.fragments.ListFragment;
import com.example.campusapp.ui.fragments.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    Integer pendingLocationId = null;

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        if (getIntent().getBooleanExtra("open_map", false)) {

            pendingLocationId = getIntent().getIntExtra("location_id", -1);

            MapFragment mapFragment = MapFragment.newInstance(pendingLocationId);
            loadFragment(mapFragment);

            bottomNav.setSelectedItemId(R.id.nav_map);

        } else {
            loadFragment(new ListFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_list) {
                selectedFragment = new ListFragment();
            } else if (item.getItemId() == R.id.nav_map) {
                selectedFragment = MapFragment.newInstance(pendingLocationId);
            } else if (item.getItemId() == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }
}
