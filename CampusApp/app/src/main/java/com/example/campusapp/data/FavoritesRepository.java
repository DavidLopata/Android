package com.example.campusapp.data;

import java.util.HashSet;
import java.util.Set;

public class FavoritesRepository {

    private static final Set<Integer> favoriteIds = new HashSet<>();

    public static void add(int locationId) {
        favoriteIds.add(locationId);
    }

    public static void remove(int locationId) {
        favoriteIds.remove(locationId);
    }

    public static boolean isFavorite(int locationId) {
        return favoriteIds.contains(locationId);
    }

    public static Set<Integer> getAll() {
        return new HashSet<>(favoriteIds);
    }
}
