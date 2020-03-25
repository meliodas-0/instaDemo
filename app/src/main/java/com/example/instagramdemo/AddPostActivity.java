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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddPostActivity extends AppCompatActivity {

    ImageView imageSelected, close;
    EditText description;
    TextView post;
    Uri selectedImage = null;

    public void close(View view){
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void post(View view){

        if(selectedImage != null){
            String des = description.getText().toString();

            try {
                Bitmap image = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getApplicationContext().getContentResolver(),selectedImage));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG,100, stream);
                byte[] bytes =stream.toByteArray();
                ParseFile file = new ParseFile("image.jpeg",bytes);

                ParseObject object = new ParseObject("Posts");
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("image",file);
                object.put("description", des);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(AddPostActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Error Please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto(new View(getApplicationContext()));
            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public void getPhoto(View view){

        CropImage.activity()
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                selectedImage = result.getUri();
                imageSelected.setImageURI(selectedImage);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        imageSelected = findViewById(R.id.imageAdded);
        close = findViewById(R.id.close);
        description = findViewById(R.id.description);
        post = findViewById(R.id.POST);

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else{
            getPhoto(imageSelected);
        }

    }
}
