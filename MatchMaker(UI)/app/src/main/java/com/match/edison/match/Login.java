package com.match.edison.match;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void startRegister(View view) {

        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        finish();
    }

    public void verifyAndStart(View view) {
        Intent i = new Intent (Login.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
