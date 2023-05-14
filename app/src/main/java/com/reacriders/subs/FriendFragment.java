package com.reacriders.subs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class FriendFragment extends Fragment {
    private String friend;
    private String imageUrl;

    public FriendFragment(String friend, String imageUrl) {
        this.friend = friend;
        this.imageUrl = imageUrl;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend, container, false);
        TextView friendData = view.findViewById(R.id.friendData);
        friendData.setText(friend);

        de.hdodenhof.circleimageview.CircleImageView imageData = view.findViewById(R.id.profileImageFriend);

        if (imageUrl != null) {
            // Use your favorite image loading library here to load the image from imageUrl into imageData
            // Here's an example with Glide:
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageData);
        }

        return view;
    }

}