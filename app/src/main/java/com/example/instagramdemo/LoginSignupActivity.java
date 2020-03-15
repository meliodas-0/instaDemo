package com.example.instagramdemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class LoginSignupActivity extends AppCompatActivity {

    boolean signup = false;
    TextView signupTextView;
    EditText usernameEdittext;
    EditText passwordEdittext;
    Button loginButton;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.INTERNET},1);
            }
        }
    }

    public void signUpClicked(View view){

        if(signup){
            loginButton.setText("Log in");
            signup = false;
            signupTextView.setText(R.string.signUp);
        }else{
            signup = true;
            signupTextView.setText(R.string.login);
            loginButton.setText("Sign up");
        }
    }

    public void login(View view){
        if(signup == false){
            //login
            String username = usernameEdittext.getText().toString();
            String password = passwordEdittext.getText().toString();
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(getApplicationContext(), "Username or Password cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }
            else{
                pd = new ProgressDialog(LoginSignupActivity.this);
                pd.setMessage("PLEASE WAIT");
                pd.show();
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e == null && user != null){
                            pd.dismiss();
                            Toast.makeText(LoginSignupActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(LoginSignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else{
            String username = usernameEdittext.getText().toString();
            String password = passwordEdittext.getText().toString();
            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(getApplicationContext(), "Username or Password cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }
            else{
                pd = new ProgressDialog(LoginSignupActivity.this);
                pd.setMessage("PLEASE WAIT");
                pd.show();
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            Toast.makeText(LoginSignupActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            finish();
                        }
                    }
                });
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        signupTextView = findViewById(R.id.signupTextView);
        usernameEdittext = findViewById(R.id.usernameEditText);
        passwordEdittext = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        if(checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
        }

//        ParseObject object = new ParseObject("newClass");
//        object.put("u","sample");
//        object.put("inte", 10);
//        object.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null){
//                    Toast.makeText(LoginSignupActivity.this, "SAVED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
//                }else
//                    e.printStackTrace();
//            }
//        });

    }
}
