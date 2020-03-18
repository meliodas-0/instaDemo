package com.example.instagramdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdemo.CommentsActivity;
import com.example.instagramdemo.Fragments.ProfileFragment;
import com.example.instagramdemo.MainActivity;
import com.example.instagramdemo.Model.Post;
import com.example.instagramdemo.Model.User;
import com.example.instagramdemo.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> posts;
    public ParseUser parseUser;
    int i = 0;

    public PostAdapter(Context mContext, List<Post> posts) {
        this.mContext = mContext;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        parseUser = ParseUser.getCurrentUser();
        final Post post = posts.get(position);

        holder.postImage.setImageBitmap(post.getImage());
        if(post.getDescription().isEmpty()){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }
        holder.noOfLikes.setVisibility(View.VISIBLE);
        holder.noOfComments.setVisibility(View.VISIBLE);

        publisherInfo(holder.imageProfile,holder.username, post.getUsername(),holder.publisher);
        isLiked(post.getPostId(), holder.like);
        nrLikes(post.getPostId(),holder.noOfLikes);
        nrComments(post.getPostId(), holder.noOfComments);
        isSaved(post.getPostId(), holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().toString().equals("save") || holder.save.getTag().toString().isEmpty()){
                    //save
                    holder.save.setTag("saved");
                    holder.save.setImageResource(R.drawable.ic_saved_fill);

                    ParseObject object = new ParseObject("Saves");
                    object.put("savedBy", parseUser.getUsername());
                    object.put("postBy", post.getUsername());
                    object.put("postId", post.getPostId());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
                                holder.save.setTag("saved");
                            }else {
                                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else if(holder.save.getTag().toString().equals("saved")){
                    //unsave
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Saves");
                    query.whereEqualTo("savedBy", parseUser.getUsername());
                    query.whereEqualTo("postId", post.getPostId());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if(e == null){
                                object.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            Toast.makeText(mContext, "Unsaved", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });

                    holder.save.setTag("save");
                    holder.save.setImageResource(R.drawable.ic_save_unfill);
                }
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.putExtra("publisher", post.getUsername());
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("profileId", holder.username.getText().toString());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new ProfileFragment()).addToBackStack(null).commit();
                //mContext.startActivity(intent);
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(i == 2){
                            if(holder.like.getTag().toString().isEmpty() || holder.like.getTag().toString().equals("LIKE")) {

                                ParseObject object = new ParseObject("Likes");
                                object.put("postId", post.getPostId());
                                object.put("likedBy",parseUser.getUsername());
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){
                                            holder.like.setImageResource(R.drawable.ic_liked);
                                            changeLike(true, post.getPostId(), holder.noOfLikes);
                                        }
                                    }
                                });
                            }
                            Toast.makeText(mContext, "You have liked this post", Toast.LENGTH_SHORT).show();
                            holder.like.setTag("LIKED");
                        }
                        i=0;
                    }
                }, 300);
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.like.getTag().toString().equals("LIKE") || holder.like.getTag().toString().isEmpty()){
                    ParseObject object = new ParseObject("Likes");
                    object.put("postId", post.getPostId());
                    object.put("likedBy",parseUser.getUsername());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null){
                                Toast.makeText(mContext, "You have liked this post", Toast.LENGTH_SHORT).show();
                                holder.like.setImageResource(R.drawable.ic_liked);
                                holder.like.setTag("LIKED");
                                changeLike(true, post.getPostId(), holder.noOfLikes);
                            }
                        }
                    });
                }else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Likes");
                    query.whereEqualTo("postId", post.getPostId());
                    query.whereEqualTo("likedBy", parseUser.getUsername());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if(e == null){
                                object.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null){
                                            Toast.makeText(mContext, "Unliked", Toast.LENGTH_SHORT).show();
                                            holder.like.setImageResource(R.drawable.ic_like_unfill);
                                            holder.like.setTag("LIKE");
                                            changeLike(false, post.getPostId(), holder.noOfLikes);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CommentsActivity.class);
                intent.putExtra("postId", post.getPostId());
                intent.putExtra("publisherId", post.getUsername());
                mContext.startActivity(intent);
                notifyDataSetChanged();
            }
        });

    }

    private void isSaved(String postId, final ImageView save) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Saves");
        query.whereEqualTo("savedBy", parseUser.getUsername());
        query.whereEqualTo("postId", postId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    if(object != null){
                        save.setTag("saved");
                        save.setImageResource(R.drawable.ic_saved_fill);
                    }
                }else {
                    save.setTag("save");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageProfile, postImage, like, comment, save;
        public TextView username, noOfLikes, publisher, description, noOfComments;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post);
            like = itemView.findViewById(R.id.like);
            comment= itemView.findViewById(R.id.comment);
            save= itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            publisher= itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            noOfLikes   = itemView.findViewById(R.id.noOfLikes);
            noOfComments = itemView.findViewById(R.id.noOfComments);
        }
    }

    private void changeLike(boolean liked, String postId, final TextView textView){

        final int i;

        if(liked){
            i = 1;
        }else{
            i = -1;
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("objectId", postId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    String s = object.get("noOfLikes").toString();
                    final int noOfLikes = Integer.parseInt(s) + i;
                    object.put("noOfLikes", noOfLikes );
                    Log.i("I", i+"");
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                textView.setText(noOfLikes+" Likes");
                                textView.setVisibility(View.VISIBLE);
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void isLiked(String postId, final ImageView imageView){

        ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Likes");
        query.whereEqualTo("postId", postId);
        query.whereEqualTo("likedBy", user.getUsername());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("LIKED");
                }else if(object == null){
                    imageView.setImageResource(R.drawable.ic_like_unfill);
                    imageView.setTag("LIKE");
                }
            }
        });

    }

    private void nrLikes(final String postId, final TextView textView){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("objectId", postId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null){
                    int noOfLikes = Integer.parseInt(object.get("noOfLikes").toString());
//                    Log.i("PostID", postId);
//                    Log.i("NUmber of likes", noOfLikes+"");
                    if(noOfLikes == 0){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("0 Likes");
                    }else if(noOfLikes == 1){
                        textView.setText("1 Like");
                        textView.setVisibility(View.VISIBLE);
                    }else{
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(noOfLikes+" Likes");
                    }
                }
            }
        });

    }

    private void nrComments(final String postId, final TextView textView){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("objectId", postId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null && object != null){
                    int noOfComments = Integer.parseInt(object.get("noOfComments").toString());
//                    Log.i("PostID", postId);
//                    Log.i("NUmber of likes", noOfLikes+"");
                    if(noOfComments == 0){
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("0 Comments");
                    }else if(noOfComments == 1){
                        textView.setText("1 Comment");
                        textView.setVisibility(View.VISIBLE);
                    }else{
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(noOfComments+" Comments");
                    }
                }
            }
        });
    }

    private void publisherInfo(final ImageView imageProfile, final TextView username, final String publisherName ,final TextView publisher){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", publisherName);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){

                    for(ParseUser user : objects){

                        final ParseUser newUser = user;

                        ParseFile file = (ParseFile) user.get("profilePic");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e == null){

                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    User user1 = new User(newUser.getObjectId(), newUser.getUsername(),bitmap);
                                    imageProfile.setImageBitmap(bitmap);
                                    username.setText(user1.getUsername());
                                    publisher.setText(user1.getUsername());

                                }
                            }
                        });
                    }

                }
            }
        });


    }
}
