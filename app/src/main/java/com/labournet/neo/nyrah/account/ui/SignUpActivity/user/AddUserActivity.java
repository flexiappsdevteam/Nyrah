package com.labournet.neo.nyrah.account.ui.SignUpActivity.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.labournet.neo.nyrah.R;
import com.labournet.neo.nyrah.Utils.BaseActivity;
import com.labournet.neo.nyrah.Utils.UserSessionManager;
import com.labournet.neo.nyrah.account.interfaces.SignUpAddUserCallBacks;
import com.labournet.neo.nyrah.account.interfaces.SignUpCallBacks;
import com.labournet.neo.nyrah.account.model.Business;
import com.labournet.neo.nyrah.account.model.User;
import com.labournet.neo.nyrah.account.ui.LoginActivity.LoginActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static com.labournet.neo.nyrah.Utils.Constants.BASEURL;
import static com.labournet.neo.nyrah.Utils.Constants.PROJECT;
import static com.labournet.neo.nyrah.Utils.Constants.VERIFY;

public class AddUserActivity extends BaseActivity implements SignUpAddUserCallBacks {

    FrameLayout addUserFragmentC;
    AddUserFragment addUserFragment2;
    String phNo = "";
    String OTP;
    boolean userRegistration = false;
    boolean userAndBusinessRegistration = false;
    String countryCodeValue;
    String responseOTP;
    User newUser;
    Business newBusiness;
    UserSessionManager userSession;
    String addUserValue = "no";
    private SignUpCallBacks signUpCallBacks;
    UserSessionManager session = null;
    private boolean confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        addUserFragmentC = findViewById(R.id.add_user_container2);
        userSession = new UserSessionManager(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            phNo = bundle.getString("mobNo", "");
            addUserValue = bundle.getString("addUser", "");

        }
        if (phNo.equals(""))
            addUserFragment2 = new AddUserFragment(getPhoneNumberFromDevice());
        else addUserFragment2 = new AddUserFragment(phNo);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.add_user_container2, addUserFragment2).commit();


    }

    @Override
    public void onSignUpUser(User user) {
        newUser = new User();
        newUser = user;
        newBusiness = session.getBusinessInfo();
        sendRegistrationData(newBusiness, newUser);
        addUserFragment2.onSaveUserTitleChange("User added");

        //To get and save the mobile number at login page
        user.setUserMobileNumber("");
        userSession.UserLoginSession(user, "PROJECT");
    }

    public void sendRegistrationData(Business newBusiness, User newUser) {
        try {

            //CREATE URL
            final HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(BASEURL)
                    .addPathSegment(PROJECT)
                    .addPathSegment(VERIFY)
                    .build();


            final RequestBody signUpFormBody;

            //CREATE FORM_BODY WITH DATA
            if (session.isBusinessAdded().equals("YES") || addUserValue.equals("YES")) {

                signUpFormBody = new FormBody.Builder()
                        .add("proj", session.getProjectID())
                        .add("saveU", "Y")
                        .add("user_name", newUser.getUserName())
                        .add("user_mob", newUser.getUserMobileNumber())
                        .add("user_email", newUser.getEmailAddress())
                        .add("user_addr", newUser.getPostalAddress())
                        .add("user_pin", newUser.getPIN())

                        .build();

                userRegistration = true;

            } else {

                Log.e("TYPE", newBusiness.getBusinessTypeName() + "\n\n" +
                        newBusiness.getBusinessTypeID());

//                showToast("no business");
                signUpFormBody = new FormBody.Builder()
                        .add("proj", session.getProjectID())
                        .add("saveB", "Y")
                        .add("saveU", "Y")
                        .add("bName", newBusiness.getName())
                        .add("bType", newBusiness.getBusinessTypeID())
                        .add("email", newBusiness.getEmailAddress())
                        .add("mob", newBusiness.getPhoneNumber())
                        .add("addr", newBusiness.getPostalAddress())
                        .add("webAddr", newBusiness.getWebAddress())

                        .add("user_name", newUser.getUserName())
                        .add("user_mob", newUser.getUserMobileNumber())
                        .add("user_email", newUser.getEmailAddress())
                        .add("user_addr", newUser.getPostalAddress())
                        .add("user_pin", newUser.getPIN())

                        .build();

                userAndBusinessRegistration = true;

            }


//        getMenu();

            //CREATE A REQUEST AND ADD DATA
            Request request = new Request.Builder()
                    .post(signUpFormBody)
                    .url(url)
                    .build();

            localOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //RETRY
                    showToast("SignUp failed !");
                    session.signUpCompleted("NO");
                    session.businessAdded("NO");
                    session.userAdded("NO");

                    Log.i("DDDD", url.toString());

                    //CHECK INTERNET CONNECTION
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Log.i("DDDD", url.toString());

                    //GET RESPONSE
                    String responseSendSignUpData = response.body().string();

                    if (userRegistration) {
                        session.userAdded("YES");
                    } else if (userAndBusinessRegistration) {
                        session.userAdded("YES");
                        session.businessAdded("YES");
                        session.signUpCompleted("YES");
                    }

                    //NEXT INTENT
                    Intent intent = new Intent(AddUserActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            session.clearAll();
        }
    }

    @Override
    public void onSendOTPSignUp(String phoneNumber) {
        String msg = isPhoneNumberValid(phoneNumber) ? null : "Not a valid phone number";
        if (msg != null) showToast(msg);
        verifyPhoneNumberWithOTP(phoneNumber);
        addUserFragment2.showVerifyButton();
    }

    @Override
    public void onBackPressed() {
        if (!confirm) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Html.fromHtml(getResources().getString(R.string.exit_message_signUp_form_cdata)))
                    .setIcon(R.drawable.ic_exit)
                    .setTitle(R.string.exit_application)
                    .setPositiveButton(Html.fromHtml(getResources().getString(R.string.exit_cdata)), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            confirm = true;
                            onBackPressed();
                        }
                    })
                    .setNegativeButton(Html.fromHtml(getResources().getString(R.string.cancel_cdata)), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            confirm = false;
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            super.onBackPressed();
            finish();
            return;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.length() == 10;
    }

    public void verifyPhoneNumberWithOTP(String phoneNumber) {


        HttpUrl otpURL = new HttpUrl.Builder()
                .scheme("https")
                .host(BASEURL)
                .addPathSegment(PROJECT)
                .addPathSegment(VERIFY)
                .addQueryParameter("otp", "Y")
                .addQueryParameter("mob", phoneNumber)
                .build();

        Log.i("OTPVerification", otpURL.toString());

        final okhttp3.Request otpRequest = new okhttp3.Request.Builder()
                .url(otpURL)
                .build();

        localOkHttpClient.newCall(otpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                responseOTP = response.body().string();

                if (getTagValue(responseOTP, "Error").contains("Not a valid phone number")) {
                    showToast("phoneNumber error");

                } else {

                    //Get otp from XML data.
                    OTP = getTagValue(responseOTP, "otp");

                    //Get the otp received in device.

                    //Compare OTPs.

                }
            }
        });
    }

    @Override
    public void onVerifyOTPSignUp(String otp) {
        if (otp.equals(OTP)) {
            closeKeyBoard();
            addUserFragment2.onSuccessOTPVerification();
        } else
            addUserFragment2.onFailureOTPVerification();
    }


    @SuppressLint("HardwareIds")
    public String getPhoneNumberFromDevice() {
        String phoneNumber = "";

        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = telephonyManager != null ? telephonyManager.getLine1Number() : null;
            if ((phoneNumber != null ? phoneNumber.length() : 0) == 12)
                phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
            if ((phoneNumber != null ? phoneNumber.length() : 0) == 11)
                phoneNumber = phoneNumber.substring(phoneNumber.length() - 11);
            countryCodeValue = telephonyManager != null ? telephonyManager.getNetworkCountryIso() : null;
        } else {
//            showToast("Permission not granted");
        }

        return phoneNumber;
    }
}
