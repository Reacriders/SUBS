package com.reacriders.subs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        BoolNum = 0;
        track = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.switchAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAccount();
            }
        });
        binding.switchAccountBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAccount();
            }
        });
        binding.mySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BoolNum != 1){
                    invis();
                    BoolNum = 1;
                    track = 1;
                }
                if (track%2 == 0){
                    binding.yourSettings.setVisibility(View.GONE);
                }else{
                    binding.yourSettings.setVisibility(View.VISIBLE);
                }
                track = track+1;
            }
        });
        binding.myFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BoolNum != 2){
                    invis();
                    BoolNum = 2;
                    track = 1;
                }
                if (track%2 == 0){
                    binding.yourSettings.setVisibility(View.GONE);
                }else{
                    binding.yourSettings.setVisibility(View.VISIBLE);
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
                binding.YoutubeIdTv.setText(UID);
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
        binding.yourSettings.setVisibility(View.GONE);
    }

}
