package com.project.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class postImageAdapter extends RecyclerView.Adapter<postImageAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Uri> imagesUriList;

    public postImageAdapter(Context context, ArrayList<Uri> imagesUriList) {
        this.context = context;
        this.imagesUriList = imagesUriList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imagesUriList.get(position); // Lấy URI của ảnh
        Picasso.get()
                .load(imageUri).resize(1080,1080).centerCrop()
                .into(holder.imageView); // Hiển thị ảnh vào ImageView

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra("imageUri", imageUri); // Truyền URI vào Intent
            context.startActivity(intent); // Khởi động Activity
        });
        Log.d("postFragment", "Selected image URI: " + imageUri.toString());
    }

    @Override
    public int getItemCount() {
        return imagesUriList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
