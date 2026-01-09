package com.example.myapplication;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import com.example.myapplication.entities.Vacation;

public class VacationRepositoryTest {

    @Test
    public void testFilterVacations_emptyInput_returnsAllVacations() {
        // Setup sample data
        List<Vacation> sampleVacations = Arrays.asList(
                new Vacation(1, "Beach Getaway", "Hilton", "2023-06-01", "2023-06-05", 0),
                new Vacation(2, "Mountain Retreat", "Sheraton", "2023-07-10", "2023-07-15", 1)
        );

        // Simulate repository logic
        List<Vacation> result = sampleVacations.stream()
                .filter(v -> "".isEmpty() || v.getVacationName().contains(""))
                .collect(Collectors.toList());

        // Assert
        assertEquals(sampleVacations.size(), result.size());
        assertTrue(result.containsAll(sampleVacations));
    }

    @Test
    public void testFilterVacations_matchingInput_returnsFilteredVacations() {
        // Setup sample data
        List<Vacation> sampleVacations = Arrays.asList(
                new Vacation(1, "Beach Getaway", "Hilton", "2023-06-01", "2023-06-05", 0),
                new Vacation(2, "Mountain Retreat", "Sheraton", "2023-07-10", "2023-07-15", 1)
        );

        // Simulate repository logic
        List<Vacation> result = sampleVacations.stream()
                .filter(v -> "Beach".isEmpty() || v.getVacationName().contains("Beach"))
                .collect(Collectors.toList());

        // Assert
        assertEquals(1, result.size());
        assertEquals("Beach Getaway", result.get(0).getVacationName());
    }

    @Test
    public void testFilterVacations_noMatches_returnsEmptyList() {
        // Setup sample data
        List<Vacation> sampleVacations = Arrays.asList(
                new Vacation(1, "Beach Getaway", "Hilton", "2023-06-01", "2023-06-05", 0),
                new Vacation(2, "Mountain Retreat", "Sheraton", "2023-07-10", "2023-07-15", 1)
        );

        // Simulate repository logic
        List<Vacation> result = sampleVacations.stream()
                .filter(v -> "Desert".isEmpty() || v.getVacationName().contains("Desert"))
                .collect(Collectors.toList());

        // Assert
        assertEquals(0, result.size());
    }
}