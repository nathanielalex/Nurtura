package com.example.nurtura.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.model.Child;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildViewHolder>{
    private List<Child> childs;

    public ChildAdapter(List<Child> childs) {
        this.childs = childs;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_item_layout, parent, false);
        return new ChildAdapter.ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        holder.txtChildName.setText(childs.get(position).getName());
        Date dob = childs.get(position).getDateOfBirth();
        if (dob != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = sdf.format(dob);
            holder.txtChildBirth.setText(formattedDate);
        } else {
            holder.txtChildBirth.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return childs.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        public TextView txtChildName;
        public TextView txtChildBirth;
        public ChildViewHolder(View itemView) {
            super(itemView);
            txtChildName = itemView.findViewById(R.id.txtChildName);
            txtChildBirth = itemView.findViewById(R.id.txtChildBirth);
        }

    }
}
