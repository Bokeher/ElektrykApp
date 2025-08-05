package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import java.util.List;

public class SingleLesson extends Lesson {

    private final LessonDetails details;

    public SingleLesson(List<Integer> lessonsNumbers, LessonDuration duration, LessonDetails details) {
        super(lessonsNumbers, duration);
        this.details = details;
    }

    public SingleLesson(Lesson lesson, LessonDetails lessonDetails) {
        this(lesson.getLessonsNumbers(), lesson.getDuration(), lessonDetails);
    }

    public LessonDetails getDetails() {
        return details;
    }

    @NonNull
    @Override
    public String toString() {
        return "SingleLesson{" +
                "details=" + details +
                '}';
    }
}
