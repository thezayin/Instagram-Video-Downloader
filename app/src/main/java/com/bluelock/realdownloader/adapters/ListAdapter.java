package com.bluelock.realdownloader.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelock.realdownloader.R;
import com.bluelock.realdownloader.models.FVideo;
import com.bumptech.glide.Glide;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private final ItemClickListener itemClickListener;
    private final Context context;
    private List<FVideo> videos;

    public ListAdapter(Context context, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.video_item_layout, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        FVideo video = videos.get(position);


        if (video.getState() == FVideo.COMPLETE) {
            Glide.with(context)
                    .load(video.getFileUri())
                    .into(holder.ivThumbnail);
        } else {
            holder.ivThumbnail.setImageResource(R.drawable.ic_forward);
        }

        holder.tvVideoTitle.setText(video.getFileName());

        //Setting video state
        switch (video.getState()) {
            case FVideo.DOWNLOADING:
                holder.cvIcons.setVisibility(View.GONE);
                holder.tvVideoState.setVisibility(View.VISIBLE);
                holder.tvVideoState.setText(R.string.downloading);
                break;
            case FVideo.PROCESSING:
                holder.cvIcons.setVisibility(View.GONE);
                holder.tvVideoState.setVisibility(View.VISIBLE);
                holder.tvVideoState.setText(R.string.processing);
                break;
            case FVideo.COMPLETE:
                //if video download and processing complete then it shows share icons
                holder.tvVideoState.setVisibility(View.GONE);
                holder.cvIcons.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        if (videos == null)
            return 0;
        return videos.size();
    }

    public void setVideos(List<FVideo> fVideos) {
        videos = fVideos;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(FVideo video);
    }

    class ListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        TextView tvVideoTitle;
        TextView tvVideoState;
        ImageView ivThumbnail;
        ConstraintLayout cvIcons;

        View itemView;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

        }

        @Override
        public void onClick(View v) {
            Log.d("Hello World", "onClick: id " + v.getId());
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("Hello World", "onClick: id " + v.getId());
            return true;
        }

    }

}
