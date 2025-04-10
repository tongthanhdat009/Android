package com.project.myapplication.view;

import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.util.UnstableApi;

import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityNavControllerBinding;
import com.project.myapplication.model.ChatBoxModel;
import com.project.myapplication.view.adapter.ShortVideoAdapter;
import com.project.myapplication.view.fragment.chatFragment;
import com.project.myapplication.view.fragment.homeFragment;
import com.project.myapplication.view.fragment.profileFragment;
import com.project.myapplication.view.fragment.searchFragment;
import com.project.myapplication.view.fragment.shortFragment;

import java.util.HashMap;
import java.util.Map;

public class navController extends AppCompatActivity {
    ActivityNavControllerBinding binding;

    private String userID;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_controller);
        userID = getIntent().getStringExtra("userID");
        binding = ActivityNavControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.home, createFragmentWithUserID(new homeFragment(), userID));
        fragmentMap.put(R.id.search, createFragmentWithUserID(new searchFragment(), userID));
//        fragmentMap.put(R.id.post, createFragmentWithUserID(new postFragment(), userID));
        fragmentMap.put(R.id.chat, createFragmentWithUserID(new chatFragment(), userID));
        fragmentMap.put(R.id.shorts, createFragmentWithUserID(new shortFragment(), userID));
        fragmentMap.put(R.id.profile, createFragmentWithUserID(new profileFragment(), userID));

        showFragment(fragmentMap.get(R.id.home));
        updateIcons(R.id.home);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                showFragment(selectedFragment);
                updateIcons(item.getItemId());
                return true;
            }
            return false;
        });
    }
    private Fragment createFragmentWithUserID(Fragment fragment, String userID) {
        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Fragment frag : fragmentManager.getFragments()) {
            transaction.hide(frag);

            // Kiểm tra nếu là homeFragment
            if (frag instanceof homeFragment) {
                ((homeFragment) frag).getController().postDisplay(userID);
            }

            // Kiểm tra nếu là chatFragment
            if (frag instanceof chatFragment) {
                ChatBoxModel chatBoxModel = new ChatBoxModel();  // Khởi tạo ChatBoxModel nếu cần
                chatBoxModel.checkAndCreateAIChatBox(userID);    // Gọi phương thức
            }

            if (frag instanceof shortFragment) {
                ShortVideoAdapter adapter = ((shortFragment) frag).getAdapter();
                if (adapter != null) {
                    adapter.pauseAllPlayers();
                }
            }
            if (fragment instanceof shortFragment) {
                ShortVideoAdapter adapter = ((shortFragment) fragment).getAdapter();
                if (adapter != null && ((shortFragment) fragment).getRecyclerView() != null) {
                    ((shortFragment) fragment).getRecyclerView().post(() ->
                            adapter.playCurrentVisible(((shortFragment) fragment).getRecyclerView())
                    );
                }
            }
        }

        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.frame_layout, fragment, fragment.getClass().getName());
        }

        transaction.commit();
    }


    private void updateIcons(int selectedItemId) {
        binding.bottomNavigationView.getMenu().findItem(R.id.home).setIcon(selectedItemId == R.id.home ? R.drawable.home_selected : R.drawable.home);
        binding.bottomNavigationView.getMenu().findItem(R.id.search).setIcon(selectedItemId == R.id.search ? R.drawable.search_selected : R.drawable.search);
//        binding.bottomNavigationView.getMenu().findItem(R.id.post).setIcon(selectedItemId == R.id.post ? R.drawable.post_selected : R.drawable.post);
        binding.bottomNavigationView.getMenu().findItem(R.id.chat).setIcon(selectedItemId == R.id.chat ? R.drawable.chat_selected : R.drawable.chat);
        binding.bottomNavigationView.getMenu().findItem(R.id.shorts).setIcon(selectedItemId == R.id.shorts ? R.drawable.short_video : R.drawable.short_video_selected);
        binding.bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(selectedItemId == R.id.profile ? R.drawable.profile_selected : R.drawable.profile);
    }
}