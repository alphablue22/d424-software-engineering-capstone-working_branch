package com.example.myapplication.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    private List<Excursion> mExcursions;
    private final Context context;

    public ExcursionAdapter(Context context) {
        this.context = context;
    }

    // Inner class for ViewHolder
    class ExcursionViewHolder extends RecyclerView.ViewHolder {
        private final TextView excursionNameView;
        private final TextView excursionDateView; // Added date TextView

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            excursionNameView = itemView.findViewById(R.id.excursionName);
            excursionDateView = itemView.findViewById(R.id.excursionDate);

            // Set up click listener for the excursion item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mExcursions != null) {
                    Excursion selectedExcursion = mExcursions.get(position);

                    // Navigate to ExcursionDetails activity with excursion details
                    Intent intent = new Intent(context, ExcursionDetails.class);
                    intent.putExtra("name", selectedExcursion.getExcursionName());
                    intent.putExtra("id", selectedExcursion.getExcursionId());
                    intent.putExtra("vacationID", selectedExcursion.getVacationId());
                    intent.putExtra("excursiondate", selectedExcursion.getExcursionDate());
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout and return a custom ViewHolder
        View itemView = LayoutInflater.from(context).inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            // Bind data to views
            holder.excursionNameView.setText(current.getExcursionName());
            holder.excursionDateView.setText(current.getExcursionDate());
        } else {
            holder.excursionNameView.setText("No Excursion");
            holder.excursionDateView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return (mExcursions != null) ? mExcursions.size() : 0;
    }

    public void setExcursions(List<Excursion> excursions) {
        mExcursions = excursions;
        notifyDataSetChanged();
    }
}