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
import com.example.myapplication.entities.Vacation;

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {
    private List<Vacation> mVacations;
    private final Context context;
    private final OnVacationClickListener onVacationClickListener;

    // Define the interface for the click listener
    public interface OnVacationClickListener {
        void onVacationClick(Vacation vacation);
    }

    // Constructor to accept Context and OnVacationClickListener
    public VacationAdapter(Context context, OnVacationClickListener listener) {
        this.context = context;
        this.onVacationClickListener = listener;
    }

    class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.textView2);

            // Set up a click listener for the ViewHolder
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mVacations != null) {
                    Vacation selectedVacation = mVacations.get(position);
                    onVacationClickListener.onVacationClick(selectedVacation);
                }
            });
        }
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.vacation_list_item, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        if (mVacations != null) {
            Vacation current = mVacations.get(position);
            holder.vacationItemView.setText(current.getVacationName());
        } else {
            holder.vacationItemView.setText("No Vacation Available");
        }
    }

    @Override
    public int getItemCount() {
        return (mVacations != null) ? mVacations.size() : 0;
    }

    public void setVacations(List<Vacation> vacations) {
        this.mVacations = vacations;
        notifyDataSetChanged();
    }
}