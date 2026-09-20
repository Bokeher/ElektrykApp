package com.example.planlekcji.ckziu_elektryk.client.article;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.example.planlekcji.ckziu_elektryk.client.response.PaginatedSuccessResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArticleServiceImpl extends ClientService implements ArticleService {
    protected ArticleServiceImpl(Config config) {
        super(config);
    }

    @Override
    public Page<Article> getArticles(int page) {
        APIResponseCall apiResponseCall = getDataWithPagination(Endpoint.ARTICLES_LIST
                .withPlaceholders(Map.of("{page}", String.valueOf(page))));

        if (!apiResponseCall.hasResponse())
            return null;

        return apiResponseCall.error(handleError())
                .success(successResponse -> {
                    PaginatedSuccessResponse paginatedSuccessResponse = (PaginatedSuccessResponse) successResponse;

                    Page<LinkedTreeMap<String, Object>> successResponsePage = paginatedSuccessResponse.getPage();

                    List<Article> articles = successResponsePage.data().stream()
                            .map(datum -> new Article(
                                    (int) ((double) datum.get("id")),
                                    Article.parsedDate((String) datum.get("creation_date")),
                                    (String) datum.get("title"),
                                    (String) datum.get("content"),
                                    parseURL((String) datum.get("header_image_url")),
                                    null,
                                    parseURL((String) datum.get("url"))
                            ))
                            .collect(Collectors.toList());

                    return new Page<>(articles, successResponsePage.links(), successResponsePage.meta());
                });
    }

    private URL parseURL(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return new URL(text.trim());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public Optional<Article> getArticle(int id, PhotoSize photoSize) {
        if (id <= 0)
            throw new IllegalArgumentException("Id must be greater than zero");

        APIResponseCall apiResponseCall = getData(Endpoint.ARTICLE_BY_ID
                .withPlaceholders(Map.of("{id}", String.valueOf(id),
                        "{photo_size}", photoSize.getName())));

        if (!apiResponseCall.hasResponse()) return Optional.empty();

        return Optional.ofNullable(apiResponseCall
                .error(handleError())
                .success(successResponse -> {
                    JsonObject jsonObject = successResponse.getJsonElement().getAsJsonObject();

                    JsonArray jsonArray = jsonObject.has("photos_urls") && !jsonObject.get("photos_urls").isJsonNull()
                            ? jsonObject.get("photos_urls").getAsJsonArray()
                            : new JsonArray();

                    List<URL> photosUrls = jsonArray.asList().stream()
                            .map(jsonElement -> parseURL(jsonElement.getAsJsonObject().get("url").getAsString()))
                            .collect(Collectors.toList());

                    URL webUrl = (jsonObject.has("url") && !jsonObject.get("url").isJsonNull())
                            ? parseURL(jsonObject.get("url").getAsString()) : null;

                    URL headerImageUrl = (jsonObject.has("header_image_url") && !jsonObject.get("header_image_url").isJsonNull())
                            ? parseURL(jsonObject.get("header_image_url").getAsString()) : null;

                    return new Article(
                            jsonObject.get("id").getAsInt(),
                            Article.parsedDate(jsonObject.get("creation_date").getAsString()),
                            jsonObject.get("title").getAsString(),
                            jsonObject.get("content").getAsString(),
                            headerImageUrl,
                            photosUrls,
                            webUrl
                    );
                }));
    }
}
