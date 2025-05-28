package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import com.google.gson.JsonObject;

import java.util.List;

final class SingleLessonFactory extends LessonFactory {

    static SingleLesson createLesson(JsonObject jsonObject, Lesson lesson) {
        List<String> groups = getGroups(jsonObject);
        LessonDetails lessonDetails = new LessonDetails(groups);

        setTeacher(jsonObject, lessonDetails);
        setClassroom(jsonObject, lessonDetails);
        setSchoolClass(jsonObject, lessonDetails);

        return new SingleLesson(lesson, lessonDetails);
    }
}
