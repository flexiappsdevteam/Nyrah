package com.labournet.nyrah.home.model;

public class DrawerItem {
    String title;
    String parentName;
    int imgResID;
    String linkURL;
    String itemID;


    public DrawerItem(String title, String parentName, String linkURL) {
        this.title = title;
        this.parentName = parentName;
        imgResID = 0;
        this.linkURL = linkURL;
    }

    public DrawerItem(String title, String parentName, String linkURL, String itemID) {
        this.title = title;
        this.parentName = parentName;
        imgResID = 0;
        this.linkURL = linkURL;
        this.itemID = itemID;
    }

    public void setImgResID(int imgResID) {
        this.imgResID = imgResID;
    }

    public String getTitle() {
        return title;
    }

    public int getImgResID() {
        return imgResID;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
