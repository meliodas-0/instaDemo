package com.example.instagramdemo.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramdemo.Fragments.PostFragment;
import com.example.instagramdemo.Model.Post;
import com.example.instagramdemo.R;
import com.google.gson.Gson;

import java.util.List;

public class ProfilePhotosAdapter extends RecyclerView.Adapter<ProfilePhotosAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public ProfilePhotosAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_posts_item, parent, false);

        return new ProfilePhotosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Post post = mPosts.get(position);
        holder.postImage.setImageBitmap(post.getImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String json = gson.toJson(mPosts);
                SharedPreferences.Editor editor = mContext.getSharedPreferences("posts", Context.MODE_PRIVATE).edit();
                editor.putInt("position", position);
                editor.putString("posts", json);
                editor.apply();
                Log.i("JSON", json);
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new PostFragment()).addToBackStack(null).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.profilePostItem);

        }
    }

}
