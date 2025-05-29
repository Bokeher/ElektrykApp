package com.example.planlekcji.timetable.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;
import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LessonFragment extends Fragment {
    private final List<String> lessonHours = new ArrayList<>();

    public static final String TITLE = "title";
    private Map<DayOfWeek, List<String>> timetableMap;

    public LessonFragment() {
    }

    public LessonFragment(Map<DayOfWeek, List<String>> timetableMap) {
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
        Context context = MainActivity.getContext();

        addLessonHours();

        assert getArguments() != null;
        String argument = getArguments().getString(TITLE);
        assert argument != null;
        int tabNumber = Character.getNumericValue(argument.charAt(3));

        DayOfWeek dayNumber = DayOfWeek.getDayOfWeek(tabNumber);

        int currentLesson = getCurrentLessonIndex(tabNumber);

        if (timetableMap == null || timetableMap.get(DayOfWeek.MONDAY) == null) {
            return;
        }

        int dayNumbers = Objects.requireNonNull(timetableMap.get(DayOfWeek.MONDAY)).size();
        for (int i = 0; i < dayNumbers; i++) {
            String number = (i + 1) + "";
            String hour = lessonHours.get(i);

            String html = Objects.requireNonNull(timetableMap.get(dayNumber)).get(i);

            SpannableStringBuilder str = new SpannableStringBuilder(html);

            LinearLayout linearLayout = view.findViewById(R.id.linearLayoutCards);

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 10);

            int color = ContextCompat.getColor(context, R.color.lessonBackgroundColor);

            CardView cardView = new CardView(context);
            cardView.setRadius(30);
            cardView.setCardBackgroundColor(color);

            ConstraintLayout.LayoutParams layoutParams_matchParent2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            ConstraintLayout.LayoutParams layoutParams_matchParent3 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

            TextView lessonNumber = new TextView(getActivity());
            lessonNumber.setId(R.id.textViewLessonNumber);
            lessonNumber.setText(number);
            lessonNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
            lessonNumber.setGravity(Gravity.CENTER);
            lessonNumber.setPadding(dpToPx(10), 0, 0, 0);

            TextView lessonHours = new TextView(getActivity());
            lessonHours.setId(R.id.textViewLessonHours);
            lessonHours.setText(hour);
            lessonHours.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lessonHours.setLayoutParams(layoutParams_matchParent2);

            TextView lessonData = new TextView(getActivity());
            lessonData.setId(R.id.textViewLessonData);
            lessonData.setText(str);
            lessonData.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            lessonData.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            lessonData.setLayoutParams(layoutParams_matchParent3);


            lessonData.setPadding(0, 0, 0, dpToPx(16));

            if (i == currentLesson - 1) {
                int bgColor = ContextCompat.getColor(context, R.color.primaryDark);
                int textColor = ContextCompat.getColor(context, R.color.black);

                cardView.setCardBackgroundColor(bgColor);
                lessonNumber.setTextColor(textColor);
                lessonHours.setTextColor(textColor);
                lessonData.setTextColor(textColor);
                lessonNumber.setTypeface(null, Typeface.BOLD);
                lessonHours.setTypeface(null, Typeface.BOLD);
                lessonData.setTypeface(null, Typeface.BOLD);
            }

            ConstraintLayout constraintLayout = new ConstraintLayout(context);
            constraintLayout.setId(R.id.constraintLayout);

            constraintLayout.addView(lessonNumber);
            constraintLayout.addView(lessonHours);
            constraintLayout.addView(lessonData);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.textViewLessonHours, ConstraintSet.TOP, R.id.constraintLayout, ConstraintSet.TOP, dpToPx(8));
            constraintSet.connect(R.id.textViewLessonHours, ConstraintSet.LEFT, R.id.textViewLessonNumber, ConstraintSet.RIGHT, dpToPx(40));
            constraintSet.connect(R.id.textViewLessonHours, ConstraintSet.RIGHT, R.id.constraintLayout, ConstraintSet.RIGHT, dpToPx(16));

            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.TOP, R.id.textViewLessonHours, ConstraintSet.BOTTOM, dpToPx(2));
            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.BOTTOM, R.id.constraintLayout, ConstraintSet.BOTTOM, dpToPx(2));
            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.RIGHT, R.id.constraintLayout, ConstraintSet.RIGHT, dpToPx(16));
            constraintSet.connect(R.id.textViewLessonData, ConstraintSet.LEFT, R.id.textViewLessonNumber, ConstraintSet.LEFT, dpToPx(40));

            constraintSet.connect(R.id.textViewLessonNumber, ConstraintSet.TOP, R.id.constraintLayout, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.textViewLessonNumber, ConstraintSet.BOTTOM, R.id.constraintLayout, ConstraintSet.BOTTOM, 0);
            constraintSet.applyTo(constraintLayout);

            cardView.addView(constraintLayout);
            cardView.setLayoutParams(layoutParams);
            linearLayout.addView(cardView);
        }
    }

    private void addLessonHours() {
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

    private int dpToPx(int dp) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}