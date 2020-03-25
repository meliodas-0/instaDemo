package com.example.instagramdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramdemo.Fragments.ProfileFragment;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    ImageView save, profilePic;
    TextView changeProfilePicTextView;
    MaterialEditText fullname, username, bio;

    ParseUser parseUser;

    String name, uName, bioString, oldUsername;
    Bitmap pic;
    Boolean picChanged = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            changeProfilePicTextViewClicked(new View(getApplicationContext()));
        }
    }

    public void changeProfilePicTextViewClicked(View view){

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {

            CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(EditProfileActivity.this);
        }

    }

    public void save(View view){
        name = fullname.getText().toString();
        uName = username.getText().toString();
        bioString = bio.getText().toString();

        parseUser.setUsername(uName);
        parseUser.put("fullname", name);
        parseUser.put("bio", bioString);
        if(picChanged){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pic.compress(Bitmap.CompressFormat.JPEG,100, stream);
            byte[] bytes = stream.toByteArray();
            ParseFile file = new ParseFile("profilePic", bytes);
            parseUser.put("profilePic", file);
        }
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        if(!oldUsername.equals(uName)){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
            query.whereEqualTo("commentBy", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects.size()>0){
                        for(ParseObject object : objects){
                            object.put("commentBy", uName);
                            object.saveInBackground();
                        }
                    }
                }
            });
            query = ParseQuery.getQuery("Comments");
            query.whereEqualTo("publisherId", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null){
                        for(ParseObject object : objects){
                            object.put("publisherId", uName);
                            object.saveInBackground();
                        }
                    }else{
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            query = ParseQuery.getQuery("FollowList");
            query.whereEqualTo("follower", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null){
                        for(ParseObject object : objects){
                            object.put("follower", uName);
                            object.saveInBackground();
                        }
                    }else{
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            query = ParseQuery.getQuery("FollowList");
            query.whereEqualTo("follows", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null){
                        for(ParseObject object : objects){
                            object.put("follows", uName);
                            object.saveInBackground();
                        }
                    }else{
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            query = ParseQuery.getQuery("Likes");
            query.whereEqualTo("likedBy", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null){
                        for(ParseObject object : objects){
                            object.put("likedBy", uName);
                            object.saveInBackground();
                        }
                    }else{
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            query = ParseQuery.getQuery("Posts");
            query.whereEqualTo("username", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null){
                        for(ParseObject object : objects){
                            object.put("username", uName);
                            object.saveInBackground();
                        }
                    }else{
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            query = ParseQuery.getQuery("Saves");
            query.whereEqualTo("postBy", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null){
                        for(ParseObject object : objects){
                            object.put("postBy", uName);
                            object.saveInBackground();
                        }
                    }else{
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            query = ParseQuery.getQuery("Saves");
            query.whereEqualTo("savedBy", oldUsername);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null && objects != null){
                        for(ParseObject object : objects){
                            object.put("savedBy", uName);
                            object.saveInBackground();
                        }
                    }
                    else{
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        Uri selectedImage = result.getUri();
                        pic = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImage));
                        profilePic.setImageURI(selectedImage);
                        Toast.makeText(this, "Profile Changed", Toast.LENGTH_SHORT).show();
                        picChanged = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profilePic = findViewById(R.id.activityEditProfileProfilePicImageView);
        save = findViewById(R.id.activityEditProfileSaveImageView);
        changeProfilePicTextView = findViewById(R.id.activityEditProfileChangeProfilePicTextView);
        fullname = findViewById(R.id.activityEditProfileFullnameEditText);
        username = findViewById(R.id.activityEditProfileUsernameEditText);
        bio = findViewById(R.id.activityEditProfileBioEditText);

        parseUser = ParseUser.getCurrentUser();

        name = parseUser.getString("fullname");
        oldUsername = uName = parseUser.getUsername();
        bioString = parseUser.getString("bio");
        ParseFile file = (ParseFile) parseUser.get("profilePic");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e == null){
                    pic = BitmapFactory.decodeByteArray(data, 0, data.length);
                    profilePic.setImageBitmap(pic);
                }
            }
        });

        fullname.setText(name);
        username.setText(uName);
        bio.setText(uName);

        Toolbar toolbar = findViewById(R.id.activityEditProfileToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
