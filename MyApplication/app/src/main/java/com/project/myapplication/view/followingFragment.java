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

import com.project.myapplication.DTO.Following;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;

import java.util.ArrayList;

public class followingFragment extends Fragment {
    private String userID;
    private String currentUserID;
    private PostModel postModel = new PostModel();
    public followingFragment(String userID, String currentUserID){
        this.userID = userID;
        this.currentUserID = currentUserID;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.following_fragment, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.following_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postModel.getAllFollowing(userID, new PostModel.OnFollowingListRetrievedCallback() {
            @Override
            public void getAllFollowing(ArrayList<Following> followingList) {
                followingAdapter followingAdapter = new followingAdapter(view.getContext(), userID, currentUserID, followingList,postModel);
                recyclerView.setAdapter(followingAdapter);
            }
        });

        return view;
    }
}
