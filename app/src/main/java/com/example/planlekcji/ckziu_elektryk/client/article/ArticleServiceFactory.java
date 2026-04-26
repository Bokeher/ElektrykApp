package com.example.planlekcji.ckziu_elektryk.client.article;

import com.example.planlekcji.ckziu_elektryk.client.Config;

public final class ArticleServiceFactory {

    public static ArticleService create(Config config) {
        return new ArticleServiceImpl(config);
    }
}
