package com.example.instagramdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.instagramdemo.Adapter.UserAdapter;
import com.example.instagramdemo.Model.User;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowerActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    String id;
    String title;

    List<String> idList;
    UserAdapter adapter;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        toolbar = findViewById(R.id.activityFollowerToolbar);
        recyclerView = findViewById(R.id.activityFollowerRecyclerView);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapter = new UserAdapter(this, userList, false);
        recyclerView.setAdapter(adapter);

        idList = new ArrayList<>();

        switch (title){
            case "likes":
                Log.i("Clicked", "likes");
                getLikes();
                break;
            case "following":
                Log.i("Clicked", "following");
                getFollowings();
                break;
            case "followers":
                Log.i("Clicked", "followers");
                getFollowers();
                break;

        }

    }

    private void getLikes() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Likes");
        query.whereEqualTo("postId", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects != null && objects.size() != 0){
                        for(ParseObject object : objects){
                            idList.add(object.getString("likedBy"));
                        }
                        showUsers();
                    }
                }
            }
        });
    }

    private void getFollowings() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowList");
        query.whereEqualTo("follower", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects != null && objects.size() != 0){
                        for(ParseObject object : objects){
                            idList.add(object.getString("follows"));
                        }
                        showUsers();
                    }
                }
            }
        });

    }

    private void getFollowers() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowList");
        query.whereEqualTo("follows", id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects != null && objects.size() != 0){
                        for(ParseObject object : objects){
                            idList.add(object.getString("follower"));
                        }
                        showUsers();
                    }
                }
            }
        });

    }

    private void showUsers(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    if(objects != null && objects.size() != 0){
                        for(final ParseUser parseUser : objects){
                            for (final String id : idList){
                                if(parseUser.getUsername().equals(id)){
                                    ParseFile file = (ParseFile) parseUser.get("profilePic");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if(e==null){
                                                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                User user = new User(parseUser.getObjectId(), id, parseUser.getString("fullname"), image, parseUser.getString("bio"));
                                                userList.add(user);
                                                Log.i("User", user.toString());
                                                adapter.notifyDataSetChanged();
                                            }else{
                                                Toast.makeText(FollowerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                }else{
                    Toast.makeText(FollowerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
