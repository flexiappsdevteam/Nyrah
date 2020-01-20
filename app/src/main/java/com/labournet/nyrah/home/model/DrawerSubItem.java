package com.labournet.nyrah.home.model;

public class DrawerSubItem {

    String subItemTitle;
    String iconID;
    String subItemLinkURL;

    public DrawerSubItem(String subItemTitle, String iconID, String subItemLinkURL) {
        this.subItemTitle = subItemTitle;
        this.iconID = iconID;
        this.subItemLinkURL = subItemLinkURL;
    }


    public String getSubItemTitle() {
        return subItemTitle;
    }

    public void setSubItemTitle(String subItemTitle) {
        this.subItemTitle = subItemTitle;
    }

    public String getIconID() {
        return iconID;
    }

    public void setIconID(String iconID) {
        this.iconID = iconID;
    }

    public String getSubItemLinkURL() {
        return subItemLinkURL;
    }

    public void setSubItemLinkURL(String subItemLinkURL) {
        this.subItemLinkURL = subItemLinkURL;
    }

    @Override
    public String toString() {
        return "DrawerSubItem{" +
                "subItemTitle='" + subItemTitle + '\'' +
                ", iconID='" + iconID + '\'' +
                ", subItemLinkURL='" + subItemLinkURL + '\'' +
                '}';
    }
}
