package com.example.nurtura.utils;

import com.example.nurtura.model.Immunization;
import com.example.nurtura.model.Vaccine;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ImmunizationUtils {

    public static Date calculateDueDate(Date dateOfBirth, int ageInMonths) {
        if (dateOfBirth == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateOfBirth);
        cal.add(Calendar.MONTH, ageInMonths);
        return cal.getTime();
    }

    public static List<Immunization> generateSchedule(Date childDob, List<Vaccine> vaccines) {
        List<Immunization> results = new ArrayList<>();
        if (childDob == null || vaccines == null || vaccines.isEmpty()) {
            return results;
        }

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        for (Vaccine vaccine : vaccines) {
            Date dueDate = calculateDueDate(childDob, vaccine.getRecommendedAgeInMonthsInt());
            if (dueDate == null) continue;

            String status;
            long diffInMillis = dueDate.getTime() - today.getTime();
            long daysDiff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

            if (daysDiff < 0) {
                status = "Overdue (" + Math.abs(daysDiff) + " days ago)";
            } else if (daysDiff == 0) {
                status = "Due Today";
            } else if (daysDiff <= 14) {
                status = "Due in " + daysDiff + " days";
            } else {
                status = "Upcoming";
            }

            String scheduleLabel = (vaccine.getRecommendedAgeInMonthsInt() == 0)
                    ? "At Birth"
                    : "Month " + vaccine.getRecommendedAgeInMonthsInt();

            results.add(new Immunization(
                    vaccine.getName(),
                    scheduleLabel,
                    status,
                    sdf.format(dueDate)
            ));
        }
        return results;
    }
}
