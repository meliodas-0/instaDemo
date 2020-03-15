package com.example.instagramdemo.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.instagramdemo.Adapter.UserAdapter;
import com.example.instagramdemo.Model.User;
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

public class SearchFragment extends Fragment {


    private RecyclerView recyclerView;
    private UserAdapter adapter;
    List<User> mUsers;
    List<String> u;
    EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search,container,false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchBar = view.findViewById(R.id.searchBar);

        mUsers = new ArrayList<>();
        u = new ArrayList<>();
        adapter = new UserAdapter(getContext(),mUsers);
        recyclerView.setAdapter(adapter);

        searchUsers("");
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Log.i("mUsers", u.toString());

        return view;
    }

    private void searchUsers(String s){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null && objects.size()>0){
                    for(ParseUser us : objects){
                        u.add(us.getUsername());
                    }
                    Log.i("users", u.toString());
                }else{
                    Log.i("NO USER", "No user");
                }
            }
        });

        query.whereContains("username", s);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e == null && users.size()>0){
                    mUsers.clear();
                    for(ParseUser object : users){
                        final Bitmap[] bitmap = new Bitmap[1];//= BitmapFactory.decodeResource(getResources(),R.drawable.writtenlogo);
                        final User[] user = new User[1];
                        final String objectId = object.getObjectId();
                        final String objectUsername = object.getUsername();
                        ParseFile file = (ParseFile) object.get("profilePic");
                        if(file != null) {
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null){
                                        bitmap[0] = BitmapFactory.decodeByteArray(data,0, data.length);

                                        user[0] = new User(objectId, objectUsername, bitmap[0]);
                                        mUsers.add(user[0]);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                    Log.i("users", u.toString());
                    adapter.notifyDataSetChanged();
                }else if(users.size() == 0){
                    Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
