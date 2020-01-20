package com.labournet.nyrah.account.ui.LoginActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.KeyboardView;
import com.labournet.nyrah.account.interfaces.LoginCallBacks;


public class LoginPINFragment extends Fragment {

    KeyboardView pinView;
    Button newUserButton;

    String mobileNumber;
    String PIN;

    TextView forgotPassword;

    LoginCallBacks loginCallBacks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_pin_fragment, container, false);
        pinView = view.findViewById(R.id.pin_view);
        newUserButton = view.findViewById(R.id.new_user);
        forgotPassword = view.findViewById(R.id.forgot_password);

        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginCallBacks.onSignUp();
                PIN = pinView.getInputText();
            }
        });
        pinView.addPINChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pinView.showErrorMessage(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginCallBacks) {
            loginCallBacks = (LoginCallBacks) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " Must implement BusinessRegListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginCallBacks = null;
    }

    public void showError() {
        pinView.showError();
        pinView.showErrorMessage(true);
        forgotPassword.setVisibility(View.VISIBLE);
    }

    public void showRegisterButton() {
        newUserButton.setVisibility(View.VISIBLE);
    }

    private TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    public void clearPIN() {
        pinView.showError();
    }
}
