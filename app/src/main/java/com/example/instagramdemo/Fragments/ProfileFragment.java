package com.example.instagramdemo.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.instagramdemo.Adapter.ProfilePhotosAdapter;
import com.example.instagramdemo.EditProfileActivity;
import com.example.instagramdemo.MainActivity;
import com.example.instagramdemo.Model.Post;
import com.example.instagramdemo.Model.User;
import com.example.instagramdemo.R;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    TextView noOfPosts, noOfFollowers, noOfFollowings, fullname, bio, username;
    Button editProfile;
    ParseUser parseUser;
    String profileId;
    ImageButton posts, saved;
    RecyclerView recyclerView;
    ProfilePhotosAdapter myPostsAdapter, savedPostsAdapter;
    List<Post> myPostList, savedPostList;
    Toolbar toolbar;

    public void editProfileClicked(View view){

        String s = editProfile.getText().toString();
        if(s.equals("Edit Profile")){
            Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        }else if(s.equals("Follow")){
            ParseObject object = new ParseObject("FollowList");
            object.put("follower", parseUser.getUsername());
            object.put("follows",profileId);
            editProfile.setText("Following");
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Toast.makeText(getContext(), "You are now Following " + profileId, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if (s.equals("Following")){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowList");
            query.whereEqualTo("follows", profileId);
            query.whereEqualTo("follower",parseUser.getUsername());
            editProfile.setText("Follow");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if(e == null){
                        try {
                            object.delete();
                            object.saveInBackground();
                            Toast.makeText(getContext(), "You have now unfollowed " + profileId, Toast.LENGTH_SHORT).show();
                        } catch (ParseException ex) {
                            Toast.makeText(getContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        parseUser = ParseUser.getCurrentUser();

        findViewByIds(view);

        Toast.makeText(getContext(), MainActivity.fragmentManager.getBackStackEntryCount()+"", Toast.LENGTH_SHORT).show();

        if(MainActivity.fragmentManager.getBackStackEntryCount() >= 1){

            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ((FragmentActivity)getContext()).getSupportFragmentManager().popBackStack();
                }
            });
        }

        setRecyclerView();

        sharedPrefs();

        userProfile();

        if(profileId.equals(parseUser.getUsername())){
            editProfile.setText(R.string.editProfile);
        }
        else{
            checkFollow();
        }

        onClickListeners();

        setPosts();
        setSavedPosts();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void sharedPrefs() {

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileId", "none");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profileId", parseUser.getUsername());
        editor.apply();
    }

    private void setRecyclerView() {

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(manager);
        myPostList = new ArrayList<>();
        savedPostList = new ArrayList<>();
        savedPostsAdapter = new ProfilePhotosAdapter(getContext(), savedPostList);
        myPostsAdapter = new ProfilePhotosAdapter(getContext(), myPostList);
        recyclerView.setAdapter(myPostsAdapter);

    }

    private void findViewByIds(View view) {

        profilePic = view.findViewById(R.id.profileProfilePic);
        noOfPosts = view.findViewById(R.id.profilePostsTextView);
        noOfFollowers = view.findViewById(R.id.profileFollowersTextView);
        noOfFollowings = view.findViewById(R.id.profileFollowingTextView);
        fullname = view.findViewById(R.id.profileFullnameTextView);
        bio = view.findViewById(R.id.profileBioTextView);
        username = view.findViewById(R.id.profileUsernameTextView);
        editProfile = view.findViewById(R.id.profileEditProfileButton);
        posts = view.findViewById(R.id.profileGridImageButton);
        saved = view.findViewById(R.id.profileSavedImageButton);
        recyclerView = view.findViewById(R.id.profilePostsRecyclerView);
        toolbar = view.findViewById(R.id.profileToolbar);

    }

    private void onClickListeners() {

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfileClicked(view);
            }
        });

        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedImageButtonClicked();
            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postsImageButtonClicked();
            }
        });

    }

    public void postsImageButtonClicked(){
        recyclerView.setAdapter(myPostsAdapter);
    }

    public void savedImageButtonClicked(){
        recyclerView.setAdapter(savedPostsAdapter);
    }

    private void userProfile(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", profileId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if(e == null && getContext() != null){
                    ParseFile file = (ParseFile) object.get("profilePic");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if(e == null){
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                User user = new User(object.getObjectId(),object.get("username").toString(), object.getString("fullname"),bitmap, object.get("bio").toString());
                                username.setText(user.getUsername());
                                fullname.setText(user.getFullName());
                                profilePic.setImageBitmap(user.getImageurl());
                                bio.setText(user.getBio());
                            }
                        }
                    });
                }
            }
        });
        getInfo();
    }

    private void checkFollow(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowList");
        query.whereEqualTo("follows", profileId);
        query.whereEqualTo("follower", parseUser.getUsername());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                        editProfile.setText("Following");
                }else{
                    //Toast.makeText(getContext(), e.getMessage() + "in check follow", Toast.LENGTH_SHORT).show();
                    editProfile.setText("Follow");
                }
            }
        });

    }

    private void getInfo(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowList");
        query.whereEqualTo("follows", profileId);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e == null){
                    noOfFollowers.setText("" + count);
                }else{
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        query = ParseQuery.getQuery("FollowList");
        query.whereEqualTo("follower", profileId);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e == null){
                    noOfFollowings.setText("" + count);
                }else{
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("username", profileId);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e==null){
                    noOfPosts.setText("" + count);
                }else{
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setPosts(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("username", profileId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(final ParseObject object : objects){
                        ParseFile file = (ParseFile) object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e == null){
                                    Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    Post post = new Post(profileId, object.get("description").toString(), object.getObjectId(), image);
                                    myPostList.add(post);
                                    myPostsAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setSavedPosts(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Saves");
        query.whereEqualTo("savedBy", profileId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object : objects){
                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Posts");
                        query1.whereEqualTo("objectId", object.get("postId"));
                        query1.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(final ParseObject postObject, ParseException e) {
                                if(e == null && postObject != null){
                                    ParseFile file = (ParseFile) postObject.get("image");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if(e == null){
                                                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                Post post = new Post(postObject.get("username").toString(), postObject.get("description").toString(),postObject.getObjectId(),image);
                                                savedPostList.add(post);
                                                savedPostsAdapter.notifyDataSetChanged();
                                            }else{
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
