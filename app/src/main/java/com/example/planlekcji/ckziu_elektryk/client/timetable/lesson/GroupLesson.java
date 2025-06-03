package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import com.example.planlekcji.ckziu_elektryk.client.utils.Time;

import java.util.List;

public class GroupLesson extends Lesson {

    private final List<LessonDetails> lessonsDetails;

    public GroupLesson(Subject subject, List<Integer> lessonsNumbers, Time startTime, Time endTime, List<LessonDetails> lessonsDetails) {
        super(subject, lessonsNumbers, startTime, endTime);
        this.lessonsDetails = lessonsDetails;
    }

    public GroupLesson(Lesson lesson, List<LessonDetails> lessonsDetails) {
        this(lesson.getSubject(), lesson.getLessonsNumbers(), lesson.getStartTime(), lesson.getEndTime(), lessonsDetails);
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
