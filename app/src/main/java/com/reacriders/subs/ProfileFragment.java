package com.reacriders.subs;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reacriders.subs.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private int BoolNum = 0;
    private int track = 0;

    private FrameLayout fragmentContainer;
    private TextView mySettings, myVideos, myFriends, myTasks;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        GeneralActivity parentActivity = (GeneralActivity) getActivity();
        boolean isChannelIdNone = parentActivity.isChannelIdNone();

        fragmentContainer = view.findViewById(R.id.fragment_container);
        mySettings = view.findViewById(R.id.my_settings);
        myVideos = view.findViewById(R.id.my_videos);
        myFriends = view.findViewById(R.id.my_friends);
        myTasks = view.findViewById(R.id.my_tasks);





        if (isChannelIdNone) {
            LinearLayout warningLayout = binding.Warning;
            LinearLayout setsLayout = binding.myBar;
            warningLayout.setVisibility(View.VISIBLE);
            setsLayout.setVisibility(View.GONE);
            Log.d("channel id", "fetchChannelName: got it");
        }


        BoolNum = 0;
        track = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);


        binding.switchAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAccount();
            }
        });






        mySettings.setOnClickListener(new View.OnClickListener() { //Settings
            @Override
            public void onClick(View v) {
                if (BoolNum != 1){
                    BoolNum = 1;
                    invis();
                    track = 1;
                }
                if (track%2 == 0){
                    fragmentContainer.setVisibility(View.GONE);
                    mySettings.setTextColor(Color.GRAY);
                    mySettings.setText("Settings");
                }else{

                    String textToUnderline = "Settings";
                    SpannableString spannableString = new SpannableString(textToUnderline);
                    spannableString.setSpan(new UnderlineSpan(), 0, textToUnderline.length(), 0);
                    mySettings.setText(spannableString);
                    mySettings.setTextColor(getPrimaryColor());

                    fragmentContainer.setVisibility(View.VISIBLE);
                    loadFragment(new YourSettingsFragment());

                }
                track = track+1;
            }
        });
        myFriends.setOnClickListener(new View.OnClickListener() {//Friends
            @Override
            public void onClick(View v) {
                if (BoolNum != 2){
                    BoolNum = 2;
                    invis();
                    track = 1;
                }
                if (track%2 == 0){
                    fragmentContainer.setVisibility(View.GONE);
                    myFriends.setTextColor(Color.GRAY);
                    myFriends.setText("Friends");
                }else{
                    String textToUnderline = "Friends";
                    SpannableString spannableString = new SpannableString(textToUnderline);
                    spannableString.setSpan(new UnderlineSpan(), 0, textToUnderline.length(), 0);
                    myFriends.setText(spannableString);
                    myFriends.setTextColor(getPrimaryColor());

                    fragmentContainer.setVisibility(View.VISIBLE);
                }
                track = track+1;
            }
        });
        myVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BoolNum != 3){
                    BoolNum = 3;
                    invis();
                    track = 1;
                }
                if (track%2 == 0){
                    fragmentContainer.setVisibility(View.GONE);
                    myVideos.setTextColor(Color.GRAY);
                    myVideos.setText("Videos");
                }else{
                    String textToUnderline = "Videos";
                    SpannableString spannableString = new SpannableString(textToUnderline);
                    spannableString.setSpan(new UnderlineSpan(), 0, textToUnderline.length(), 0);
                    myVideos.setText(spannableString);
                    myVideos.setTextColor(getPrimaryColor());

                    fragmentContainer.setVisibility(View.VISIBLE);
                }
                track = track+1;
            }
        });
        myTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BoolNum != 4){
                    BoolNum = 4;
                    invis();
                    track = 1;
                }
                if (track%2 == 0){
                    fragmentContainer.setVisibility(View.GONE);
                    myTasks.setTextColor(Color.GRAY);
                    myTasks.setText("Tasks");
                }else{
                    String textToUnderline = "Tasks";
                    SpannableString spannableString = new SpannableString(textToUnderline);
                    spannableString.setSpan(new UnderlineSpan(), 0, textToUnderline.length(), 0);
                    myTasks.setText(spannableString);
                    myTasks.setTextColor(getPrimaryColor());


                    fragmentContainer.setVisibility(View.VISIBLE);
                }
                track = track+1;
            }
        });

        return view;
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // user not found, go back to MainActivity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            // get user email
            String email = firebaseUser.getEmail();
            String UID = firebaseUser.getUid();
            if(email != null) {
                binding.emailTv.setText(email);
            }if(UID != null) {
                binding.UID.setText(UID);
            }
        }
    }

    private void switchAccount() {
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                });
    }
    public void showChannelWarning() {
        LinearLayout warningLayout = getView().findViewById(R.id.Warning);
        if (warningLayout != null) {
            warningLayout.setVisibility(View.VISIBLE);
            Log.d("channel id", "fetchChannelName: got it");
        }
        LinearLayout setsLayout = getView().findViewById(R.id.my_bar);
        if (setsLayout != null) {
            setsLayout.setVisibility(View.GONE);
            Log.d("channel id", "fetchChannelName: got it");
        }

    }
    private void invis(){
        if(BoolNum != 1){
            mySettings.setText("Settings");
            mySettings.setTextColor(Color.GRAY);
        }if(BoolNum != 2){
            myFriends.setText("Friends");
            myFriends.setTextColor(Color.GRAY);
        }if(BoolNum != 3){
            myVideos.setText("Videos");
            myVideos.setTextColor(Color.GRAY);
        }if(BoolNum != 4){
            myTasks.setText("Tasks");
            myTasks.setTextColor(Color.GRAY);
        }

    }
    private int getPrimaryColor() {
        TypedArray typedArray = requireActivity().getTheme().obtainStyledAttributes(new int[]{androidx.appcompat.R.attr.colorPrimary});
        int colorPrimary = typedArray.getColor(0, 0);
        typedArray.recycle();
        return colorPrimary;
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
    public void handleLogoutClicked() {
        firebaseAuth.signOut();
        checkUser();
    }

    public void handleSwitchAccountClicked() {
        switchAccount();
    }

    public void handleUpgradeClicked() {
        // Handle upgrade button click
    }



}
