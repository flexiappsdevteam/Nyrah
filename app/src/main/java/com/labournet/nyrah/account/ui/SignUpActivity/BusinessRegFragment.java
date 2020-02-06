package com.labournet.nyrah.account.ui.SignUpActivity;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.EnlivenTextInputLayout;
import com.labournet.nyrah.Utils.UserSessionManager;
import com.labournet.nyrah.account.interfaces.SignUpCallBacks;
import com.labournet.nyrah.account.model.Business;
import com.labournet.nyrah.account.model.BusinessType;

import java.util.ArrayList;

/**
 * Fragment class for business registration
 **/
public class BusinessRegFragment extends Fragment {

    private SignUpCallBacks signUpCallBacks;

    private TextView textView;


    private Spinner businessTypeSpinner;

    private Button confirmButton;
    private Button addUserButton;

    private LinearLayout goBackButton;

    //Business details preview
    private TextView businessTypeTV;
    private TextView businessTitlePreview;
    private TextView businessTypePreview;
    private TextView businessPhoneNumberPreview;
    private TextView businessEmailPreview;
    private TextView businessWebAddressPreview;
    private TextView businessAddressPreview;

    private LinearLayout rootView;
    private LinearLayout businessDetailsPreview;
    private LinearLayout businessDetailsETFields;

    private Business newBusiness;

    private String adminNameString;
    private UserSessionManager sessionManager;

    private EnlivenTextInputLayout bTitle;
    private EnlivenTextInputLayout bPhoneNumberET;
    private EnlivenTextInputLayout bEmailET;
    private EnlivenTextInputLayout bPostalAddressET;
    private EnlivenTextInputLayout bWebAddressET;

    UserSessionManager session = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_business_reg_fragment, container, false);
        textView = view.findViewById(R.id.titleTV);
        closeKeyBoard();

        sessionManager = new UserSessionManager(getActivity());
        newBusiness = new Business();

        session = new UserSessionManager(getActivity());


        bTitle = view.findViewById(R.id.text_input_bTitle);
        bPhoneNumberET = view.findViewById(R.id.text_input_bPhoneNumber);
        bEmailET = view.findViewById(R.id.text_input_bEmail);
        bPostalAddressET = view.findViewById(R.id.text_input_bAddress);
        bWebAddressET = view.findViewById(R.id.text_input_bWebAddress);

        textView = view.findViewById(R.id.titleTV);
        businessTypeSpinner = view.findViewById(R.id.businessType_spinner);

        confirmButton = view.findViewById(R.id.confirm_button);
        addUserButton = view.findViewById(R.id.add_user_Button);

        goBackButton = view.findViewById(R.id.goBack_button);

        rootView = view.findViewById(R.id.root);
        businessDetailsPreview = view.findViewById(R.id.businessDetails_preview);
        businessDetailsETFields = view.findViewById(R.id.businessDetailsETFields);

        businessTypeTV = view.findViewById(R.id.businessType_TV);
        businessTitlePreview = view.findViewById(R.id.business_titleTV);
        businessTypePreview = view.findViewById(R.id.business_typeTV);
        businessPhoneNumberPreview = view.findViewById(R.id.business_phoneNumberTV);
        businessEmailPreview = view.findViewById(R.id.business_emailTV);
        businessWebAddressPreview = view.findViewById(R.id.business_webAddressTV);
        businessAddressPreview = view.findViewById(R.id.business_addressTV);

        ViewGroup layout = rootView;
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure to delete current product key and enter a new one ?")
                        .setTitle("Enter new product key")
                        .setPositiveButton("Enter new key", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                session.clearAll();
                                session.signUpCompleted("NO");
                                Intent launcher = new Intent(getActivity(), SignUpActivity.class);
                                startActivity(launcher);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        bPhoneNumberET.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    validateBusinessPhoneNumber();
                    bEmailET.getEditText().requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bPhoneNumberET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (bPhoneNumberET.getEditText().length() != 10) {
                        bPhoneNumberET.getEditText().setTextColor(getContext().getColor(R.color.lighter_red));
                    }
                }
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Business business;

                if (submitBusinessInfo(view)) {
                    business = getFormData();
                    signUpCallBacks.onConfirmBusinessReg(business);
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


        newBusiness.setName(bTitle.getEditText().getText().toString());
        newBusiness.setEmailAddress(bEmailET.getEditText().getText().toString());
        newBusiness.setPhoneNumber(bPhoneNumberET.getEditText().getText().toString());
        newBusiness.setType(new BusinessType("1", businessTypeTV.getText().toString()));
        newBusiness.setWebAddress(bWebAddressET.getEditText().getText().toString());
        newBusiness.setPostalAddress(bPostalAddressET.getEditText().getText().toString());

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

//        HintAdapter hintAdapter = new HintAdapter(getActivity(), android.R.layout.simple_spinner_item, businessTypes);
//        hintAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        businessTypeSpinner.setAdapter(hintAdapter);
//        businessTypeSpinner.setSelection(businessTypes.size() - 1);

        businessTypeSpinner.setVisibility(View.GONE);
        businessTypeTV.setText(businessTypes.get(0).getName());
        businessTypeTV.setVisibility(View.VISIBLE);


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


    ///////////////////////////////////////////////////////////////

    private boolean validateBusinessTitle() {

        String emailET = bTitle.getEditText().getText().toString().trim();
        if (emailET.isEmpty()) {
            bTitle.setError("* Business name cant be empty");
            return false;
        } else {
            bTitle.setError(null);
            return true;
        }
    }

    private boolean validateBusinessPhoneNumber() {
        String phoneNumberTemp = bPhoneNumberET.getEditText().getText().toString().trim();
        if (phoneNumberTemp.isEmpty()) {
            bPhoneNumberET.setError("* Phone number cant be empty");
            return false;
        } else if (phoneNumberTemp.length() < 10) {
            bPhoneNumberET.setError("* Phone number should be 10 digits");
            return false;
        } else {
            bPhoneNumberET.setError(null);
            return true;
        }

    }

    private boolean validateBusinessEmail() {
        String emailTemp = bEmailET.getEditText().getText().toString().trim();
        if (emailTemp.isEmpty()) {
            bEmailET.setError("* Email cant be empty");
            return false;
        } else if (!emailTemp.contains("@")) {
            bEmailET.setError("* Incorrect email address");
            return false;
        } else {
            bEmailET.setError(null);
            return true;
        }
    }

    private boolean validateBusinessWebAddress() {
        String webaddrtemp = bWebAddressET.getEditText().getText().toString().trim();
        if (webaddrtemp.isEmpty()) {
            bWebAddressET.setError("* Website address cant be empty");
            return false;
        } else {
            bWebAddressET.setError(null);
            return true;
        }

    }

    private boolean validateBusinessPostalAddress() {
        String adddrtemp = bPostalAddressET.getEditText().getText().toString().trim();
        if (adddrtemp.isEmpty()) {
            bPostalAddressET.setError("* Address cant be empty");
            return false;
        } else {
            bPostalAddressET.setError(null);
            return true;
        }

    }

    public boolean submitBusinessInfo(View v) {
        if (validateBusinessTitle() & validateBusinessPhoneNumber() & validateBusinessEmail() & validateBusinessWebAddress()
                & validateBusinessPostalAddress()) {
            return true;
        } else {
            return false;
        }
    }
}
