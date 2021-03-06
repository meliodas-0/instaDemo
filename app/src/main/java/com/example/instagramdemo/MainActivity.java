package com.example.instagramdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.instagramdemo.Fragments.HomeFragment;
import com.example.instagramdemo.Fragments.NotificationFragment;
import com.example.instagramdemo.Fragments.ProfileFragment;
import com.example.instagramdemo.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    public static int globalStackedFragments = 0;
    public static FragmentManager fragmentManager;
    private Toolbar toolbar;
    public boolean isTaskRoot;

    @Override
    protected void onStart() {
        super.onStart();
        if(ParseUser.getCurrentUser() == null){
            Intent intent = new Intent(getApplicationContext(),LoginSignupActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isTaskRoot = isTaskRoot();
        bottomNavigationView = findViewById(R.id.bottomIconBar);
        toolbar = findViewById(R.id.activityMainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(!isTaskRoot && getSupportFragmentManager().getBackStackEntryCount()!=0){
            toolbar.setVisibility(View.VISIBLE);
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.addPost:
                        selectedFragment = null;
                        Intent intent = new Intent(getApplicationContext(),AddPostActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.activity:
                        selectedFragment = new NotificationFragment();
                        break;
                    case R.id.profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                if(selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,selectedFragment).commit();
                }

                return true;
            }
        });

        fragmentManager = getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                globalStackedFragments = fragmentManager.getBackStackEntryCount();
            }
        });

        Bundle intent = getIntent().getExtras();
        if(intent != null && !isTaskRoot){
            Log.i("Intent", intent.toString());
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment()).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();
        }
    }
}
