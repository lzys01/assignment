package com.example.myapplication;

import java.util.List;

public class SocketBean {
    private List<Video> feeds;
    private Boolean success;

    public List<Video> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Video> feeds) {
        this.feeds = feeds;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
