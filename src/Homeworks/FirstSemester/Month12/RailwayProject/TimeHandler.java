package Homeworks.FirstSemester.Month12.RailwayProject;

import static Methods.Methods.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

class TimeHandler {
    private GregorianCalendar calendar;
    private final SimpleDateFormat formatter;
    private String formattedDate;

    TimeHandler() {
        Calendar currentDate = Calendar.getInstance();
        calendar = new GregorianCalendar(
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH));
        formatter = new SimpleDateFormat("HH 'ч.' dd MMMM yyyy", Locale.US);
        updateFormattedDate();
    }

    void nextHour() {
        calendar.add(Calendar.HOUR, 1);
        updateFormattedDate();
        printDate();
    }

    String getTimeForwardedBy(int hours) {
        Calendar copy = (Calendar)calendar.clone();
        copy.add(Calendar.HOUR, hours);
        return formatter.format(copy.getTime());
    }


    void printDate() {
        System.out.println(paint(Colors.GREEN,"#####   CURRENT DATE: " + formattedDate + "   #####"));
    }

    void updateFormattedDate() {
        formattedDate = formatter.format(calendar.getTime());
    }

    int getHour() {
        return calendar.get(Calendar.HOUR);
    }

    int getDay() {
        return calendar.get(Calendar.MONTH);
    }

    @Override
    public String toString() {
        return formattedDate;
    }
}
