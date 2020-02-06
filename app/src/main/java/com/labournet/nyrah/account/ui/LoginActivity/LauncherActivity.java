package com.labournet.nyrah.account.ui.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.BaseActivity;
import com.labournet.nyrah.Utils.UserSessionManager;
import com.labournet.nyrah.account.model.BusinessType;
import com.labournet.nyrah.account.ui.SignUpActivity.SignUpActivity;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

import static com.labournet.nyrah.Utils.Constants.BASEURL;
import static com.labournet.nyrah.Utils.Constants.PROJECT;
import static com.labournet.nyrah.Utils.Constants.VERIFY;

public class LauncherActivity extends BaseActivity {

    boolean checkForBusinessStatus = false;

    UserSessionManager session = null;
    String mobNo;
    String productKey;
    String responseCheckForBusiness;
    String projectName;
    String users;

    public static ArrayList<BusinessType> businessTypesFromLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        session = new UserSessionManager(getApplicationContext());

        mobNo = session.getUserMobileNumber();
        productKey = session.getProductKey();

        if (session.isSignUpCompleted().equals("YES")) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } else if (mobNo.equals("") && productKey.equals("")) {
            Intent signUpIntnet = new Intent(LauncherActivity.this, SignUpActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("mobNo", getPhoneNumberFromDevice());
            signUpIntnet.putExtras(bundle);
            startActivity(signUpIntnet);

        } else if (!productKey.equals("") && mobNo.equals("")) {
            checkForBusiness();

        } else if (productKey.equals("")) {
            Intent signUpIntnet = new Intent(LauncherActivity.this, SignUpActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("mobNo", getPhoneNumberFromDevice());
            signUpIntnet.putExtras(bundle);
            startActivity(signUpIntnet);
        }
    }

    boolean checkForBusiness() {

        projectName = "";
        users = "";

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
                checkForBusinessStatus = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    responseCheckForBusiness = response.body().string();

                    if (responseCheckForBusiness != null) {

                        if (getTagValue(responseCheckForBusiness, "Error").contains("Invalid Product Key")) {
                            showToast("Invalid Product Key");
                        } else {

                            projectName = getTagValue(responseCheckForBusiness, "projectName");
                            users = getTagValue(responseCheckForBusiness, "users");

                            getBusinessTypesData();
                            if (projectName.equals("")) {
                                session.businessAdded("NO");
                                Intent intent = new Intent(LauncherActivity.this, SignUpActivity.class);
                                startActivity(intent);
                            } else if (users.equals("0")) {
                                session.userAdded("NO");
                                Intent intent = new Intent(LauncherActivity.this, SignUpActivity.class);
                                startActivity(intent);
                            }
                            checkForBusinessStatus = true;
                        }
                    }
                }
            }
        });
        return checkForBusinessStatus;
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

    public boolean getBusinessTypesData() {
        //No Business found, Add a new Business.
        try {
            businessTypesFromLauncher = new ArrayList<>(50);
            NodeList list = createList(responseCheckForBusiness, "bType");
            if (list != null) {
                for (int i = 0; i < list.getLength(); i++) {
                    Node eachNode = list.item(i);
                    if (eachNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eachElement = (Element) eachNode;
                        businessTypesFromLauncher.add(new BusinessType(eachElement.getElementsByTagName("id").item(0).getTextContent(),
                                eachElement.getElementsByTagName("name").item(0).getTextContent()));
                    }
                }
                businessTypesFromLauncher.add(new BusinessType("hint", "Select"));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
