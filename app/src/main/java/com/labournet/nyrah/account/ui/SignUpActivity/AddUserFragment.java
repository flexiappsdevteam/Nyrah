package com.labournet.nyrah.account.ui.SignUpActivity;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.MaskWatcher;
import com.labournet.nyrah.account.interfaces.SignUpCallBacks;
import com.labournet.nyrah.account.model.User;

public class AddUserFragment extends Fragment {

    SignUpCallBacks signUpCallBacks;

    User newUser;
    private LinearLayout rootView;
    private Button saveButton;

    private EditText userFormNameET;
    private EditText userFormPhoneNumberET;
    private EditText userFormEmailET;
    private EditText userFormDesignationET;
    private EditText userFormNewPIN_ET;
    private EditText userFormConfirmPIN_ET;

    private TextView titleTV;
    private TextView resendOTPTV;
    private LinearLayout otpFieldContainer;
    private LinearLayout otpParent;
    private LinearLayout userFormContainer;
    private EditText otpPhoneNumberET;
    private EditText otpET;
    private Button sendOTPButton;
    private Button otpVerifyButton;


    private TextView userAddedTV;

    private String phoneNumber;
    private String phNosFromDevice;
    private String userFormNewPIN;
    private String userFormConfirmPIN;


    public AddUserFragment(String phoneNumberFomDevice) {
        phNosFromDevice = phoneNumberFomDevice;
    }

