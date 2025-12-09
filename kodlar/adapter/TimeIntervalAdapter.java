package com.mainPackage.randevuapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mainPackage.randevuapp.Model.TimeInterval;
import com.mainPackage.randevuapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeIntervalAdapter extends RecyclerView.Adapter<TimeIntervalAdapter.TimeIntervalViewHolder> {

    private List<TimeInterval> timeIntervals;
    private FirebaseFirestore db;
    private Context context;
    private String userId;
    private Runnable refreshCallback;

    public TimeIntervalAdapter(List<TimeInterval> timeIntervals, FirebaseFirestore db, Context context, String userId, Runnable refreshCallback) {
        this.timeIntervals = timeIntervals;
        this.db = db;
        this.context = context;
        this.userId = userId;
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public TimeIntervalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_interval, parent, false);
        return new TimeIntervalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeIntervalViewHolder holder, int position) {
        TimeInterval timeInterval = timeIntervals.get(position);
        holder.timeIntervalButton.setText(timeInterval.getStartTime());

        if (timeInterval.isAvailable()) {
            holder.timeIntervalButton.setEnabled(true);
            holder.timeIntervalButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Randevu Seç")
                        .setMessage("Bu zamanda mı randevu almak istiyor musunuz?")
                        .setPositiveButton("Evet", (dialog, which) -> {
                            if (timeInterval.getTimeIntervalId() == null) {
                                Toast.makeText(context, "Error: Missing Time Interval ID.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("isAvailable", false);
                            updates.put("bookedByUserId", userId);

                            db.collection("time_intervals").document(timeInterval.getTimeIntervalId())
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Randevu Alındı!", Toast.LENGTH_SHORT).show();
                                        refreshCallback.run();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(context, "Booking failed.", Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Hayır", null)
                        .show();
            });
        } else {
            holder.timeIntervalButton.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return timeIntervals.size();
    }

    static class TimeIntervalViewHolder extends RecyclerView.ViewHolder {
        Button timeIntervalButton;

        public TimeIntervalViewHolder(@NonNull View itemView) {
            super(itemView);
            timeIntervalButton = itemView.findViewById(R.id.timeIntervalButton);
        }
    }
}
