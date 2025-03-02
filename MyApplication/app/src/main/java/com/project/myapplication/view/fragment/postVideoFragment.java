package com.project.myapplication.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.ComponentActivity;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_video_fragment, container, false);
        CustomProgressDialog progressDialog = new CustomProgressDialog(view.getContext());

        postVideoController controller = new postVideoController(view, progressDialog);
        controller.wordCounter();
        controller.setUserInfor(user.getUid());
        controller.addItemTargetSpinner();
        controller.registerVideoPicker(this);
        controller.chooseVideoButton();
        return view;
    }
}
