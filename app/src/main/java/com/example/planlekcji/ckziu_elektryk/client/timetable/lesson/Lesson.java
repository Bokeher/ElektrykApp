package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Lesson {

    private final Subject subject;
    @SerializedName("lesson_numbers")
    private final List<Integer> lessonsNumbers;
    @SerializedName("start_time")
    private final String startTime;
    @SerializedName("end_time")
    private final String endTime;

    protected Lesson(Subject subject, List<Integer> lessonsNumbers, String startTime, String endTime) {
        this.subject = subject;
        this.lessonsNumbers = lessonsNumbers;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<Integer> getLessonsNumbers() {
        return lessonsNumbers;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Lesson{" +
                "subject=" + subject +
                ", lessonsNumbers=" + lessonsNumbers +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
