package com.example.indiasocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private Button reset;
    private EditText email;

    ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        reset = findViewById(R.id.reset);
        email = findViewById(R.id.email);
        progressBar = findViewById(R.id.progressBar);


        reset.setOnClickListener(V->{

            String mail = email.getText().toString();

            if(TextUtils.isEmpty(mail)){
                email.setError("Email id required");
                email.requestFocus();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                email.setError("Enter valid Email");
                email.requestFocus();
            }else {
                progressBar.setVisibility(View.VISIBLE);
                auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(mail).addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        Toast.makeText(ForgetPassword.this, "Email send Go Reset", Toast.LENGTH_SHORT).show();

                        // sign in activity
                        Intent intent = new Intent(ForgetPassword.this, Sign_in.class);
                        startActivity(intent);
                        finish();
                        progressBar.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(ForgetPassword.this, "No account with the Email, Please enter Correct Email", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                });
            }
        });
    }
}