package com.project.myapplication.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.DTO.ShortVideo;
import com.project.myapplication.R;
import com.project.myapplication.model.ShortModel;
import com.project.myapplication.view.adapter.ShortVideoAdapter;

import java.util.ArrayList;

public class shortFragment extends Fragment {

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

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // Lấy dữ liệu từ Firestore thông qua ShortModel
        loadShortVideos();

        View addShortButton = view.findViewById(R.id.addShortButton);
        addShortButton.setOnClickListener(v -> {
            // TODO: thay bằng hành động chuyển sang AddShortFragment
            // NavHostFragment.findNavController(this).navigate(R.id.action_shortFragment_to_addShortFragment);
        });

        return view;
    }

    private void loadShortVideos() {
        shortModel.getAllShortVideos(videos -> {
            adapter = new ShortVideoAdapter(requireContext(), videos);
            recyclerView.setAdapter(adapter);
        });
    }

    public ShortVideoAdapter getAdapter() {
        return adapter;
    }
}
