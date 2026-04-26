package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import java.util.List;

public class GroupLesson extends Lesson {

    private final List<LessonDetails> lessonsDetails;

    public GroupLesson(List<Integer> lessonsNumbers, LessonDuration duration, List<LessonDetails> lessonsDetails) {
        super(lessonsNumbers, duration);
        this.lessonsDetails = lessonsDetails;
    }

    public GroupLesson(Lesson lesson, List<LessonDetails> lessonsDetails) {
        this(lesson.getLessonsNumbers(), lesson.getDuration(), lessonsDetails);
    }

    public List<LessonDetails> getLessonsDetails() {
        return lessonsDetails;
    }

    @NonNull
    @Override
    public String toString() {
        return "GroupLesson{" +
                "lessonsDetails=" + lessonsDetails +
                '}';
    }
}
