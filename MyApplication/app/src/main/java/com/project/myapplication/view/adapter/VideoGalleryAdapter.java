package com.project.myapplication.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.myapplication.R;

import java.util.List;

public class VideoGalleryAdapter extends RecyclerView.Adapter<VideoGalleryAdapter.VideoViewHolder> {

    private final Context context;
    private final List<Uri> videoUris;
    private final OnVideoSelectListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnVideoSelectListener {
        void onVideoSelected(Uri uri);
    }

    public VideoGalleryAdapter(Context context, List<Uri> videoUris, OnVideoSelectListener listener) {
        this.context = context;
        this.videoUris = videoUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_thumbnail, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Uri videoUri = videoUris.get(position);

        Glide.with(context)
                .load(videoUri)
                .thumbnail(0.1f)
                .into(holder.thumbnail);

        // Hiệu ứng chọn
        holder.overlay.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            listener.onVideoSelected(videoUri);
        });
    }

    @Override
    public int getItemCount() {
        return videoUris.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        View overlay;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            overlay = itemView.findViewById(R.id.selectedOverlay);
        }
    }
}
