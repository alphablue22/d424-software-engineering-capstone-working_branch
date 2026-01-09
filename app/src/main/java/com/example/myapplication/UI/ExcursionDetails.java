package com.example.myapplication.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.NotificationReceiver;
import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Excursion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExcursionDetails extends AppCompatActivity {
    private String name;
    private int excursionID;
    private int vacationID;
    private EditText editName;
    private TextView editDate;
    private String excursionDate;
    private CheckBox alertCheckBox;

    private String vacationStartDate;
    private String vacationEndDate;

    private Repository repository;
    private final Calendar calendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        // Initialize repository
        repository = new Repository(getApplication());

        // Initialize UI components
        editName = findViewById(R.id.excursionname);
        editDate = findViewById(R.id.excursiondate);
        alertCheckBox = findViewById(R.id.alertCheckBox);

        // Retrieve data passed via Intent
        name = getIntent().getStringExtra("name");
        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        excursionDate = getIntent().getStringExtra("excursiondate");
        vacationStartDate = getIntent().getStringExtra("vacationStartDate");
        vacationEndDate = getIntent().getStringExtra("vacationEndDate");

        // Validate and handle null values
        if (excursionDate == null || excursionDate.isEmpty()) {
            excursionDate = new SimpleDateFormat("MM/dd/yy", Locale.US).format(new Date());
        }
        if (vacationStartDate == null || vacationStartDate.isEmpty()) {
            vacationStartDate = "01/01/2000"; // Default value
        }
        if (vacationEndDate == null || vacationEndDate.isEmpty()) {
            vacationEndDate = "12/31/2099"; // Default value
        }

        editName.setText(name != null ? name : "");
        editDate.setText(excursionDate);

        // Set up DatePickerDialog
        editDate.setOnClickListener(v -> showDatePicker());

        dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        // Set up Save Button
        Button saveExcursionButton = findViewById(R.id.saveExcursionButton);
        saveExcursionButton.setOnClickListener(v -> saveExcursion());

        // Set up Delete Button
        Button deleteExcursionButton = findViewById(R.id.deleteExcursionButton);
        deleteExcursionButton.setOnClickListener(v -> deleteExcursion());
    }

    private void showDatePicker() {
        try {
            String currentDate = editDate.getText().toString();
            Date date = new SimpleDateFormat("MM/dd/yy", Locale.US).parse(currentDate);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
        editDate.setText(sdf.format(calendar.getTime()));
    }

    private void saveExcursion() {
        String excursionName = editName.getText().toString().trim();
        String excursionDate = editDate.getText().toString().trim();

        // Validations
        if (excursionName.isEmpty()) {
            Toast.makeText(this, "Excursion name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (excursionDate.isEmpty()) {
            Toast.makeText(this, "Excursion date cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidDate(excursionDate)) {
            Toast.makeText(this, "Date must be in the format MM/dd/yy!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date selectedDate = sdf.parse(excursionDate);
            Date startDate = sdf.parse(vacationStartDate);
            Date endDate = sdf.parse(vacationEndDate);

            if (selectedDate != null && (selectedDate.before(startDate) || selectedDate.after(endDate))) {
                Toast.makeText(this, "Excursion date must be within the vacation dates!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format!", Toast.LENGTH_SHORT).show();
            return;
        }

        Excursion excursion = new Excursion(excursionName, vacationID, excursionDate);
        Log.d("ExcursionDetails", "saveExcursion: excursionID before save " + excursionID);

        ExecutorService executorService = Executors.newSingleThreadExecutor(); // Create a thread pool
        executorService.execute(() -> {
            if (excursionID == -1) {
                Future<Integer> future = repository.getExcursionId(excursion.getExcursionName(), excursion.getExcursionDate());
                try {
                    int newId = future.get(); // Get the result from the Future (blocking call)
                    excursionID = newId;
                    Log.d("ExcursionDetails", "saveExcursion: excursionID after insert " + excursionID);
                    excursion.setExcursionId(excursionID);
                    repository.insertExcursion(excursion);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                excursion.setExcursionId(excursionID);
                repository.updateExcursion(excursion);
            }
            runOnUiThread(() -> { // Update UI on the main thread
                Log.d("ExcursionDetails", "saveExcursion: excursionID after save " + excursionID);
                if (alertCheckBox.isChecked()) {
                    scheduleNotification(excursionName, excursionDate, excursionID);
                    Log.d("ExcursionDetails", "saveExcursion: Alert checked - scheduling notification");
                } else {
                    cancelNotification();
                }
                finish(); // Go back to the previous screen
            });
        });
    }

    private void deleteExcursion() {
        if (excursionID == -1) {
            Toast.makeText(this, "Cannot delete an unsaved excursion!", Toast.LENGTH_SHORT).show();
            return;
        }
        Excursion excursion = new Excursion(editName.getText().toString(), vacationID, editDate.getText().toString());
        excursion.setExcursionId(excursionID);
        repository.deleteExcursion(excursion);
        cancelNotification();
        Log.d("ExcursionDetails", "deleteExcursion: Notification cancelled");

        Toast.makeText(this, "Excursion deleted successfully!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void cancelNotification() {
        Log.d("ExcursionDetails", "cancelNotification: Cancelling notification");

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                excursionID, // Use excursionID as the unique identifier
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void scheduleNotification(String excursionName, String excursionDate, int id) {
        Log.d("ExcursionDetails", "scheduleNotification: Scheduling notification for " + excursionName + " on " + excursionDate);

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("excursionName", excursionName);
        intent.putExtra("excursionID", id);
        intent.putExtra("isExcursionNotification", true); // Add the isExcursionNotification extra
        Log.d("ExcursionDetails", "scheduleNotification: Excursion name: " + excursionName + " id: " + id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                id, // Use the excursionID
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar alarmCalendar = Calendar.getInstance();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            Date parsedDate = sdf.parse(excursionDate);
            alarmCalendar.setTime(parsedDate);
            Log.d("ExcursionDetails", "scheduleNotification: Parsed date: " + parsedDate.toString());

            alarmCalendar.set(Calendar.HOUR_OF_DAY, 9); // Set the hour to 9:00
            alarmCalendar.set(Calendar.MINUTE, 0);
            alarmCalendar.set(Calendar.SECOND, 0);
            alarmCalendar.set(Calendar.MILLISECOND, 0);

            Log.d("ExcursionDetails", "scheduleNotification: Alarm calendar time: " + alarmCalendar.getTime().toString());
            // Check if the alarm date is in the past
            if (alarmCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                // If it's in the past, add one day to make it trigger tomorrow at 9:00 AM
                alarmCalendar.add(Calendar.DAY_OF_YEAR, 1);
                Log.d("ExcursionDetails", "scheduleNotification: Alarm was in the past. Moved to next day " + alarmCalendar.getTime().toString());
            }

            long alarmTime = alarmCalendar.getTimeInMillis();
            Log.d("ExcursionDetails", "scheduleNotification: Alarm time in millis: " + alarmTime);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

            Toast.makeText(this, "Alert set for excursion date at 9:00 AM!", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Log.e("ExcursionDetails", "scheduleNotification: Error parsing date for alarm", e);
            e.printStackTrace();
            Toast.makeText(this, "Failed to set alert: Invalid date!", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        dateFormat.setLenient(false); // Don't allow lenient parsing

        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}