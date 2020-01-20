package com.labournet.nyrah.account.ui.SignUpActivity;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.labournet.nyrah.R;
import com.labournet.nyrah.account.interfaces.SignUpCallBacks;

public class ProductKeyFragment extends Fragment {

    SignUpCallBacks signUpCallBacks;
    private TextView textView;
    private TextView productKeyErrorTV;
    EditText productKeyET;
    private Button productKeyButton;
    RelativeLayout rootView;
    boolean businessExisting = false;

    String productKey;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_product_key_fragment, container, false);
        textView = view.findViewById(R.id.productKeyTV);
        rootView = view.findViewById(R.id.root);
        productKeyET = view.findViewById(R.id.product_key_ET);
        productKeyButton = view.findViewById(R.id.product_key_button);
        productKeyErrorTV = view.findViewById(R.id.productKey_info);

        ViewGroup layout = rootView;
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        if (productKeyET.getText().toString().trim().length() == 0)
            productKeyErrorTV.setVisibility(View.GONE);


        productKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                productKey = productKeyET.getText().toString().trim();
                if (productKey.equals("")) {
                    Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
                } else {
                    signUpCallBacks.onVerifyProductKey(productKey);
                    hideKeyboardFrom(getActivity(), view);
                }


            }
        });

        productKeyET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 0)
                    productKeyErrorTV.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SignUpCallBacks) {
            signUpCallBacks = (SignUpCallBacks) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " Must implement ProductKeyListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        signUpCallBacks = null;
    }


    public void updateProductKeyEditText(CharSequence newText) {
        productKeyET.setText(newText);
    }

    public void updateProductKeyTextView(String text) {
        textView.setText(text);
    }

    public void updateViews(String update) {

        textView.setTextSize(25);
        productKeyET.setVisibility(View.GONE);
        productKeyButton.setVisibility(View.GONE);
        textView.setText(update);

    }

    public void updateForInvalidProductKey(String updateString) {
        signUpCallBacks.onInvalidProductKey();
        textView.setTextSize(25);
        productKeyET.setVisibility(View.GONE);
        productKeyButton.setVisibility(View.GONE);
        textView.setText(updateString);
    }

    public String getProductKey() {
        return productKeyET.getText().toString().trim();
    }

    public void hide() {
        view.setVisibility(View.GONE);
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void errorTV(int visibility) {
        productKeyErrorTV.setVisibility(visibility);
    }
}
