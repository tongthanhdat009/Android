package com.project.myapplication.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class authorProfileAdapter extends RecyclerView.Adapter<authorProfileAdapter.AuthorProfileViewHolder> {
    @NonNull
    @Override
    public authorProfileAdapter.AuthorProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull authorProfileAdapter.AuthorProfileViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public static class AuthorProfileViewHolder extends RecyclerView.ViewHolder {
        public AuthorProfileViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
