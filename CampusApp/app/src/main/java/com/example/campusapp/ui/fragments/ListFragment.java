package com.example.campusapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusapp.R;
import com.example.campusapp.adapter.LocationAdapter;
import com.example.campusapp.data.LocationRepository;
import com.example.campusapp.data.SimpleItemSelectedListener;
import com.example.campusapp.model.LocationItem;
import com.example.campusapp.ui.detail.DetailActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListFragment extends Fragment {

    private List<LocationItem> originalList;
    private final List<LocationItem> displayList = new ArrayList<>();
    private LocationAdapter adapter;

    private Spinner spinnerFilter, spinnerSort;
    private EditText searchView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        spinnerFilter = view.findViewById(R.id.spinnerFilter);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        searchView = view.findViewById(R.id.searchView);

        originalList = LocationRepository.getLocations();
        displayList.addAll(originalList);

        adapter = new LocationAdapter(
                requireContext(),
                displayList,
                location -> {
                    Intent intent = new Intent(requireContext(), DetailActivity.class);
                    intent.putExtra("location_id", location.getId());
                    startActivity(intent);
                },
                true
        );

        recyclerView.setAdapter(adapter);

        setupSpinners();
        setupSearch();

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
}
