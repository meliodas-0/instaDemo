package com.example.instagramdemo.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdemo.Fragments.PostFragment;
import com.example.instagramdemo.Fragments.ProfileFragment;
import com.example.instagramdemo.Model.Notification;
import com.example.instagramdemo.Model.User;
import com.example.instagramdemo.R;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification = mNotification.get(position);
        holder.descriptionTextView.setText(notification.getDescription());
        publisherInfo(holder.profileImageView, holder.usernameTextView, notification.getUserId());
        if(notification.isPost()){
            holder.postImageView.setVisibility(View.VISIBLE);
            getPost(notification.getPostId(), holder.postImageView);
        }else{
            holder.postImageView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notification.isPost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postId", notification.getPostId());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragmentContainer, new PostFragment()).commit();
                }
                else{
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileId", notification.getUserId());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.fragmentContainer, new ProfileFragment()).commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView postImageView;
        private CircleImageView profileImageView;
        private TextView usernameTextView, descriptionTextView;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImageView = itemView.findViewById(R.id.notificationItemPostImageView);
            profileImageView = itemView.findViewById(R.id.notificationItemProfileImageView);
            usernameTextView = itemView.findViewById(R.id.notificationItemUsernameTextView);
            descriptionTextView = itemView.findViewById(R.id.notificationItemDescriptionTextView);

        }
    }

    private void publisherInfo(final ImageView imageProfile, final TextView username, final String publisherName){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", publisherName);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){

                    for(final ParseUser newUser : objects){
                        ParseFile file = (ParseFile) newUser.get("profilePic");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e == null){

                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    User user1 = new User(newUser.getObjectId(), newUser.getUsername(),newUser.getString("fullname"), bitmap, newUser.getString("bio"));
                                    imageProfile.setImageBitmap(bitmap);
                                    username.setText(user1.getUsername());

                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void getPost(String postId, final ImageView postImageView){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Posts");
        query.whereEqualTo("objectId", postId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    ParseFile file = (ParseFile) object.get("image");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                            postImageView.setImageBitmap(image);
                        }
                    });
                }
            }
        });

    }
}
