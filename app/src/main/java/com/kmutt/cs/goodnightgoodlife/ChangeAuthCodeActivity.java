package com.kmutt.cs.goodnightgoodlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChangeAuthCodeActivity extends AppCompatActivity {

    TextView authInput;
    Button saveAuth;
    private Map<String, Object> auth = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeauthcode);
        setTitle("Authorization Code Setting");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);

        setupUIView();

        saveAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.authCode = authInput.getText()+"";
                auth.put("authToken", authInput.getText()+"");
                if(user != null) {
                    db.collection(user.getEmail().toString().trim()).document("Authentication Token").set(auth);
                }else{
                    Toast.makeText(getApplicationContext(), "Cannot find instance of this user", Toast.LENGTH_LONG).show();
                }
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });
    }

    private void setupUIView() {
        authInput = (TextView) findViewById(R.id.auth);
        saveAuth = (Button) findViewById(R.id.saveAuth);
    }

}
