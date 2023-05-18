package com.reacriders.subs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class VideoPublisherFragment extends BottomSheetDialogFragment {

    private String duration, videoName, videoUrl, videoId, description;
    private WebView webView;
    private TextView videoDuration, videoTitle, videoDescription, publishStars, publishMoney;
    private LinearLayout publishBtn, publishForMoneyBtn;

    private ImageButton backBtn, question;
    private int minutes, mmin, stars, money;

    public VideoPublisherFragment(String videoId, String duration, String videoName, String description) {
        this.videoId = videoId;
        this.duration = duration;
        this.videoName = videoName;
        this.description = description;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_publisher_fragment, container, false);

        //wv
        webView = view.findViewById(R.id.video);
        //tv
        videoDuration = view.findViewById(R.id.videoDuration);
        videoTitle = view.findViewById(R.id.videoTitle);
        videoDescription = view.findViewById(R.id.videoDescription);
        publishStars = view.findViewById(R.id.publish_stars);
        publishMoney = view.findViewById(R.id.publish_money);
        //btn LL
        publishBtn = view.findViewById(R.id.publishBtn);
        publishForMoneyBtn = view.findViewById(R.id.publish_for_money_Btn);
        //btn img-btn
        backBtn = view.findViewById(R.id.backBtn);//close fragment button
        question = view.findViewById(R.id.question);

        webView.getSettings().setJavaScriptEnabled(true);





        videoUrl = "<html><body style='margin:0;padding:0;'><iframe style='height:100%;width:100%' src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\"></iframe></body></html>";
        webView.loadData(videoUrl, "text/html", "utf-8");

        minutes = convertToMinutes(duration);
        mmin = (minutes/10)+1;
        stars = minutes*100;
        money = mmin*8;


        videoDuration.setText(duration);
        videoTitle.setText(videoName);
        videoDescription.setText(description);

        publishStars.setText(String.valueOf(stars));
        publishMoney.setText(String.valueOf(money));



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(),R.style.MyTransparentBottomSheetDialogTheme);
    }
    public int convertToMinutes(String duration) {
        String[] parts = duration.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return hours * 60 + minutes + 1;
    }

}
