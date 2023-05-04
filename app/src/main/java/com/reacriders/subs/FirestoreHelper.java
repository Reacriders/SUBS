package com.reacriders.subs;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";

    public static void updateScore(FirebaseUser currentUser, TextView starValue, ProgressBar pg) {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            int score = document.getLong("score").intValue();
                            starValue.setText(String.valueOf(score));
                            starValue.setVisibility(View.VISIBLE);
                            pg.setVisibility(View.INVISIBLE);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    public static void incrementScore(FirebaseUser currentUser, long incrementValue, TextView starValue, ProgressBar pg) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference docRef = db.collection("Users").document(uid);

            // Increment score in Firestore
            docRef.update("score", FieldValue.increment(incrementValue))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            // Update score in TextView
                            updateScore(currentUser, starValue, pg);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
    }
    public static void decrementScore(FirebaseUser currentUser, long decrementValue, TextView starValue, ProgressBar pg) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference docRef = db.collection("Users").document(uid);

            // Decrement score in Firestore
            docRef.update("score", FieldValue.increment(-decrementValue))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            // Update score in TextView
                            updateScore(currentUser, starValue, pg);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
    }
}

//        FirestoreHelper.updateScore(currentUser, starValue, pg);

//        long incrementValue = 10;  // achum
//        FirestoreHelper.incrementScore(currentUser, incrementValue, starValue, pg);

//        long decrementValue = 10;  // nvazum
//        FirestoreHelper.decrementScore(currentUser, decrementValue, starValue, pg);