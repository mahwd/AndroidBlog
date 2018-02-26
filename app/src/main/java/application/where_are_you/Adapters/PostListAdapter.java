package application.where_are_you.Adapters;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import application.where_are_you.MainActivity;
import application.where_are_you.Model.Post;
import application.where_are_you.R;
import application.where_are_you.SetupActivity;

/**
 * Created by mahwd on 2/26/18.
 */

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private List<Post> posts;
    private View view;
    public PostListAdapter(List<Post> _posts) {
        this.posts = _posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.post_title.setText(posts.get(position).getTitle());
        holder.post_description.setText(posts.get(position).getDescription());
        Uri imageUrl = Uri.parse(posts.get(position).getImageUrl());
        Glide.with(view.getContext()).load(imageUrl).into(holder.post_cover);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView post_title;
        TextView post_description;
        ImageView post_cover;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            post_title = itemView.findViewById(R.id.list_post_title);
            post_description= itemView.findViewById(R.id.list_post_desc);
            post_cover = itemView.findViewById(R.id.list_post_cover);

        }
    }
}
