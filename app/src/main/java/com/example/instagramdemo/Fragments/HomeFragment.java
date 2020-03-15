package com.example.instagramdemo.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    public static PostAdapter adapter;
    List<Post> posts;
    List<String> following;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        posts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(adapter);
        isFollowing();

        return view;
    }

    private void isFollowing(){

        following = new ArrayList<>();
        following.clear();
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
                        if(!following.contains(username)){
                            following.add(username);
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

                    for(ParseObject object : objects){

                        ParseFile file = (ParseFile)object.get("image");
                        final ParseObject newObject = object;
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {

                                if(e == null){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    Post post = new Post(newObject.get("username").toString(),newObject.get("description").toString(),newObject.getObjectId(),bitmap);
                                    if(following.contains(post.getUsername())) {
                                        posts.add(post);
                                        adapter.notifyDataSetChanged();
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
