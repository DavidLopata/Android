package com.example.campusapp.data;

import com.example.campusapp.model.LocationItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LocationFilterUtils {

    public static List<LocationItem> filter(
            List<LocationItem> source,
            String category
    ) {
        if (category.equals("All")) return new ArrayList<>(source);

        List<LocationItem> result = new ArrayList<>();
        for (LocationItem l : source) {
            if (l.getCategory().equals(category)) {
                result.add(l);
            }
        }
        return result;
    }

    public static void sort(List<LocationItem> list, String mode) {
        if (mode.equals("A-Z")) {
            list.sort(Comparator.comparing(LocationItem::getName));
        } else {
            list.sort(Comparator.comparingInt(LocationItem::getId));
        }
    }
}
