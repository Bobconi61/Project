package com.mainPackage.randevuapp.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mainPackage.randevuapp.Model.Appointment;
import com.mainPackage.randevuapp.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments;
    private FirebaseFirestore db;
    private Runnable refreshCallback;

    public AppointmentAdapter(List<Appointment> appointments, FirebaseFirestore db, Runnable refreshCallback) {
        this.appointments = appointments;
        this.db = db;
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.doctorNameTextView.setText(appointment.getDoctorName());
        holder.departmentTextView.setText(appointment.getDepartment());
        holder.hospitalNameTextView.setText(appointment.getHospitalName());
        holder.dateTextView.setText(appointment.getDate());
        holder.timeTextView.setText(appointment.getTime());

        holder.cancelAppointmentButton.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Randevuyu İptal Et")
                    .setMessage("Bu randevuyu iptal etmek istediğinizden emin misiniz?")
                    .setPositiveButton("Evet", (dialog, which) -> {
                        db.collection("time_intervals").document(appointment.getTimeIntervalId())
                                .update("isAvailable", true, "bookedByUserId", null)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(holder.itemView.getContext(), "Randevu iptal edildi.", Toast.LENGTH_SHORT).show();
                                    refreshCallback.run();
                                })
                                .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "İptal etme başarısız.", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Hayır", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView doctorNameTextView, departmentTextView, hospitalNameTextView, dateTextView, timeTextView;
        Button cancelAppointmentButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
            departmentTextView = itemView.findViewById(R.id.departmentTextView);
            hospitalNameTextView = itemView.findViewById(R.id.hospitalNameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            cancelAppointmentButton = itemView.findViewById(R.id.cancelAppointmentButton);
        }
    }
}
