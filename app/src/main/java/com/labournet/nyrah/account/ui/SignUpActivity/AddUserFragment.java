package com.labournet.nyrah.account.ui.SignUpActivity;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.EnlivenTextInputLayout;
import com.labournet.nyrah.Utils.MaskWatcher;
import com.labournet.nyrah.account.interfaces.SignUpCallBacks;
import com.labournet.nyrah.account.model.User;

public class AddUserFragment extends Fragment {

    SignUpCallBacks signUpCallBacks;

    User newUser;
    private LinearLayout rootView;
    private Button saveButton;

    private EnlivenTextInputLayout userFormNameET;
    private EnlivenTextInputLayout userFormPhoneNumberET;
    private EnlivenTextInputLayout userFormEmailET;
    private EnlivenTextInputLayout userFormAddressET;
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

    private ScrollView userFormScrollView;


    private TextView userAddedTV;
    private TextView PIN_errorTV;

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
            userFormScrollView.setVisibility(View.VISIBLE);
            userFormNameET.requestFocus();
            userFormPhoneNumberET.getEditText().setText(phoneNumber);
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
        PIN_errorTV = view.findViewById(R.id.PIN_errorTV);
        userFormScrollView = view.findViewById(R.id.userFormScrollView);

        userFormContainer = view.findViewById(R.id.user_form);
        userFormNameET = view.findViewById(R.id.text_input_uName);
        userFormPhoneNumberET = view.findViewById(R.id.text_input_uPhoneNumber);
        userFormEmailET = view.findViewById(R.id.text_input_uEmail);
        userFormAddressET = view.findViewById(R.id.text_input_uAddress);
        userFormNewPIN_ET = view.findViewById(R.id.set_pin_field);
        userFormConfirmPIN_ET = view.findViewById(R.id.confirm_pin_field);
        saveButton = view.findViewById(R.id.save_button);

        userAddedTV = view.findViewById(R.id.user_addedTV);

        userFormPhoneNumberET.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    validatePhoneNumber();
                    if (userFormNameET.getEditText().getText().toString().isEmpty())
                        userFormNameET.requestFocus();
                    else
                        userFormEmailET.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        userFormNewPIN_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 4)
                    userFormConfirmPIN_ET.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        newUser = new User();

        if (otpPhoneNumberET.getText().toString().length() < 10)
            sendOTPButton.setVisibility(View.GONE);

        otpPhoneNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10) {
                    sendOTPButton.setVisibility(View.VISIBLE);
                    otpVerifyButton.setVisibility(View.GONE);
                    resendOTPTV.setVisibility(View.GONE);
                    otpFieldContainer.setVisibility(View.GONE);

                } else
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
                if (submitUserInfo(view)) {
                    user = getFormData();
                    signUpCallBacks.onSignUpUser(user);
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
                        PIN_errorTV.setVisibility(View.GONE);
                        closeKeyBoard();
                    } else {
                        userFormNewPIN_ET.setText("");
                        userFormConfirmPIN_ET.setText("");
                        PIN_errorTV.setVisibility(View.VISIBLE);
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

        newUser.setUserName(userFormNameET.getEditText().getText().toString());
        newUser.setUserMobileNumber(userFormPhoneNumberET.getEditText().getText().toString());
        newUser.setEmailAddress(userFormEmailET.getEditText().getText().toString());
        newUser.setPostalAddress(userFormAddressET.getEditText().getText().toString());
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
        resendOTPTV.setVisibility(View.VISIBLE);
        setCounterResendOTP(2000, 1000);
        resendOTPTV.setClickable(false);

        setCounterResendOTP(20000, 1000);
    }

    public void onSuccessOTPVerification() {
        titleTV.setText("Phone number verified");
        otpParent.setVisibility(View.GONE);
        otpTitleHandler.postDelayed(otpTitleRunnable, 2000);

    }

    public void onSaveUserTitleChange(String title) {

        userFormScrollView.setVisibility(View.GONE);
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


    ///////////////////////////////////////////////////////////////

    private boolean validateUserName() {

        String emailET = userFormNameET.getEditText().getText().toString().trim();
        if (emailET.isEmpty()) {
            userFormNameET.setError("* Name cant be empty");
            return false;
        } else {
            userFormNameET.setError(null);
            return true;
        }
    }

    private boolean validatePIN() {

        userFormNewPIN = userFormNewPIN_ET.getText().toString().trim();
        userFormConfirmPIN = userFormConfirmPIN_ET.getText().toString().trim();

        if (userFormNewPIN.isEmpty() || userFormConfirmPIN.isEmpty()) {
            userFormNewPIN_ET.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_error_bg));
            userFormConfirmPIN_ET.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_error_bg));
            userFormNewPIN_ET.startAnimation(shakeError());
            userFormConfirmPIN_ET.startAnimation(shakeError());
            Toast.makeText(getActivity(), "Enter your PIN", Toast.LENGTH_SHORT).show();
            return false;
        } else if (checkNewPassword(userFormNewPIN, userFormConfirmPIN))
            return true;
        else {
            userFormNewPIN_ET.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_error_bg));
            userFormConfirmPIN_ET.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.et_error_bg));
            userFormNewPIN_ET.startAnimation(shakeError());
            userFormConfirmPIN_ET.startAnimation(shakeError());
            Toast.makeText(getActivity(), "Enter your PIN", Toast.LENGTH_SHORT).show();
            return false;

        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumberTemp = userFormPhoneNumberET.getEditText().getText().toString().trim();
        if (phoneNumberTemp.isEmpty()) {
            userFormPhoneNumberET.setError("* Phone number cant be empty");
            return false;
        } else if (phoneNumberTemp.length() < 10) {
            userFormPhoneNumberET.setError("* Phone number should be 10 digits");
            return false;
        } else {
            userFormPhoneNumberET.setError(null);
            return true;
        }

    }

    private boolean validateUserEmail() {
        String emailTemp = userFormEmailET.getEditText().getText().toString().trim();
        if (emailTemp.isEmpty()) {
            userFormEmailET.setError("* Email cant be empty");
            return false;
        } else if (!emailTemp.contains("@")) {
            userFormEmailET.setError("* Incorrect email address");
            return false;
        } else {
            userFormEmailET.setError(null);
            return true;
        }
    }

    private boolean validateUserPostalAddress() {
        String adddrtemp = userFormAddressET.getEditText().getText().toString().trim();
        if (adddrtemp.isEmpty()) {
            userFormAddressET.setError("* Address cant be empty");
            return false;
        } else {
            userFormAddressET.setError(null);
            return true;
        }

    }

    public boolean submitUserInfo(View v) {
        if (validateUserName() & validateUserPostalAddress() & validatePhoneNumber() & validateUserEmail()
                & validatePIN()) {
            Toast.makeText(getActivity(), "VALID", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}
