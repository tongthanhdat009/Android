package com.project.myapplication.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.Followers;
import com.project.myapplication.DTO.Following;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;

import java.util.ArrayList;

public class followerFragment extends Fragment {
    private String authorID;
    private String currentUserID;
    private PostModel postModel = new PostModel();
    public followerFragment(String authorID, String currentUserID){
        this.authorID = authorID;
        this.currentUserID = currentUserID;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follower_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.follower_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postModel.getAllFollower(authorID, new PostModel.OnFollowerListRetrievedCallback() {
            @Override
            public void getAllFollower(ArrayList<Followers> followersList) {
                followerAdapter followerAdapter = new followerAdapter(view.getContext(), authorID, currentUserID, followersList,postModel);
                recyclerView.setAdapter(followerAdapter);
            }
        });

        return view;    }
}
