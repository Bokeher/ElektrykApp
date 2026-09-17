package com.example.planlekcji.ckziu_elektryk.client.article;

import com.example.planlekcji.ckziu_elektryk.client.utils.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public record Article(
        int id,
        @SerializedName("creation_date") Date creationDate,
        String title,
        String content,
        @SerializedName("header_image_url") URL headerImageUrl,
        @SerializedName("photos_urls") List<URL> photosURLs,
        @SerializedName("url") URL url
) {

    public static final SimpleDateFormat CREATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    public Article(int id, Date creationDate, String title, String content, URL headerImageUrl) {
        this(id, creationDate, title, content, headerImageUrl, null, null);
    }

    public Article(int id, Date creationDate, String title, String content, URL headerImageUrl, List<URL> photosURLs) {
        this(id, creationDate, title, content, headerImageUrl, photosURLs, null);
    }

    public static Date parsedDate(String text) {
        return DateUtil.parseDate(Article.CREATION_DATE_FORMAT, text);
    }

    public String getWebUrl() {
        if (url != null) {
            String urlStr = url.toString();
            return urlStr.contains("#") ? urlStr : (urlStr.endsWith("/") ? urlStr + "#main" : urlStr + "/#main");
        }
        if (creationDate != null && title != null && !title.trim().isEmpty()) {
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy/MM", Locale.ROOT);
            String yearMonth = yearMonthFormat.format(creationDate);
            String slug = generateSlug(title);
            if (!slug.isEmpty()) {
                return "https://www.ckziu-elektryk.pl/" + yearMonth + "/" + slug + "/#main";
            }
        }
        return "https://www.ckziu-elektryk.pl/#main";
    }

    public static String generateSlug(String text) {
        if (text == null) return "";
        String normalized = text
                .replace('ł', 'l') // normalizer doesn't handle this letter
                .replace('Ł', 'l')
                .replace("\"", "")
                .replace("'", "")
                .replace("„", "")
                .replace("”", "")
                .replace("“", "")
                .replace("’", "")
                .replace('/', '-')
                .replace('\\', '-')
                .replace('_', '-');

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("[\\s-]+", "-");
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

    public int getId() {
        return id();
    }

    public String getContent() {
        return content();
    }

}
