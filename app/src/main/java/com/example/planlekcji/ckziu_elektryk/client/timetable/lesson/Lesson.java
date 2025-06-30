package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import com.example.planlekcji.ckziu_elektryk.client.utils.Time;

import java.util.List;

public class Lesson {

    private final Subject subject;
    private final List<Integer> lessonsNumbers;
    private final Time startTime;
    private final Time endTime;

    protected Lesson(Subject subject, List<Integer> lessonsNumbers, Time startTime, Time endTime) {
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

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
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
