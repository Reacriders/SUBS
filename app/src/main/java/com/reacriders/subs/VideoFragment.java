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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class VideoFragment extends Fragment {
    private String videoName;
    private String duration;
    private String imageUrl;
    private TextView videoTitle;  // Declare as instance variable

    public VideoFragment(String videoName, String imageUrl, String duration) {
        this.videoName = videoName;
        this.imageUrl = imageUrl;
        this.duration = duration;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video, container, false);
        videoTitle = view.findViewById(R.id.videoTitle);  // Assign here
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
