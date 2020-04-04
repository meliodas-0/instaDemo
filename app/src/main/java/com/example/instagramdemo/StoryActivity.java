package com.example.instagramdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramdemo.Model.Story;
import com.example.instagramdemo.Model.User;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
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

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener, GestureDetector.OnGestureListener{

    private static final float SWIPE_MIN_LIMIT = 200;
    private static final float SWIPE_THRESHOLD_VELOCITY = 200;
    private int counter = 0;
    long pressTime = 0L;
    long limit = 500L;
    String TAG = "tag";
    StoriesProgressView storiesProgressView;
    ImageView userPic, storyPic;
    TextView usernameTextView, timeTextView;
    View reverseView, skipView;

    List<Story> imageList;
    List<String> storyIds;
    String userId;

    LinearLayout rSeen;
    TextView seenNumber;
    ImageView storyDelete;

    private GestureDetector gestureDetector;

    private String getTime(){
        String time = "";
        long l = new Date().getTime() - imageList.get(counter).getCreatedAt().getTime();
        Log.i(TAG, l + "");

        if(l/(60*60*1000) != 0){
            time = l/(60*60*1000) + "h";
        }else if(l/(60*1000) != 0){
            time = l/(60*1000)  + "m";
        }else {
            time = l/1000 + "s";
        }
        Log.i(TAG, time + "");

        return time;
    }

    private void setImage(){

        storyPic.setImageBitmap(imageList.get(counter).getImage());
        timeTextView.setText(getTime());
        if(rSeen.getVisibility() == View.VISIBLE)
        seenNumber.setText(imageList.get(counter).getSeenBy().size()+"");
        addViews();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            gestureDetector.onTouchEvent(motionEvent);
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    storiesProgressView.animate().alpha(0).setDuration(300);
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    storiesProgressView.animate().alpha(1).setDuration(300);
                    return limit < now - pressTime;

            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storiesProgressView = findViewById(R.id.activityStoryStories);
        userPic = findViewById(R.id.activityStoryUserImageView);
        usernameTextView = findViewById(R.id.activityStoryUsernameTextView);
        storyPic = findViewById(R.id.activityStoryImageview);
        reverseView = findViewById(R.id.activityStoryReverseView);
        skipView = findViewById(R.id.activityStorySkipView);
        timeTextView = findViewById(R.id.activityStoryTimeTextView);
        rSeen = findViewById(R.id.activityStoryLinearLayout);
        storyDelete = findViewById(R.id.activityStoryDeleteImageView);
        seenNumber = findViewById(R.id.activityStoryViewsTextView);

        rSeen.setVisibility(View.GONE);
        storyDelete.setVisibility(View.GONE);

        gestureDetector = new GestureDetector(this, this, new Handler());

        reverseView.setOnTouchListener(onTouchListener);
        skipView.setOnTouchListener(onTouchListener);

        Intent intent = getIntent();

        userId = intent.getStringExtra("userIdForStoryActivity");
        reverseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });

        if(userId.equals(ParseUser.getCurrentUser().getUsername())){
            rSeen.setVisibility(View.VISIBLE);
            storyDelete.setVisibility(View.VISIBLE);
        }

        rSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoryActivity.this, FollowerActivity.class);
                intent.putExtra("id", userId);
                intent.putExtra("title", "views");
                intent.putStringArrayListExtra("Viewed By", (ArrayList<String>) imageList.get(counter).getSeenBy());
                startActivity(intent);
            }
        });

        storyDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storyId = imageList.get(counter).getStoryId();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Stories");
                query.whereEqualTo("objectId", storyId);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if(e==null && object != null){
                            object.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e != null){
                                        Toast.makeText(StoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }else{
                                        imageList.remove(counter);
                                        if(imageList.size()==0)
                                            onComplete();
                                        else {
                                            storiesProgressView.setStoriesCount(imageList.size());
                                            counter = 0;
                                            storiesProgressView.startStories(counter);
                                            setImage();
                                        }
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(StoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        getStories();
        userInfo();

        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
            }
        });
    }

    @Override
    public void onNext() {
        if(counter+1>=imageList.size()){
            return;
        }
        counter++;
        setImage();
    }

    @Override
    public void onPrev() {
        if(counter-1 < 0){
            return;
        }
        counter--;
        setImage();
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    public void addViews(){
        Toast.makeText(StoryActivity.this, "addV", Toast.LENGTH_SHORT).show();
        if(!userId.equals(ParseUser.getCurrentUser().getUsername())){
            if(!imageList.get(counter).getSeenBy().contains(ParseUser.getCurrentUser().getUsername())){
                imageList.get(counter).getSeenBy().add(ParseUser.getCurrentUser().getUsername());
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Stories");
                query.whereEqualTo("objectId", imageList.get(counter).getStoryId());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if(e == null){
                            Toast.makeText(StoryActivity.this, "addViews", Toast.LENGTH_SHORT).show();
                            object.put("seenBy", imageList.get(counter).getSeenBy());
                            object.saveInBackground();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(){
        imageList = new ArrayList<>();
        storyIds = new ArrayList<>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stories");
        query.addDescendingOrder("createdAt");
        query.whereEqualTo("storyBy", userId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if(e==null){
                    imageList.clear();
                    storyIds.clear();
                    if(objects != null && objects.size()!=0){
                        for (final ParseObject object : objects){
                            ParseFile file = (ParseFile) object.get("story");
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e==null){
                                        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        Story story = new Story(image, object.getObjectId(), object.getString("storyBy"), object.getCreatedAt(), object.<String>getList("seenBy"));
                                        imageList.add(story);
                                        Collections.sort(imageList);
                                        storyIds.add(story.getStoryId());
                                        Log.i(TAG, objects.size() + "");
                                        storiesProgressView.setStoriesCount(objects.size());
                                        storiesProgressView.setStoryDuration(5000);
                                        storiesProgressView.setStoriesListener(StoryActivity.this);
                                        storiesProgressView.startStories(counter);
                                        setImage();
                                    }
                                }
                            });
                        }
                    }else if(userId.equals(ParseUser.getCurrentUser().getUsername())){
                        Intent intent = new Intent(getApplicationContext(), AddStoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }

    private void userInfo(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if(e == null){
                    ParseFile file = (ParseFile) object.get("profilePic");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if(e == null){
                                Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                                User user = new User(userId, object.getUsername(), object.getString("fullname"), image, object.getString("bio"));
                                userPic.setImageBitmap(user.getImage());
                                usernameTextView.setText(object.getUsername());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {

        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        float x = motionEvent1.getX() - motionEvent.getX();
        float y = motionEvent1.getY() - motionEvent.getY();
        if(Math.abs(y) > Math.abs(x)){
            if(Math.abs(y)> SWIPE_MIN_LIMIT && Math.abs(v1)> SWIPE_THRESHOLD_VELOCITY){
                if(y>0){
                    finish();
                }
            }
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

}
