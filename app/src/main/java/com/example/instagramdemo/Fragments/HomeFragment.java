package com.example.instagramdemo.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagramdemo.Adapter.PostAdapter;
import com.example.instagramdemo.Model.Post;
import com.example.instagramdemo.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    public static PostAdapter adapter;
    private List<Post> posts;
    private List<String> followingList;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container, false);

        progressBar = view.findViewById(R.id.fragmentHomeProgressBar);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        //manager.setReverseLayout(true);
        //manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        posts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(adapter);
        if(ParseUser.getCurrentUser() != null)
        isFollowing();

        return view;
    }

    private void isFollowing(){

        followingList = new ArrayList<>();
        followingList.clear();
        posts.clear();
        adapter.notifyDataSetChanged();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowList");
        query.whereEqualTo("follower", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){

                    for(ParseObject object: objects){
                        String username = object.get("follows").toString();
                        if(!followingList.contains(username)){
                            followingList.add(username);
                        }
                    }

                    readPost();
                }
            }
        });


    }

    private void readPost(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    posts.clear();

                    for(final ParseObject object : objects){
                        final long timeDate = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(object.getCreatedAt())); //Set time here and check if the format is correct or not

                        ParseFile file = (ParseFile)object.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {

                                if(e == null){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    Post post = new Post(object.get("username").toString(),object.get("description").toString(),object.getObjectId(),bitmap, timeDate);
                                    if(followingList.contains(post.getUsername())) {
                                        posts.add(post);
                                        Collections.sort(posts);
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                            }
                        });


                    }

                }
            }
        });

    }

}
