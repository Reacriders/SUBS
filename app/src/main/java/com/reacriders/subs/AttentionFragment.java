package com.reacriders.subs;


import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttentionFragment extends DialogFragment {
    private String uid, email, duration, videoName, videoUrl, videoId, description;
    private int minutes, mmin, stars, money, condition;
    private boolean forstars;

    private Button continueBtn, cancelBtn;

    private ColorStateList colorStateList;

    private LinearLayout music, game, news, meme, tutorials, build, arts, programming, cooking, all, other;

    private String MusicStr, GameStr, NewsStr, MemeStr, TutorialsStr, BuildStr, ArtsStr, ProgrammingStr, CookingStr, AllStr, OtherStr;
    private CheckBox music_CB, game_CB, news_CB, meme_CB, tutorials_CB, build_CB, arts_CB, programming_CB, cooking_CB, all_CB, other_CB;
    private EditText other_ET;

    private HashMap<View, Boolean> clickedStateMap = new HashMap<>();

    private boolean custom = false;

    private int colorll,colorSH,colorDefaultThumb;

    private int contin = 0;

    private Switch fleshSwitch;

    private LinearLayout next_step, warn_viewers;

    private TextView warning_text, title;

    private String TAG = "AttentionPublisher";




    public static AttentionFragment newInstance(String duration, String videoName, String videoUrl, String videoId, String description, int minutes, int mmin, int stars, int money, boolean forstars) {
        AttentionFragment fragment = new AttentionFragment();
        Bundle args = new Bundle();
        args.putString("duration", duration);
        args.putString("videoName", videoName);
        args.putString("videoUrl", videoUrl);
        args.putString("videoId", videoId);
        args.putString("description", description);
        args.putInt("minutes", minutes);
        args.putInt("mmin", mmin);
        args.putInt("stars", stars);
        args.putInt("money", money);
        args.putBoolean("forstars", forstars);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
            email = getArguments().getString("email");
            duration = getArguments().getString("duration");
            videoName = getArguments().getString("videoName");
            videoUrl = getArguments().getString("videoUrl");
            videoId = getArguments().getString("videoId");
            description = getArguments().getString("description");
            minutes = getArguments().getInt("minutes");
            mmin = getArguments().getInt("mmin");
            stars = getArguments().getInt("stars");
            money = getArguments().getInt("money");
            forstars = getArguments().getBoolean("forstars");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attention_fragment, container, false);
        continueBtn = view.findViewById(R.id.continueBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);


        fleshSwitch = view.findViewById(R.id.flesh_switch);

        // LinearLayouts
        music = view.findViewById(R.id.music);
        game = view.findViewById(R.id.game);
        news = view.findViewById(R.id.news);
        meme = view.findViewById(R.id.meme);
        tutorials = view.findViewById(R.id.tutorials);
        build = view.findViewById(R.id.build);
        arts = view.findViewById(R.id.arts);
        programming = view.findViewById(R.id.programming);
        cooking = view.findViewById(R.id.cooking);
        all = view.findViewById(R.id.all);
        other = view.findViewById(R.id.other);

        // CheckBoxes
        music_CB = view.findViewById(R.id.music_CB);
        game_CB = view.findViewById(R.id.game_CB);
        news_CB = view.findViewById(R.id.news_CB);
        meme_CB = view.findViewById(R.id.meme_CB);
        tutorials_CB = view.findViewById(R.id.tutorials_CB);
        build_CB = view.findViewById(R.id.build_CB);
        arts_CB = view.findViewById(R.id.arts_CB);
        programming_CB = view.findViewById(R.id.programming_CB);
        cooking_CB = view.findViewById(R.id.cooking_CB);
        all_CB = view.findViewById(R.id.all_CB);
        other_CB = view.findViewById(R.id.other_CB);

        // EditText
        other_ET = view.findViewById(R.id.other_ET);

        //pgs
        next_step = view.findViewById(R.id.next_step);
        warn_viewers = view.findViewById(R.id.warn_viewers);
        warning_text = view.findViewById(R.id.warning_text);
        title = view.findViewById(R.id.title);

        warning_text.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
            Log.d("AttentionVideos", "onCreateView: "+ uid + "\n"+ email);
        }else{
            email = "null";
            uid = "null";
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);
        DocumentReference docRefVideos = db.collection("Videos").document(videoId);



        colorDefaultThumb = ContextCompat.getColor(getContext(), R.color.gray);
        if (forstars) {
            colorStateList = ContextCompat.getColorStateList(getContext(), R.color.blue);
            colorll = ContextCompat.getColor(getContext(), R.color.blue);
            colorSH = ContextCompat.getColor(getContext(), R.color.blue);
            condition = 2;
        }else{
            colorStateList = ContextCompat.getColorStateList(getContext(), R.color.green);
            colorll = ContextCompat.getColor(getContext(), R.color.green);
            colorSH = ContextCompat.getColor(getContext(), R.color.green);
            condition = 1;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            continueBtn.setBackgroundTintList(colorStateList);
        }
        List<LinearLayout> views = Arrays.asList(music, game, news, meme, tutorials, build, arts, programming, cooking, all, other);
        List<CheckBox> checkBoxes = Arrays.asList(music_CB, game_CB, news_CB, meme_CB, tutorials_CB, build_CB, arts_CB, programming_CB, cooking_CB, all_CB, other_CB);

        for (int i = 0; i < views.size(); i++) {
            LinearLayout view1 = views.get(i);
            CheckBox checkBox = checkBoxes.get(i);
            String viewTag = (String) view1.getTag();

            clickedStateMap.put(view1, false);

            View.OnClickListener clickListener = v -> handleClick(view1, checkBox, viewTag);

            view1.setOnClickListener(clickListener);
            checkBox.setOnClickListener(clickListener);
        }


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contin == 0){
                    contin += 1;
                    title.setText("Select tag(s) for video");
                    warning_text.setVisibility(View.GONE);
                    next_step.setVisibility(View.VISIBLE);
                } else if (contin == 1) {
                    contin += 1;
                    title.setText("Warn viewers");
                    next_step.setVisibility(View.GONE);
                    warn_viewers.setVisibility(View.VISIBLE);
                    continueBtn.setText("Publish");
                } else if (contin >= 2) {
                    docRefVideos.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "Document exists!");
                                    Toast.makeText(getActivity(), "this video has already been posted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "Preparing to publish!");
                                    // Check if sub-collection "myVideos" exists in the "Users" document
                                    docRef.collection("myVideos").document(videoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Toast.makeText(getActivity(), "This video has already been published once", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Map<String, Object> newVideo = new HashMap<>();
                                                    newVideo.put("condition", condition);
                                                    docRef.collection("myVideos").document(videoId).set(newVideo)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "Document successfully written!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error writing document", e);
                                                                }
                                                            });
                                                    //TODO: after 1 week condition should become 3 if forstars is true, write server side code do not run this process on device.
                                                }
                                            } else {
                                                Log.d(TAG, "Failed with: ", task.getException());
                                            }
                                        }
                                    });
                                }
                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                            }
                        }
                    });
                }
            }
        });

        fleshSwitch = view.findViewById(R.id.flesh_switch);

        fleshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If the switch is turned on, change the thumb color to blue
                    fleshSwitch.getThumbDrawable().setColorFilter(colorSH, PorterDuff.Mode.MULTIPLY);
                } else {
                    // If the switch is turned off, change the thumb color back to the default color
                    fleshSwitch.getThumbDrawable().setColorFilter(colorDefaultThumb, PorterDuff.Mode.MULTIPLY);
                }
            }
        });



        return view;
    }




    private void handleClick(LinearLayout view1, CheckBox checkBox, String viewTag) {
        boolean isClicked = clickedStateMap.get(view1);
        if (!isClicked) { // If clicked first time
            view1.setBackgroundColor(colorll);
            checkBox.setChecked(true);

            // Set the corresponding string based on the view's tag
            switch (viewTag) {
                case "music":
                    MusicStr = "music";
                    break;
                case "game":
                    GameStr = "game";
                    break;
                case "news":
                    NewsStr = "news,critics";
                    break;
                case "meme":
                    MemeStr = "memes,jokes";
                    break;
                case "tutorials":
                    TutorialsStr = "learning,tutorials";
                    break;
                case "build":
                    BuildStr = "building,crafting";
                    break;
                case "arts":
                    ArtsStr = "arts,design";
                    break;
                case "programming":
                    ProgrammingStr = "programming,coding";
                    break;
                case "cooking":
                    CookingStr = "cooking";
                    break;
                case "all":
                    AllStr = "mixed type of content";
                    break;
                case "other":
                    OtherStr = other_ET.getText().toString();
                    setCustom(true);
                    break;
            }
        } else { // If clicked next time
            view1.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            checkBox.setChecked(false);

            // Clear the corresponding string based on the view's tag
             switch (viewTag) {
                case "music":
                    MusicStr = "";
                    break;
                case "game":
                    GameStr = "";
                    break;
                case "news":
                    NewsStr = "";
                    break;
                case "meme":
                    MemeStr = "";
                    break;
                case "tutorials":
                    TutorialsStr = "";
                    break;
                case "build":
                    BuildStr = "";
                    break;
                case "arts":
                    ArtsStr = "";
                    break;
                case "programming":
                    ProgrammingStr = "";
                    break;
                case "cooking":
                    CookingStr = "";
                    break;
                case "all":
                    AllStr = "";
                    break;
                case "other":
                    OtherStr = "";
                    setCustom(false);
                    break;
            }
        }

        clickedStateMap.put(view1, !isClicked);
    }


    private void setCustom(boolean custom_bul){
        custom = custom_bul;
        Log.d("checkCustom", "checkCustom: "+Boolean.toString(custom));
    }
}
