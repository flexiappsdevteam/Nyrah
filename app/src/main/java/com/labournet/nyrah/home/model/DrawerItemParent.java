package com.labournet.nyrah.home.model;

import java.util.ArrayList;

public class DrawerItemParent {

    String parentItemName;

    ArrayList<DrawerSubItem> drawerSubItemArrayList;

    public DrawerItemParent(String parentItemName, ArrayList<DrawerSubItem> drawerSubItemArrayList) {
        this.parentItemName = parentItemName;
        this.drawerSubItemArrayList = drawerSubItemArrayList;
    }

    public String getParentItemName() {
        return parentItemName;
    }

    public void setParentItemName(String parentItemName) {
        this.parentItemName = parentItemName;
    }

    public ArrayList<DrawerSubItem> getDrawerSubItemArrayList() {
        return drawerSubItemArrayList;
    }

    public void setDrawerSubItemArrayList(ArrayList<DrawerSubItem> drawerSubItemArrayList) {
        this.drawerSubItemArrayList = drawerSubItemArrayList;
    }

}
