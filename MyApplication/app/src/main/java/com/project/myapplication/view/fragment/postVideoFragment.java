package com.project.myapplication.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.myapplication.R;
import com.project.myapplication.controller.postVideoController;
import com.project.myapplication.view.components.CustomProgressDialog;


public class postVideoFragment extends Fragment {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    postVideoController controller;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.post_video_fragment, container, false);
        CustomProgressDialog progressDialog = new CustomProgressDialog(view.getContext());

        controller = new postVideoController(view, progressDialog);
        controller.wordCounter();
        controller.setUserInfor(user.getUid());
        controller.addItemTargetSpinner();
        controller.registerVideoPicker(this);
        controller.chooseVideoButton();
        controller.setDeleteVideoBTN();
        controller.setPostVideoBTN(user.getUid());
        return view;
    }

    public void resetData() {
        controller.resetData();
    }
}
