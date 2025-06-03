package com.example.planlekcji.ckziu_elektryk.client.utils;

import androidx.annotation.NonNull;

public final class Time {

    private final int hours;
    private final int minutes;

    public static Time of(String time) {
        if (!time.contains(":"))
            throw new IllegalArgumentException("Incorrect format of time");

        String[] timeAsArray = time.split(":");

        return new Time(timeAsArray);
    }

    private Time(String[] timeAsArray) {
        try {
            this.hours = Integer.parseInt(timeAsArray[0]);
            this.minutes = Integer.parseInt(timeAsArray[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("This is not a time");
        }
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    @NonNull
    @Override
    public String toString() {
        String hoursAsString = String.valueOf(hours);
        String minutesAsString = String.valueOf(minutes);

        hoursAsString = setCorrectFormat(hoursAsString);
        minutesAsString = setCorrectFormat(minutesAsString);

        return String.format("%s:%s", hoursAsString, minutesAsString);
    }

    private String setCorrectFormat(String timeElementAsString) {

        String newTimeElement = "";

        if (timeElementAsString.length() == 1) {
            newTimeElement = "0" + timeElementAsString;
        }

        return newTimeElement;
    }
}
