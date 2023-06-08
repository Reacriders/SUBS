package com.reacriders.subs;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WatchVideoFragment extends Fragment {

    private String videoId, publisherChannelId, category, description, duration, publisher, title;
    private int reports, minutes;
    private boolean publishType, flash;

    private String channelId, channelName, profileImageUrl;
    private String email,uid;

    private ImageView watchButton, videoImage;

    private Handler handler;
    private Runnable runnable;

    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private TextView videoDuration;

    private LinearLayout accountLL;

    private ImageView checkedIcon;

    private TextView videoTitle;
    private TextView tags;
    private TextView accountTextView;

    private ImageView accountCopyBtn;
    private ProgressBar likePb, subscribePb;
    private ImageView likeTick, subscribeTick;

    private LinearLayout reportWarnLL, flashWarnLL;
    private boolean shouldCheckTubsUp;
    private boolean needSubs;
    private boolean shouldShowInternet = true;

    private LinearLayout BackBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.watch_video_fragment, container, false);
        watchButton = view.findViewById(R.id.watchButton);
        videoImage = view.findViewById(R.id.videoImage720);
        profileImage = view.findViewById(R.id.profileImage);

        videoTitle = view.findViewById(R.id.videoTitle);
        videoDuration = view.findViewById(R.id.videoDuration);


        checkedIcon = view.findViewById(R.id.checked_icon);
        tags = view.findViewById(R.id.tags);
        accountTextView = view.findViewById(R.id.account);
        accountLL = view.findViewById(R.id.accountLL);
        accountCopyBtn = view.findViewById(R.id.accountCopyBtn);

        flashWarnLL = view.findViewById(R.id.flashWarnLL);
        reportWarnLL = view.findViewById(R.id.reportWarnLL);
        shouldCheckTubsUp = true;
        needSubs = true;


        likePb = view.findViewById(R.id.likePb);
        subscribePb = view.findViewById(R.id.subscribePb);


        likeTick = view.findViewById(R.id.likeTick);
        subscribeTick = view.findViewById(R.id.subscribeTick);


        BackBtn = view.findViewById(R.id.BackBtn);


        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContentFragment();
            }
        });





        Bundle args = getArguments();
        if (args != null) {
            videoId = args.getString("videoId");
            publisherChannelId = args.getString("publisherChannelId");
            category = args.getString("category");
            description = args.getString("description");
            duration = args.getString("duration");
            publisher = args.getString("publisher");
            title = args.getString("title");
            reports = args.getInt("reports");
            minutes = args.getInt("minutes");
            publishType = args.getBoolean("publishType");
            flash = args.getBoolean("flash");
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
        }else{
            email = "null";
            uid = "null";
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);



        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("channel2")) {
                        channelId = documentSnapshot.getString("channel2");
                        channelName = documentSnapshot.getString("channelName2");
                        profileImageUrl = documentSnapshot.getString("profileImageUrl2");

                    } else if (documentSnapshot.contains("channel")) {
                        channelId = documentSnapshot.getString("channel");
                        channelName = documentSnapshot.getString("channelName");
                        profileImageUrl = documentSnapshot.getString("profileImageUrl");
                    }
                    if(channelId != null){
                        // Set up onClickListener for watchButton
                        watchButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                                startActivity(intent);
                                Log.d("TimerR", "Duration: "+duration);

                            }
                        });
                        handler = new Handler();
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (isInternetAvailable()) {
                                    if (!shouldShowInternet) {
                                        shouldShowInternet = true;
                                        Toast.makeText(getActivity(), "Internet connection restored", Toast.LENGTH_SHORT).show();
                                    }
                                    if (shouldCheckTubsUp) {
                                        CheckTubsUp(docRef, documentSnapshot);
                                    }
                                    if(needSubs){
                                        CheckSubscription(docRef, documentSnapshot);
                                    }
                                } else {
                                    if (shouldShowInternet) {
                                        shouldShowInternet = false;
                                        Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                handler.postDelayed(this, 3000);
                            }
                        };
                        handler.postDelayed(runnable, 3000);
                    }else{
                        Toast.makeText(getActivity(), "Error channel not found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.d("Firestore", "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error getting documents.", e);
            }
        });

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
                            Glide.with(WatchVideoFragment.this)
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

                                    // Load the image into profileImage ImageView
                                    Glide.with(WatchVideoFragment.this)
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

        videoDuration.setText(duration);


        videoTitle.setText(title);

        if(category.equals("")){
            tags.setVisibility(View.INVISIBLE);
        }else {
            tags.setText(category);
        }
        if(flash==true){
            flashWarnLL.setVisibility(View.VISIBLE);
        }
        if(publishType==false){
            checkedIcon.setVisibility(View.VISIBLE);
        }
        if(reports >= 100){
            reportWarnLL.setVisibility(View.VISIBLE);
        }


        db.collection("Users").document(publisher).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains("account")) {
                    final String account = documentSnapshot.getString("account");
                    accountLL.setVisibility(View.VISIBLE);
                    accountTextView.setText(account);
                    accountCopyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("account", account);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getActivity(), "Account copied to clipboard", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    accountLL.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
    private boolean isInternetAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (NullPointerException e) {
            // handle the exception, or return a sensible default
            return false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }


    @SuppressLint("LongLogTag")
    private void CheckTubsUp(DocumentReference docRef, DocumentSnapshot documentSnapshot){
        final GeneralActivity generalActivity = (GeneralActivity) getActivity();
        Log.d("YouTubeLikedVideosPlaylist", "Video ID: " + videoId);
        if (generalActivity != null) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    // Call the method on a background thread
                    generalActivity.retrieveLikedVideosPlaylist(channelId);

                    // Switch back to the main thread to access UI elements
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            String likedId = generalActivity.getLastVideoId();
                            if (likedId != null && likedId.equals(videoId)) {
                                // Getting the reference to the 'likes' collection inside the current document
                                CollectionReference likesRef = docRef.collection("likes");

                                // Check if the 'likes' collection already contains the document with id 'videoId'
                                likesRef.document(videoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d("Firestore", "Document already exists!");
                                                shouldCheckTubsUp = false;
                                                likePb.setVisibility(View.GONE);
                                                likeTick.setVisibility(View.VISIBLE);
                                            } else {
                                                // If not, then add an empty document with id 'videoId'
                                                likesRef.document(videoId).set(new HashMap<>())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("Firestore", "DocumentSnapshot successfully written!");
                                                                // Get the current score from the document
                                                                Integer currentScore = documentSnapshot.getLong("score").intValue();
                                                                int plusToStars;
                                                                if (!publishType) {
                                                                    plusToStars = 4;
                                                                } else {
                                                                    plusToStars = 2;
                                                                }
                                                                currentScore += plusToStars;
                                                                Toast.makeText(getActivity(), "★ Stars +"+String.valueOf(plusToStars), Toast.LENGTH_SHORT).show();

                                                                docRef.update("score", currentScore)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                                                                shouldCheckTubsUp = false;
                                                                                likePb.setVisibility(View.GONE);
                                                                                likeTick.setVisibility(View.VISIBLE);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w("Firestore", "Error updating document", e);
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        } else {
                                            Log.d("Firestore", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }else{
                                CollectionReference likesRef = docRef.collection("likes");

                                // Check if the 'likes' collection already contains the document with id 'videoId'
                                likesRef.document(videoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d("Firestore", "Document already exists!");
                                                shouldCheckTubsUp = false;
                                                likePb.setVisibility(View.GONE);
                                                likeTick.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            Log.d("Firestore", "get failed with ", task.getException());
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            });
        }
    }
    private void CheckSubscription(DocumentReference docRef, DocumentSnapshot documentSnapshot){
        final GeneralActivity generalActivity = (GeneralActivity) getActivity();
        if (generalActivity != null) {
            generalActivity.retrieveSubscriptions(channelId, publisherChannelId, new GeneralActivity.SubscriptionCheckListener() {
                @Override
                public void onSubscriptionCheckCompleted(boolean isSubscribed) {
                    if(isSubscribed){
                        Log.d("Lokopoko", "onSubscriptionCheckCompleted: U call me Pocoloko un putititi tu loko!!!");

                        // check if inside docRef exists sub-collection "subscribes",
                        CollectionReference subCollectionRef = docRef.collection("subscribes");
                        DocumentReference docRefInSub = subCollectionRef.document(publisherChannelId);
                        docRefInSub.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (!document.exists()) {
                                        // if document does not exist add it
                                        Map<String, Object> data = new HashMap<>();
                                        // add necessary data to the map
                                        docRefInSub.set(data)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("SubCheck", "DocumentSnapshot successfully written!");

                                                        // Fetch the most recent document snapshot
                                                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                // Get the current score from the document
                                                                Integer currentScore = documentSnapshot.getLong("score").intValue();
                                                                int plusToStars;
                                                                if (!publishType) {
                                                                    plusToStars = 20;
                                                                } else {
                                                                    plusToStars = 10;
                                                                }
                                                                currentScore += plusToStars;
                                                                Toast.makeText(getActivity(), "★ Stars +"+String.valueOf(plusToStars), Toast.LENGTH_SHORT).show();

                                                                docRef.update("score", currentScore)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d("Firestore", "DocumentSnapshot successfully updated!");

                                                                                needSubs = false;
                                                                                subscribePb.setVisibility(View.GONE);
                                                                                subscribeTick.setVisibility(View.VISIBLE);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w("Firestore", "Error updating document", e);
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                    }
                                                });
                                    }else{
                                        Log.d("SubCheck", "Chrakadil.", task.getException());
                                        needSubs = false;
                                        subscribePb.setVisibility(View.GONE);
                                        subscribeTick.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    Log.d("SubCheck", "Failed to get document.", task.getException());
                                }
                            }
                        });
                    } else {
                        needSubs = true;
                    }
                }
            });
        }
    }
    private void openContentFragment() {
        // Create new fragment and transaction
        ContentFragment newFragment = new ContentFragment();

        // If you have any data to pass to the ContentFragment, create a Bundle, put data in the Bundle, and set it to fragment.

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // The view ID here should be the ID of the layout container where the ContentFragment should be placed.
        transaction.replace(R.id.fragment_container, newFragment);

        // Commit the transaction
        transaction.commit();
    }

}
