package com.connectus.mobile.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FirestoreHandler {
    private static final String TAG = FirestoreHandler.class.getSimpleName();

    private final FirebaseFirestore db;

    public FirestoreHandler() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void saveFCMToken(UUID userId, String token) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("token", token);
        if (userId != null) {
            db.collection("fcm_tokens").document(userId.toString())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "FCMToken successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
        }
    }
}
