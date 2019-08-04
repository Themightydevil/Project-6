package com.example.project6;

public class News {
    private String mHeadline;
    private String mSection;
    private String mDate;
    private String mUrl;
    private String mAuthor;

    public News(String headline, String section, String date, String url, String author) {

        mHeadline = headline;
        mSection = section;
        mDate = date;
        mUrl = url;
        mAuthor = author;
    }

    public String getDate() {
        return mDate;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getSection() {
        return mSection;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }
}
