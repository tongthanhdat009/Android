package com.project.myapplication.view.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.project.myapplication.R;
import com.project.myapplication.controller.homeController;

public class homeFragment extends Fragment {
    private String userID;

    private homeController controller;
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
        // Yêu cầu nhiều bộ nhớ hơn cho process
        Context context = getContext();
        assert context != null;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);

            if (memoryInfo.lowMemory) {
                Toast.makeText(context, "Thiết bị đang ở trạng thái bộ nhớ thấp", Toast.LENGTH_SHORT).show();
                // Có thể thực hiện các hành động giải phóng bộ nhớ ở đây
                controller.postAdapter.releaseAllPlayers();
            }

            // Log thông tin bộ nhớ
            long availableMegs = memoryInfo.availMem / 1048576L; // Đổi sang MB
            long totalMegs = memoryInfo.totalMem / 1048576L;
            Log.d("MemoryInfo", "Available: " + availableMegs + "MB / Total: " + totalMegs + "MB");
        }
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        controller = new homeController(view);
        controller.postDisplay(userID);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(controller.postAdapter!=null){
            controller.postAdapter.releaseAllPlayers();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(controller.postAdapter!=null){
            controller.postAdapter.releaseAllPlayers();
        }
    }

    // Thêm phương thức gọi khi Activity/Fragment pause
    public void pauseAllPlayers() {
        for (int i = 0; i < controller.postAdapter.playerCache.size(); i++) {
            ExoPlayer player = controller.postAdapter.playerCache.get(i);
            if (player != null) {
                player.setPlayWhenReady(false);
            }
        }
    }

    // Thêm phương thức gọi khi Activity/Fragment resume
    public void resumeVisiblePlayers(RecyclerView recyclerView) {
        controller.postAdapter.checkVisibleVideos(recyclerView);
    }

    // Trong Activity/Fragment


    @Override
    public void onPause() {
        super.onPause();
        if (controller.postAdapter != null) {
            controller.postAdapter.pauseAllPlayers();
            pauseAllPlayers();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (controller.postAdapter != null && controller.postRecyclerView != null) {
            controller.postAdapter.resumeVisiblePlayers(controller.postRecyclerView);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            // Fragment bị ẩn - dừng video
            if (controller.postAdapter != null) {
                controller.postAdapter.pauseAllPlayers();
            }
        }
    }
}
