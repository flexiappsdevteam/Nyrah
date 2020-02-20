package com.labournet.neo.nyrah.account.ui.SignUpActivity.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;

import androidx.annotation.Nullable;
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

    public static final int MOBILE_DATA_REQUEST_CODE = 1117;
    public static final int WIFI_REQUEST_CODE = 1118;

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

    Switch mobileDataSwitch;
    Switch wifiSwitch;

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

            //CREATE A REQUEST AND ADD DATA
            Request request = new Request.Builder()
                    .post(signUpFormBody)
                    .url(url)
                    .build();

            localOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //RETRY
                    session.signUpCompleted("NO");
                    session.businessAdded("NO");
                    session.userAdded("NO");

                    Log.i("SendRegistrationData", url.toString());

                    //CHECK INTERNET CONNECTION

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showNoInternetDialog();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Log.i("SendRegistrationData", url.toString());

                    //GET RESPONSE
                    String responseSendSignUpData = response.body().string();

                    if (userRegistration) {
                        session.userAdded("YES");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addUserFragment2.onSaveUserTitleChange("User added");
                            }
                        });
                    } else if (userAndBusinessRegistration) {
                        session.userAdded("YES");
                        session.businessAdded("YES");
                        session.signUpCompleted("YES");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addUserFragment2.onSaveUserTitleChange("Business and User registration completed");
                            }
                        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOBILE_DATA_REQUEST_CODE || requestCode == WIFI_REQUEST_CODE) {
            if (checkNetworkConnection()) {

            }
        }
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void showNoInternetDialog() {

        //before inflating the custom alert dialog layout, we will get the current activity viewGroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.no_internet_dialog, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        mobileDataSwitch = dialogView.findViewById(R.id.mobile_data_switch);
        wifiSwitch = dialogView.findViewById(R.id.wifi_switch);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        mobileDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startActivityForResult(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS), MOBILE_DATA_REQUEST_CODE);
                alertDialog.hide();
            }
        });

        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), WIFI_REQUEST_CODE);
                alertDialog.hide();
            }
        });

    }
}
