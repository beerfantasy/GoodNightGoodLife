package com.kmutt.cs.goodnightgoodlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChangeAuthCodeActivity extends AppCompatActivity {

    TextView authInput;
    Button saveAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeauthcode);
        setTitle("Authorization Code Setting");

        setupUIView();

        saveAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.authCode = authInput.getText()+"";
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });
    }

    private void setupUIView() {
        authInput = (TextView) findViewById(R.id.auth);
        saveAuth = (Button) findViewById(R.id.saveAuth);
    }

}
