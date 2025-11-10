package com.example.planlekcji.fragments.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementChange;
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
        this.mainViewModel.fetchReplacements();

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
            CardView dayCard = (CardView) inflater.inflate(R.layout.replacement_day_card, layout, false);
            TextView dayTitle = dayCard.findViewById(R.id.textView_dayTitle);
            dayTitle.setText(R.string.no_replacements);

            layout.addView(dayCard);

            return;
        }

        Date[] dates = ReplacementDataDownloader.getNext5Dates(); // holds next 5 non-weekend dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd EEEE", Locale.getDefault());

        for (int i = 0; i < replacements.size(); i++) {
            List<Replacement> dayReplacements = replacements.get(i);
            if(dayReplacements.isEmpty()) continue;

            CardView dayCard = (CardView) inflater.inflate(R.layout.replacement_day_card, layout, false);
            LinearLayout dayCardLayout = dayCard.findViewById(R.id.replacementDay_layout);

            TextView dayTitle = dayCard.findViewById(R.id.textView_dayTitle);
            dayTitle.setText(sdf.format(dates[i]));

            for (Replacement replacement : dayReplacements) {
                CardView replacementCard = (CardView) inflater.inflate(R.layout.replacement_card, dayCardLayout, false);
                TextView replacementTitle = replacementCard.findViewById(R.id.textView_replacementTitle);
                TextView replacementDetails = replacementCard.findViewById(R.id.textView_replacementDetails);

                replacementTitle.setText(replacement.name());

                StringBuilder details = getReplacementDetails(replacement);
                replacementDetails.setText(details.toString());

                dayCardLayout.addView(replacementCard);
            }

            layout.addView(dayCard);
        }
    }

    private static @NonNull StringBuilder getReplacementDetails(Replacement replacement) {
        List<ReplacementChange> changes = replacement.changes();
        StringBuilder stringBuilder = new StringBuilder();
        for (ReplacementChange change : changes) {
            stringBuilder.append(change.period()).append(" | ").append(change.info()).append("\n");
        }

        if (stringBuilder.length() > 0 && stringBuilder.charAt(stringBuilder.length() - 1) == '\n') {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder;
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