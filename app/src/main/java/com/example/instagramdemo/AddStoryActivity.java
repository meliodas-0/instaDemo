package com.example.instagramdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddStoryActivity extends AppCompatActivity {

    private ParseUser parseUser;
    private Uri selectedImage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        parseUser = ParseUser.getCurrentUser();
        progressBar = findViewById(R.id.activityAddStoryProgressBar);

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            getImage();
        }else{
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length!=0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getImage();
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                selectedImage = result.getUri();
            }
        }
    }

    private void getImage() {

        CropImage.activity()
                .setAspectRatio(9, 16)
                .start(AddStoryActivity.this);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void publishStory(View view){
        try {
            progressBar.setVisibility(View.VISIBLE);
            Bitmap storyImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), selectedImage));
            ParseObject object = new ParseObject("Stories");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            storyImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byte[] bytes = stream.toByteArray();
            ParseFile file = new ParseFile(bytes);
            object.put("storyBy", parseUser.getUsername());
            object.put("seenBy", new ArrayList<String>());
            object.put("story", file);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        Toast.makeText(getApplicationContext(), "Posted", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
