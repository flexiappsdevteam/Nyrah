package com.labournet.nyrah.account.ui.LoginActivity;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.BaseActivity;
import com.labournet.nyrah.Utils.KeyboardView;
import com.labournet.nyrah.Utils.KeyboardViewCallBack;
import com.labournet.nyrah.Utils.NetworkChangeReceiver;
import com.labournet.nyrah.Utils.NyrahSingleton;
import com.labournet.nyrah.Utils.UserSessionManager;
import com.labournet.nyrah.account.interfaces.LoginCallBacks;
import com.labournet.nyrah.account.model.Login;
import com.labournet.nyrah.account.model.User;
import com.labournet.nyrah.account.ui.SignUpActivity.SignUpActivity;
import com.labournet.nyrah.home.ui.HomeActivity;

import java.io.IOException;

import maes.tech.intentanim.CustomIntent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.labournet.nyrah.Utils.Constants.BASEURL;
import static com.labournet.nyrah.Utils.Constants.GET_MENU_URL;
import static com.labournet.nyrah.Utils.Constants.PROJECT;

public class LoginActivity extends BaseActivity implements LoginCallBacks, KeyboardViewCallBack {

    Boolean debug = true;

    NyrahSingleton nyrahSingleton;
    private String mResponse;
    private String p;
    private String landingPageUrl;
    private String userType;
    String XML_DATA;
    String mobileNumber;
    String PIN;

    long backPressedTime;

    public Boolean loginStatus = false;

    KeyboardView pinView;
    User userData = new User();
    Button button;

    FrameLayout frameLayout;
    LinearLayout linearLayout;
    LinearLayout root;

    LoginPhoneNumberFragment loginPhoneNumberFragment;
    LoginPINFragment loginPINFragment;

    UserSessionManager session = null;

    private Toast backToast;

    private BroadcastReceiver networkChangeReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        root = findViewById(R.id.root);

        nyrahSingleton = new NyrahSingleton(getApplicationContext());
        loginPhoneNumberFragment = new LoginPhoneNumberFragment();
        frameLayout = findViewById(R.id.login_container);

        session = new UserSessionManager(getApplicationContext());

        ViewGroup layout = root;
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        pinView = findViewById(R.id.pin_view);
        linearLayout = findViewById(R.id.PIN_view);
        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PIN = pinView.getInputText();
                Log.e("DDD", PIN);
            }
        });


        loginPINFragment = new LoginPINFragment();

        String mobNo = session.getUserMobileNumber();
        if (mobNo.equals("")) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_container, loginPhoneNumberFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_container, loginPINFragment)
                    .commit();
        }

    }


    //==================================== ON CREATE() =============================================

    public void SignInRequest(Login login) {

        if (nyrahSingleton.checkNetworkConnection()) {
            new LoginAsyncTask(login).execute();//Async Task to login
        } else {
            loginPINFragment.clearPIN();
            networkChangeReceiver = new NetworkChangeReceiver();
            registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            showToast("No internet connection");
        }
    }

    //LOGIN PIN
    @Override
    public void onComplete(String pin) {
        mobileNumber = session.getUserMobileNumber();
        Login login = new Login(mobileNumber, pin);
        SignInRequest(login);
    }

    @Override
    public void onPhoneNumberOKLoginCallBack(String phoneNumber) {
        showToast(phoneNumber);
        closeKeyBoard();
        frameLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLogin(Login login) {
        SignInRequest(login);

    }

    @Override
    public void onSignUp() {
        closeKeyBoard();
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("addUser", "YES");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void resetApp() {
        session.clearAll();
        session.signUpCompleted("NO");
        Intent launcher = new Intent(this, LauncherActivity.class);
        startActivity(launcher);
    }

    @Override
    public void onSuccessPINLoginCallBack() {
        closeKeyBoard();
        Toast.makeText(getApplicationContext(), pinView.getInputText(), Toast.LENGTH_SHORT).show();
    }


    private class LoginAsyncTask extends AsyncTask<String, Integer, Double> {

        String lUsername;
        String lPassword;

        String projectID = session.getProjectID();

        public LoginAsyncTask(Login login) {
            this.lUsername = login.getMobileNumber();
            this.lPassword = login.getPIN();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress_spinner.show();
                }
            });
        }

        @Override
        protected Double doInBackground(String... strings) {

            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(BASEURL)
                    .addPathSegment(PROJECT)
                    .addPathSegment(GET_MENU_URL)
                    .addQueryParameter("login", "Y")
                    .addQueryParameter("proj", projectID)
                    .addQueryParameter("uid", lUsername)
                    .addQueryParameter("pwd", lPassword)
                    .build();

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();

            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    showToast("Something went wrong !,Try again");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        mResponse = response.body().string();
                        Log.e("SignInRequest", ": response-> :" + mResponse);
                        XML_DATA = mResponse;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mResponse != null) {
                                    if (getTagValue(mResponse, "Error").contains("Invalid User Id/Password. Please try again")) {
                                        try {
                                            progress_spinner.hide();
                                            loginPhoneNumberFragment.showRegisterButton();
                                            loginPhoneNumberFragment.showError(true, shakeError());
                                        } catch (Exception e) {
                                            //Exception is thrown when sigIn request is made from LoginPIN fragment
                                            loginPINFragment.showRegisterButton();
                                            loginPINFragment.showError();
                                        }
                                    } else {
                                        String name = getTagValue(mResponse, "name");
                                        session.setUserProfileName(name);
                                        p = getTagValue(mResponse, "project_name");
                                        landingPageUrl = getTagValue(mResponse, "defUrl");
                                        userType = getTagValue(mResponse, "userType");

                                        userData.setUserName(getTagValue(mResponse, "userId"));
                                        userData.setUserMobileNumber(getTagValue(mResponse, "phone"));

                                        try {
//                                             FileUtils.deleteQuietly(getApplicationContext().getCacheDir());
                                        } catch (Exception e) {
                                            Log.e("CLEAR_CACHE", e.toString());
                                        } finally {
                                            Log.d("CLEAR_CACHE", "cache is cleared");
                                        }

                                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);

                                        //sending data to MainActivity
                                        Bundle extras = new Bundle();
                                        extras.putString("landingPageUrl", landingPageUrl);
                                        extras.putString("userName", userData.getUserName());
                                        extras.putString("userMobileNumber", userData.getUserMobileNumber());
                                        extras.putString("XML_DATA", XML_DATA);

                                        intent.putExtras(extras);


                                        startActivity(intent);

                                        CustomIntent.customType(LoginActivity.this, "fadein-to-fadeout");
                                        SaveValue(userData, p);

                                        loginStatus = true;
                                        //  SharedPreferences callLogPrefs = context.getSharedPreferences(CALL_LOG_PREF, MODE_PRIVATE);

                                    }
                                } else {
//                                    FlashMessage("Unable to complete your request.. Please try again..");
                                }
                            }
                        });


                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    progress_spinner.hide();
                }
            });
            super.onPostExecute(aDouble);
        }
    }

    public String getTagValue(String xml, String tagName) {
        try {
            return xml.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
        } catch (Exception e) {
            return "";
        }
    }

    private void SaveValue(User user, String project) {
        try {
            session.UserLoginSession(user, project);
//            Toast.makeText(this,"save value",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            if (debug) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
            return;

        } else {
            backToast = Toast.makeText(getBaseContext(), "Press again to exit", Toast.LENGTH_SHORT);
            backToast.show();
            backPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }
}
