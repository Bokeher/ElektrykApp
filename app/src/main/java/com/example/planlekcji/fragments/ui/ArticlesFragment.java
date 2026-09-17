package com.example.planlekcji.fragments.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.planlekcji.MainViewModel;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.article.Article;
import com.example.planlekcji.utils.EmptyStateHelper;
import com.example.planlekcji.utils.EmptyStateType;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ArticlesFragment extends Fragment {
    private MainViewModel mainViewModel;
    private LinearLayout articlesContainer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        articlesContainer = view.findViewById(R.id.linearLayout_articles);

        articlesContainer.addView(EmptyStateHelper.create(LayoutInflater.from(requireContext()), articlesContainer, EmptyStateType.ARTICLES));

        observeArticlesData();

        return view;
    }

    private void observeArticlesData() {
        mainViewModel.getArticlesLiveData().observe(getViewLifecycleOwner(), this::updateArticlesList);
    }

    private void updateArticlesList(List<Article> articles) {
        articlesContainer.removeAllViews();

        if (articles == null || articles.isEmpty()) {
            articlesContainer.addView(EmptyStateHelper.create(LayoutInflater.from(requireContext()), articlesContainer, EmptyStateType.ARTICLES));
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (Article article : articles) {
            View cardView = inflater.inflate(R.layout.article_card, articlesContainer, false);

            ImageView imageViewHeader = cardView.findViewById(R.id.imageView_articleHeader);
            TextView textViewTitle = cardView.findViewById(R.id.textView_articleTitle);
            TextView textViewDate = cardView.findViewById(R.id.textView_articleDate);
            TextView textViewSnippet = cardView.findViewById(R.id.textView_articleSnippet);

            textViewTitle.setText(article.title());

            if (article.creationDate() != null) {
                textViewDate.setText(dateFormat.format(article.creationDate()));
            } else {
                textViewDate.setVisibility(View.GONE);
            }

            if (article.content() != null) {
                String plainText = Html.fromHtml(article.content(), Html.FROM_HTML_MODE_LEGACY).toString().trim();
                textViewSnippet.setText(plainText);
            } else {
                textViewSnippet.setVisibility(View.GONE);
            }

            if (article.headerImageUrl() != null) {
                imageViewHeader.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(article.headerImageUrl().toString())
                        .placeholder(R.drawable.image_placeholder)
                        .into(imageViewHeader);
            }

            cardView.setOnClickListener(v -> openArticleInBrowser(article));
            articlesContainer.addView(cardView);
        }
    }

    private void openArticleInBrowser(Article article) {
        if (article == null || getContext() == null) return;

        String url = article.getWebUrl();
        Log.d("ArticlesFragment", "Opening article URL: " + url + " (title: " + article.title() + ")");
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build();
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(requireContext(), R.string.error_cannot_open_link, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
