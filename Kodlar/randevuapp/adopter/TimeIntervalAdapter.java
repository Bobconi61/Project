package com.mainPackage.randevuapp.adopter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mainPackage.randevuapp.Database.DatabaseHelper;
import com.mainPackage.randevuapp.Model.TimeInterval;
import com.mainPackage.randevuapp.R;

import java.util.List;

public class TimeIntervalAdapter extends RecyclerView.Adapter<TimeIntervalAdapter.TimeIntervalViewHolder> {

    private List<TimeInterval> timeIntervals;
    private DatabaseHelper dbHelper;
    private Context context;
    private String userIdNumber;
    private Runnable refreshCallback;

    public TimeIntervalAdapter(List<TimeInterval> timeIntervals, DatabaseHelper dbHelper, Context context, String userIdNumber, Runnable refreshCallback) {
        this.timeIntervals = timeIntervals;
        this.dbHelper = dbHelper;
        this.context = context;
        this.userIdNumber = userIdNumber;
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
                            int userId = dbHelper.getUserId(userIdNumber);
                            if (userId != -1) {
                                dbHelper.bookTimeInterval(timeInterval.getTimeIntervalId(), userId);
                                Toast.makeText(context, "Randevu Alındı!", Toast.LENGTH_SHORT).show();
                                refreshCallback.run();
                            } else {
                                Toast.makeText(context, "Error: User not found.", Toast.LENGTH_SHORT).show();
                            }
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
