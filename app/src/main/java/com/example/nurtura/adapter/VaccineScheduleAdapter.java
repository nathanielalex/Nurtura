package com.example.nurtura.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.model.Vaccine;

import java.util.List;

public class VaccineScheduleAdapter extends RecyclerView.Adapter<VaccineScheduleAdapter.VaccineViewHolder>{

    private Context context;
    private List<Immunization> immunizationList;

    public VaccineScheduleAdapter(Context context, List<Immunization> immunizationList) {
        this.context = context;
        this.immunizationList = immunizationList;
    }

    @NonNull
    @Override
    public VaccineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vaccine_schedule_item, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineViewHolder holder, int position) {
        Immunization item = immunizationList.get(position);
        holder.txtVaccineName.setText(item.getName());
        holder.txtDueDate.setText(item.getDate());

        String status = item.getDueStatus();

        if ("OVERDUE".equals(status)) {
            holder.chipStatus.setText("Overdue");
            holder.chipStatus.setTextColor(Color.parseColor("#d32f2f"));
            holder.chipStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffebee")));
        } else if ("UPCOMING".equals(status)) {
            holder.chipStatus.setText("Upcoming");
            holder.chipStatus.setTextColor(Color.parseColor("#1976d2"));
            holder.chipStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e3f2fd")));
        } else if ("DONE".equals(status)) {
            holder.chipStatus.setText("Done");
            holder.chipStatus.setTextColor(Color.parseColor("#388e3c"));
            holder.chipStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e8f5e9")));
        } else {
            // Optional: Handle the null/unknown case
            holder.chipStatus.setText("Unknown");
            holder.chipStatus.setTextColor(Color.GRAY);
        }


    }

    @Override
    public int getItemCount() {
        return immunizationList.size();
    }

    public static class VaccineViewHolder extends RecyclerView.ViewHolder {
        TextView txtVaccineName, txtDueDate, chipStatus;

        public VaccineViewHolder(@NonNull View itemView) {
            super(itemView);
            txtVaccineName = itemView.findViewById(R.id.txtVaccineName);
            txtDueDate = itemView.findViewById(R.id.txtDueDate);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
