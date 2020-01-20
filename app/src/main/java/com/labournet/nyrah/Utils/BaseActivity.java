package com.labournet.nyrah.Utils;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.labournet.nyrah.R;

import java.util.ArrayList;

import okhttp3.OkHttpClient;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CALL_PRIVILEGED;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SYNC_SETTINGS;
import static android.Manifest.permission.RECEIVE_BOOT_COMPLETED;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_SYNC_SETTINGS;

public abstract class BaseActivity extends AppCompatActivity {

    OkHttpSingleton okHttpSingleton = OkHttpSingleton.getInstance();
    public OkHttpClient localOkHttpClient;
    UserSessionManager userSessionManager;


    String mResponse;
    String XML_DATA;

    String countryCodeValue;

    public Dialog progress_spinner;

    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils = new PermissionUtils(BaseActivity.this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpSingleton.closeConnections();
        localOkHttpClient = okHttpSingleton.getClient();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.accent));
        }

    }

    @Override
    public void setContentView(int layoutResID) {
        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(layoutResID);
        userSessionManager = new UserSessionManager(getApplicationContext());

        progress_spinner = LoadingSpinner(BaseActivity.this);


    }

    public static Dialog LoadingSpinner(Context mContext) {
        Dialog pd = new Dialog(mContext, android.R.style.Theme_Black);
        View view = LayoutInflater.from(mContext).inflate(R.layout.loader_spinner, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pd.setTitle("Loading..");
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view);
        return pd;
    }


    public String getTagValue(String xml, String tagName) {
        try {
            return xml.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
        } catch (Exception e) {
            return "";
        }
    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void closeKeyBoard() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.length() == 10;
    }

    public String getCountryCode() {
        String countryCode = "";
        String locale = getApplicationContext().getResources().getConfiguration().locale.getCountry();
        return countryCode;
    }

    public ArrayList<String> addPermissions() {

        permissions.add(INTERNET);
        permissions.add(ACCESS_NETWORK_STATE);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(READ_PHONE_NUMBERS);
        }
        permissions.add(READ_PHONE_STATE);
        permissions.add(RECEIVE_BOOT_COMPLETED);
        permissions.add(READ_SYNC_SETTINGS);
        permissions.add(WRITE_SYNC_SETTINGS);

        permissions.add(READ_CALL_LOG);
        permissions.add(WRITE_CALL_LOG);
        permissions.add(CALL_PHONE);
        permissions.add(CALL_PRIVILEGED);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(Manifest.permission.MANAGE_OWN_CALLS);
        }

        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WAKE_LOCK);
        permissions.add(Manifest.permission.DISABLE_KEYGUARD);

        return permissions;

    }

    public void checkPermissions() {
        permissionUtils.check_permission(addPermissions(), "You need to allow all permissions to use all app features", 1);
    }

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
            showToast("Permission not granted");
        }

        return phoneNumber;
    }

    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }
}
