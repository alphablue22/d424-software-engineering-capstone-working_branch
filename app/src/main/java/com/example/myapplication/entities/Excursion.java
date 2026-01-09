package com.example.myapplication.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "excursions")
public class Excursion {

    @PrimaryKey(autoGenerate = true)
    private int excursionId;
    private String excursionName;
    private int vacationId;
    private String excursionDate;

    // Constructor
    public Excursion(String excursionName, int vacationId, String excursionDate) {
        this.excursionName = excursionName;
        this.vacationId = vacationId;
        this.excursionDate = excursionDate;
    }

    // Getter for excursionId
    public int getExcursionId() {
        return excursionId;
    }

    // Setter for excursionId
    public void setExcursionId(int excursionId) {
        this.excursionId = excursionId;
    }

    // Getter for excursionName
    public String getExcursionName() {
        return excursionName;
    }

    // Setter for excursionName
    public void setExcursionName(String excursionName) {
        this.excursionName = excursionName;
    }

    // Getter for vacationId
    public int getVacationId() {
        return vacationId;
    }

    // Setter for vacationId
    public void setVacationId(int vacationId) {
        this.vacationId = vacationId;
    }
    // Getter for excursionDate
    public String getExcursionDate() {
        return excursionDate;
    }
    // Setter for excursionDate
    public void setExcursionDate(String excursionDate){
        this.excursionDate = excursionDate;
    }
}