    private Handler otpTitleHandler = new Handler();
    private Runnable otpTitleRunnable = new Runnable() {
        @Override
        public void run() {
            titleTV.setVisibility(View.GONE);
            userFormContainer.setVisibility(View.VISIBLE);
            userFormPhoneNumberET.setText(phoneNumber);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_add_user_fragment, container, false);
        rootView = view.findViewById(R.id.rootView);

        ViewGroup layout = rootView;
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        titleTV = view.findViewById(R.id.otp_validation_title);
        otpFieldContainer = view.findViewById(R.id.otp_field_container);
        otpParent = view.findViewById(R.id.otp_validation_parent);
        otpET = view.findViewById(R.id.otp_et_field);
        otpET.addTextChangedListener(new MaskWatcher("____"));
        otpPhoneNumberET = view.findViewById(R.id.otp_validation_number);
        otpPhoneNumberET.setText(phNosFromDevice);
        sendOTPButton = view.findViewById(R.id.send_otp_button);
        resendOTPTV = view.findViewById(R.id.resend_otp_timerTV);
        otpVerifyButton = view.findViewById(R.id.otp_verify_button);

        userFormContainer = view.findViewById(R.id.user_form);
        userFormNameET = view.findViewById(R.id.nameET);
        userFormPhoneNumberET = view.findViewById(R.id.phoneNumberET);
        userFormEmailET = view.findViewById(R.id.emailET);
        userFormDesignationET = view.findViewById(R.id.user_addressET);
        userFormNewPIN_ET = view.findViewById(R.id.set_pin_field);
        userFormConfirmPIN_ET = view.findViewById(R.id.confirm_pin_field);
        saveButton = view.findViewById(R.id.save_button);

        userAddedTV = view.findViewById(R.id.user_addedTV);

        newUser = new User();

        if (otpPhoneNumberET.getText().toString().length() < 10)
            sendOTPButton.setVisibility(View.GONE);

        otpPhoneNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10)
                    sendOTPButton.setVisibility(View.VISIBLE);
                else
                    sendOTPButton.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user;
                if (validateForm()) {
                    user = getFormData();

                    userFormNewPIN = userFormNewPIN_ET.getText().toString().trim();
                    userFormConfirmPIN = userFormConfirmPIN_ET.getText().toString().trim();

                    if (checkNewPassword(userFormNewPIN, userFormConfirmPIN))
                        signUpCallBacks.onSignUpUser(user);
                    else {
                        userFormNewPIN_ET.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_error_bg));
                        userFormConfirmPIN_ET.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_error_bg));
                        userFormNewPIN_ET.startAnimation(shakeError());
                        userFormConfirmPIN_ET.startAnimation(shakeError());
                    }

                }

            }
        });

        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = otpPhoneNumberET.getText().toString().trim();
                signUpCallBacks.onSendOTPSignUp(phoneNumber);
                otpFieldContainer.setVisibility(View.VISIBLE);

            }
        });

        otpVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpCallBacks.onVerifyOTPSignUp(otpET.getText().toString().trim());
            }
        });

        resendOTPTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = otpPhoneNumberET.getText().toString().trim();
                signUpCallBacks.onSendOTPSignUp(phoneNumber);
                setCounterResendOTP(2000, 1000);
                Toast.makeText(getActivity(), "OTP send", Toast.LENGTH_SHORT).show();
            }
        });

        userFormConfirmPIN_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 4) {
                    if (checkPIN(userFormNewPIN_ET.getText().toString().trim(), userFormConfirmPIN_ET.getText().toString().trim())) {
                        closeKeyBoard();
                    } else {
                        userFormNewPIN_ET.startAnimation(shakeError());
                        userFormConfirmPIN_ET.startAnimation(shakeError());
                        userFormConfirmPIN_ET.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_error_bg));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }
    //OnCreateView


    public User getFormData() {

        newUser.setUserName(userFormNameET.getText().toString());
        newUser.setUserMobileNumber(userFormPhoneNumberET.getText().toString());
        newUser.setEmailAddress(userFormEmailET.getText().toString());
        newUser.setPostalAddress(userFormDesignationET.getText().toString());
        newUser.setPIN(userFormNewPIN_ET.getText().toString());

        return newUser;
    }

    public User getNewUserData() {
        return newUser;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SignUpCallBacks) {
            signUpCallBacks = (SignUpCallBacks) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " Must implement AddUserListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        signUpCallBacks = null;
    }

    public void showVerifyButton() {
        otpVerifyButton.setVisibility(View.VISIBLE);
        sendOTPButton.setVisibility(View.GONE);
        resendOTPTV.setClickable(false);

        setCounterResendOTP(20000, 1000);
    }

    public void onSuccessOTPVerification() {
        titleTV.setText("Phone number verified");
        otpParent.setVisibility(View.GONE);
        otpTitleHandler.postDelayed(otpTitleRunnable, 2000);

    }

    public void onSaveUserTitleChange(String title) {

        userFormContainer.setVisibility(View.GONE);
        userAddedTV.setText(title);
        userAddedTV.setVisibility(View.VISIBLE);


    }

    public void onFailureOTPVerification() {
        otpET.setText("");
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        otpET.startAnimation(shakeError());
        Toast.makeText(getActivity(), "Wrong OTP entered,Try again.", Toast.LENGTH_SHORT).show();
    }

    private TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    public void setName(String name) {
        userFormNameET.setText(name);
    }

    public boolean checkNewPassword(String newPassword, String confirmPassword) {
        if (newPassword.equals(confirmPassword))
            return true;
        else
            return false;
    }

    public void setCounterResendOTP(long totalDuration, long interval) {
        new CountDownTimer(totalDuration, interval) {
            @Override
            public void onTick(long l) {
                resendOTPTV.setClickable(false);
                resendOTPTV.setText("Resend OTP in " + l / 1000);
            }

            @Override
            public void onFinish() {
                resendOTPTV.setText("Resend OTP");
                resendOTPTV.setClickable(true);
            }
        }.start();
    }


    public boolean validateForm() {

        boolean val = true;
        if (TextUtils.isEmpty(userFormNameET.getText().toString().trim())) {
            userFormNameET.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            userFormNameET.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(userFormPhoneNumberET.getText().toString().trim())) {
            userFormPhoneNumberET.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            userFormPhoneNumberET.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(userFormEmailET.getText().toString().trim())) {
            userFormEmailET.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            userFormEmailET.startAnimation(shakeError());
            val = false;
        } else val = true;

        if (TextUtils.isEmpty(userFormNewPIN_ET.getText().toString().trim())) {
            userFormNewPIN_ET.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            userFormNewPIN_ET.startAnimation(shakeError());
            val = false;
        } else val = true;
        if (TextUtils.isEmpty(userFormConfirmPIN_ET.getText().toString().trim())) {
            userFormConfirmPIN_ET.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.lighter_red));
            userFormConfirmPIN_ET.startAnimation(shakeError());
            val = false;
        } else val = true;
        return val;
    }

    public void closeKeyBoard() {

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean checkPIN(String newPIN, String confirmPIN) {
        return newPIN.equals(confirmPIN);
    }
}
