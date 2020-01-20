package com.labournet.nyrah.account.ui.SignUpActivity;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.BaseActivity;
import com.labournet.nyrah.Utils.PermissionUtils;
import com.labournet.nyrah.Utils.UserSessionManager;
import com.labournet.nyrah.account.interfaces.SignUpCallBacks;
import com.labournet.nyrah.account.model.Business;
import com.labournet.nyrah.account.model.BusinessType;
import com.labournet.nyrah.account.model.User;
import com.labournet.nyrah.account.ui.LoginActivity.LoginActivity;
import com.labournet.nyrah.home.ui.HomeActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import maes.tech.intentanim.CustomIntent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static com.labournet.nyrah.Utils.Constants.BASEURL;
import static com.labournet.nyrah.Utils.Constants.GET_MENU_URL;
import static com.labournet.nyrah.Utils.Constants.PROJECT;
import static com.labournet.nyrah.Utils.Constants.VERIFY;
import static com.labournet.nyrah.account.ui.LoginActivity.LauncherActivity.businessTypesFromLauncher;

public class SignUpActivity extends BaseActivity implements SignUpCallBacks {

    boolean debug = false;

    boolean userRegistration = false;
    boolean userAndBusinessRegistration = false;

    boolean launchAddUserFrag = false;

    String projectName;
    String projectID;

    //API CALL RESPONSES
    String responseProductKeyVerification;
    String responseOTP;
    String responseSendSignUpData;

    String countryCodeValue;
    String tempMenu;
    String tempXML_DATA;
    String OTP;
    String users;

    boolean isValidProductKey;

    UserSessionManager session = null;

    private ProductKeyFragment productKeyFragment;
    private BusinessRegFragment businessRegFragment;
    private AddUserFragment addUserFragment;

    ArrayList<BusinessType> businessTypes;

    LinearLayout root;
    FrameLayout productKeyLayout;
    FrameLayout businessRegLayout;
    FrameLayout addUserLayout;

    UserSessionManager userSession;

    User newUser;
    Business newBusiness;

    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils = new PermissionUtils(SignUpActivity.this);


    boolean value = false;

    String addUserValue = "no";
    String productKey;
    String mobNo;

    String phNo = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        productKeyLayout = findViewById(R.id.productKey_container);
        businessRegLayout = findViewById(R.id.businessReg_container);
        addUserLayout = findViewById(R.id.add_user_container);
        root = findViewById(R.id.root);

        checkPermissions();

