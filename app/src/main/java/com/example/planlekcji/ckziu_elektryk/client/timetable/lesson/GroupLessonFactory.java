package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

final class GroupLessonFactory extends LessonFactory {

    static GroupLesson createLesson(JsonObject lessonJsonObject, Lesson lesson) {
        JsonArray lessonsJsonArray = lessonJsonObject.getAsJsonArray("lessons");
        List<LessonDetails> lessonDetails = new ArrayList<>();

        lessonsJsonArray.forEach(lessonJsonObj -> {
            JsonObject jsonObject1 = lessonJsonObj.getAsJsonObject();

            LessonDetails lessonDetail = new LessonDetails(getGroups(jsonObject1));

            setTeacher(jsonObject1, lessonDetail);
            setClassroom(jsonObject1, lessonDetail);
            setSchoolClass(jsonObject1, lessonDetail);

            lessonDetails.add(lessonDetail);
        });

        return new GroupLesson(lesson, lessonDetails);
    }
}
