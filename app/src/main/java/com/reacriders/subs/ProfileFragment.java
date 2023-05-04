package com.reacriders.subs;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reacriders.subs.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding; // Add this line

    private FirebaseAuth firebaseAuth;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false); // Modify this line
        View view = binding.getRoot(); // Modify this line

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        binding.logoutBtn.setOnClickListener(new View.OnClickListener() { // Modify this line
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
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
                binding.emailTv.setText(email); // Modify this line
            }if(UID != null) {
                binding.YoutubeIdTv.setText(UID); // Modify this line
            }
        }
    }
}


