package com.labournet.nyrah.home.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.BaseActivity;
import com.labournet.nyrah.Utils.UserSessionManager;
import com.labournet.nyrah.Utils.webviewclients.ChromeClient;
import com.labournet.nyrah.Utils.webviewclients.WebViewClientAPI24andBelow;
import com.labournet.nyrah.Utils.webviewclients.WebViewClientNougat;
import com.labournet.nyrah.Utils.webviewclients.WebViewDownloadListener;
import com.labournet.nyrah.home.model.DrawerItem;
import com.labournet.nyrah.home.model.MenuItemNode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

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

import static com.labournet.nyrah.Utils.Constants.LOAD_BASE_URL;

public class HomeActivity extends BaseActivity {

    boolean debug = false;

    AccountHeader headerResult = null;
    Toolbar toolbar;

    String XML_DATA;
    int parentCount = 0;

    ArrayList<DrawerItem> drawerItemList;

    WebView webView;
    String loadURL;

    String DASHBOARD_URL;
    String dashboardURL;

    UserSessionManager session = null;
    long backPressedTime;
    private Toast backToast;

    DrawerBuilder drawerBuilder;
    Drawer drawer;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        toolbar.setTitle(R.string.app_secondary_name);
        setSupportActionBar(toolbar);

        session = new UserSessionManager(getApplicationContext());

        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary3));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        webView = findViewById(R.id.webview);

        if (bundle != null) {
            XML_DATA = bundle.getString("XML_DATA", null);
        }

        Thread drawerThread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDrawerData(XML_DATA);
                    }
                });
            }
        };

        drawerThread.start();
        setWebViewClients();

        dashboardURL = LOAD_BASE_URL + getTagValue(XML_DATA, "defUrl");
        Log.e("CCC", "" + dashboardURL);

        if (checkNetworkConnection()) {
            webView.loadUrl(dashboardURL);
        } else showToast("No connection internet connection");

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

