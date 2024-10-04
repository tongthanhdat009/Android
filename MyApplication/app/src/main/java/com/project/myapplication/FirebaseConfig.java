package com.project.myapplication;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {
    private static FirebaseDatabase firebaseDatabase;
    public static FirebaseDatabase getDatabase() {
        if (firebaseDatabase == null) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        return firebaseDatabase;
    }
}
