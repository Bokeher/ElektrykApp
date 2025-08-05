package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

final class GroupLessonFactory extends LessonFactory {

    GroupLesson createLesson(JsonObject lessonJsonObject, Lesson lesson) {
        JsonArray lessonsJsonArray = lessonJsonObject.getAsJsonArray("lessons");
        List<LessonDetails> details = new ArrayList<>();

        lessonsJsonArray.forEach(lessonJsonObj -> {
            JsonObject jsonObject = lessonJsonObj.getAsJsonObject();

            LessonDetails lessonDetails = new LessonDetails(getSubject(jsonObject), getGroups(jsonObject));

            setTeacher(jsonObject, lessonDetails);
            setClassroom(jsonObject, lessonDetails);
            setSchoolClass(jsonObject, lessonDetails);

            details.add(lessonDetails);
        });

        return new GroupLesson(lesson, details);
    }
}
