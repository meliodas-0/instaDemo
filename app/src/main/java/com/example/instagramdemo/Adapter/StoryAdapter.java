package com.example.instagramdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdemo.AddStoryActivity;
import com.example.instagramdemo.Fragments.HomeFragment;
import com.example.instagramdemo.Model.Story;
import com.example.instagramdemo.Model.User;
import com.example.instagramdemo.R;
import com.example.instagramdemo.StoryActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Story> mStory;
    ParseUser parseUser;

    public StoryAdapter(Context mContext, List<Story> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false);
        return new StoryAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Story story = mStory.get(position);
        getUserInfo(holder, story.getStoryBy(), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StoryActivity.class);
                intent.putExtra("userIdForStoryActivity", story.getStoryBy());
                mContext.startActivity(intent);
            }
        });
        holder.storyPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddStoryActivity.class);
                mContext.startActivity(intent);
                ((FragmentActivity)mContext)
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView storyPhoto, storyPlus;
        private TextView storyUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyPhoto = itemView.findViewById(R.id.addStoryProfilePhoto);
            storyPlus = itemView.findViewById(R.id.addStoryPlus);
            storyUsername = itemView.findViewById(R.id.addStoryTextView);
            parseUser = ParseUser.getCurrentUser();
            storyPhoto.setBackgroundResource(R.drawable.grey_color);
            itemView.setTag("seen");
        }
    }

//    public int getItemViewType(int position){
//        if(position == 0){
//            return 0;
//        }
//        return 1;
//    }

    private void getUserInfo(final ViewHolder holder, final String userId, final int position){
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
                                holder.storyPhoto.setImageBitmap(user.getImage());
                                if(position == 0){
                                    holder.storyPlus.setVisibility(View.VISIBLE);
                                }else{
                                    holder.storyUsername.setText(user.getUsername());
                                }

                                areStoriesSeenByTheUser(holder, position);
                            }
                        }
                    });
                }
            }
        });
    }

    private void areStoriesSeenByTheUser(final ViewHolder holder, int position){
        final String id = holder.storyUsername.getText().toString();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stories");
        if(position != 0 && holder.itemView.getTag().equals("seen")){
            query.whereEqualTo("storyBy", id);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e== null){
                        for(ParseObject object : objects){
                            List<String> seenBy = object.getList("seenBy");
                            if(!seenBy.contains(parseUser.getUsername()) && holder.itemView.getTag().equals("seen")){
                                holder.storyPhoto.setBackgroundResource(R.drawable.story_unseen);
                                holder.itemView.setTag("unseen");
                            }
                        }
                    }
                }
            });
        }
    }

//    private void seeStory(final TextView textView, final CircleImageView imageView){
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Stories");
//        query.whereEqualTo("storyBy", parseUser.getUsername());
//        query.addDescendingOrder("createdAt");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if(e == null){
//                    int count = 0;
//                    long timeCurrent = System.currentTimeMillis();
//                    for(final ParseObject object: objects){
//                        final Date createdDate = object.getCreatedAt();
//                        Date now = new Date();
//                        long timeElapsed = Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(createdDate)) - Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(now));
//                        if(timeElapsed < 86400) {
//                            ParseFile file = (ParseFile) object.get("story");
//                            file.getDataInBackground(new GetDataCallback() {
//                                @Override
//                                public void done(byte[] data, ParseException e) {
//                                    if (e == null) {
//                                        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                        Story story = new Story(image, object.getLong("timeStart"), object.getLong("timeEnd"), object.getObjectId(), object.getString("storyBy"), createdDate, object.<String>getList("seenBy"));
//                                        mStory.add(story);
//                                        notifyDataSetChanged();
//                                    }
//                                }
//                            });
//                        }else{
//                            object.deleteInBackground();
//                        }
//                    }
//                }
//            }
//        });
//    }

}
