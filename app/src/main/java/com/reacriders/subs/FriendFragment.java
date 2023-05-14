package com.reacriders.subs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FriendFragment extends Fragment{
    private String email;

    public FriendFragment(String email) {
        this.email = email;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend, container, false);
        TextView friendEmail = view.findViewById(R.id.friendEmail);
        friendEmail.setText(email);
        return view;
    }
}

