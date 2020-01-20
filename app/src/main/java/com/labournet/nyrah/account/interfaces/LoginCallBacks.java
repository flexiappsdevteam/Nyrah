package com.labournet.nyrah.account.interfaces;

import com.labournet.nyrah.account.model.Login;

//*
// Login callbacks for
// PhoneNumber login fragment
// LoginPIN fragment *
// *//

public interface LoginCallBacks {

    void onPhoneNumberOKLoginCallBack(String phoneNumber);

    void onLogin(Login login);

    void onSignUp();

    void resetApp();

    void onSuccessPINLoginCallBack();

}