        productKeyFragment = new ProductKeyFragment();
        businessRegFragment = new BusinessRegFragment();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            phNo = bundle.getString("mobNo", "");
            launchAddUserFrag = bundle.getBoolean("launchAddUserFrag");
            addUserValue = bundle.getString("addUser", "");
        }

        if (phNo.equals(""))
            addUserFragment = new AddUserFragment(getPhoneNumberFromDevice());
        else addUserFragment = new AddUserFragment(phNo);

        session = new UserSessionManager(getApplicationContext());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.productKey_container, productKeyFragment)
                .replace(R.id.businessReg_container, businessRegFragment)
                .replace(R.id.add_user_container, addUserFragment)
                .commit();


        productKey = session.getProductKey();
        mobNo = session.getUserMobileNumber();

        userSession = new UserSessionManager(getApplicationContext());

        ViewGroup layout = root;
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);


        if (session.isSignUpCompleted().equals("YES") && !addUserValue.equals("YES")) {
            Intent intent1 = new Intent(this, LoginActivity.class);
            startActivity(intent1);
        } else if (session.isBusinessAdded().equals("YES") && session.isUserAdded().equals("NO")) {
            productKeyFragment.hide();
            businessRegLayout.setVisibility(View.GONE);
            addUserLayout.setVisibility(View.VISIBLE);
        } else if (session.isBusinessAdded().equals("NO")) {
            checkProductKey(session.getProductKey());
        }

        if (bundle != null) {
            if (addUserValue.equals("YES"))
                addNewUser();
        }

    }

    private void showBusinessFeilds() {
        businessRegFragment.showBusinessETFields(businessTypesFromLauncher);
        businessRegLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (debug)
                showToast("GRANTED");
        } else {
        }
    }

    public NodeList createList(String mResponseData, String tagName) {
        NodeList list = null;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = docFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                Log.e("createList", "" + e.toString());
            }
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(mResponseData));
            Document document = null;
            try {
                if (documentBuilder != null) {
                    document = documentBuilder.parse(inputSource);
                }
            } catch (SAXException e) {
                Log.e("createList", "" + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (document != null) {
                list = document.getElementsByTagName(tagName);
            }
        } catch (Exception e) {

        }
        return list;
    }


    //PRODUCT KEY VERIFICATION API CALL
    public boolean checkProductKey(final String productKey) {

        try {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host(BASEURL)
                    .addPathSegment(PROJECT)
                    .addPathSegment(VERIFY)
                    .addQueryParameter("key", "Y")
                    .addQueryParameter("prodKey", productKey)
                    .build();


            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .build();

            localOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    value = false;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {

                        responseProductKeyVerification = response.body().string();
                        Log.i("response", responseProductKeyVerification);

                        if (responseProductKeyVerification != null) {

                            if (getTagValue(responseProductKeyVerification, "Error").contains("Invalid Product Key")) {
                                showToast("Invalid Product Key");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        productKeyFragment.errorTV(View.VISIBLE);
                                    }
                                });
                                isValidProductKey = false;
                                value = false;
                            } else {
                                session.signUpCompleted("NO");
                                session.saveProductKey(productKey);
                                session.productKeySaved("YES");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        productKeyFragment.errorTV(View.GONE);
                                    }
                                });
                                value = true;
                                isValidProductKey = true;
                                session.businessAdded("NO");
                                businessTypes = new ArrayList<>(50);
                                businessTypes.add(new BusinessType("0", "-- Select --"));

                                projectID = getTagValue(responseProductKeyVerification, "projectId");
                                projectName = getTagValue(responseProductKeyVerification, "projectName");
                                users = getTagValue(responseProductKeyVerification, "users");

                                if (!projectID.equals("")) {
                                    userSession.saveProjectID(projectID);
                                }

                                userSession.saveProjectIdAndName(projectID, projectName);


                                if (projectName.equals("")) {
                                    //No Business found, Add a new Business.
                                    value = true;
                                    isValidProductKey = true;
                                    session.businessAdded("NO");
                                    NodeList list = createList(responseProductKeyVerification, "bType");

                                    if (list != null) {
                                        for (int i = 0; i < list.getLength(); i++) {
                                            Node eachNode = list.item(i);
                                            if (eachNode.getNodeType() == Node.ELEMENT_NODE) {
                                                Element eachElement = (Element) eachNode;
                                                businessTypes.add(new BusinessType(eachElement.getElementsByTagName("id").item(0).getTextContent(),
                                                        eachElement.getElementsByTagName("name").item(0).getTextContent()));
                                            }
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                productKeyFragment.updateViews("Product key verified");
                                                productKeyFragment.hide();

                                                root.findViewById(R.id.businessReg_container).setVisibility(View.VISIBLE);
                                                businessRegFragment.showBusinessETFields(businessTypes);

                                            }
                                        });
                                    }

                                } else {
                                    value = true;
                                    isValidProductKey = true;
                                    session.businessAdded("YES");
                                    if (users.equals("0")) {
//                                        showToast("No users found");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showToast("Business already exists");

                                                productKeyFragment.hide();
                                                businessRegLayout.setVisibility(View.GONE);
                                                addUserLayout.setVisibility(View.VISIBLE);

                                            }
                                        });
                                    } else {
                                        Log.i("PRODUCT_KEY", "users found " + users);
                                        session.signUpCompleted("YES");
                                        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(loginIntent);
                                    }

                                }
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.e("checkProductKey", e.toString());
            return false;
        }

        return value;
    }

    //PHONE NUMBER VERIFICATION VIA OTP API CALL
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

    public void addNewUser() {
        productKeyLayout.setVisibility(View.GONE);
        businessRegLayout.setVisibility(View.GONE);
        addUserLayout.setVisibility(View.VISIBLE);
    }

    //SEND BUSINESS AND USER DATA TO SERVER
    public void sendRegistrationData(Business newBusiness, User newUser) {
        try {

            showToast("Send Reg data");
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


//                showToast("no business");
                signUpFormBody = new FormBody.Builder()
                        .add("proj", session.getProjectID())
                        .add("saveB", "Y")
                        .add("saveU", "Y")
                        .add("bName", newBusiness.getName())
                        .add("bType", newBusiness.getType())
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
                    responseSendSignUpData = response.body().string();

                    if (userRegistration) {
                        session.userAdded("YES");
                    } else if (userAndBusinessRegistration) {
                        session.userAdded("YES");
                        session.businessAdded("YES");
                        session.signUpCompleted("YES");
                    }

                    //NEXT INTENT
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            session.clearAll();
        }
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
            showToast("Permission not granted");
        }

        return phoneNumber;
    }

    public void getMenu() {

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(BASEURL)
                .addPathSegment(PROJECT)
                .addPathSegment(GET_MENU_URL)
                .addQueryParameter("login", "Y")
                .addQueryParameter("uid", "")
                .addQueryParameter("pwd", "")
                .addQueryParameter("deviceID", "")
                .build();


        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    tempMenu = response.body().string();
                    tempXML_DATA = tempMenu;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseProductKeyVerification != null) {
                                if (getTagValue(responseProductKeyVerification, "Error").contains("Invalid User Id/Password. Please try again")) {

                                } else if (getTagValue(responseProductKeyVerification, "Error").contains("Your app version is outdated. Please update to the Latest Version of the App")) {

                                } else {

                                    try {
//                                             FileUtils.deleteQuietly(getApplicationContext().getCacheDir());
                                    } catch (Exception e) {
                                        Log.e("CLEAR_CACHE", e.toString());
                                    } finally {
                                        Log.d("CLEAR_CACHE", "cache is cleared");
                                    }

                                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);

                                    //sending data to MainActivity
                                    Bundle extras = new Bundle();
                                    extras.putString("XML_DATA", tempXML_DATA);
                                    intent.putExtras(extras);

                                    CustomIntent.customType(SignUpActivity.this, "fadein-to-fadeout");
                                    startActivity(intent);


                                }
                            } else {
                                showToast("Unable to complete your request.. Please try again..");
                            }
                        }
                    });


                }
            }
        });
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void closeKeyBoard() {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onSignUpUser(User user) {
        newUser = new User();
        newUser = user;
        sendRegistrationData(newBusiness, newUser);
        showToast("User added");
        addUserFragment.onSaveUserTitleChange("User added");
        user.setUserMobileNumber("");
        userSession.UserLoginSession(user, "PROJECT");
    }

    @Override
    public void onSendOTPSignUp(String phoneNumber) {
        String msg = isPhoneNumberValid(phoneNumber) ? null : "Not a valid phone number";
        if (msg != null) showToast(msg);
        verifyPhoneNumberWithOTP(phoneNumber);
        addUserFragment.showVerifyButton();
    }

    @Override
    public void onVerifyOTPSignUp(String otp) {
        if (otp.equals(OTP)) {
            closeKeyBoard();
            addUserFragment.onSuccessOTPVerification();
        } else
            addUserFragment.onFailureOTPVerification();
    }

    @Override
    public void onShowBusinessRegFields() {
        closeKeyBoard();
        productKeyFragment.hide();
    }

    @Override
    public void onConfirmBusinessReg(Business business) {
        newBusiness = new Business();
        newBusiness = business;
        businessRegFragment.updateBusinessPreview(newBusiness);
        addUserFragment.setName(newBusiness.getAdminName());
        userSession.saveBusinessInfo(newBusiness);
    }

    @Override
    public void onAddUserSignUp() {
        businessRegLayout.setVisibility(View.GONE);
        addUserLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVerifyProductKey(String productKey) {
        closeKeyBoard();
        checkProductKey(productKey);
    }

    @Override
    public void onInvalidProductKey() {
        productKeyFragment.errorTV(View.VISIBLE);
        showToast("Invalid Product Key !");
    }
}

