package com.reacriders.subs;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reacriders.subs.R;

import java.util.HashMap;
import java.util.Map;

public class YourFriendsFragment extends Fragment {
    private TextView uidTextView;
    private TextView emailTextView;
    private Button inviteBtn;
    private EditText inviteInput;

    private String uid, email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_friends_fragment, container, false);

        inviteBtn = view.findViewById(R.id.inviteBtn);
        inviteInput = view.findViewById(R.id.invite_input);

        // Access the current user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
            Log.d("Friends", "onCreateView: "+ uid + "\n"+ email);
        }else{
            email = "null";
            uid = "null";
        }



        FirebaseFirestore db = FirebaseFirestore.getInstance();

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to_email = inviteInput.getText().toString();
                if (to_email.equals("")) {
                    Toast.makeText(getActivity(), "Please write your friend's Google account to invite", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(to_email)) {
                    Toast.makeText(getActivity(), "Please enter a valid Gmail address", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(to_email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                        if (isNewUser) {
                                            // Check if a friend request document already exists
                                            DocumentReference docRef = db.collection("FriendRequests").document(to_email);
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            // Check if the inviter is the current user
                                                            String inviter = document.getString("inviter");
                                                            if (uid.equals(inviter)) {
                                                                // Send the email as usual
                                                                String subject = "SUBS invite";
                                                                String content = "Hey, welcome to SUBS. Your friend '" + email + "' has invited you on our journey from under the ocean all the way to the stars! Do you wanna start?\nIf you want, then sign up as '" + to_email + "' and get 30 stars as a gift from a friend. \uD83D\uDC19";
                                                                sendEmail(subject, content, to_email);
                                                            } else {
                                                                // The person is invited by another user
                                                                Toast.makeText(getActivity(), "This person is already invited by another user", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            // Document does not exist, create it
                                                            Map<String, Object> friendRequest = new HashMap<>();
                                                            friendRequest.put("inviter", uid);

                                                            db.collection("FriendRequests")
                                                                    .document(to_email)
                                                                    .set(friendRequest)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Log.d("TAG", "DocumentSnapshot successfully written!");

                                                                            String subject = "SUBS invite";
                                                                            String content = "Hey, welcome to SUBS. Your friend '" + email + "' has invited you on our journey from under the ocean all the way to the stars! Do you wanna start?\nIf you want, then sign up as '" + to_email + "' and get 30 stars as a gift from a friend. \uD83D\uDC19";
                                                                            sendEmail(subject, content, to_email);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.w("TAG", "Error writing document", e);
                                                                        }
                                                                    });
                                                        }
                                                    } else {
                                                        Log.d("TAG", "Failed with: ", task.getException());
                                                    }
                                                }
                                            });
                                        } else {
                                            // User exists
                                            Toast.makeText(getActivity(), "User exists", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Error
                                        Log.e("TAG", "Error checking if user exists", task.getException());
                                    }
                                }
                            });
                }
            }
        });
        return view;
    }
    public void sendEmail(String subject, String content, String to_email){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to_email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);

        intent.setPackage(null);
        startActivity(Intent.createChooser(intent, "Choose an app:"));

        Toast.makeText(getActivity(), "Invitation in progress ...", Toast.LENGTH_SHORT).show();

    }
    public boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@gmail+\\.+com+";
        return email.matches(emailPattern);
    }
}

