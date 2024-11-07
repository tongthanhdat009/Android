package com.project.myapplication.controller;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.project.myapplication.DTO.Post;
import com.project.myapplication.DTO.User;
import com.project.myapplication.R;
import com.project.myapplication.model.PostModel;
import com.project.myapplication.model.SearchModel;
import com.project.myapplication.view.postShowAdapter;
import com.project.myapplication.view.searchResultAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class searchController {
    private View view;
    private SearchModel searchModel;
    private PostModel postModel;
    private searchActivity activity;
    private String currentUserID;
    public searchController(View view, String currentUserID){
        this.view = view;
        this.currentUserID = currentUserID;
        searchModel = new SearchModel();
        activity = new searchActivity(view);
        postModel = new PostModel();
    }

    public void inputSearch(){
        activity.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = Objects.requireNonNull(activity.inputSearch.getText()).toString();
                searchModel.getSearchResult(input, new SearchModel.OnUserSearchListRetrievedCallback() {
                    @Override
                    public void getSearchResult(ArrayList<User> usersList) {
                        if(!input.isEmpty()){
                            activity.thinkingIcon.setVisibility(View.GONE);
                            activity.hint.setVisibility(View.GONE);
                            searchResultAdapter adapter = new searchResultAdapter(view.getContext(), usersList, searchModel, currentUserID);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
                            activity.recyclerView.setLayoutManager(linearLayoutManager);
                            activity.recyclerView.setAdapter(adapter);
                            if(usersList.isEmpty()){
                                activity.notification.setVisibility(View.VISIBLE);
                            }
                            else{
                                activity.notification.setVisibility(View.GONE);
                            }
                        }
                        else {
                            activity.recyclerView.setAdapter(null);
                            activity.notification.setVisibility(View.GONE);
                            activity.thinkingIcon.setVisibility(View.VISIBLE);
                            activity.hint.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static class searchActivity{
        TextInputEditText inputSearch;
        RecyclerView recyclerView;
        TextView notification, hint;
        ImageView thinkingIcon;
        public searchActivity(View view){
            notification = view.findViewById(R.id.notification);
            inputSearch = view.findViewById(R.id.search_input);
            recyclerView = view.findViewById(R.id.result_container);
            hint = view.findViewById(R.id.hint);
            thinkingIcon = view.findViewById(R.id.thinking_icon);
        }
    }
}
