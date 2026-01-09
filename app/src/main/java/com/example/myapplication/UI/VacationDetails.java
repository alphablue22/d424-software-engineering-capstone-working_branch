package com.example.myapplication.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.Log;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.NotificationReceiver;
import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // This is the correct import
import java.util.concurrent.Future;

public class VacationDetails extends AppCompatActivity {
    private EditText editName;
    private EditText hotelNameField;
    private EditText startDateField;
    private EditText endDateField;
    private int vacationID = -1; // Default ID for new vacation
    private String name;
    private String hotelName;
    private String startDate;
    private String endDate;
    private int vacationAlert;
    private Repository repository;
    private final Calendar calendar = Calendar.getInstance();
    private RecyclerView excursionRecyclerView;
    private CheckBox alertVacationCheckBox;

    private ExcursionAdapter excursionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Initialize repository
        repository = new Repository(getApplication());

        // Initialize UI components
        editName = findViewById(R.id.titletext);
        hotelNameField = findViewById(R.id.hoteltext);
        startDateField = findViewById(R.id.startDateField);
        endDateField = findViewById(R.id.endDateField);
        excursionRecyclerView = findViewById(R.id.excursionreceylerview);
        alertVacationCheckBox = findViewById(R.id.alertVacationCheckBox);

        // Retrieve data passed via Intent
        vacationID = getIntent().getIntExtra("id", -1);
        name = getIntent().getStringExtra("vacationName");
        hotelName = getIntent().getStringExtra("hotelName");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        vacationAlert = getIntent().getIntExtra("alert", 0);

