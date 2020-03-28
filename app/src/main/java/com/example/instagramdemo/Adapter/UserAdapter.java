package com.example.instagramdemo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdemo.Fragments.ProfileFragment;
import com.example.instagramdemo.MainActivity;
import com.example.instagramdemo.Model.User;
import com.example.instagramdemo.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private ParseUser parseUser;
    private boolean isFragment;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        parseUser = ParseUser.getCurrentUser();
        final User user = mUsers.get(position);
        holder.followButton.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullName());
        holder.imageProfile.setImageBitmap(user.getImageurl());
        if(user.getUsername().equals(parseUser.getUsername())){
            holder.followButton.setVisibility(View.GONE);
        }

        isFollowing(user, holder.followButton);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("profileId", holder.username.getText().toString());
                    editor.apply();
                if(isFragment){
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer,new ProfileFragment()).addToBackStack(null).commit();
                }else{
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("profileId", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button)view;
                String status = button.getText().toString();
                if(status.equals("Follow")){
                    ParseObject object = new ParseObject("FollowList");
                    object.put("follower", parseUser.getUsername());
                    object.put("follows",user.getUsername());
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText(mContext, "You are now Following " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    addNotifications(user.getUsername());
                    button.setText("Following");
                }else if(status.equals("Following")){
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("FollowList");
                    query.whereEqualTo("follows", user.getUsername());
                    query.whereEqualTo("follower",parseUser.getUsername());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if(e == null){
                                try {
                                    object.delete();
                                    object.saveInBackground();
                                    Toast.makeText(mContext, "You have now unfollowed " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                } catch (ParseException ex) {
                                    Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    button.setText("Follow");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username, fullname;
        public CircleImageView imageProfile;
        public Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            imageProfile = itemView.findViewById(R.id.image_profile);
            followButton = itemView.findViewById(R.id.followButton);
            fullname = itemView.findViewById(R.id.userItemFullName);

        }
    }

    private void isFollowing(final User user, final Button button){
        //check whether the current user follows the given user
        ParseQuery<ParseUser> query = ParseQuery.getQuery("FollowList");
        query.whereEqualTo("follower", parseUser.getUsername());
        query.whereEqualTo("follows", user.getUsername());
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e==null && objects.size()>0){
                    button.setText("Following");
                }
                else if (objects.size()==0){
                    button.setText("Follow");
                }
            }
        });
    }

    private void addNotifications(String notificationFor){

        ParseObject object = new ParseObject("Notifications");
        object.put("notificationFor", notificationFor);
        object.put("notificationBy", parseUser.getUsername());
        object.put("description", " started Following ");
        object.put("postId", "");
        object.put("isPost", false);
        object.saveInBackground();

    }

}
