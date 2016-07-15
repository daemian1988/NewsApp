package com.example.android.newsapp;

/**
 * Created by Daemian on 3/7/2016.
 */

public class NewsDetails {

    private String title;
    private String imageLink;
    private String hyperLink;

    public NewsDetails(String title, String imageLink, String hyperLink) {
        this.title = title;
        this.imageLink = imageLink;
        this.hyperLink = hyperLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getHyperLink() {
        return hyperLink;
    }

    public String getTitle() {
        return title;
    }


}
