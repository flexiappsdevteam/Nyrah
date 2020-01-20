package com.labournet.nyrah.account.model;

public class Business {
    String businessID;
    String name;
    BusinessType type;
    String phoneNumber;
    String emailAddress;
    String webAddress;
    String postalAddress;
    String adminName;

    public Business(String name, BusinessType type, String phoneNumber, String emailAddress, String webAddress, String postalAddress) {
        this.name = name;
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.webAddress = webAddress;
        this.postalAddress = postalAddress;
    }

    public Business() {
    }

    public String getBusinessID() {
        return businessID;
    }

    public void setBusinessID(String businessID) {
        this.businessID = businessID;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type.name;
    }

    public void setType(BusinessType type) {
        this.type = type;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    @Override
    public String toString() {
        return "Business{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", webAddress='" + webAddress + '\'' +
                '}';
    }
}
