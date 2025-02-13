package com.example.futurefridgesapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InventoryManagerUnitTest {

    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Test
    public void testGetDateDiff_SameDay() {
        long actualDiff = InventoryManager.getDateDiff(format, "10/02/2025", "10/02/2025");
        assertEquals("Difference should be 0 for same date", 0, actualDiff);
    }

    @Test
    public void testGetDateDiff_OneDayDifference() {
        long actualDiff = InventoryManager.getDateDiff(format, "10/02/2025", "11/02/2025");
        assertEquals("Difference should be 1 day", 1, actualDiff);
    }

    @Test
    public void testGetDateDiff_EndOfMonth() {
        long actualDiff = InventoryManager.getDateDiff(format, "28/02/2025", "01/03/2025");
        assertEquals("Difference should be 1 day across months", 1, actualDiff);
    }

    @Test
    public void testGetDateDiff_LeapYear() {
        long actualDiff = InventoryManager.getDateDiff(format, "28/02/2024", "01/03/2024"); // Leap year
        assertEquals("Leap year should correctly account for Feb 29", 2, actualDiff);
    }

    @Test
    public void testGetDateDiff_YearChange() {
        long actualDiff = InventoryManager.getDateDiff(format, "31/12/2024", "01/01/2025");
        assertEquals("Difference should be 1 across years", 1, actualDiff);
    }



    @Test
    public void testGetDateDiff_BackwardDates() {
        long actualDiff = InventoryManager.getDateDiff(format, "10/02/2025", "01/02/2025");
        assertEquals("Should return negative value when dates are reversed", -9, actualDiff);
    }


        @Test
        public void testRemoveTime_RemovesTimeComponents() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2025, Calendar.FEBRUARY, 10, 15, 30, 45);
            Date dateWithTime = calendar.getTime();

            Date cleanedDate = InventoryManager.removeTime(dateWithTime);

            Calendar cleanedCalendar = Calendar.getInstance();
            cleanedCalendar.setTime(cleanedDate);

            assertEquals(0, cleanedCalendar.get(Calendar.HOUR_OF_DAY));
            assertEquals(0, cleanedCalendar.get(Calendar.MINUTE));
            assertEquals(0, cleanedCalendar.get(Calendar.SECOND));
            assertEquals(0, cleanedCalendar.get(Calendar.MILLISECOND));
        }


    @Test
        public void testRemoveTime_DoesNotChangeDate() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2025, Calendar.FEBRUARY, 10, 12, 45, 30); // Original time

            Date cleanedDate = InventoryManager.removeTime(calendar.getTime());

            Calendar cleanedCalendar = Calendar.getInstance();
            cleanedCalendar.setTime(cleanedDate);

            assertEquals(calendar.get(Calendar.YEAR), cleanedCalendar.get(Calendar.YEAR));
            assertEquals(calendar.get(Calendar.MONTH), cleanedCalendar.get(Calendar.MONTH));
            assertEquals(calendar.get(Calendar.DAY_OF_MONTH), cleanedCalendar.get(Calendar.DAY_OF_MONTH));
        }

}




