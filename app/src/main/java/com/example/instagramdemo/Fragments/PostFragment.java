package com.example.instagramdemo.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.instagramdemo.Adapter.PostAdapter;
import com.example.instagramdemo.Model.Post;
import com.example.instagramdemo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParseUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    Toolbar toolbar;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        toolbar = view.findViewById(R.id.fragmentPostToolbar);
        recyclerView = view.findViewById(R.id.fragmentPostRecyclerView);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((FragmentActivity)getContext()).getSupportFragmentManager().popBackStack();
            }
        });

        SharedPreferences preferences = getContext().getSharedPreferences("posts", Context.MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = preferences.getString("posts", "");
        Type listOfPosts = new TypeToken<ArrayList<Post>>(){}.getType();
        postList = gson1.fromJson(json1, listOfPosts);
        Log.i("PostId of 1st item", postList.get(1).getDescription()+"");
        adapter = new PostAdapter(getContext(), postList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(preferences.getInt("position", 0));

        return view;
    }
}
