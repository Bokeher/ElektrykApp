package com.example.planlekcji.fragments.ui;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.utils.EmptyStateHelper;
import com.example.planlekcji.utils.EmptyStateType;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementChange;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.replacements.ReplacementDataDownloader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReplacementsFragment extends Fragment {
    private List<List<Replacement>> replacements;
    private MainViewModel mainViewModel;
    private LayoutInflater inflater;
    private LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_replacements, container, false);
        this.mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        this.inflater = inflater;
        this.layout = view.findViewById(R.id.linearLayout_replacements);

        observeAndHandleReplacementsLiveData();
        layout.addView(EmptyStateHelper.create(inflater, layout, EmptyStateType.REPLACEMENTS));

        return view;
    }

    private void observeAndHandleReplacementsLiveData() {
        mainViewModel.getReplacementsLiveData().observe(getViewLifecycleOwner(), newReplacements -> {
            replacements = newReplacements;

            updateReplacements();
        });
    }

    private void updateReplacements() {
        layout.removeAllViews();

        if(replacements == null || replacements.isEmpty() || areReplacementsEmpty()) {
            layout.addView(EmptyStateHelper.create(inflater, layout, EmptyStateType.REPLACEMENTS));

            return;
        }

        Date[] dates = ReplacementDataDownloader.getNext5Dates(); // holds next 5 non-weekend dates
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault());

        for (int i = 0; i < replacements.size(); i++) {
            List<Replacement> dayReplacements = replacements.get(i);
            if(dayReplacements.isEmpty()) continue;

            CardView dayCard = (CardView) inflater.inflate(R.layout.replacement_day_card, layout, false);
            LinearLayout dayCardLayout = dayCard.findViewById(R.id.replacementDay_layout);

            TextView dayTitle = dayCard.findViewById(R.id.textView_dayTitle);
            String formattedDate = sdf.format(dates[i]);
            if (!formattedDate.isEmpty()) {
                formattedDate = Character.toUpperCase(formattedDate.charAt(0)) + formattedDate.substring(1);
            }
            dayTitle.setText(formattedDate);

            boolean isClassType = (MainActivity.getTimetableType() == SchoolEntryType.CLASSES);

            for (Replacement replacement : dayReplacements) {
                if (replacement.changes() != null && !replacement.changes().isEmpty()) {
                    for (ReplacementChange change : replacement.changes()) {
                        CardView replacementCard = (CardView) inflater.inflate(R.layout.replacement_card, dayCardLayout, false);
                        TextView replacementTitle = replacementCard.findViewById(R.id.textView_replacementTitle);
                        TextView replacementDetails = replacementCard.findViewById(R.id.textView_replacementDetails);

                        String period = change.period();
                        String lessonText;
                        if (period == null) {
                            lessonText = "";
                        } else if (isAllDay(period)) {
                            lessonText = period;
                        } else {
                            lessonText = getString(R.string.lesson_label, period);
                        }

                        if (isClassType) {
                            String info = change.info();
                            String subjectName = info;
                            String statusOrTeacher = "";

                            if (info != null && info.contains(" - ")) {
                                String[] parts = info.split(" - ", 2);
                                subjectName = parts[0].trim();
                                statusOrTeacher = parts[1].trim();
                            }

                            replacementTitle.setText(subjectName);
                            if (!statusOrTeacher.isEmpty()) {
                                replacementDetails.setText(getString(R.string.replacement_details_format, lessonText, statusOrTeacher));
                            } else {
                                replacementDetails.setText(lessonText);
                            }
                        } else {
                            replacementTitle.setText(replacement.name());
                            replacementDetails.setText(getString(R.string.replacement_details_format, lessonText, change.info()));
                        }

                        dayCardLayout.addView(replacementCard);
                    }
                }
            }

            layout.addView(dayCard);
        }
    }

    private boolean areReplacementsEmpty() {
        for (List<Replacement> dayReplacements: replacements) {
            if (dayReplacements != null && !dayReplacements.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllDay(String period) {
        if (period == null) return false;
        String normalized = period.trim().toLowerCase(Locale.ROOT);
        return normalized.contains("cały dzień") || normalized.contains("caly dzien");
    }
}