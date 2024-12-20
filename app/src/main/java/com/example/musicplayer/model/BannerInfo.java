package com.example.musicplayer.model;

public class BannerInfo {
    private int id;
    private String bannerImgUrl;

    public BannerInfo(int id, String bannerImgUrl) {
        this.id = id;
        this.bannerImgUrl = bannerImgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBannerImgUrl() {
        return bannerImgUrl;
    }

    public void setBannerImgUrl(String bannerImgUrl) {
        this.bannerImgUrl = bannerImgUrl;
    }
}
