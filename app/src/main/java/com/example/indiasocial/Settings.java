package com.example.indiasocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.indiasocial.Moduls.Users;
import com.example.indiasocial.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    private String  Name, Bio, profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.edit.setOnClickListener(V ->{

            Intent intent = new Intent(Settings.this, SettingsEdit.class);
            startActivity(intent);
            finish();

        });

        auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            Toast.makeText(Settings.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }else {
            showUserProfile(firebaseUser);
        }
    }

    // Method to show the User profile in the home section
    private void showUserProfile(FirebaseUser firebaseUser) {

        String userId = auth.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Register Users");

        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users readUserDetails = snapshot.getValue(Users.class);
                if (readUserDetails != null){
                    // get data form database
                    Name = readUserDetails.getUserName();
                    Bio = readUserDetails.getBio();
                    Picasso.get().load(readUserDetails.getProfilePic())
                            .placeholder(R.drawable.test_image)
                                    .into(binding.settingProfileImg);

                    // set the data in the user profile
                    binding.sName.setText(Name.toUpperCase(Locale.ROOT));
                    binding.sBio.setText(Bio);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Settings.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}