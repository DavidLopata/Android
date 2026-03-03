package com.example.campusapp.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusapp.R;
import com.example.campusapp.adapter.LocationAdapter;
import com.example.campusapp.data.DatabaseHelper;
import com.example.campusapp.data.LocationRepository;
import com.example.campusapp.data.SimpleItemSelectedListener;
import com.example.campusapp.model.LocationItem;
import com.example.campusapp.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LocationAdapter adapter;

    private final List<LocationItem> originalList = new ArrayList<>();
    private final List<LocationItem> displayList = new ArrayList<>();

    private DatabaseHelper db;
    private String currentUser;

    private Spinner spinnerFilter, spinnerSort;
    private EditText searchView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        searchView = view.findViewById(R.id.searchView);

        adapter = new LocationAdapter(
                requireContext(),
                displayList,
                location -> {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("location_id", location.getId());
                    startActivity(intent);
                },
                false
        );

        recyclerView.setAdapter(adapter);

        db = new DatabaseHelper(requireContext());
        SharedPreferences prefs =
                requireContext().getSharedPreferences("campus_prefs", Context.MODE_PRIVATE);
        currentUser = prefs.getString("logged_user", null);

        if (currentUser == null) {
            Toast.makeText(getContext(),
                    "Please log in again",
                    Toast.LENGTH_SHORT).show();
            return view;
        }


        setupSpinners();
        setupSearch();
        loadFavorites();


        return view;
    }

    private void setupSearch() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSort();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadFavorites() {
        originalList.clear();
        displayList.clear();

        List<Integer> ids = db.getFavoritesForUser(currentUser);
        for (int id : ids) {
            LocationItem location = LocationRepository.getById(id);
            if (location != null) {
                originalList.add(location);
            }
        }

        displayList.addAll(originalList);
        adapter.notifyDataSetChanged();
    }

    private void setupSpinners() {
        List<String> categories = new ArrayList<>();
        categories.add("All");

        for (LocationItem l : originalList) {
            if (!categories.contains(l.getCategory())) {
                categories.add(l.getCategory());
            }
        }

        spinnerFilter.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
        ));

        spinnerSort.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Default", "A-Z"}
        ));

        spinnerFilter.setOnItemSelectedListener(
                new SimpleItemSelectedListener(this::applyFilterAndSort));
        spinnerSort.setOnItemSelectedListener(
                new SimpleItemSelectedListener(this::applyFilterAndSort));
    }

    private void applyFilterAndSort() {
        String selectedCategory = spinnerFilter.getSelectedItem().toString();
        String sortMode = spinnerSort.getSelectedItem().toString();
        String query = searchView.getText().toString().toLowerCase();

        displayList.clear();

        for (LocationItem l : originalList) {
            boolean matchesCategory =
                    selectedCategory.equals("All") ||
                            l.getCategory().equals(selectedCategory);

            boolean matchesSearch =
                    l.getName().toLowerCase().contains(query);

            if (matchesCategory && matchesSearch) {
                displayList.add(l);
            }
        }

        if (sortMode.equals("A-Z")) {
            displayList.sort(Comparator.comparing(LocationItem::getName));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadFavorites();
        }
    }
}
