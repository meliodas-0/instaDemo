package com.example.instagramdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdemo.Adapter.CommentAdapter;
import com.example.instagramdemo.Fragments.HomeFragment;
import com.example.instagramdemo.Model.Comment;
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

public class CommentsActivity extends AppCompatActivity {

    EditText addCommentsEditText;
    ImageView imageprofileImageView;
    TextView postTextView;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> commentList;

    String postId;
    String publisherId;
    String comment;

    ParseUser parseUser;

    public void postComment(View view){
        comment = addCommentsEditText.getText().toString();
        if(comment.isEmpty()){
            Toast.makeText(this, "Enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }
        ParseObject object = new ParseObject("Comments");
        object.put("postId", postId);
        object.put("publisherId", publisherId);
        object.put("commentBy", parseUser.getUsername());
        object.put("comment", comment);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(CommentsActivity.this, "Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("objectId",postId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                String s = object.get("noOfComments").toString();
                int noOfComments = Integer.parseInt(s);
                object.put("noOfComments", noOfComments+1);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(CommentsActivity.this, "DONE", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        addCommentsEditText.setText("");
        readComments();
        addNotifications();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HomeFragment.postAdapter.notifyDataSetChanged();
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("profileId", parseUser.getUsername());
                editor.apply();
                finish();
            }
        });

        parseUser = ParseUser.getCurrentUser();
        ParseFile file = (ParseFile) parseUser.get("profilePic");
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e == null){
                    Bitmap userImage = BitmapFactory.decodeByteArray(data,0,data.length);
                    imageprofileImageView.setImageBitmap(userImage);

                }
            }
        });

        addCommentsEditText = findViewById(R.id.addComent);
        imageprofileImageView = findViewById(R.id.image_profile);
        postTextView = findViewById(R.id.post);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        adapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        publisherId = intent.getStringExtra("publisherId");

        readComments();

    }

    private void readComments(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("postId", postId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                commentList.clear();
                for(ParseObject object : objects){
                    Comment comment = new Comment(object.get("comment").toString(), object.get("commentBy").toString());
                    commentList.add(comment);
                }
                Log.i("commentList", commentList.toString());
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addNotifications(){

        ParseObject object = new ParseObject("Notifications");
        object.put("notificationFor", publisherId);
        object.put("notificationBy", parseUser.getUsername());
        object.put("description", "commented: " + comment);
        object.put("postId", postId);
        object.put("isPost", true);
        object.saveInBackground();

    }
}
