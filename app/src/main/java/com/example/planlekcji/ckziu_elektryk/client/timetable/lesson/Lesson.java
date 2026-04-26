package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import java.util.List;

public class Lesson {

    private final List<Integer> lessonsNumbers;
    private final LessonDuration duration;

    protected Lesson(List<Integer> lessonsNumbers, LessonDuration duration) {
        this.lessonsNumbers = lessonsNumbers;
        this.duration = duration;
    }

    public List<Integer> getLessonsNumbers() {
        return lessonsNumbers;
    }

    public LessonDuration getDuration() {
        return duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "Lesson{" +
                "lessonsNumbers=" + lessonsNumbers +
                ", duration=" + duration +
                '}';
    }
}
