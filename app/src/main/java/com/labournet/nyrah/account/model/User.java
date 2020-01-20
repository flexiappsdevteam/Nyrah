package com.labournet.nyrah.account.model;

public class User {

    String userName;
    String userMobileNumber;
    String emailAddress;
    String postalAddress;
    String PIN;

    public User(String userName, String userMobileNumber) {
        this.userName = userName;
        this.userMobileNumber = userMobileNumber;
    }

    public User() {
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public void setUserMobileNumber(String userMobileNumber) {
        this.userMobileNumber = userMobileNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userMobileNumber='" + userMobileNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", postalAddress='" + postalAddress + '\'' +
                ", PIN='" + PIN + '\'' +
                '}';
    }
}
