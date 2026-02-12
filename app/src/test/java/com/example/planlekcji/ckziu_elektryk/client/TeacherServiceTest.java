package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertNotNull;

import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStubFactory;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntry;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.timetable.model.DayOfWeek;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TeacherServiceTest {


    private TimetableService service;

    @Before
    public void init() throws IOException {
        CKZiUElektrykClient client = CKZiUElektrykClientStubFactory.createClient();

        service = client.getTimetableService(SchoolEntryType.TEACHERS);
    }

    @Test
    public void shouldGetAllTeachers() {
        List<SchoolEntry> list = service.getList();

        assertNotNull(list);
    }

    @Test
    public void shouldGetTeacherTimetable() {
        Map<DayOfWeek, List<Lesson>> timetable = service.getTimetable("ma");

        timetable.forEach((dayOfWeek, lessons) -> {
            System.out.printf("Day: %s -> Lessons: %s", dayOfWeek, lessons);
            System.out.println();
        });

        assertNotNull(timetable);
    }
}
