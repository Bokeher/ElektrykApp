package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import com.example.planlekcji.ckziu_elektryk.client.utils.Time;

import java.util.List;

public class SingleLesson extends Lesson{

    private final LessonDetails details;

    public SingleLesson(Subject subject, List<Integer> lessonsNumbers, Time startTime, Time endTime, LessonDetails details) {
        super(subject, lessonsNumbers, startTime, endTime);
        this.details = details;
    }

    public SingleLesson(Lesson lesson, LessonDetails lessonDetails) {
        this(lesson.getSubject(), lesson.getLessonsNumbers(), lesson.getStartTime(), lesson.getEndTime(), lessonDetails);
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
