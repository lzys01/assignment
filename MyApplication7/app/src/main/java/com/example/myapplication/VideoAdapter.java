package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Video> data;
    private OnItemActionListener onItemActionListener;
    public VideoAdapter(Context context){
        super();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }
    public void setData(List<Video> videoList){
        data = videoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cover,parent,false);
        return new ViewHolder(root);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Video video = data.get(position);
        //holder.bind(data.get(position));
        //View itemView = ((LinearLayout) holder.itemView).getChildAt(0);

        if (onItemActionListener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemActionListener.onItemClickListener(holder.itemView, position);
                }
            });
        }
        /*CardView.LayoutParams layoutParams = (CardView.LayoutParams) holder.cardView.getLayoutParams();
        float itemWidth = (ScreenUtils.getScreenWidth(holder.itemView.getContext()))/2;
        layoutParams.width = (int) itemWidth;
        float scale = (itemWidth + 0f)/video.getImageW();
        layoutParams.height = (int) (video.getImageH()*scale);
        holder.coverSD.setLayoutParams(layoutParams);*/
        Glide.with(holder.coverSD).load(video.getImageUrl()).into(holder.coverSD);
        holder.user_name.setText(video.getStudentId());
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView coverSD;
        private TextView user_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            coverSD = itemView.findViewById(R.id.sd_cover);
            user_name = itemView.findViewById(R.id.user_name);
        }
        /*public void bind(Video video){
            Glide.with(coverSD).load(video.getImageUrl()).into(coverSD);
        }*/
    }
    public interface OnItemActionListener
    {
        public void onItemClickListener(View v,int position);
        public boolean onItemLongClickListener(View v,int position);
    }
    public void setOnItemActionListener(OnItemActionListener onItemActionListener){
        this.onItemActionListener = onItemActionListener;
    }
    /*private onItemClickListener mOnItemClickListener;//声明接口

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }*/


    /*@Override
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
    }*/
}
