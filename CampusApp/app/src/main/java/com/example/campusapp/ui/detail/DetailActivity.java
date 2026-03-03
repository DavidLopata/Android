package com.example.campusapp.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusapp.R;
import com.example.campusapp.data.LocationRepository;
import com.example.campusapp.model.LocationItem;
import com.example.campusapp.ui.main.MainActivity;

public class DetailActivity extends AppCompatActivity {

    TextView tvName, tvCategory, tvAddress, tvDescription, tvContact;
    Button btnShowOnMap;
    ;
    LocationItem location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvContact = findViewById(R.id.tvDetailContact);
        btnShowOnMap = findViewById(R.id.btnShowOnMap);


        int locationId = getIntent().getIntExtra("location_id", -1);
        location = LocationRepository.getById(locationId);

        if (location == null) {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateUI();

        btnShowOnMap.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, MainActivity.class);
            intent.putExtra("open_map", true);
            intent.putExtra("location_id", location.getId());
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });



    }

    private void populateUI() {
        tvName.setText(location.getName());
        tvCategory.setText(location.getCategory());
        tvDescription.setText(location.getDescription());
        tvAddress.setText(location.getAddress());
        tvContact.setText(location.getContact());
    }
}
