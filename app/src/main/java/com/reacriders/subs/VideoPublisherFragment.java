package com.reacriders.subs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoPublisherFragment extends BottomSheetDialogFragment{

    private String uid, email;
    private String duration, videoName, videoUrl, videoId, description;
    private WebView webView;
    private TextView videoDuration, videoTitle, videoDescription, publishStars, publishMoney;
    private LinearLayout publishBtn, publishForMoneyBtn;

    private ImageButton backBtn, question;
    private int minutes, mmin, stars, money;

    private ImageView banImg,publicImg,checkingImg,lockImg;

    private boolean forstars = false;







    public VideoPublisherFragment(String videoId, String duration, String videoName, String description, ImageView banImg, ImageView publicImg, ImageView checkingImg, ImageView lockImg) {
        this.videoId = videoId;
        this.duration = duration;
        this.videoName = videoName;
        this.description = description;
        this.banImg = banImg;
        this.publicImg = publicImg;
        this.checkingImg = checkingImg;
        this.lockImg = lockImg;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_publisher_fragment, container, false);


        //wv
        webView = view.findViewById(R.id.video);
        //tv
        videoDuration = view.findViewById(R.id.videoDuration);
        videoTitle = view.findViewById(R.id.videoTitle);
        videoDescription = view.findViewById(R.id.videoDescription);
        publishStars = view.findViewById(R.id.publish_stars);
        publishMoney = view.findViewById(R.id.publish_money);
        //btn LL
        publishBtn = view.findViewById(R.id.publishBtn);
        publishForMoneyBtn = view.findViewById(R.id.publish_for_money_Btn);
        //btn img-btn
        backBtn = view.findViewById(R.id.backBtn);//close fragment button
        question = view.findViewById(R.id.question);

        webView.getSettings().setJavaScriptEnabled(true);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
            Log.d("PubVideos", "onCreateView: "+ uid + "\n"+ email);
        }else{
            email = "null";
            uid = "null";
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);





        videoUrl = "<html><body style='margin:0;padding:0;'><iframe style='height:100%;width:100%' src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\"></iframe></body></html>";
        webView.loadData(videoUrl, "text/html", "utf-8");

        minutes = convertToMinutes(duration);
        mmin = (minutes/10)+1;
        stars = minutes*100;
        money = mmin*8;


        videoDuration.setText(duration);
        videoTitle.setText(videoName);
        videoDescription.setText(description);

        publishStars.setText(String.valueOf(stars));
        publishMoney.setText(String.valueOf(money));

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                             DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the score from the document
                                long score = document.getLong("score");
                                // Check if score is greater than stars
                                if (score >= stars) {
                                    forstars = true;
                                    AttentionFragment attentionFragment = AttentionFragment.newInstance(duration, videoName, videoUrl, videoId, description, minutes, mmin, stars, money, forstars, banImg, publicImg, checkingImg, lockImg);
                                    attentionFragment.show(getFragmentManager(), "AttentionFragment");
                                } else {
                                    Toast.makeText(getActivity(),"You don't have enough stars", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("PubVideos", "No such document");
                            }
                        } else {
                            Log.d("PubVideos", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
        publishForMoneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forstars = false;
                AttentionFragment attentionFragment = AttentionFragment.newInstance(duration, videoName, videoUrl, videoId, description, minutes, mmin, stars, money, forstars, banImg, publicImg, checkingImg, lockImg);
                attentionFragment.show(getFragmentManager(), "AttentionFragment");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(),R.style.MyTransparentBottomSheetDialogTheme);
    }
    public int convertToMinutes(String duration) {
        String[] parts = duration.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return hours * 60 + minutes + 1;
    }

}
