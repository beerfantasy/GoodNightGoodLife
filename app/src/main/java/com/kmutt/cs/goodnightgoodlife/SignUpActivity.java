package com.kmutt.cs.goodnightgoodlife;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    private EditText email,username,password;
    private Button regButton;
    private TextView textLogin;
    private FirebaseAuth firebaseAuth;
    private Map<String, Object> user= new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setupUIViews();
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(c);
        user.put("date", formattedDate);


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    //Upload to database
                    final String user_email = email.getText().toString().trim();
                    final String user_name = username.getText().toString().trim();
                    String user_password = password.getText().toString().trim();


                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                db.collection(user_email).document("Date Created").set(user);
                                startActivity(new Intent(getApplicationContext(),Login.class));
                            }
                            else {
                                Toast.makeText(SignUpActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }

    private void setupUIViews() {
        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        regButton = (Button) findViewById(R.id.regButton);
        textLogin = (TextView) findViewById(R.id.text_login);
    }

    private boolean validate() {
        boolean result = false;

        String name = username.getText().toString();
        String pass = password.getText().toString();
        String mail = email.getText().toString();

        if(name.isEmpty() || pass.isEmpty() || mail.isEmpty()) {
            Toast.makeText(this,"Please enter all of the information.", Toast.LENGTH_SHORT).show();
        }
        else {
            result = true;
        }
        return result;
    }
}
