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
import android.widget.ProgressBar;

import com.example.instagramdemo.Adapter.PostAdapter;
import com.example.instagramdemo.Adapter.StoryAdapter;
import com.example.instagramdemo.Model.Post;
import com.example.instagramdemo.Model.Story;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts, recyclerViewStories;
    public static PostAdapter postAdapter;
    public static StoryAdapter storyAdapter;
    private List<Post> posts;
    private List<String> followingList;
    private List<Story> storyList;
    private List<String> storiesBy;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container, false);

        progressBar = view.findViewById(R.id.fragmentHomeProgressBar);
        recyclerViewPosts = view.findViewById(R.id.recycler_view);
        recyclerViewPosts.setHasFixedSize(true);

        recyclerViewStories = view.findViewById(R.id.recyclerViewStories);
        recyclerViewStories.setHasFixedSize(true);
        recyclerViewStories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        storyList = new ArrayList<>();
        storiesBy = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerViewStories.setAdapter(storyAdapter);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewPosts.setLayoutManager(manager);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        recyclerViewPosts.setAdapter(postAdapter);
        if(ParseUser.getCurrentUser() != null) {
            isFollowing();
        }


        return view;
    }

    private void isFollowing(){

        followingList = new ArrayList<>();
        followingList.clear();
        posts.clear();
        postAdapter.notifyDataSetChanged();

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
                    readStories();
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
                                        postAdapter.notifyDataSetChanged();
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

    private void readStories(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stories");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    storyList.clear();
                    Story story = new Story(null,
                            "",
                            ParseUser.getCurrentUser().getUsername(),
                            new Date(),
                            new ArrayList<String>());
                    storyList.add(story);
                    storiesBy.add(ParseUser.getCurrentUser().getUsername());
                    storyAdapter.notifyDataSetChanged();
                    if (objects != null && objects.size() != 0) {

                        for(ParseObject object: objects){
                            String username = object.getString("storyBy");
                            Date createdAt = object.getCreatedAt();
                            long time = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
                                    - Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(createdAt));
                            if(time<1000000) {
                                if (!storiesBy.contains(username) && followingList.contains(username)) {
                                    story = new Story(null,
                                            "",
                                            username,
                                            new Date(),
                                            new ArrayList<String>());
                                    storyList.add(story);
                                    storiesBy.add(username);
                                    Collections.sort(storyList);
                                    storyAdapter.notifyDataSetChanged();
                                }
                            }
                            else{
                                object.deleteInBackground();
                            }
                        }

                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        storyAdapter.notifyDataSetChanged();
        postAdapter.notifyDataSetChanged();
    }
}
