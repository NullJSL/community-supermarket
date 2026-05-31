package com.community.supermarket.util;

import java.time.LocalDate;
import java.time.Month;

public class SeasonUtil {

    private SeasonUtil() {}

    public static String getCurrentSeason() {
        return getSeason(LocalDate.now());
    }

    public static String getSeason(LocalDate date) {
        Month month = date.getMonth();
        return switch (month) {
            case MARCH, APRIL, MAY -> "SPRING";
            case JUNE, JULY, AUGUST -> "SUMMER";
            case SEPTEMBER, OCTOBER, NOVEMBER -> "AUTUMN";
            case DECEMBER, JANUARY, FEBRUARY -> "WINTER";
        };
    }
}
