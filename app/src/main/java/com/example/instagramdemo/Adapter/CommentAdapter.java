package com.example.instagramdemo.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdemo.CommentsActivity;
import com.example.instagramdemo.MainActivity;
import com.example.instagramdemo.Model.Comment;
import com.example.instagramdemo.R;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static android.app.PendingIntent.getActivity;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    Context mContext;
    List<Comment> mComments;

    private ParseUser parseUser;

    public CommentAdapter(Context mContext, List<Comment> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);


        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        parseUser = ParseUser.getCurrentUser();
        final Comment comment = mComments.get(position);

        holder.comment.setText(comment.getComment());
        getUserInfo(holder.profilePic, holder.username, comment.getPublisher());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!comment.getPublisher().equals(parseUser.getUsername()))
                    return false;
                new AlertDialog.Builder(mContext)
                        .setTitle("Delete Comment")
                        .setMessage("Do you really want to delete this comment")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
                                query.whereEqualTo("objectId", comment.getCommentId());
                                query.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        if(e==null){
                                            object.deleteInBackground();
                                            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                            mComments.remove(position);
                                            notifyDataSetChanged();
                                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Posts");
                                            query1.whereEqualTo("objectId", CommentsActivity.postId);
                                            query1.getFirstInBackground(new GetCallback<ParseObject>() {
                                                @Override
                                                public void done(ParseObject object, ParseException e) {
                                                    if(e==null){
                                                        int i = object.getInt("noOfComment");
                                                        object.put("noOfComment", --i);
                                                        object.saveInBackground();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();

                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisher", comment.getPublisher());
                SharedPreferences sharedPreferences = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("profileId", holder.username.getText().toString());
                editor.apply();
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profilePic;
        TextView comment, username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profilepic);
            comment = itemView.findViewById(R.id.commentTextView);
            username = itemView.findViewById(R.id.usernameTextView);

        }
    }


    private void getUserInfo(final ImageView imageView, final TextView textView, final String publisher){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", publisher);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser object, ParseException e) {
                if(e == null){
                    ParseFile file = (ParseFile) object.get("profilePic");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imageView.setImageBitmap(image);
                            textView.setText(object.getUsername());
                        }
                    });
                }else{
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
