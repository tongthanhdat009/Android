package com.project.myapplication.view;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityNavControllerBinding;
import com.project.myapplication.network.NetworkChangeReceiver;
import com.project.myapplication.view.fragment.chatFragment;
import com.project.myapplication.view.fragment.homeFragment;
import com.project.myapplication.view.fragment.postFragment;
import com.project.myapplication.view.fragment.profileFragment;
import com.project.myapplication.view.fragment.searchFragment;

import java.util.HashMap;
import java.util.Map;

public class navController extends AppCompatActivity {
    ActivityNavControllerBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_controller);
        String userID = getIntent().getStringExtra("userID");
        binding = ActivityNavControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.home, createFragmentWithUserID(new homeFragment(), userID));
        fragmentMap.put(R.id.search, createFragmentWithUserID(new searchFragment(), userID));
        fragmentMap.put(R.id.post, createFragmentWithUserID(new postFragment(), userID));
        fragmentMap.put(R.id.chat, createFragmentWithUserID(new chatFragment(), userID));
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

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment frag : fragmentManager.getFragments()) {
            transaction.hide(frag);
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
        binding.bottomNavigationView.getMenu().findItem(R.id.post).setIcon(selectedItemId == R.id.post ? R.drawable.post_selected : R.drawable.post);
        binding.bottomNavigationView.getMenu().findItem(R.id.chat).setIcon(selectedItemId == R.id.chat ? R.drawable.chat_selected : R.drawable.chat);
        binding.bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(selectedItemId == R.id.profile ? R.drawable.profile_selected : R.drawable.profile);
    }
}