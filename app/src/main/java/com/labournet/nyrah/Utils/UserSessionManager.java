package com.labournet.nyrah.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.labournet.nyrah.account.model.Business;
import com.labournet.nyrah.account.model.User;
import com.labournet.nyrah.account.ui.LoginActivity.LauncherActivity;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class UserSessionManager {

    public SharedPreferences sharedPreferences;
    public SharedPreferences menuSharedPreference;

    SharedPreferences.Editor editor;
    SharedPreferences.Editor menuItemsEditor;
    Context _context;

    public static final String USER_INFO_PREFERENCE = "UserInfo";
    public static final String MENU_PREFERENCE = "MenuPreference";

    public static final String Login_Status = "IsLoggedIn";
    public static final String PRODUCT_KEY = "productKey";
    public static final String USER_PROFILE_NAME = "profileName";
    public static final String PIN = "PIN";
    public static final String USER_NAME = "UserName";
    public static final String USER_MOBILE_NUMBER = "UserMobileNumber";
    public static final String project = "project";
    public static final String USER_ADDRESS = "user_postal_address";
    public static final String USER_EMAIL = "User_email";

    public static final String Weburl = "web";
    public static final String PROJECT_ID = "ProjectID";
    public static final String PROJECT_NAME = "ProjectName";

    public static final String BUSINESS_TITLE = "Business_Title";
    public static final String BUSINESS_TYPE = "Business_Type";
    public static final String BUSINESS_ADMINISTRATOR_NAME = "Business_AdministratorName";
    public static final String BUSINESS_PHONE_NUMBER = "Business_PhoneNumber";
    public static final String BUSINESS_EMAIL = "Business_EMail";
    public static final String BUSINESS_POSTAL_ADDRESS = "Business_PostalAddress";
    public static final String BUSINESS_WEB_ADDRESS = "Business_WebAddress";

    public static final String SIGN_UP_COMPLETED = "signUP_completed";
    public static final String PROD_KEY_VERIFIED = "ProductKey verified";
    public static final String BUSINESS_ADDED = "Business added";
    public static final String USER_ADDED = "User added";

    User user;

    public UserSessionManager(Context context) {
        _context = context;
        user = new User();
        sharedPreferences = _context.getSharedPreferences(USER_INFO_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        menuSharedPreference = _context.getSharedPreferences(MENU_PREFERENCE, MODE_PRIVATE);
        menuItemsEditor = menuSharedPreference.edit();

        editor.commit();
        menuItemsEditor.commit();
    }

    public void UserLoginSession(User user, String Project) {

        editor.putBoolean(Login_Status, true);
        editor.putString(PIN, user.getPIN());
        editor.putString(USER_NAME, user.getUserName());
        editor.putString(USER_MOBILE_NUMBER, user.getUserMobileNumber());
        editor.putString(USER_ADDRESS, user.getPostalAddress());
        editor.putString(USER_EMAIL, user.getEmailAddress());
        editor.putString(project, Project);
        editor.commit();
    }

    public void saveProjectID(String project_id) {
        editor.putString(PROJECT_ID, project_id);
        editor.commit();
    }

    public void saveProductKey(String productKey) {
        editor.putString(PRODUCT_KEY, productKey);
        editor.commit();
    }

    public String getProductKey() {
        return sharedPreferences.getString(PRODUCT_KEY, "");
    }

    public String getUserProfileName() {
        return sharedPreferences.getString(USER_PROFILE_NAME, "");
    }

    public void setUserProfileName(String name) {
        editor.putString(USER_PROFILE_NAME, name);
        editor.commit();
    }

    public String getProjectID() {
        return sharedPreferences.getString(PROJECT_ID, "");
    }

    public void saveBusinessInfo(Business business) {
        editor.putString(BUSINESS_TITLE, business.getName());
        editor.putString(BUSINESS_TYPE, business.getType());
        editor.putString(BUSINESS_ADMINISTRATOR_NAME, business.getAdminName());
        editor.putString(BUSINESS_PHONE_NUMBER, business.getPhoneNumber());
        editor.putString(BUSINESS_EMAIL, business.getEmailAddress());
        editor.putString(BUSINESS_POSTAL_ADDRESS, business.getPostalAddress());
        editor.putString(BUSINESS_WEB_ADDRESS, business.getWebAddress());
        editor.commit();
    }

    public void signUpCompleted(String completed) {
        editor.putString(SIGN_UP_COMPLETED, completed);
        editor.commit();
    }

    public void productKeySaved(String saved) {
        editor.putString(PROD_KEY_VERIFIED, saved);
        editor.commit();
    }

    public void businessAdded(String added) {
        editor.putString(BUSINESS_ADDED, added);
        editor.commit();
    }

    public void userAdded(String added) {
        editor.putString(USER_ADDED, added);
        editor.commit();
    }

    public String isBusinessAdded() {
        return sharedPreferences.getString(BUSINESS_ADDED, "");
    }

    public String isUserAdded() {
        return sharedPreferences.getString(USER_ADDED, "");
    }

    public String isProdKeySaved() {
        return sharedPreferences.getString(PROD_KEY_VERIFIED, "");
    }

    public String isSignUpCompleted() {
        return sharedPreferences.getString(SIGN_UP_COMPLETED, "");
    }

    public void clearAll() {
        editor.clear().commit();
    }

    public void saveMenuItems(String xmlData) {
        menuItemsEditor.putString("XML_data", xmlData);
        menuItemsEditor.commit();
    }

    public String getMenuPreference() {
        return menuSharedPreference.getString("XML_data", "");
    }

    public void saveProjectIdAndName(String projID, String projName) {
        editor.putString(PROJECT_ID, projID);
        editor.putString(PROJECT_NAME, projName);
    }

    public void setBusinessAdministratorName(String businessAdministratorName) {
        editor.putString(BUSINESS_ADMINISTRATOR_NAME, businessAdministratorName);
    }

    public String getProjectId() {
        return sharedPreferences.getString(PROJECT_ID, "");
    }

    public String getProjectName() {
        return sharedPreferences.getString(PROJECT_NAME, "");
    }

    public String getUserMobileNumber() {
        return sharedPreferences.getString(USER_MOBILE_NUMBER, "");
    }

    public void updateUserMobileNumber(String number) {
        editor.putString(USER_MOBILE_NUMBER, number);
        editor.commit();
    }


    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(PIN, sharedPreferences.getString(PIN, null));
        user.put(project, sharedPreferences.getString(project, null));
        return user;
    }

    public void logout() {
        editor.putBoolean(Login_Status, false);
        editor.putString(USER_MOBILE_NUMBER, "");
        editor.commit();
        Intent i = new Intent(_context, LauncherActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

}
