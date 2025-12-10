package com.example.nurtura.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.ScheduleActivity;
import com.example.nurtura.model.Immunization;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ImmunizationAdapter extends RecyclerView.Adapter<ImmunizationAdapter.ViewHolder> {

    private List<Immunization> immunizationList;
    private Context context;

    public ImmunizationAdapter(Context context, List<Immunization> immunizationList) {
        this.context = context;
        this.immunizationList = immunizationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_immunization, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Immunization item = immunizationList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvSchedule.setText(item.getSchedule());
        holder.tvDueStatus.setText(item.getDueStatus());
        holder.tvDate.setText(item.getDate());

        holder.btnViewSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScheduleActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return immunizationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSchedule, tvDueStatus, tvDate;
        MaterialButton btnViewSchedule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvImmName);
            tvSchedule = itemView.findViewById(R.id.tvImmSchedule);
            tvDueStatus = itemView.findViewById(R.id.tvImmDue);
            tvDate = itemView.findViewById(R.id.tvImmDate);
            btnViewSchedule = itemView.findViewById(R.id.btnViewSchedule);
        }
    }
}