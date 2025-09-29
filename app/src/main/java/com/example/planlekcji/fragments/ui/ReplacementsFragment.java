package com.example.planlekcji.fragments.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementChange;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.replacements.ReplacementDataDownloader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReplacementsFragment extends Fragment {
    private View view;
    private List<List<Replacement>> replacements;
    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_replacements, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        observeAndHandleReplacementsLiveData();
        mainViewModel.fetchReplacements();

        return view;
    }

    private void observeAndHandleReplacementsLiveData() {
        mainViewModel.getReplacementsLiveData().observe(getViewLifecycleOwner(), newReplacements -> {
            replacements = newReplacements;

            if (replacements == null || replacements.isEmpty()) return;

            updateReplacements();
        });
    }

    private void updateReplacements() {
        TextView textFieldReplacements = view.findViewById(R.id.textView_replacements);
        TextView textView_noResults = view.findViewById(R.id.textView_noResults);

        if(replacements == null || replacements.isEmpty() || areReplacementsEmpty()) {
            textFieldReplacements.setText(getString(R.string.no_replacements));
            return;
        }

        StringBuilder displayedText = new StringBuilder();
        Date[] dates = ReplacementDataDownloader.getNext5Dates(); // holds next 5 non-weekend dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault());

        // TODO: Fix random crashes
        // TODO: Use multiple TextViews and insert a horizontal line between each one
        // TODO: Display loading when downloading replacements
        int i = 0;
        for (List<Replacement> dayReplacements : replacements) {
            if(dayReplacements.isEmpty()) continue;

            if(i != 0) {
                displayedText.append("<br><br>");
            }

            displayedText.append("<h2>")
                    .append(sdf.format(dates[i]))
                    .append("</h2>");

            for (Replacement replacement : dayReplacements) {
                displayedText.append(replacementToString(replacement));
                displayedText.append("<br>");
            }

            i++;
        }

        Spanned spannedText = Html.fromHtml(displayedText.toString(), Html.FROM_HTML_MODE_LEGACY);
        textFieldReplacements.setText(spannedText);
        textView_noResults.setVisibility(View.GONE);
    }

    private String replacementToString(Replacement replacement) {
        StringBuilder res = new StringBuilder();
        SchoolEntryType timetableType = MainActivity.getTimetableType();
        if(timetableType != SchoolEntryType.CLASSES) {
            res.append(replacement.name()).append("<br>");
        }

        List<ReplacementChange> changes = replacement.changes();
        for (ReplacementChange change : changes) {
            res.append(change.period()).append(" | ").append(change.info()).append("<br>");
        }

        return res.toString();
    }

    private boolean areReplacementsEmpty() {
        for (List<Replacement> dayReplacements: replacements) {
            if(!dayReplacements.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}