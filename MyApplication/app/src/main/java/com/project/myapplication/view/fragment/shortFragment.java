package com.project.myapplication.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.R;
import com.project.myapplication.model.ShortModel;
import com.project.myapplication.view.activity.AddShortActivity;
import com.project.myapplication.view.adapter.ShortVideoAdapter;

import java.util.ArrayList;

public class shortFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private String userID;
    private ShortVideoAdapter adapter;
    private RecyclerView recyclerView;
    private ShortModel shortModel;

    public static shortFragment newInstance(String userID) {
        shortFragment fragment = new shortFragment();
        Bundle args = new Bundle();
        args.putString("userID", userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString("userID");
        }

        shortModel = new ShortModel(); // Khởi tạo model
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_short_video, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewShortVideos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true); // Hiển thị vòng xoay loading
            loadShortVideos(); // Tải lại dữ liệu
        });

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // Lấy dữ liệu từ Firestore thông qua ShortModel
        loadShortVideos();

        View addShortButton = view.findViewById(R.id.addShortButton);
        addShortButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddShortActivity.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null && recyclerView != null) {
            recyclerView.post(() -> adapter.playCurrentVisible(recyclerView));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.pauseAllPlayers();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.releaseAllPlayers();
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    private void loadShortVideos() {
        shortModel.getAllShortVideos(videos -> {
            adapter = new ShortVideoAdapter(requireContext(), videos, userID);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    public ShortVideoAdapter getAdapter() {
        return adapter;
    }
}
