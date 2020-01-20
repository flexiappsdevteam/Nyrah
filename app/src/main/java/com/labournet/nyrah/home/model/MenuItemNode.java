package com.labournet.nyrah.home.model;

import java.util.ArrayList;
import java.util.List;

public class MenuItemNode {
    private List<DrawerItem> childItems = null;
    private String parentName = null;
    private String parentID = null;
    private String linkURL;
    private String iconID;

    public MenuItemNode() {
        this.childItems = new ArrayList<>(50);
    }

    public void addChild(DrawerItem child) {
        childItems.add(child);
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getIconID() {
        return iconID;
    }

    public void setIconID(String iconID) {
        this.iconID = iconID;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public boolean hasChild() {
        return childItems.size() != 0;
    }

    public int getChildItemCount() {
        return childItems.size();
    }

    public DrawerItem getChildItem(int position) {
        return childItems.get(position);
    }
}
