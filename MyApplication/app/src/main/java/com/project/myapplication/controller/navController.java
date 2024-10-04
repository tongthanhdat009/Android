package com.project.myapplication.controller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityNavControllerBinding;

import java.util.HashMap;
import java.util.Map;

public class navController extends AppCompatActivity {
    ActivityNavControllerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_controller);
        binding = ActivityNavControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.home, new homeFragment());
        fragmentMap.put(R.id.search, new searchFragment());
        fragmentMap.put(R.id.post, new postFragment());
        fragmentMap.put(R.id.chat, new chatFragment());
        fragmentMap.put(R.id.profile, new profileFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
                updateIcons(item.getItemId());
                return true;
            }
            return false;
        });
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void updateIcons(int selectedItemId) {
        binding.bottomNavigationView.getMenu().findItem(R.id.home).setIcon(selectedItemId == R.id.home ? R.drawable.home_selected : R.drawable.home);
        binding.bottomNavigationView.getMenu().findItem(R.id.search).setIcon(selectedItemId == R.id.search ? R.drawable.search_selected : R.drawable.search);
        binding.bottomNavigationView.getMenu().findItem(R.id.post).setIcon(selectedItemId == R.id.post ? R.drawable.post_selected : R.drawable.post);
        binding.bottomNavigationView.getMenu().findItem(R.id.chat).setIcon(selectedItemId == R.id.chat ? R.drawable.chat_selected : R.drawable.chat);
        binding.bottomNavigationView.getMenu().findItem(R.id.profile).setIcon(selectedItemId == R.id.profile ? R.drawable.profile_selected : R.drawable.profile);
    }
}