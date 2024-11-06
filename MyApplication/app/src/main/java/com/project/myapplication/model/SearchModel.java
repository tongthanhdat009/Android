package com.project.myapplication.model;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.myapplication.DTO.User;

import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Objects;

public class SearchModel {
    private final FirebaseFirestore firestore;

    public SearchModel() {
        // Khởi tạo Firestore
        firestore = FirebaseFirestore.getInstance();
    }
    public void getSearchResult(String inputSearch, OnUserSearchListRetrievedCallback callback){
        firestore.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<User> usersList = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    if(document.getString("Name") != null){
                        String rsNormal = Objects.requireNonNull(removeAccents(document.getString("Name"))).toLowerCase();
                        String inputNormal = removeAccents(inputSearch).toLowerCase();
                        if(rsNormal.contains(inputNormal.trim())){
                            User userTemp = document.toObject(User.class);
                            assert userTemp != null;
                            userTemp.setUserID(document.getId());
                            usersList.add(userTemp);
                        }
                    }
                }
                callback.getSearchResult(usersList);
            } else {
                System.err.println("Error getting documents: " + task.getException());
            }
        });

    }
    //callback lấy dữ liệu của user tìm kiếm đượ
    public interface OnUserSearchListRetrievedCallback{
        void getSearchResult(ArrayList<User> usersList);
    }
    public static String removeAccents(String text) {
        if (text == null) {
            return null;
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
