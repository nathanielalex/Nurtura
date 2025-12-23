package com.example.nurtura.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.model.Vaccine;

import java.util.List;

public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.VaccineViewHolder>{
    private List<Vaccine> vaccineList;
    private Context context;

    public VaccineAdapter(List<Vaccine> vaccineList, Context context) {
        this.vaccineList = vaccineList;
        this.context = context;
    }

    @NonNull
    @Override
    public VaccineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vaccine_item_row, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineViewHolder holder, int position) {
        Vaccine item = vaccineList.get(position);
        holder.txtDescription.setText(item.getDescription());
        holder.txtRecAge.setText(
                item.getRecommendedAgeInMonthsInt() + " months"
        );
        holder.txtSideEffects.setText(item.getSideEffectsInfo());
        holder.txtVaccineName.setText(item.getName());

        if (item.isMandatory()) {
            holder.chipMandatory.setVisibility(View.VISIBLE);
        } else {
            holder.chipMandatory.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return vaccineList.size();
    }

    public void updateList(List<Vaccine> newList) {
        vaccineList.clear();
        vaccineList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class VaccineViewHolder extends RecyclerView.ViewHolder {
        TextView txtVaccineName, chipMandatory, txtRecAge, txtDescription, txtSideEffects;

        public VaccineViewHolder(@NonNull View itemView) {
            super(itemView);
            txtVaccineName = itemView.findViewById(R.id.txtVaccineName);
            chipMandatory = itemView.findViewById(R.id.chipMandatory);
            txtRecAge = itemView.findViewById(R.id.txtRecAge);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtSideEffects = itemView.findViewById(R.id.txtSideEffects);
        }
    }
}
