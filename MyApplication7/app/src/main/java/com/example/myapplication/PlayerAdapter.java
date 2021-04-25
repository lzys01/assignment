package com.example.myapplication;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
    private List<Video> data;
    public void setData(List<Video> videoList){
        data = videoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerAdapter.PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player,parent,false);
        return new PlayerAdapter.PlayerViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerAdapter.PlayerViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder{
        public VideoView videoView;
        //private Video video;
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view);
        }
        public void bind(Video video){
            videoView.setVideoURI(Uri.parse(video.getVideoUrl()));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView (recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager ();
        if(manager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager = (GridLayoutManager)manager;
            gridLayoutManager.setSpanSizeLookup (new GridLayoutManager.SpanSizeLookup () {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
        }
    }
}
