package com.project.myapplication.view.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.messaging.FirebaseMessaging;
import com.project.myapplication.R;
import com.project.myapplication.controller.homeController;

public class homeFragment extends Fragment {
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Lấy token thất bại", task.getException());
                        return;
                    }

                    // Lấy token thành công
                    String token = task.getResult();
                    Log.d("FCM", "Token hiện tại: " + token);
                });
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeController controller = new homeController(view);
        controller.postDisplay(userID);
        return view;
    }
}
