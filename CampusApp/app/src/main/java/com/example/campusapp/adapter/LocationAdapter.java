package com.example.campusapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusapp.R;
import com.example.campusapp.data.DatabaseHelper;
import com.example.campusapp.model.LocationItem;

import java.util.List;

public class LocationAdapter
        extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(LocationItem location);
    }

    private final Context context;
    private final List<LocationItem> locations;
    private final OnItemClickListener listener;
    private final DatabaseHelper db;
    private final String currentUser;
    private final boolean allowFavoriteToggle;

    public LocationAdapter(
            Context context,
            List<LocationItem> locations,
            OnItemClickListener listener,
            boolean allowFavoriteToggle
    ) {
        this.context = context;
        this.locations = locations;
        this.listener = listener;
        this.allowFavoriteToggle = allowFavoriteToggle;

        db = new DatabaseHelper(context);
        SharedPreferences prefs =
                context.getSharedPreferences("campus_prefs", Context.MODE_PRIVATE);
        currentUser = prefs.getString("logged_user", null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        LocationItem location = locations.get(position);

        holder.tvName.setText(location.getName());
        holder.tvCategory.setText(location.getCategory());

        boolean isFavorite =
                currentUser != null && db.isFavorite(currentUser, location.getId());

        holder.imgFavorite.setImageResource(
                isFavorite
                        ? R.drawable.ic_star_filled
                        : R.drawable.ic_star_outline
        );

        holder.itemView.setOnClickListener(v ->
                listener.onItemClick(location)
        );

        if (allowFavoriteToggle && currentUser != null) {
            holder.imgFavorite.setOnClickListener(v -> {
                if (db.isFavorite(currentUser, location.getId())) {
                    db.removeFavorite(currentUser, location.getId());
                    holder.imgFavorite.setImageResource(R.drawable.ic_star_outline);
                } else {
                    db.addToFavorites(currentUser, location.getId());
                    holder.imgFavorite.setImageResource(R.drawable.ic_star_filled);
                }
            });
        } else {
            holder.imgFavorite.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory;
        ImageView imgFavorite;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);
        }
    }
}

