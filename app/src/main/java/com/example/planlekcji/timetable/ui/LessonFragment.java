package com.example.planlekcji.timetable.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.GroupLesson;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.LessonDetails;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.SingleLesson;
import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LessonFragment extends Fragment {
    private final List<String> lessonHours = new ArrayList<>();

    public static final String TITLE = "title";
    private Map<DayOfWeek, List<Lesson>> timetableMap;

    public LessonFragment() {
    }

    public LessonFragment(Map<DayOfWeek, List<Lesson>> timetableMap) {
        this.timetableMap = timetableMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Context mainContext = MainActivity.getContext();
        Context context = requireContext();
        LinearLayout layout = view.findViewById(R.id.linearLayoutCards);
        LayoutInflater inflater = LayoutInflater.from(context);

        addLessonHours();

        assert getArguments() != null;
        String argument = getArguments().getString(TITLE);
        assert argument != null;
        int tabNumber = Character.getNumericValue(argument.charAt(3));

        DayOfWeek thisDayNumber = DayOfWeek.getDayOfWeek(tabNumber);

        int currentLessonIndex = getCurrentLessonIndex(tabNumber);

        if (timetableMap == null || timetableMap.get(DayOfWeek.MONDAY) == null) {
            return;
        }

        List<Lesson> lessonList = timetableMap.get(thisDayNumber);

        Map<Integer, String> lessonData = new HashMap<>();

        if (lessonList == null) {
            Log.e("TimetableLessonFragment", "lessonList is empty");
            return;
        }


        for(Lesson lesson : lessonList) {
            for (int number : lesson.getLessonsNumbers()) {
                if(lesson instanceof SingleLesson) {
                    LessonDetails lessonDetails = ((SingleLesson) lesson).getDetails();

                    lessonData.put(number, detailsToString(lessonDetails));
                } else {
                    List<LessonDetails> lessonDetailsObject = ((GroupLesson) lesson).getLessonsDetails();

                    String text = lessonDetailsObject.stream()
                            .map(this::detailsToString)
                            .collect(Collectors.joining("\n"));

                    lessonData.put(number, text);
                }
            }
        }

        for (int i = 1; i <= lessonData.size(); i++) {
            View cardView = inflater.inflate(R.layout.lesson_card, layout, false);

            TextView lessonHoursText = cardView.findViewById(R.id.textViewLessonHours);
            TextView viewLessonData = cardView.findViewById(R.id.textViewLessonData);
            TextView lessonNumber = cardView.findViewById(R.id.textViewLessonNumber);

            String timeRangeString = lessonHours.get(i - 1);
            lessonHoursText.setText(timeRangeString);
            lessonNumber.setText(String.valueOf(i));
            viewLessonData.setText(lessonData.get(i));

            layout.addView(cardView);
        }
    }

    private String detailsToString(LessonDetails lessonDetails) {
        SchoolEntryType timetableType = MainActivity.getTimetableType();

        String subjectName = lessonDetails.getSubject().name();
        String schoolClassName = "";
        if (lessonDetails.getSchoolClass().isPresent()) schoolClassName = lessonDetails.getSchoolClass().get().shortcut();
        String teacherName = lessonDetails.getTeacher();
        String classroomName = lessonDetails.getClassroom();

        if (timetableType == SchoolEntryType.CLASSES) {
            return subjectName + " " + classroomName + " " + teacherName;
        } else if (timetableType == SchoolEntryType.TEACHERS) {
            return schoolClassName + " " + subjectName + " " + classroomName;
        } else {
            return subjectName + " " + schoolClassName + " " + teacherName;
        }
    }

    private void addLessonHours() {
        // TODO: This should probably be acquired from API
        String[] hours = {
                "08:00 - 08:45", "08:50 - 09:35", "09:45 - 10:30", "10:50 - 11:35", "11:45 - 12:30",
                "12:40 - 13:25", "13:35 - 14:20", "14:25 - 15:10", "15:15 - 16:00", "16:05 - 16:50",
                "16:55 - 17:40", "17:45 - 18:30", "18:35 - 19:20", "19:25 - 20:10", "20:15 - 21:00",
                "21:05 - 21:50", "21:55 - 22:40", "22:45 - 23:30", "23:35 - 00:20"
        };

        Collections.addAll(lessonHours, hours);
    }

    /**
     * Determines the index of the current lesson based on the current time.
     * <p>
     * This method calculates the current lesson index by comparing the current hour
     * and minutes with the predefined ending times of lessons.
     *
     * @return The index of the current lesson or 0 if there is no active lesson.
     */
    private int getCurrentLessonIndex(int tabNumber) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Monday == 1, Tuesday == 2, etc

        if (dayOfWeek != tabNumber) return 0;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        String[] lessonEndTimers = {
                "8:45", // time of 1st lesson ending
                "9:35",
                "10:30",
                "11:35",
                "12:30",
                "13:25",
                "14:20",
                "15:10",
                "16:00",
                "16:50",
                "17:40",
                "18:30",
                "19:20",
                "20:10",
                "21:00",
                "21:50",
                "22:40",
                "23:30",
                "00:20"
        };

        for (int i = 0; i < lessonEndTimers.length; i++) {
            String[] args = lessonEndTimers[i].split(":");
            int lessonHour = Integer.parseInt(args[0]);
            int lessonMinutes = Integer.parseInt(args[1]);

            if (hour == lessonHour) {
                if (minutes < lessonMinutes) return i + 1;
                return i + 2;
            }
        }

        return 0;
    }

}