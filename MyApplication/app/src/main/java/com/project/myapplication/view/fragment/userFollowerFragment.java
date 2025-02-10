package com.project.myapplication.view.fragment;

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
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.view.adapter.userFollowerAdapter;

import java.util.ArrayList;

public class userFollowerFragment extends Fragment {
    private String currentUserID;
    private PostModel postModel = new PostModel();
    public userFollowerFragment( String currentUserID){
        this.currentUserID = currentUserID;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follower_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.follower_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postModel.getAllFollower(currentUserID, new PostModel.OnFollowerListRetrievedCallback() {
            @Override
            public void getAllFollower(ArrayList<Followers> followersList) {
                userFollowerAdapter followerAdapter = new userFollowerAdapter(view.getContext(), currentUserID, followersList,postModel);
                recyclerView.setAdapter(followerAdapter);
            }
        });

        return view;    }
}
