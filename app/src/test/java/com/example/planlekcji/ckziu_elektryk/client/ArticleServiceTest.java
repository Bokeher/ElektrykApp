package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.planlekcji.ckziu_elektryk.client.article.Article;
import com.example.planlekcji.ckziu_elektryk.client.article.ArticleService;
import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class ArticleServiceTest {

    private ArticleService articleService;

    @Before
    public void init() {
        Config config = mock(Config.class);

        when(config.getAPIUrl()).thenReturn(TestConstants.URL);
        when(config.getToken()).thenReturn(TestConstants.TOKEN);

        CKZiUElektrykClient client = new CKZiUElektrykClient(config);

        articleService = client.getArticleService();
    }

    @Test
    public void shouldRespondArticlesWithPagination() {
        Page<Article> page = articleService.getArticles();

        assertNotNull(page);
        assertNotNull(page.data());
        assertNotNull(page.links());
        assertNotNull(page.links());
        assertNotNull(page.meta());
        assertEquals(page.meta().currentPage(), 1);
        assertEquals(page.meta().lastPage(), 2);
        assertEquals(page.meta().perPage(), 5);
    }

    @Test
    public void shouldRespondArticlesWithPaginationWithPageTwo() {
        Page<Article> page = articleService.getArticles(2);

        assertNotNull(page);
        assertNotNull(page.data());
        assertNotNull(page.links());
        assertNotNull(page.links());
        assertNotNull(page.meta());
        assertEquals(page.meta().currentPage(), 2);
        assertEquals(page.meta().lastPage(), 2);
        assertEquals(page.meta().perPage(), 5);
    }

    @Test
    public void shouldGetArticleById() {
        Optional<Article> articleOptional = articleService.getArticle(5);

        if (articleOptional.isPresent()) {
            Article article = articleOptional.get();

            assertNotNull(article.getCreationDate());
            assertNotNull(article.getContent());
            assertTrue(article.getId() >= 1);
            assertNotNull(article.getPhotosURLs());
            assertNotNull(article.getTitle());
            assertNotNull(article.getHeaderImageUrl());
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

    @Test()
    public void shouldReturnEmptyOptionalWhenResourceNotFound() {
        Optional<Article> articleOptional = articleService.getArticle(1);

        assertFalse(articleOptional.isPresent());
    }
}
