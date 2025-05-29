package com.example.planlekcji.ckziu_elektryk.client.timetable;

import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.List;
import java.util.Map;

public interface TimetableService {

    List<SchoolEntry> getList();

    Map<DayOfWeek, List<Lesson>> getTimetable(String name);
}
