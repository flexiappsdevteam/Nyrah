package com.labournet.neo.nyrah.account.interfaces;


import com.labournet.neo.nyrah.account.model.User;

public interface SignUpAddUserCallBacks {
    void onSignUpUser(User user);

    void onSendOTPSignUp(String phoneNumber);

    void onVerifyOTPSignUp(String otp);
}
