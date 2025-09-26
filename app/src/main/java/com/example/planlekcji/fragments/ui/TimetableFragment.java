package com.example.planlekcji.fragments.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.timetable.model.DayOfWeek;
import com.example.planlekcji.timetable.ui.Adapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class TimetableFragment extends Fragment {
    private ViewPager2 viewPager_timetable;
    private MainViewModel mainViewModel;

    private Map<DayOfWeek, List<Lesson>> timetableMap;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timetable, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        observeAndHandleTimetableLiveData();

        mainViewModel.fetchTimetable();

        viewPager_timetable = view.findViewById(R.id.viewPager_timetable);
        viewPager_timetable.setOffscreenPageLimit(5);

        setAdapterToViewPager();

        return view;
    }

    private void setAdapterToViewPager() {
        Adapter adapter = new Adapter(getChildFragmentManager(), getLifecycle(), timetableMap);
        viewPager_timetable.setAdapter(adapter);
    }

    private void observeAndHandleTimetableLiveData() {
        mainViewModel.getTimetableLiveData().observe(getViewLifecycleOwner(), newTimetableMap -> {
            timetableMap = newTimetableMap;

            setAdapterToViewPager();

            setHeadersToTabLayout();
            setCurrentDay();
        });
    }

    private void setHeadersToTabLayout() {
        TabLayout tabLayout = view.findViewById(R.id.tabLayout_dayNames);
        new TabLayoutMediator(tabLayout, viewPager_timetable, (tab, position) -> {
            String data = switch (position) {
                case 0 -> getResources().getString(R.string.mondayShortcut);
                case 1 -> getResources().getString(R.string.tuesdayShortcut);
                case 2 -> getResources().getString(R.string.wednesdayShortcut);
                case 3 -> getResources().getString(R.string.thursdayShortcut);
                case 4 -> getResources().getString(R.string.fridayShortcut);
                default -> "";
            };

            tab.setText(data);
        }).attach();
    }

    /**
     * Sets the current item of the ViewPager based on the current day of the week.
     * Monday corresponds to the first tab, Tuesday to the second tab, and so on.
     * If the current day is Saturday or Sunday, it defaults to Monday.
     */
    private void setCurrentDay() {
        Calendar calendar = GregorianCalendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int dayNumb = switch (dayOfWeek) {
            case Calendar.TUESDAY -> 1;
            case Calendar.WEDNESDAY -> 2;
            case Calendar.THURSDAY -> 3;
            case Calendar.FRIDAY -> 4;
            default -> 0;
        };

        viewPager_timetable.setCurrentItem(dayNumb);
    }
}