package com.labournet.nyrah.account.ui.LoginActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.labournet.nyrah.R;
import com.labournet.nyrah.Utils.UserSessionManager;
import com.labournet.nyrah.account.interfaces.LoginCallBacks;
import com.labournet.nyrah.account.interfaces.NetworkChangeCallBack;
import com.labournet.nyrah.account.model.Login;

public class LoginPhoneNumberFragment extends Fragment {

    EditText loginPhoneNumber;
    EditText pinET;

    TextView errorTV;
    Button signUpButton;

    String phoneNumber;

    ImageView resetApp;


    UserSessionManager session = null;

    LoginCallBacks loginCallBacks;
    NetworkChangeCallBack networkChangeCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_phonenumber_fragment, container, false);

        loginPhoneNumber = view.findViewById(R.id.login_phoneNumber);
        pinET = view.findViewById(R.id.password_field);
        signUpButton = view.findViewById(R.id.signUp_button);
        errorTV = view.findViewById(R.id.login_pin_error);

        resetApp = view.findViewById(R.id.reset_application);

        session = new UserSessionManager(getActivity());

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginCallBacks.onSignUp();
            }
        });

        resetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Press Confirm to reset application")
                        .setTitle("Reset application")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loginCallBacks.resetApp();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        pinET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 4) {
                    phoneNumber = loginPhoneNumber.getText().toString().trim();
                    if (phoneNumber.equals("")) {
                        Toast.makeText(getActivity(), "Phone number is empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Login login = new Login(phoneNumber, pinET.getText().toString());
                        loginCallBacks.onLogin(login);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 10 && pinET.getText().toString().length() == 4) {
                    phoneNumber = loginPhoneNumber.getText().toString().trim();
                    Login login = new Login(phoneNumber, pinET.getText().toString());
                    loginCallBacks.onLogin(login);
                }
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
        } else
            throw new RuntimeException(context.toString() + " Must implement BusinessRegListener");

        if (context instanceof NetworkChangeCallBack) {
            networkChangeCallBack = (NetworkChangeCallBack) context;
        }
//        else
//            throw new RuntimeException(context.toString() + "Must implement BusinessRegListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginCallBacks = null;
        networkChangeCallBack = null;
    }

    public void showRegisterButton() {
        signUpButton.setVisibility(View.VISIBLE);
    }

    public void showError(boolean showError, TranslateAnimation animation) {
        if (showError) {
            errorTV.setVisibility(View.VISIBLE);
            pinET.setText("");
            pinET.startAnimation(animation);
        } else
            errorTV.setVisibility(View.GONE);
    }

    public Login getLoginData() {
        Login loginData = new Login(loginPhoneNumber.getText().toString(), pinET.getText().toString());
        return loginData;
    }
}