        // Populate fields dynamically
        if (name != null && !name.isEmpty()) {
            editName.setText(name);
        }
        if (hotelName != null && !hotelName.isEmpty()) {
            hotelNameField.setText(hotelName);
        }
        if (startDate != null && !startDate.isEmpty()) {
            startDateField.setText(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            endDateField.setText(endDate);
        }
        alertVacationCheckBox.setChecked(vacationAlert == 1);

        // Add listeners for date selection
        setupDatePicker(startDateField);
        setupDatePicker(endDateField);

        // Set up the RecyclerView for excursions
        setupExcursionRecyclerView();

        // Add Vacation Button
        Button addVacationButton = findViewById(R.id.addVacationButton);
        addVacationButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add Vacation button clicked", Toast.LENGTH_SHORT).show();
        });

        // Add Excursion Button
        Button addExcursionButton = findViewById(R.id.addExcursionButton);
        addExcursionButton.setOnClickListener(v -> {
            Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
            intent.putExtra("vacationID", vacationID);
            intent.putExtra("excursionID", -1);
            intent.putExtra("vacationStartDate", startDate);
            intent.putExtra("vacationEndDate", endDate);
            startActivity(intent);
        });

        // Share Vacation Button
        Button shareVacationButton = findViewById(R.id.shareVacationButton);
        shareVacationButton.setOnClickListener(v -> shareVacationDetails());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupExcursionRecyclerView(); // Reload excursions dynamically when returning to this screen
    }


    private void setupExcursionRecyclerView() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // Corrected line
        executorService.execute(() -> {
            Future<List<Excursion>> future = repository.getExcursionsByVacationId(vacationID); // Corrected line
            try {
                List<Excursion> excursions = future.get();

                runOnUiThread(() -> {
                    if (excursionAdapter == null) {
                        excursionAdapter = new ExcursionAdapter(this);
                        excursionRecyclerView.setAdapter(excursionAdapter);
                        excursionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    }
                    excursionAdapter.setExcursions(excursions);

                    View emptyPlaceholder = findViewById(R.id.emptyPlaceholderView);
                    if (excursions.isEmpty()) {
                        emptyPlaceholder.setVisibility(View.VISIBLE);
                        excursionRecyclerView.setVisibility(View.GONE);
                    } else {
                        emptyPlaceholder.setVisibility(View.GONE);
                        excursionRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void setupDatePicker(EditText dateField) {
        dateField.setOnClickListener(v -> {
            new DatePickerDialog(VacationDetails.this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateString = formatDate(calendar);
                dateField.setText(dateString);
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        return sdf.format(calendar.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.vacationsave) {
            saveVacation();
            return true;
        } else if (item.getItemId() == R.id.vacationdelete) {
            deleteVacation();
            return true;
        } else if (item.getItemId() == R.id.generateReport) {
            generateReport(); // Call the method to generate the report
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteVacation() {
        if (vacationID != -1) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(() -> {
                repository.deleteVacation(vacationID);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Vacation deleted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        } else {
            Toast.makeText(this, "Cannot delete: No vacation selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveVacation() {
        String vacationName = editName.getText().toString();
        String hotelName = hotelNameField.getText().toString();
        String startDate = startDateField.getText().toString();
        String endDate = endDateField.getText().toString();

        if (vacationName.isEmpty() || hotelName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "All fields must be filled!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!isValidDate(startDate) || !isValidDate(endDate)) {
            Toast.makeText(this, "Dates must be in the format MM/dd/yy!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!isEndDateAfterStartDate(startDate, endDate)) {
            Toast.makeText(this, "End date must be after the start date!", Toast.LENGTH_SHORT).show();
            return true;
        }

        int alert = alertVacationCheckBox.isChecked() ? 1 : 0;

        Vacation vacation;
        if (vacationID == -1) {
            //New vacation
            Future<List<Vacation>> vacationsFuture = repository.getAllVacations();
            List<Vacation> allVacations;
            int newVacationId;
            try {
                allVacations = vacationsFuture.get();
                newVacationId = allVacations.isEmpty() ? 1 : allVacations.get(allVacations.size() - 1).getVacationID() + 1;
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            vacation = new Vacation(newVacationId, vacationName, hotelName, startDate, endDate, alert);

            try {
                repository.insertVacation(vacation).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (alert == 1) {
                scheduleVacationAlert(vacationName, startDate, true, newVacationId); // Alert for start date
                scheduleVacationAlert(vacationName, endDate, false, newVacationId); // Alert for end date
            }
            Toast.makeText(this, "Vacation saved successfully!", Toast.LENGTH_SHORT).show();
        } else {
            //Existing vacation
            vacation = new Vacation(vacationID, vacationName, hotelName, startDate, endDate, alert);
            try {
                repository.updateVacation(vacation).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (alert == 1) {
                scheduleVacationAlert(vacationName, startDate, true, vacationID);
                scheduleVacationAlert(vacationName, endDate, false, vacationID);
            } else {
                cancelNotification(vacationID);
            }
            Toast.makeText(this, "Vacation updated successfully!", Toast.LENGTH_SHORT).show();
        }
        finish();
        return true;
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        sdf.setLenient(false); // Ensures strict validation
        try {
            sdf.parse(date); // Parses the date string
            return true; // Returns true if successful
        } catch (ParseException e) {
            return false; // Returns false if parsing fails
        }
    }

    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date start = sdf.parse(startDate); // Parses start date
            Date end = sdf.parse(endDate); // Parses end date
            return end != null && start != null && end.after(start); // Validates that end is after start
        } catch (ParseException e) {
            return false; // Returns false if parsing fails
        }
    }

    private void scheduleVacationAlert(String title, String date, boolean isStart, int id) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        try {
            Date alertDate = sdf.parse(date); // Parse the date for the alert
            if (alertDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(alertDate); // Set the alert date in the calendar
                // Set the time to 9:00 AM
                calendar.set(Calendar.HOUR_OF_DAY, 9);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.putExtra("vacationTitle", title); // Add the vacation title
                intent.putExtra("isStart", isStart); // Specify if it's a start or end alert
                intent.putExtra("isVacationNotification", true); // Specify that is a vacation notification

                // Generate unique IDs for notifications
                int notificationID = isStart ? id * 2 : id * 2 + 1;

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this,
                        notificationID, // Unique ID for start or end notification
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                String alertType = isStart ? "Vacation starting" : "Vacation ending";
                Toast.makeText(this, alertType + " alert set for " + date + " at 9:00 AM", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to set alert: Invalid date!", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareVacationDetails() {
        String vacationDetails = "Vacation Name: " + name + "\n" +
                "Hotel Name: " + hotelName + "\n" +
                "Start Date: " + startDate + "\n" +
                "End Date: " + endDate;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, vacationDetails);
        startActivity(Intent.createChooser(shareIntent, "Share vacation details using"));
    }

    private void cancelNotification(int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntentStart = PendingIntent.getBroadcast(this, id * 2, intent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(this, id * 2 + 1, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntentStart);
        alarmManager.cancel(pendingIntentEnd);

        pendingIntentStart.cancel();
        pendingIntentEnd.cancel();
    }

    private void generateReport() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(); // Corrected line
        executorService.execute(() -> {
            Future<List<Excursion>> future = repository.getExcursionsByVacationId(vacationID); // Corrected line
            try {
                List<Excursion> excursions = future.get();

                StringBuilder report = new StringBuilder();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.US);

                // Add Title
                report.append("Vacation Report - ").append(name).append("\n");
                report.append("Generated on: ").append(dateTimeFormat.format(new Date())).append("\n\n");

                // Add Vacation Details
                report.append("Vacation Details:\n");
                report.append("  Name: ").append(name).append("\n");
                report.append("  Hotel: ").append(hotelName).append("\n");
                report.append("  Start Date: ").append(startDate).append("\n");
                report.append("  End Date: ").append(endDate).append("\n\n");

                // Add Excursion Details
                report.append("Excursions:\n");
                if (excursions.isEmpty()) {
                    report.append("  No excursions added.\n");
                } else {
                    for (Excursion excursion : excursions) {
                        report.append("  - ").append(excursion.getExcursionName()).append(" (").append(excursion.getExcursionDate()).append(")\n");
                    }
                }
                runOnUiThread(() -> {
                    // Create a ScrollView
                    ScrollView scrollView = new ScrollView(this);
                    scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

                    // Create a TextView
                    TextView reportTextView = new TextView(this);
                    reportTextView.setText(report.toString());
                    reportTextView.setPadding(16, 16, 16, 16);

                    // Add the TextView to the ScrollView
                    scrollView.addView(reportTextView);

                    // Set the ScrollView as the content view
                    setContentView(scrollView);
                });

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }
}