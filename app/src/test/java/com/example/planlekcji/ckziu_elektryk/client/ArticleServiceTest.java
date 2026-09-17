package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.planlekcji.ckziu_elektryk.client.article.Article;
import com.example.planlekcji.ckziu_elektryk.client.article.ArticleService;
import com.example.planlekcji.ckziu_elektryk.client.article.PhotoSize;
import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStubFactory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;

public class ArticleServiceTest {

    private ArticleService articleService;

    @Before
    public void init() throws IOException {
        CKZiUElektrykClient client = CKZiUElektrykClientStubFactory.createClient();

        articleService = client.getArticleService();
    }

    @Test
    public void shouldRespondArticlesWithPagination() {
        Page<Article> page = articleService.getArticles();

        assertNotNull(page);
        assertNotNull(page.data());
        assertNotNull(page.links());
        assertNotNull(page.meta());
        assertEquals(1, page.meta().currentPage());
        assertEquals(5, page.meta().perPage());
    }

    @Test
    public void shouldRespondArticlesWithPaginationWithPageTwo() {
        Page<Article> page = articleService.getArticles(2);

        assertNotNull(page);
        assertNotNull(page.data());
        assertNotNull(page.links());
        assertNotNull(page.meta());
        assertEquals(2, page.meta().currentPage());
        assertEquals(5, page.meta().perPage());
    }

    @Test
    public void shouldGetArticleById() throws MalformedURLException {
        Optional<Article> articleOptional = articleService.getArticle(5);

        if (articleOptional.isPresent()) {
            Article article = articleOptional.get();

            assertNotNull(article.creationDate());
            assertNotNull(article.content());
            assertTrue(article.id() >= 1);
            assertNotNull(article.photosURLs());
            assertNotNull(article.title());
            assertNotNull(article.getHeaderImageUrl(PhotoSize.SIZE_FULL));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenIdIsGreaterThanZero() {
        articleService.getArticle(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenIdIsZero() {
        articleService.getArticle(0);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenResourceNotFound() {
        Optional<Article> articleOptional = articleService.getArticle(1_000_000);

        assertFalse(articleOptional.isPresent());
    }

    @Test
    public void shouldUseThePhotoSizeForHeaderImageURLNumericValue() throws MalformedURLException {
        Article article = new Article(1, new Date(), "", "",
                new URL("https://www.ckziu-elektryk.pl/wp-content/uploads/2025/05/Logo_UE_RGB_UE_Dofinansowane_RGB-1-340x242.png"));

        URL headerImageUrl = article.getHeaderImageUrl(PhotoSize.SIZE_100x75);

        assertEquals("https://www.ckziu-elektryk.pl/wp-content/uploads/2025/05/Logo_UE_RGB_UE_Dofinansowane_RGB-1-100x75.png", headerImageUrl.toString());
    }

    @Test
    public void shouldUseThePhotoSizeForHeaderImageURLFullValue() throws MalformedURLException {
        Article article = new Article(1, new Date(), "", "",
                new URL("https://www.ckziu-elektryk.pl/wp-content/uploads/2025/05/Logo_UE_RGB_UE_Dofinansowane_RGB-1-340x242.png"));

        URL headerImageUrl = article.getHeaderImageUrl(PhotoSize.SIZE_FULL);

        assertEquals("https://www.ckziu-elektryk.pl/wp-content/uploads/2025/05/Logo_UE_RGB_UE_Dofinansowane_RGB-1.png", headerImageUrl.toString());
    }

    @Test
    public void shouldGenerateCorrectWebUrlFromDateAndTitle() {
        Date date = Article.parsedDate("2026-09-15 08:03:14");
        Article article = new Article(19664, date, "Integracja klas pierwszych w CKZiU „Elektryk”", "", null);

        assertEquals("https://www.ckziu-elektryk.pl/2026/09/integracja-klas-pierwszych-w-ckziu-elektryk/#main", article.getWebUrl());
    }

    @Test
    public void shouldGenerateCorrectWebUrlFor80Lecie() {
        Date date = Article.parsedDate("2026-09-10 12:00:00");
        Article article = new Article(19665, date, "PROGRAM OBCHODÓW 80-LECIA SZKOŁY", "", null);

        assertEquals("https://www.ckziu-elektryk.pl/2026/09/program-obchodow-80-lecia-szkoly/#main", article.getWebUrl());
    }

    @Test
    public void shouldPreferExplicitUrlIfPresent() throws MalformedURLException {
        Date date = Article.parsedDate("2026-09-10 12:00:00");
        URL explicitUrl = new URL("https://www.ckziu-elektryk.pl/custom-url/");
        Article article = new Article(1, date, "Test", "", null, null, explicitUrl);

        assertEquals("https://www.ckziu-elektryk.pl/custom-url/#main", article.getWebUrl());
    }

    @Test
    public void shouldGenerateCorrectWebUrlWithSlashAndQuotes() {
        Date date = Article.parsedDate("2026-09-01 09:00:00");
        Article article = new Article(19666, date, "Rozpoczęcie roku szkolnego 2026/2027 w CKZiU „Elektryk”", "", null);

        assertEquals("https://www.ckziu-elektryk.pl/2026/09/rozpoczecie-roku-szkolnego-2026-2027-w-ckziu-elektryk/#main", article.getWebUrl());
    }
}
