package com.example.planlekcji.ckziu_elektryk.client.article;

import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;

import java.util.Optional;

public interface ArticleService {

    Page<Article> getArticles(int page);

    default Page<Article> getArticles() {
        return getArticles(1);
    }

    Optional<Article> getArticle(int id, PhotoSize photoSize);

    default Optional<Article> getArticle(int id) {
        return getArticle(id, PhotoSize.SIZE_FULL);
    }
}
