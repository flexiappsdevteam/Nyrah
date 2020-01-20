package com.labournet.nyrah;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.labournet.nyrah.account.ui.SignUpActivity.SignUpActivity;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        Intent intent = new Intent(FlashActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Intent intent = new Intent(FlashActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
