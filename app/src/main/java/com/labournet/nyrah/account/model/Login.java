package com.labournet.nyrah.account.model;

public class Login {

    private String mobileNumber;
    private String PIN;

    public Login(String mobileNumber, String PIN) {
        this.mobileNumber = mobileNumber;
        this.PIN = PIN;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    @Override
    public String toString() {
        return "Login{" +
                "mobileNumber='" + mobileNumber + '\'' +
                ", PIN='" + PIN + '\'' +
                '}';
    }
}
