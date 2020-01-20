package com.labournet.nyrah.account.interfaces;

import com.labournet.nyrah.account.model.Business;
import com.labournet.nyrah.account.model.User;

//**
// SignUp callBacks for
// AddUserFragment
// BusinessRegFragment
// ProductKeyFragment**//

public interface SignUpCallBacks {

    void onSignUpUser(User user);

    void onSendOTPSignUp(String phoneNumber);

    void onVerifyOTPSignUp(String otp);

    void onShowBusinessRegFields();

    void onConfirmBusinessReg(Business business);

    void onAddUserSignUp();

    void onVerifyProductKey(String productKey);

    void onInvalidProductKey();

}
