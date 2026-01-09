package com.example.myapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDAO {
    @Query("DELETE FROM vacations WHERE vacationID = :id")
    void deleteById(int id);

    @Insert
    void insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Query("SELECT * FROM vacations ORDER BY vacationID ASC")
    List<Vacation> getAllVacations();

    @Query("SELECT * FROM vacations WHERE vacationName LIKE '%' || :searchTerm || '%' ORDER BY vacationID ASC")
    List<Vacation> getVacationsByNameContaining(String searchTerm);
}