//        webView.loadUrl("file:///android_asset/sampleweb.html");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            drawer.openDrawer();
                            break;
                        case R.id.navigation_dashboard:
                            String url = LOAD_BASE_URL + DASHBOARD_URL;
                            webView.loadUrl(url);
                            break;
                        case R.id.navigation_notifications:
                            showToast("No new notifications");
                            break;
                    }
                    return true;
                }
            };

    public ArrayList<MenuItemNode> getDrawerData(String XML_DATA) {

        ArrayList<MenuItemNode> drawerItemNodes = new ArrayList<>();

        try {
            drawerItemList = new ArrayList<>();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            try {
                documentBuilder = docFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                Log.e("OKHttp3", "" + e.toString());
            }
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(XML_DATA));
            Document document = null;
            try {
                if (documentBuilder != null) {
                    document = documentBuilder.parse(inputSource);
                }
            } catch (SAXException e) {
                Log.e("OKHttp3", "" + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            NodeList list = null;
            if (document != null) {
                list = document.getElementsByTagName("item");
            }

            if (list != null) {
                for (int i = 0; i < list.getLength(); i++) {
                    Node eachNode = list.item(i);
                    if (eachNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eachElement = (Element) eachNode;

                        String itemName = eachElement.getElementsByTagName("item_name").item(0).getTextContent();
                        String itemID = eachElement.getElementsByTagName("item_id").item(0).getTextContent();
                        String itemLink = eachElement.getElementsByTagName("item_link").item(0).getTextContent();
                        String itemParent = eachElement.getElementsByTagName("item_parent").item(0).getTextContent();
                        String itemIcon = eachElement.getElementsByTagName("item_icon").item(0).getTextContent();

                        if (itemName.equals("Home"))
                            DASHBOARD_URL = itemLink;

                        if (itemParent.equals("") && itemLink.equals("")) {

                            //PARENT

                            MenuItemNode menuItemNode = new MenuItemNode();
                            menuItemNode.setParentName(itemName);
                            menuItemNode.setParentID(itemID);
                            menuItemNode.setIconID(itemIcon);
                            drawerItemNodes.add(menuItemNode);

                            if (debug) Log.i("DRAWER_DATA", itemName + " is a parent");


                        } else if (!itemParent.equals("") && !itemLink.equals("")) {

                            //CHILD ITEM

                            DrawerItem drawerItem = new DrawerItem(itemName, itemParent, itemLink);
                            drawerItem.setLinkURL(itemLink);
                            String parentName = drawerItemNodes.get(parentCount).getParentName();

                            if (drawerItemNodes.get(parentCount).getParentID() == null || drawerItemNodes.get(parentCount).getParentID().equals(itemParent)) {
                                drawerItemNodes.get(parentCount).addChild(drawerItem);
                            } else {
                                parentCount++;
                                drawerItemNodes.get(parentCount).addChild(drawerItem);
                            }
                            if (debug)
                                Log.i("DRAWER_DATA", itemName + " is a child of " + itemParent);

                        } else if (itemParent.equals("") && !itemLink.equals("")) {

                            //INDIVIDUAL MENU ITEM

                            parentCount++;
                            MenuItemNode menuItemNode = new MenuItemNode();
                            menuItemNode.setParentName(itemName);
                            menuItemNode.setParentID(itemID);
                            menuItemNode.setLinkURL(itemLink);
                            drawerItemNodes.add(menuItemNode);

                            if (debug) Log.i("DRAWER_DATA", itemName + " is a Menu item");
                        }
                    }
                }

                Log.i("XXX", "ggg");
                fillDrawer(drawerItemNodes);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drawerItemNodes;
    }

    private void goToDashboard() {
        webView.loadUrl(dashboardURL);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            if (drawer != null && !drawer.isDrawerOpen()) {
                backToast.cancel();
                super.onBackPressed();
                finish();
                return;
            }

        } else {
            if (drawer != null && drawer.isDrawerOpen())
                drawer.closeDrawer();
            else {
                if (webView.getUrl().equals(dashboardURL)) {
                    backToast = Toast.makeText(getBaseContext(), "Press again to exit", Toast.LENGTH_SHORT);
                    backToast.show();
                    backPressedTime = System.currentTimeMillis();
                } else {
                    webView.goBack();
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    public void fillDrawer(ArrayList<MenuItemNode> mDrawerItemsList) {

        final ArrayList<PrimaryDrawerItem> drItems = new ArrayList<>(50);
        final ArrayList<ExpandableBadgeDrawerItem> expandableBadgeDrawerItems = new ArrayList<>(50);
        long identifier;

        FontAwesome.Icon iconId = FontAwesome.Icon.faw_list_alt;

        Typeface roboto = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Roboto-Bold.ttf");
        String name = session.getUserProfileName();
        final IProfile profile = new ProfileDrawerItem().withName(name)
                .withTypeface(roboto)
                .withIcon(ContextCompat.getDrawable(this, R.drawable.ic_user2))
                .withTextColor(ContextCompat.getColor(this, R.color.white));

        headerResult = new AccountHeaderBuilder()
                .withActivity(HomeActivity.this)
                .withTranslucentStatusBar(true)
                .withNameTypeface(roboto)
                .withTextColor(ContextCompat.getColor(this, R.color.white))
                .withHeaderBackground(R.drawable.ic_rainbow_vortex)
                .addProfiles(profile)
                .build();
        Typeface robotoBold = Typeface.createFromAsset(this.getAssets(), "fonts/Roboto-Black.ttf");

        for (int i = 0; i < mDrawerItemsList.size(); i++) {
            final MenuItemNode menuItemNode;
            menuItemNode = mDrawerItemsList.get(i);

            if (menuItemNode.hasChild()) {

                ExpandableBadgeDrawerItem expandableBadgeDrawerItem = new ExpandableBadgeDrawerItem()
                        .withName(menuItemNode.getParentName())
                        .withTypeface(robotoBold)
                        .withIcon(getGoogleIconId(menuItemNode.getIconID()))
                        .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary3))
                        .withIdentifier(222)
                        .withSelectable(false)
                        .withTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                        .withBadgeStyle(new BadgeStyle()
                                .withColor(Color.GRAY)
                                .withTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white)))
                        .withBadge(String.valueOf(menuItemNode.getChildItemCount()));

                for (int k = 0; k < menuItemNode.getChildItemCount(); k++) {

                    //generate Identifier with position of parent and child.
                    String id = (i + 1) + String.valueOf(k + 1);
                    identifier = Long.parseLong(id);

                    final int finalK = k;
                    expandableBadgeDrawerItem.withSubItems(new SecondaryDrawerItem().withName(menuItemNode.getChildItem(k).getTitle())
                            .withLevel(2)
                            .withTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                            .withIcon(GoogleMaterial.Icon.gmd_menu)
                            .withSelectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                            .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.gray2))
                            .withSelectedTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                            .withSelectedIconColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                            .withSelectedBackgroundAnimated(true)
                            .withSelectedColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary3))
                            .withEnabled(true)
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                                    String tempS = menuItemNode.getChildItem(finalK).getLinkURL();
                                    if (tempS.startsWith("http") || tempS.startsWith("https")) {
                                        loadURL = tempS;
                                    } else {
                                        loadURL = LOAD_BASE_URL + tempS;
                                    }
                                    webView.loadUrl(loadURL);
                                    if (debug)
                                        Toast.makeText(getApplicationContext(), " " + menuItemNode.getChildItem(finalK).getLinkURL(), Toast.LENGTH_LONG).show();
                                    Log.e("URL", " -> " + loadURL);
                                    return false;
                                }
                            })
                            .withIdentifier(identifier)
                            .withLevel(2)
                            .withSelectable(true)
                    );
                }
                expandableBadgeDrawerItems.add(expandableBadgeDrawerItem);
            } else {
                drItems.add(new PrimaryDrawerItem()
                        .withName(menuItemNode.getParentName())
                        .withIcon(GoogleMaterial.Icon.gmd_home)
                        .withIconColor(ContextCompat.getColor(getApplicationContext(), R.color.green2))
                        .withTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                        .withIconTintingEnabled(true)
                        .withIdentifier(333)
                        .withSelectedBackgroundAnimated(true)
                        .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                String tmpS = menuItemNode.getLinkURL();
                                if (tmpS.startsWith("http") || tmpS.startsWith("https")) {
                                    loadURL = tmpS;
                                } else {
                                    loadURL = "https://www.eduflex.co.in/msbms/" + tmpS;
                                }
                                webView.loadUrl(loadURL);
                                if (debug)
                                    Toast.makeText(getApplicationContext(), " " + menuItemNode.getLinkURL(), Toast.LENGTH_LONG).show();
                                Log.e("URL", " -- >> " + loadURL);
                                return false;
                            }
                        }));
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                drawerBuilder = new DrawerBuilder(HomeActivity.this);
                drawerBuilder.withToolbar(toolbar)
                        .withTranslucentStatusBar(true)
                        .withDisplayBelowStatusBar(true)
                        .withFireOnInitialOnClick(true)
                        .withTranslucentNavigationBar(true)
                        .withCloseOnClick(true)
                        .withSliderBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                        .withAccountHeader(headerResult);

                drawerBuilder.withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        closeKeyBoard();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        closeKeyBoard();
                    }
                });


                for (int j = 0; j < expandableBadgeDrawerItems.size(); j++) {
                    drawerBuilder.addDrawerItems(expandableBadgeDrawerItems.get(j));
                }


                for (int j = 0; j < drItems.size(); j++) {
                    drawerBuilder.addDrawerItems(drItems.get(j)
                            .withSelectedColor(ContextCompat.getColor(getApplicationContext(), R.color.gray2)));
                }

                drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        drawerBuilder.withCloseOnClick(true);
                        String pos = String.valueOf(position);

                        if (debug)
                            Toast.makeText(getApplicationContext(), "clicked at : " + pos + " " + drawerItem.getIdentifier(), Toast.LENGTH_LONG).show();

                        if (drawerItem.getIdentifier() == 333) {
                            if (debug)
                                Toast.makeText(getApplicationContext(), "clicked on MENU", Toast.LENGTH_LONG).show();
                        } else {
                            if (debug)
                                Toast.makeText(getApplicationContext(), "clicked on SUB_MENU", Toast.LENGTH_LONG).show();
                        }

                        return false;
                    }
                });

                drawer = drawerBuilder
                        .build();


            }
        });
    }

    public GoogleMaterial.Icon getGoogleIconId(String title) {

        GoogleMaterial.Icon id = GoogleMaterial.Icon.gmd_info;
        switch (title) {
            case "transaction":
                id = GoogleMaterial.Icon.gmd_menu;
                break;
            default:
                id = GoogleMaterial.Icon.gmd_group;
                break;
        }
        return id;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setWebViewClients() {

        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAppCacheEnabled(false);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.clearCache(true);
        webView.clearHistory();
        webView.clearSslPreferences();
        webView.requestFocusFromTouch();
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        ChromeClient chromeClient = new ChromeClient(this);
        WebViewDownloadListener webviewDownloadListener = new WebViewDownloadListener(this);

        webView.setWebChromeClient(chromeClient);
        webView.setDownloadListener(webviewDownloadListener);

//        webView.addJavascriptInterface(new Object() {
//            @JavascriptInterface // For API 17+
//            public void performClick(String strl) {
//                Toast.makeText(HomeActivity.this, strl, Toast.LENGTH_LONG).show();
//            }
//        }, "ok");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            WebViewClientNougat webviewClientNougat = new WebViewClientNougat(this);
            webView.setWebViewClient(webviewClientNougat);

        } else {
            WebViewClientAPI24andBelow webViewClient = new WebViewClientAPI24andBelow(this);
            webView.setWebViewClient(webViewClient);
        }

    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOutIcon) {
            session.logout();
            progress_spinner.dismiss();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress_spinner.dismiss();
    }
}
