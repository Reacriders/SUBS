package com.reacriders.subs;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Duration;


public class TaskVideo extends Fragment {
    private ImageView videoImage;
    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private TextView videoDuration, videoTitle, tags;
    private ImageView warnIcon, checkedIcon;
    private LinearLayout checksLL, checked1, checked2, checked3, shadow;
    private String videoId,publisherChannelId,category,description,duration,publisher,title;
    private int reports, minutes;
    private boolean publishType, flash;

    public TaskVideo(String videoId, String publisherChannelId, String category, String description, String duration, String publisher, String title, int reports, int minutes, boolean publishType, boolean flash) {
        this.videoId = videoId;
        this.publisherChannelId = publisherChannelId;
        this.category = category;
        this.description = description;
        this.duration = duration;
        this.publisher = publisher;
        this.title = title;
        this.reports = reports;
        this.minutes = minutes;
        this.publishType = publishType;
        this.flash = flash;
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_video, container, false);
        //Img-s
        videoImage = view.findViewById(R.id.videoImage720);
        profileImage = view.findViewById(R.id.profileImage);
        warnIcon = view.findViewById(R.id.warn_icon);
        checkedIcon = view.findViewById(R.id.checked_icon);
        //Tv-s
        videoDuration = view.findViewById(R.id.videoDuration);
        videoTitle = view.findViewById(R.id.videoTitle);
        tags = view.findViewById(R.id.tags);
        //LL-s
        checksLL = view.findViewById(R.id.checksLL);
        checked1 = view.findViewById(R.id.checked1);
        checked2 = view.findViewById(R.id.checked2);
        checked3 = view.findViewById(R.id.checked3);
        shadow = view.findViewById(R.id.shadowLL);

        // Set video title
        videoTitle.setText(title);

        // Set video tags (category in this case)
        if(category.equals("")){
            tags.setVisibility(View.INVISIBLE);
        }else {
            tags.setText(category);
        }
        if(flash==true){
            warnIcon.setVisibility(View.VISIBLE);
        }
        if(publishType==false){
            checkedIcon.setVisibility(View.VISIBLE);
        }
        videoDuration.setText(duration);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(publisher)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists() && document.contains("profileImageUrl")) {
                                    // Get the profileImageUrl from the document
                                    String profileImageUrl = document.getString("profileImageUrl");
                                    shadow.setVisibility(View.VISIBLE);

                                    // Load the image into profileImage ImageView
                                    Glide.with(TaskVideo.this)
                                            .load(profileImageUrl)
                                            .into(profileImage);
                                } else {
                                    Log.d("Firestore", "No such document or profileImageUrl not found");
                                }
                            }
                        } else {
                            Log.d("Firestore", "Failed with: ", task.getException());
                        }
                    }
                });


        // Set video image source
        String videoImageUrl = "https://i.ytimg.com/vi/"+videoId+"/hq720.jpg";

        // Check the image size before loading it
        Glide.with(this)
                .asBitmap()
                .load(videoImageUrl)
                .error(R.drawable.video_err) // This line will set the error image in case of a failure
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("GlideTag", "Load failed", e);
                        // Important to return false so the error placeholder can be placed
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.i("GlideTag", "Resource ready");
                        // Important to return false so the success placeholder can be placed
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        if (resource.getWidth() == 1280 && resource.getHeight() == 720) {
                            Glide.with(TaskVideo.this)
                                    .load(videoImageUrl)
                                    .into(videoImage);
                        }
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        videoImage.setImageDrawable(errorDrawable);
                    }
                });



        return view;
    }
}
