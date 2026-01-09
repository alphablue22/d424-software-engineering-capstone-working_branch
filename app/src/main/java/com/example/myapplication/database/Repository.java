package com.example.myapplication.database;

import android.app.Application;

import com.example.myapplication.dao.ExcursionDAO;
import com.example.myapplication.dao.VacationDAO;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Repository {
    private VacationDAO mVacationDao;
    private ExcursionDAO mExcursionDao;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        this.mVacationDao = db.vacationDAO();
        this.mExcursionDao = db.excursionDAO();
    }

    // Fetch all vacations
    public Future<List<Vacation>> getAllVacations() {
        return databaseExecutor.submit(mVacationDao::getAllVacations);
    }

    // Fetch all excursions
    public Future<List<Excursion>> getAllExcursions() {
        return databaseExecutor.submit(mExcursionDao::getAllExcursions);
    }

    // Insert a vacation
    public Future<Void> insertVacation(Vacation vacation) {
        return databaseExecutor.submit(() -> {
            mVacationDao.insert(vacation);
            return null;
        });
    }

    // Insert an excursion
    public Future<Void> insertExcursion(Excursion excursion) {
        return databaseExecutor.submit(() -> {
            mExcursionDao.insert(excursion);
            return null;
        });
    }

    public Future<Void> updateExcursion(Excursion excursion) {
        return databaseExecutor.submit(() -> {
            mExcursionDao.update(excursion);
            return null;
        });
    }

    public Future<Void> deleteExcursion(Excursion excursion) {
        return databaseExecutor.submit(() -> {
            mExcursionDao.delete(excursion);
            return null;
        });
    }

    public Future<Integer> getExcursionId(String excursionName, String excursionDate) {
        return databaseExecutor.submit(() -> mExcursionDao.getExcursionId(excursionName, excursionDate));
    }

    public Future<Void> updateVacation(Vacation vacation) {
        return databaseExecutor.submit(() -> {
            mVacationDao.update(vacation);
            return null;
        });
    }

    public Future<Void> deleteVacation(int vacationID) {
        return databaseExecutor.submit(() -> {
            mVacationDao.deleteById(vacationID);
            return null;
        });
    }

    public Future<List<Excursion>> getExcursionsByVacationId(int vacationID) {
        return databaseExecutor.submit(() -> mExcursionDao.getExcursionsByVacationId(vacationID));
    }

    public Future<List<Vacation>> getVacationsByNameContaining(String searchTerm) {
        return databaseExecutor.submit(() -> mVacationDao.getVacationsByNameContaining(searchTerm));
    }

    public Future<List<Excursion>> getExcursionsByNameContaining(String searchTerm) {
        return databaseExecutor.submit(() -> mExcursionDao.getExcursionsByNameContaining(searchTerm));
    }
}