package com.example.myapplication.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.entities.Excursion;

import java.util.List;

@Dao
public interface ExcursionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursions ORDER BY excursionID ASC")
    List<Excursion> getAllExcursions();

    // Retrieve excursions by vacationID
    @Query("SELECT * FROM excursions WHERE vacationID = :vacationID")
    List<Excursion> getExcursionsByVacationId(int vacationID);

    @Query("SELECT excursionID FROM excursions WHERE excursionName = :excursionName AND excursionDate = :excursionDate")
    int getExcursionId(String excursionName, String excursionDate);

    //New method to search excursions by name
    @Query("SELECT * FROM excursions WHERE excursionName LIKE '%' || :searchTerm || '%' ORDER BY excursionID ASC")
    List<Excursion> getExcursionsByNameContaining(String searchTerm);
}