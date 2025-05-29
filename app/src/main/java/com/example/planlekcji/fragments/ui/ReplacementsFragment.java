package com.example.planlekcji.fragments.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.utils.BoyerMooreSearch;
import com.example.planlekcji.utils.DelayedSearchTextWatcher;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ReplacementsFragment extends Fragment {
    private View view;
    private List<String> replacements;
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

            // Prepare replacements without html for searching (also remove multiple spaces caused by removing html tags)
            replacementsWithoutHtml = replacements.stream()
                    .map(s -> s.replaceAll("<[^>]*>", "").replaceAll("\\s+", " "))
                    .collect(Collectors.toList());

            updateReplacements();
        });
    }

    private void setEventListenerToSearchBar() {
        EditText searchBar = view.findViewById(R.id.editText_searchBar);

        searchBar.addTextChangedListener(new DelayedSearchTextWatcher(query -> {
            replacementIdsToShow = searchReplacements(query.toLowerCase());
            updateReplacements();
        }));
    }

    private void updateReplacements() {
        TextView textFieldReplacements = view.findViewById(R.id.textView_replacements);
        EditText searchBar = view.findViewById(R.id.editText_searchBar);
        View divider = view.findViewById(R.id.divider);
        TextView textView_noResults = view.findViewById(R.id.textView_noResults);

        if(replacements == null || replacements.isEmpty()) {
            searchBar.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);

            textFieldReplacements.setText(getString(R.string.no_replacements));
            return;
        }

        searchBar.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);

        if (replacementIdsToShow == null || replacementIdsToShow.isEmpty()) {
            textView_noResults.setVisibility(replacementIdsToShow == null ? View.GONE : View.VISIBLE);
            textFieldReplacements.setText(Html.fromHtml(String.join("<br><br>", replacements), Html.FROM_HTML_MODE_LEGACY));
            return;
        }

        List<String> filteredReplacements = replacements.stream()
                .filter(rep -> replacementIdsToShow.contains(replacements.indexOf(rep)))
                .collect(Collectors.toList());

        textFieldReplacements.setText(Html.fromHtml(String.join("<br><br>", filteredReplacements), Html.FROM_HTML_MODE_LEGACY));
        textView_noResults.setVisibility(View.GONE);
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
}