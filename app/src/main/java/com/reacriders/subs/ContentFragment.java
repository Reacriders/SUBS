package com.reacriders.subs;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.reacriders.subs.R;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContentFragment extends Fragment {

    private Button sendEmailButton;
    private String email, uid;
    private Spinner spinner;
    private String TAG = "ContentFragment";
    private String selected;
    private String channelId = null;
    private LinearLayout guideLL, videoList;
    private TextView guideText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        guideLL = view.findViewById(R.id.guide_LL);
        videoList = view.findViewById(R.id.VideoList);
        guideText = view.findViewById(R.id.guide_text);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
            Log.d("UrVideos", "onCreateView: "+ uid + "\n"+ email);
        }else{
            email = "null";
            uid = "null";
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);
        // Check if device is connected to the internet
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();



        spinner = view.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_data, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: "+selected);
                if (isConnected && uid != null) {
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            guideLL.setVisibility(View.GONE);
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.contains("channel2")) {
                                    channelId = documentSnapshot.getString("channel2");
                                    Log.d(TAG, "onSuccess: "+channelId);
                                    videoList.removeAllViews();
                                    switch (selected) {
                                        case "Tester Mode":
                                            VideoList("Checkpoint");
                                            break;
                                        default:
                                            VideoList("Videos");
                                            break;
                                    }

                                } else if (documentSnapshot.contains("channel")) {
                                    channelId = documentSnapshot.getString("channel");
                                    Log.d(TAG, "onSuccess: "+channelId);
                                    videoList.removeAllViews();
                                    switch (selected) {
                                        case "Tester Mode":
                                            VideoList("Checkpoint");
                                            break;
                                        default:
                                            VideoList("Videos");
                                            break;
                                    }
                                }else{
                                    guideLL.setVisibility(View.VISIBLE);
                                    channelId = "null";
                                    guideText.setText("Your account is not associated with any YouTube channel. To fix this issue, \"switch\" your account in \"Account Settings\" and select the Google account that is connected to your channel.\n" +
                                            "\n" +
                                            "Here is a step by step guide:\n" +
                                            "1. go to \"Account\",\n" +
                                            "2. Click \"Settings\"\n" +
                                            "3. Press the switch button,\n" +
                                            "4. Sign in to your channel account.\n" +
                                            "\n" +
                                            "If you don't see the settings section, you may see the RED button to switch account inside the Account section, this is what you need.");
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to fetch data", e);
                        }
                    });
                }else {
                    guideLL.setVisibility(View.VISIBLE);
                    guideText.setText("If you see this text, make sure the device is connected to the internet.\nIf the device is connected to the Internet, however you still see this text, please contact us by clicking the button below to report your problem.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected = "All Videos";
                Log.d(TAG, "onItemSelected: "+selected);
                if (isConnected && uid != null) {
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            guideLL.setVisibility(View.GONE);
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.contains("channel2")) {
                                    channelId = documentSnapshot.getString("channel2");
                                    Log.d(TAG, "onSuccess: "+channelId);
                                    videoList.removeAllViews();
                                    switch (selected) {
                                        case "Tester Mode":
                                            VideoList("Checkpoint");
                                            break;
                                        default:
                                            VideoList("Videos");
                                            break;
                                    }

                                } else if (documentSnapshot.contains("channel")) {
                                    channelId = documentSnapshot.getString("channel");
                                    Log.d(TAG, "onSuccess: "+channelId);
                                    videoList.removeAllViews();
                                    switch (selected) {
                                        case "Tester Mode":
                                            VideoList("Checkpoint");
                                            break;
                                        default:
                                            VideoList("Videos");
                                            break;
                                    }
                                }else{
                                    guideLL.setVisibility(View.VISIBLE);
                                    channelId = "null";
                                    guideText.setText("Your account is not associated with any YouTube channel. To fix this issue, \"switch\" your account in \"Account Settings\" and select the Google account that is connected to your channel.\n" +
                                            "\n" +
                                            "Here is a step by step guide:\n" +
                                            "1. go to \"Account\",\n" +
                                            "2. Click \"Settings\"\n" +
                                            "3. Press the switch button,\n" +
                                            "4. Sign in to your channel account.\n" +
                                            "\n" +
                                            "If you don't see the settings section, you may see the RED button to switch account inside the Account section, this is what you need.");
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Failed to fetch data", e);
                        }
                    });
                }else {
                    guideLL.setVisibility(View.VISIBLE);
                    guideText.setText("If you see this text, make sure the device is connected to the internet.\nIf the device is connected to the Internet, however you still see this text, please contact us by clicking the button below to report your problem.");
                }
            }
        });



        sendEmailButton = view.findViewById(R.id.sendEmailButton);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        return view;
    }

    protected void sendEmail() {
        String[] TO = {"reacriders@gmail.com"}; //replace with the recipient's email
        String[] CC = {email}; //replace with the CC recipient's email
        Intent emailIntent = new Intent(Intent.ACTION_VIEW);

        // Try to directly open Gmail application
        Uri data = Uri.parse("mailto:?subject=" + "SUBS: Help (1)"
                + "&body=" + "Hello, I wanted to complete my tasks, but I ran into a problem that I don’t know how to solve.\n\n" +
                "(Describe the problem)\n\n(Describe what you tried to solve the problem)\n\n\n"+
                "Here is my account Info: " + email + "\n" + "\"" + uid +"\""
                + "&to=" + TO[0]
                + "&cc=" + CC[0]);
        emailIntent.setData(data);
        emailIntent.setPackage("com.google.android.gm");

        try {
            startActivity(emailIntent);
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Gmail App is not installed", Toast.LENGTH_SHORT).show();
        }
    }
    private void VideoList(String scanDocumentName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(scanDocumentName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try{
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            boolean gates = false;
                            switch (selected) {
                                case "Checked Videos":
                                    if(document.getBoolean("publish_type")==false){gates = true;}
                                    break;
                                case "My Videos":
                                    if(document.getString("publisher").equals(uid)){gates = true;}
                                    break;
                                default:
                                    gates = true;
                                    break;
                            }
                            if (gates) {
                                String videoId = document.getId();
                                String publisherChannelId = document.getString("channel");
                                String category = document.getString("category");
                                String description = document.getString("description");
                                String duration = document.getString("duration");
                                String publisher = document.getString("publisher");
                                String title = document.getString("title");
                                int reports = document.getLong("reports").intValue();
                                int minutes = document.getLong("minutes").intValue();
                                boolean publishType = document.getBoolean("publish_type");
                                boolean flash = document.getBoolean("flash");


                                TaskVideo fragment = new TaskVideo(videoId, publisherChannelId, category, description, duration, publisher, title, reports, minutes, publishType, flash);

                                fragmentTransaction.add(R.id.VideoList, fragment);
                            }
                        }

                        fragmentTransaction.commit();
                    }catch (Exception e){
                        Log.d(TAG, "onComplete: Err: "+e);
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
