package com.example.myapplication.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    private int vacationID;
    private String vacationName;
    private String hotelName;

    private String startDate;
    private String endDate;

    private int alert;

    public Vacation(int vacationID, String vacationName, String hotelName, String startDate, String endDate, int alert) {
        this.vacationID = vacationID;
        this.vacationName = vacationName;
        this.hotelName = hotelName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alert = alert;
    }

    // Constructor for creating a new vacation (ignored by Room)
    @Ignore
    public Vacation(String vacationName, String hotelName, String startDate, String endDate, int alert) {
        this.vacationName = vacationName;
        this.hotelName = hotelName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alert = alert;
    }

    // Getters and setters
    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getVacationName() {
        return vacationName;
    }

    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getAlert() {
        return alert;
    }

    public void setAlert(int alert) {
        this.alert = alert;
    }
}