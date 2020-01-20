package com.labournet.nyrah.account.ui.SignUpActivity;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.UserSessionManager;
import com.labournet.nyrah.account.interfaces.SignUpCallBacks;
import com.labournet.nyrah.account.model.Business;
import com.labournet.nyrah.account.model.BusinessType;

import java.util.ArrayList;

public class BusinessRegFragment extends Fragment {

    private SignUpCallBacks signUpCallBacks;

    private TextView textView;
    private EditText businessTitle;
    private EditText phoneNumber;
    private EditText email;
    private EditText address;
    private EditText webAddress;
    private EditText adminNameET;


    private Spinner businessTypeSpinner;

    private Button confirmButton;
    private Button addUserButton;

    //Business details preview
    private TextView businessTitlePreview;
    private TextView businessTypePreview;
    private TextView businessPhoneNumberPreview;
    private TextView businessEmailPreview;
    private TextView businessWebAddressPreview;
    private TextView businessAddressPreview;
    private TextView businessAdminName;

    private LinearLayout rootView;
    private LinearLayout businessDetailsPreview;
    private LinearLayout businessDetailsETFields;

    private Business newBusiness;

    private String adminNameString;
    private UserSessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_business_reg_fragment, container, false);
        textView = view.findViewById(R.id.titleTV);
        closeKeyBoard();

        sessionManager = new UserSessionManager(getActivity());
        newBusiness = new Business();

        businessTitle = view.findViewById(R.id.business_title);
        phoneNumber = view.findViewById(R.id.business_phoneNumber);
        email = view.findViewById(R.id.business_email);
        address = view.findViewById(R.id.business_address);
        webAddress = view.findViewById(R.id.business_website);
        textView = view.findViewById(R.id.titleTV);
        businessTypeSpinner = view.findViewById(R.id.businessType_spinner);
        adminNameET = view.findViewById(R.id.business_administrator);

        confirmButton = view.findViewById(R.id.confirm_button);
        addUserButton = view.findViewById(R.id.add_user_Button);

        rootView = view.findViewById(R.id.root);
        businessDetailsPreview = view.findViewById(R.id.businessDetails_preview);
        businessDetailsETFields = view.findViewById(R.id.businessDetailsETFields);

        businessTitlePreview = view.findViewById(R.id.business_titleTV);
        businessTypePreview = view.findViewById(R.id.business_typeTV);
        businessPhoneNumberPreview = view.findViewById(R.id.business_phoneNumberTV);
        businessEmailPreview = view.findViewById(R.id.business_emailTV);
        businessWebAddressPreview = view.findViewById(R.id.business_webAddressTV);
        businessAddressPreview = view.findViewById(R.id.business_addressTV);
        businessAdminName = view.findViewById(R.id.business_adminNameTV);

        ViewGroup layout = rootView;
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Business business;

                if (validateForm()) {
                    business = getFormData();

                    signUpCallBacks.onConfirmBusinessReg(business);
                    sessionManager.setBusinessAdministratorName(business.getAdminName());
                }

            }
        });

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpCallBacks.onAddUserSignUp();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboardFrom(getActivity(), view);
                return false;
            }
        });
        return view;
    }

    public boolean validateForm() {

        boolean val = true;
        if (TextUtils.isEmpty(businessTitle.getText().toString().trim())) {
            businessTitle.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            businessTitle.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(businessAdminName.getText().toString().trim())) {
            businessAdminName.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            businessAdminName.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            email.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            email.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(phoneNumber.getText().toString().trim())) {
            phoneNumber.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            phoneNumber.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(adminNameET.getText().toString().trim())) {
            adminNameET.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            adminNameET.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(webAddress.getText().toString().trim())) {
            webAddress.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            webAddress.startAnimation(shakeError());
            val = false;
        } else val = true;
        return val;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    private Business getFormData() {


        newBusiness.setName(businessTitle.getText().toString());
        newBusiness.setEmailAddress(email.getText().toString());
        newBusiness.setPhoneNumber(phoneNumber.getText().toString());
        newBusiness.setType((BusinessType) businessTypeSpinner.getSelectedItem());
        newBusiness.setAdminName(adminNameET.getText().toString());
        newBusiness.setWebAddress(webAddress.getText().toString());
        newBusiness.setPostalAddress(address.getText().toString());

        adminNameString = adminNameET.getText().toString().trim();
        return newBusiness;
    }

    public Business getNewBusinessData() {
        return newBusiness;
    }

    public void updateBusinessPreview(Business business) {
        textView.setTextSize(25);
        showBusinessPreview(business);
    }

    public void showUserRegForm() {
        businessDetailsPreview.setVisibility(View.GONE);
    }

    public void hideBusinessRegFragment(int visibility) {
        rootView.setVisibility(visibility);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SignUpCallBacks) {
            signUpCallBacks = (SignUpCallBacks) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " Must implement BusinessRegListner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        signUpCallBacks = null;
    }

    public void showBusinessPreview(Business business) {


//        Setting onShowBusinessRegFields() to hide the productKey fragment.
        signUpCallBacks.onShowBusinessRegFields();
        businessDetailsPreview.setVisibility(View.VISIBLE);
        if (businessDetailsETFields.getVisibility() == View.VISIBLE)
            businessDetailsETFields.setVisibility(View.GONE);

        textView.setText("Your Business details");
        textView.setTextSize(25);
        businessTitlePreview.setText(business.getName());
        businessTypePreview.setText(business.getType());
        businessPhoneNumberPreview.setText(business.getPhoneNumber());
        businessAdminName.setText(business.getAdminName());
        businessEmailPreview.setText(business.getEmailAddress());
        businessWebAddressPreview.setText(business.getWebAddress());
        businessAddressPreview.setText(business.getPostalAddress());
        confirmButton.setVisibility(View.GONE);
        addUserButton.setVisibility(View.VISIBLE);
        businessDetailsPreview.setVisibility(View.VISIBLE);
    }

    public void showBusinessETFields(ArrayList<BusinessType> businessTypes) {

//        Setting onShowBusinessRegFields() to hide the productKey fragment.
        try {
            signUpCallBacks.onShowBusinessRegFields();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        textView.setText("Enter your Business details");
        textView.setTextSize(25);

        ArrayAdapter<BusinessType> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, businessTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessTypeSpinner.setAdapter(adapter);
        businessTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BusinessType businessType = (BusinessType) adapterView.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        businessDetailsPreview.setVisibility(View.GONE);
        confirmButton.setVisibility(View.VISIBLE);
        businessDetailsETFields.setVisibility(View.VISIBLE);

    }


    public void businessTypeSelected(View view) {
        BusinessType businessType = (BusinessType) businessTypeSpinner.getSelectedItem();
        Log.e("SELECTED", businessType.getName());
    }

    public String getBusinessAdminName() {
        return adminNameString;
    }

    public void closeKeyBoard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
