package com.example.indiasocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.indiasocial.Moduls.Users;
import com.example.indiasocial.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Sign_up extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private ProgressBar progressBar;

    // firebase Variables
    private FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Hide the Action bar
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Already have a account
        binding.btnHaveAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_up.this, Sign_in.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Event on Sign Up Button
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Getting the Data By id from the xml and storing them in the string variable
                String userName = binding.fullName.getText().toString();
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                String ConPassword = binding.conPassword.getText().toString();

                // on Button click checking the Validation
                if(TextUtils.isEmpty(userName)){
                    binding.fullName.setError("Full Name is required");
                    binding.fullName.requestFocus();
                }else if(TextUtils.isEmpty(email)){
                    binding.email.setError("Email id required");
                    binding.email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.email.setError("Enter valid Email");
                    binding.email.requestFocus();
                }else if(TextUtils.isEmpty(password)){
                    binding.password.setError("Enter the Password");
                    binding.password.requestFocus();
                }else if(password.length() < 6){
                    binding.conPassword.setError("Password is too week");
                    binding.conPassword.requestFocus();
                }else if(TextUtils.isEmpty(ConPassword)){
                    binding.conPassword.setError("Password conformation required");
                    binding.conPassword.requestFocus();
                }else if(!ConPassword.equals(password)){
                    binding.conPassword.setError("Password Not match");
                    binding.conPassword.requestFocus();
                    // Clear the Entered Password
                    binding.password.clearComposingText();
                    binding.conPassword.clearComposingText();
                }else {

                    // Calling the method reg
                    reg(userName,email,password);
                    binding.progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void reg(String userName, String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // Creating a object of the class Users
                            Users user = new Users(userName, email, password);

                            // taking the reference database for Register user
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Register Users");

                            reference.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(Sign_up.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                                        binding.progressBar.setVisibility(View.GONE);

                                        // after the success full registration and saving the data into the database sending the user to the
                                        // sign in activity
                                        Intent intent = new Intent(Sign_up.this, Sign_in.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        binding.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Sign_up.this, "Registration Field", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(Sign_up.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}