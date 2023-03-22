package com.example.indiasocial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.indiasocial.databinding.ActivitySettingsEditBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class SettingsEdit extends AppCompatActivity {

    ActivitySettingsEditBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Hiding the navigation bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // invoking the following
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // on click event on addImg image btn
        binding.addImg.setOnClickListener(v -> {

            // Rules to take or select photo form camera or gallery
            ImagePicker.with(SettingsEdit.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        binding.save.setOnClickListener( v ->{

            String UserName = binding.name.getText().toString();
            String Bio = binding.bio.getText().toString();

            database.getReference().child("Register Users").child(auth.getCurrentUser().getUid()).child("userName").setValue(UserName);
            database.getReference().child("Register Users").child(auth.getCurrentUser().getUid()).child("bio").setValue(Bio);
            Toast.makeText(SettingsEdit.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsEdit.this, Settings.class);
            startActivity(intent);
            finish();
        });

    }

    // taking the image and setting on the profile in the setting Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // saving the image in uri variable
        assert data != null;
        Uri uri = data.getData();
        // saving the image in the profile in the setting Activity
        binding.circleImageView.setImageURI(uri);

        // creating the reference in firebase storage where the images of the user is going to save
        final StorageReference reference = storage
                .getReference().child("Register Users").child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("profilePic");



        // putting the file in the reference and checking that the task was successful or not
        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> {

            //downloading the url of the image saved in the database
            reference.getDownloadUrl().addOnSuccessListener(uri1 -> {

                // now storing the url of the image in the realtime database
                database.getReference().child("Register Users").child(auth.getCurrentUser().getUid()).child("profilePic").setValue(uri1.toString())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SettingsEdit.this, "Image Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

        });
    }
}