package com.example.myapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // Correct import
import java.util.concurrent.Future;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class VacationList extends AppCompatActivity implements VacationAdapter.OnVacationClickListener {
    private Repository repository;
    private RecyclerView recyclerView;
    private VacationAdapter vacationAdapter; // Add this line
    private EditText searchVacation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);

        // Initialize repository
        repository = new Repository(getApplication());

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter
        vacationAdapter = new VacationAdapter(this, this);
        recyclerView.setAdapter(vacationAdapter);

        // Load vacations into the RecyclerView
        loadVacations();

        // Set up the "Add Vacation" button
        Button addVacationButton = findViewById(R.id.addVacationButton);
        addVacationButton.setOnClickListener(v -> {
            Intent intent = new Intent(VacationList.this, VacationDetails.class);
            startActivity(intent);
        });

        // Search Bar
        searchVacation = findViewById(R.id.searchVacation);
        searchVacation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVacations(s.toString()); // Call the filter method
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadVacations() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Future<List<Vacation>> vacationsFuture = repository.getAllVacations();
            try {
                List<Vacation> vacations = vacationsFuture.get();
                runOnUiThread(() -> vacationAdapter.setVacations(vacations));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void filterVacations(String searchTerm) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            Future<List<Vacation>> filteredVacationsFuture;
            if (searchTerm.isEmpty()) {
                filteredVacationsFuture = repository.getAllVacations();
            } else {
                filteredVacationsFuture = repository.getVacationsByNameContaining(searchTerm);
            }
            try {
                List<Vacation> filteredVacations = filteredVacationsFuture.get();
                runOnUiThread(() -> vacationAdapter.setVacations(filteredVacations));
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onVacationClick(Vacation vacation) {
        // Handle click events for vacation items
        Intent intent = new Intent(VacationList.this, VacationDetails.class);
        intent.putExtra("id", vacation.getVacationID());
        intent.putExtra("vacationName", vacation.getVacationName());
        intent.putExtra("hotelName", vacation.getHotelName());
        intent.putExtra("startDate", vacation.getStartDate());
        intent.putExtra("endDate", vacation.getEndDate());
        intent.putExtra("alert", vacation.getAlert());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload vacations when returning to this screen
        loadVacations();
    }
}