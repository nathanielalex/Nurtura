package com.example.nurtura.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.StaffChildDetailActivity;
import com.example.nurtura.model.Child;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaffChildAdapter extends RecyclerView.Adapter<StaffChildAdapter.StaffChildViewHolder>{
    private List<Child> childs;

    public StaffChildAdapter(List<Child> childs) {
        this.childs = childs;
    }

    @NonNull
    @Override
    public StaffChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_item_layout, parent, false);
        return new StaffChildAdapter.StaffChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffChildViewHolder holder, int position) {
        //nanti ada bedanya di intent
        holder.txtChildName.setText(childs.get(position).getName());
        Date dob = childs.get(position).getDateOfBirth();
        if (dob != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(dob);
            holder.txtChildBirth.setText(formattedDate);
        } else {
            holder.txtChildBirth.setText("N/A");
        }
        holder.btnEditInformationStaff.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), StaffChildDetailActivity.class);
            v.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return childs.size();
    }

    public static class StaffChildViewHolder extends RecyclerView.ViewHolder {
        public TextView txtChildName;
        public TextView txtChildBirth;
        public Button btnEditInformationStaff;

        public StaffChildViewHolder(View itemView) {
            super(itemView);
            txtChildName = itemView.findViewById(R.id.txtChildName);
            txtChildBirth = itemView.findViewById(R.id.txtChildBirth);
            btnEditInformationStaff = itemView.findViewById(R.id.btnEditChild);
        }

    }
}
