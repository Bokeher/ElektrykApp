package com.example.planlekcji.ckziu_elektryk.client.article;

import com.example.planlekcji.ckziu_elektryk.client.utils.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Article {

    public static final SimpleDateFormat CREATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    private final int id;
    @SerializedName("creation_date")
    private final Date creationDate;
    private final String title;
    private final String content;
    @SerializedName("header_image_url")
    private final URL headerImageUrl;
    @SerializedName("photos_urls")
    private List<URL> photosURLs;

    public Article(int id, Date creationDate, String title, String content, URL headerImageUrl) {
        this.id = id;
        this.creationDate = creationDate;
        this.title = title;
        this.content = content;
        this.headerImageUrl = headerImageUrl;
    }


    public int getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public URL getHeaderImageUrl() {
        return headerImageUrl;
    }

    public List<URL> getPhotosURLs() {
        return photosURLs;
    }

    public void setPhotosURLs(List<URL> photosURLs) {
        this.photosURLs = photosURLs;
    }

    public static Date parsedDate(String text) {
        return DateUtil.parseDate(Article.CREATION_DATE_FORMAT, text);
    }

    public URL getHeaderImageUrl(PhotoSize photoSize) throws MalformedURLException {
        String imageUrlAsString = headerImageUrl.toString();

        int index = imageUrlAsString.lastIndexOf("-");
        int lastIndexOfDot = imageUrlAsString.lastIndexOf(".");
        String extension = imageUrlAsString.substring(lastIndexOfDot);
        String urlWithoutSize = imageUrlAsString.substring(0, index) + extension;

        String[] urlAsArray = urlWithoutSize.split("\\.");

        String size = "";

        if (photoSize != PhotoSize.SIZE_FULL) {
            size = "-" + photoSize.getName();
        }

        String newUrl = urlAsArray[0] + "." + urlAsArray[1] + "." + urlAsArray[2] + size + "." + urlAsArray[3];

        return new URL(newUrl);
    }
}
