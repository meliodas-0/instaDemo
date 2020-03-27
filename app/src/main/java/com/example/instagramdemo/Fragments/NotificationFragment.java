package com.example.instagramdemo.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramdemo.Adapter.NotificationAdapter;
import com.example.instagramdemo.Model.Notification;
import com.example.instagramdemo.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.fragmentNotificationRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(adapter);

        readNotifications();

        return view;
    }

    private void readNotifications() {
        final ParseUser parseUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notifications");
        query.whereEqualTo("notificationFor", parseUser.getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(objects != null && objects.size() != 0){
                        for(ParseObject object : objects){
                            Notification notification = new Notification(object.getString("notificationBy"), object.getString("description"), object.getString("postId"), object.getBoolean("isPost"));
                            notificationList.add(notification);
                        }
                        Collections.reverse(notificationList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
