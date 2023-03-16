package com.example.indiasocial;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.indiasocial.Adapter.FragmentAdapter;
import com.example.indiasocial.Moduls.Users;
import com.example.indiasocial.databinding.ActivityHomeBinding;
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

public class Home extends AppCompatActivity {

    ActivityHomeBinding binding;
    FirebaseAuth auth;

    private String  userName, bio;
    private CircleImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference().child("Register Users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("profilePic");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                Picasso.get().load(value).into(binding.profileImg);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(Home.this, "Please Update Profile Picture", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            Toast.makeText(Home.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
        }else {
            showUserProfile(firebaseUser);
        }

        // View Pager for People in chat box
        binding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);

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
                    userName = readUserDetails.getUserName();
                    bio = readUserDetails.getBio();

                    // set the data in the user profile
                    binding.username.setText(userName.toUpperCase(Locale.ROOT));
                    binding.bio.setText(bio);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.Settings:
                Intent intent1 = new Intent(Home.this, Settings.class);
                startActivity(intent1);
                break;

            case R.id.LogOut:
                 auth.signOut();
                Intent intent = new Intent(Home.this, Sign_in.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                 break;
        }

        return super.onOptionsItemSelected(item);
    }
}