package com.reacriders.subs;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoFragment extends Fragment {
    private String videoName, duration, imageUrl, videoId, description, email, uid;
    private TextView videoTitle;

    private String VideoCondition;
    private int cond;
    private ImageView banImg,publicImg,checkingImg,lockImg;

    public VideoFragment(String videoName, String imageUrl, String duration, String videoId, String description) {
        this.videoName = videoName;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.videoId = videoId;
        this.description = description;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
            Log.d("VideoFragment", "onCreateView: "+ uid + "\n"+ email);
        }else{
            email = "null";
            uid = "null";
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);

        //img-s
        banImg = view.findViewById(R.id.banIcon);
        lockImg = view.findViewById(R.id.lockIcon);
        publicImg = view.findViewById(R.id.publicIcon);
        checkingImg = view.findViewById(R.id.checkIcon);



        videoTitle = view.findViewById(R.id.videoTitle);
        videoTitle.setText(videoName);


        TextView videoDuration = view.findViewById(R.id.videoDuration);
        videoDuration.setText(duration);

        ImageView imageData = view.findViewById(R.id.videoImage);

        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .error(R.drawable.ic_video_tv)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Image load failed", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageData);
        }

        // Set OnClickListener to the root view of the fragment
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cond == 0){
                    VideoPublisherFragment videoPublisherFragment = new VideoPublisherFragment(videoId, duration, videoName, description, banImg, publicImg, checkingImg, lockImg);
                    videoPublisherFragment.show(getFragmentManager(), "VideoPublisherFragment");
                }else{
                    Toast.makeText(getActivity(), VideoCondition, Toast.LENGTH_SHORT).show();
                }
            }
        });


        docRef.collection("myVideos").document(videoId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Document exists
                                cond = document.getLong("condition").intValue(); // Assuming "condition" is a number
                                switch(cond){
                                    case 1:
                                        checkingImg.setVisibility(View.VISIBLE);
                                        VideoCondition = "Video is under review";
                                        break;
                                    case 2:
                                        publicImg.setVisibility(View.VISIBLE);
                                        VideoCondition = "Video successfully published";
                                        break;
                                    case 3:
                                        lockImg.setVisibility(View.VISIBLE);
                                        VideoCondition = "You can't post the same video twice";
                                        break;
                                    case 4:
                                        banImg.setVisibility(View.VISIBLE);
                                        VideoCondition = "Video has been banned";
                                        break;
                                    default:
                                        VideoCondition = "Something went wrong";
                                        break;
                                }
                            } else {
                                // Document does not exist
                                cond = 0;
                            }
                        } else {
                            Log.d("VideoFragment", "get failed with ", task.getException());
                        }
                    }
                });

        return view;
    }

    public void highlightTitleIfMatches(String searchText) {
        // Convert both videoName and searchText to lower case before comparing
        if (videoName.toLowerCase().contains(searchText.toLowerCase())) {
            videoTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue));
        } else {
            TypedValue typedValue = new TypedValue();
            // Resolve the colorPrimary attribute to the typedValue
            getActivity().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
            // Get the color from the typedValue
            int colorPrimary = typedValue.data;
            videoTitle.setTextColor(colorPrimary);
        }
    }
}
