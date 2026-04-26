package com.example.planlekcji.fragments.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntry;
import com.example.planlekcji.settings.SchoolEntriesDownloader;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {
    private SharedPreferences sharedPref;
    private List<SchoolEntry> classesSchoolEntries = new ArrayList<>();
    private List<SchoolEntry> teachersSchoolEntries = new ArrayList<>();
    private List<SchoolEntry> classroomsSchoolEntries = new ArrayList<>();
    private View view;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Initialize SharedPreferences for storing application settings.
        sharedPref = MainActivity.getContext().getSharedPreferences("sharedPrefs", 0);

        // Fetch data relevant to settings.
        getData();

        // Initialize spinners and populate them with data.
        initSpinners();

        return view;
    }

    private void initSpinners() {
        Spinner spinnerClassTokens = view.findViewById(R.id.spinnerClassTokens);
        Spinner spinnerTeacherTokens = view.findViewById(R.id.spinnerTeacherTokens);
        Spinner spinnerClassroomTokens = view.findViewById(R.id.spinnerClassroomTokens);

        setSpinner(spinnerClassTokens, classesSchoolEntries, getString(R.string.classTokenKey));
        setSpinner(spinnerTeacherTokens, teachersSchoolEntries, getString(R.string.teacherTokenKey));
        setSpinner(spinnerClassroomTokens, classroomsSchoolEntries, getString(R.string.classroomTokenKey));

        setTypeOfTimetableSpinner();
    }

    private void setSpinner(Spinner spinner, List<SchoolEntry> schoolEntries, String sharedPreferencesToken) {
        List<String> tokenList = new ArrayList<>();
        for (SchoolEntry schoolEntry : schoolEntries) {
            tokenList.add(schoolEntry.shortcut());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.getContext(), android.R.layout.simple_list_item_1, tokenList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String token = sharedPref.getString(sharedPreferencesToken, "");
        int selectedPosition = adapter.getPosition(token);

        if (selectedPosition < 0 && !schoolEntries.isEmpty()) {
            SchoolEntry defaultEntry = schoolEntries.get(0);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(sharedPreferencesToken, defaultEntry.shortcut());
            editor.apply();

            selectedPosition = 0;
        }

        if (selectedPosition >= 0) {
            spinner.setSelection(selectedPosition);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SchoolEntry schoolEntry = schoolEntries.get(i);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(sharedPreferencesToken, schoolEntry.shortcut());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void setTypeOfTimetableSpinner() {
        Spinner spinnerUserType = view.findViewById(R.id.spinnerUserType);

        int typeIndex = sharedPref.getInt(getString(R.string.selectedTypeOfTimetableKey), 0);
        spinnerUserType.setSelection(typeIndex);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.selectedTypeOfTimetableKey), i);
                editor.apply();

                changeVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    /**
     * Changes the visibility of three components: class, teacher, and classroom.
     */
    private void changeVisibility() {
        int whichIsVisible = sharedPref.getInt(getString(R.string.selectedTypeOfTimetableKey), 0) + 1;

        int classVisibility = View.GONE;
        int teacherVisibility = View.GONE;
        int classroomVisibility = View.GONE;

        if(whichIsVisible == 1) classVisibility = View.VISIBLE;
        else if(whichIsVisible == 2) teacherVisibility = View.VISIBLE;
        else if(whichIsVisible == 3) classroomVisibility = View.VISIBLE;

        view.findViewById(R.id.textView_selectClass).setVisibility(classVisibility);
        view.findViewById(R.id.textView_selectTeacher).setVisibility(teacherVisibility);
        view.findViewById(R.id.textView_selectClassroom).setVisibility(classroomVisibility);

        view.findViewById(R.id.spinnerClassTokens).setVisibility(classVisibility);
        view.findViewById(R.id.spinnerTeacherTokens).setVisibility(teacherVisibility);
        view.findViewById(R.id.spinnerClassroomTokens).setVisibility(classroomVisibility);
    }

    private void getData() {
        SchoolEntriesDownloader spinnersDataDownloader = new SchoolEntriesDownloader(mainViewModel.getClient());
        Thread thread = new Thread(spinnersDataDownloader);
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        classesSchoolEntries = spinnersDataDownloader.getClassesSchoolEntries();
        teachersSchoolEntries = spinnersDataDownloader.getTeachersSchoolEntries();
        classroomsSchoolEntries = spinnersDataDownloader.getClassroomsSchoolEntries();

        ensurePreviewDefaults();
    }

    private void ensurePreviewDefaults() {
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean changed = false;

        if (!sharedPref.contains(getString(R.string.classTokenKey)) && !classesSchoolEntries.isEmpty()) {
            editor.putString(getString(R.string.classTokenKey), classesSchoolEntries.get(0).shortcut());
            changed = true;
        }

        if (!sharedPref.contains(getString(R.string.teacherTokenKey)) && !teachersSchoolEntries.isEmpty()) {
            editor.putString(getString(R.string.teacherTokenKey), teachersSchoolEntries.get(0).shortcut());
            changed = true;
        }

        if (!sharedPref.contains(getString(R.string.classroomTokenKey)) && !classroomsSchoolEntries.isEmpty()) {
            editor.putString(getString(R.string.classroomTokenKey), classroomsSchoolEntries.get(0).shortcut());
            changed = true;
        }

        if (changed) {
            editor.apply();
        }
    }
}
