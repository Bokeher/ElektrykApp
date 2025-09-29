package com.example.planlekcji.fragments.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementChange;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.replacements.ReplacementDataDownloader;
import com.example.planlekcji.utils.BoyerMooreSearch;
import com.example.planlekcji.utils.DelayedSearchTextWatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class ReplacementsFragment extends Fragment {
    private View view;
    private List<List<Replacement>> replacements;
    private MainViewModel mainViewModel;

    // used for searching
    private List<String> replacementsWithoutHtml;
    private HashSet<Integer> replacementIdsToShow; // Ids of replacements that are supposed to be shown after being filtered by searching

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_replacements, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        observeAndHandleReplacementsLiveData();
        mainViewModel.fetchReplacements();

        setEventListenerToSearchBar();

        return view;
    }

    private void observeAndHandleReplacementsLiveData() {
        mainViewModel.getReplacementsLiveData().observe(getViewLifecycleOwner(), newReplacements -> {
            replacements = newReplacements;

            if (replacements == null || replacements.isEmpty()) return;

            // Prepare replacements without html for searching (also remove multiple spaces caused by removing html tags)
//            replacementsWithoutHtml = replacements.stream()
//                    .map(s -> s.replaceAll("<[^>]*>", "").replaceAll("\\s+", " "))
//                    .collect(Collectors.toList());

            updateReplacements();
        });
    }

    private void setEventListenerToSearchBar() {
        EditText searchBar = view.findViewById(R.id.editText_searchBar);
//
        searchBar.addTextChangedListener(new DelayedSearchTextWatcher(query -> {
//            replacementIdsToShow = searchReplacements(query.toLowerCase());
            updateReplacements();
        }));
    }

    private void updateReplacements() {
        TextView textFieldReplacements = view.findViewById(R.id.textView_replacements);
        EditText searchBar = view.findViewById(R.id.editText_searchBar);
        View divider = view.findViewById(R.id.divider);
        TextView textView_noResults = view.findViewById(R.id.textView_noResults);

        if(replacements == null || replacements.isEmpty() || areReplacementsEmpty()) {
//            searchBar.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);

            textFieldReplacements.setText(getString(R.string.no_replacements));
            return;
        }

        searchBar.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

//        if (replacementIdsToShow == null || replacementIdsToShow.isEmpty()) {
//            Log.d("adw", "222");
//
//            textView_noResults.setVisibility(replacementIdsToShow == null ? View.GONE : View.VISIBLE);
//            textFieldReplacements.setText(Html.fromHtml(replacements.toString(), Html.FROM_HTML_MODE_LEGACY));
//            return;
//        }

        StringBuilder displayedText = new StringBuilder();
        Date[] dates = ReplacementDataDownloader.getNext5Dates(); // holds next 5 non-weekend dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault());

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
//
//        List<String> filteredReplacements = replacements.stream()
//                .filter(rep -> replacementIdsToShow.contains(replacements.indexOf(rep)))
//                .collect(Collectors.toList());
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

    private HashSet<Integer> searchReplacements(String searchingKey) {
        if (searchingKey.isEmpty()) return null;

        BoyerMooreSearch boyerMooreSearch = new BoyerMooreSearch();
        HashSet<Integer> replacementIds = new HashSet<>();

        for (int i = 0; i < replacementsWithoutHtml.size(); i++) {
            if (boyerMooreSearch.search(replacementsWithoutHtml.get(i).toLowerCase(), searchingKey)) {
                replacementIds.add(i);
            }
        }

        return replacementIds;
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