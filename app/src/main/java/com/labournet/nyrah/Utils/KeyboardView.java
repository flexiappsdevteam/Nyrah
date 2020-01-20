package com.labournet.nyrah.Utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;

import com.labournet.nyrah.R;

/***
 * Created by JEFFIN JOSE
 * FlexiApps solutions*
 * 8/12/2019**/


public class KeyboardView extends FrameLayout implements View.OnClickListener {
    private EditText mPINField;
    private TextView errorMessageTV;

    KeyboardViewCallBack keyboardViewCallBack;

    public KeyboardView(Context context) {
        super(context);
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.keyboard_layout, this);
        initViews();
    }

    private void initViews() {
        mPINField = $(R.id.password_field);
        errorMessageTV = $(R.id.errorTV);
        $(R.id.t9_key_0).setOnClickListener(this);
        $(R.id.t9_key_1).setOnClickListener(this);
        $(R.id.t9_key_2).setOnClickListener(this);
        $(R.id.t9_key_3).setOnClickListener(this);
        $(R.id.t9_key_4).setOnClickListener(this);
        $(R.id.t9_key_5).setOnClickListener(this);
        $(R.id.t9_key_6).setOnClickListener(this);
        $(R.id.t9_key_7).setOnClickListener(this);
        $(R.id.t9_key_8).setOnClickListener(this);
        $(R.id.t9_key_9).setOnClickListener(this);
        $(R.id.t9_key_clear).setOnClickListener(this);
        $(R.id.t9_key_backspace).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // handle number button click
        keyboardViewCallBack = (KeyboardViewCallBack) v.getContext();
        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            mPINField.append(((TextView) v).getText());
            String h = String.valueOf(mPINField.getText().length());
            if (h.equals("4"))
                keyboardViewCallBack.onComplete(mPINField.getText().toString());

            return;
        }
        switch (v.getId()) {
            case R.id.t9_key_clear: { // handle clear button
                mPINField.setText(null);
            }
            break;
            case R.id.t9_key_backspace: { // handle backspace button
                // delete one character
                Editable editable = mPINField.getText();
                int charCount = editable.length();
                if (charCount > 0) {
                    editable.delete(charCount - 1, charCount);
                }
            }
            break;
        }
    }


    public String getInputText() {
        return mPINField.getText().toString();
    }

    public void showError() {
        mPINField.setText("");
        mPINField.startAnimation(shakeError());
    }

    public void clearPIN() {
        mPINField.setText("");
    }

    public void setErrorMessageTV(String errorMessage) {
        errorMessageTV.setText(errorMessage);
    }

    public void showErrorMessage(boolean show) {
        if (show)
            errorMessageTV.setVisibility(VISIBLE);
        else errorMessageTV.setVisibility(INVISIBLE);
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }

    private TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    public void addPINChangeListener(TextWatcher textWatcher) {
        mPINField.addTextChangedListener(textWatcher);
    }
}