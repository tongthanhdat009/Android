package com.project.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.project.myapplication.DTO.User;
import com.project.myapplication.firebase.MyFirebaseMessagingService;
import com.project.myapplication.model.UserModel;
import com.project.myapplication.view.navController;
import com.project.myapplication.view.activity.loginActivity;

public class MainActivity extends AppCompatActivity {

    private final UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userID = getIntent().getStringExtra("userID");
        if(userID == null){
            //Mã thiết bị: 5ddada8a6ed6004d

            String currentDeviceID = userModel.getDeviceId(MainActivity.this);
            userModel.loggedCheck(currentDeviceID, new UserModel.OnLoggedCheckCallback() {
                @Override
                public void loggedCheck(User user) {
                    if( user != null && !user.getLogged().isEmpty()){
                        Intent intent = new Intent(MainActivity.this, navController.class);
                        intent.putExtra("userID", user.getUserID());
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, loginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
        else{
            MyFirebaseMessagingService test = new MyFirebaseMessagingService();

            Intent intent = new Intent(MainActivity.this, navController.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
            finish();
        }
    }
}
