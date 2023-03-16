package com.example.indiasocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.indiasocial.Moduls.Users;

import com.example.indiasocial.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Sign_in extends AppCompatActivity {

    ActivitySignInBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    private ProgressBar progressBar;

    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Hide the Action bar...
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Forget Password
        binding.forgetPassword.setOnClickListener(v->{
            Toast.makeText(Sign_in.this, "Reset Password", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Sign_in.this,ForgetPassword.class));
        });




        // sign in btn ...
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();

                // on Button click checking the Validation
                if(TextUtils.isEmpty(email)){
                    binding.email.setError("Enter valid Email");
                    binding.email.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.email.setError("Enter valid Email");
                    binding.email.requestFocus();
                }else if(TextUtils.isEmpty(password)){
                    binding.password.setError("Enter the Password");
                    binding.password.requestFocus();
                }else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }

            private void loginUser(String email, String password) {

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(Sign_in.this, "Welcome", Toast.LENGTH_SHORT).show();
                                // home activity
                                Intent intent = new Intent(Sign_in.this, Home.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(Sign_in.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        // Btn for sending the user to Sign In Activity...
        binding.btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_in.this, Sign_up.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Google Sign in request ...
        signInRequest();

        // on click googleSignInButton
        binding.googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                signIn();
            }
        });
    }
    // SignInRequest method to google the taking data from google
    private void signInRequest(){

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    // singIn method
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    FirebaseUser user = mAuth.getCurrentUser();

                    Users users = new Users();
                    users.setUserId(user.getUid());
                    users.setUserName(user.getDisplayName());
                    users.setEmail(user.getEmail());
                    users.setProfilePic(user.getPhotoUrl().toString());

                    firebaseDatabase.getReference().child("Register Users").child(user.getUid()).setValue(users);

                    Toast.makeText(Sign_in.this, "Sign In with Google", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),Home.class);
                    binding.progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                }else {
                    Toast.makeText(Sign_in.this, "Authentication is failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registrationUser(View view) {
    }
}