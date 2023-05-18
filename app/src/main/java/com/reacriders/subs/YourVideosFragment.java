package com.reacriders.subs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.youtube.model.SearchResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YourVideosFragment extends Fragment {
    private String uid, email;

    EditText SearchInput;
    Button SearchBtn;

    private List<VideoFragment> videoFragments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_videos_fragment, container, false);

        SearchBtn = view.findViewById(R.id.search_btn);
        SearchInput=view.findViewById(R.id.search_input);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
            Log.d("Friends", "onCreateView: "+ uid + "\n"+ email);
        }else{
            email = "null";
            uid = "null";
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // The document with the UID exists, get the channel id
                        String channelId = document.getString("channel");
                        if (channelId != null) {
                            // Use the Youtube Data API to get the videos for the channel
                            new FetchYoutubeDataTask().execute(channelId);
                        } else {
                            Log.d("VideosData", "User " + uid + " does not have a channel ID");
                        }
                    } else {
                        Log.d("VideosData", "No such document");
                    }
                } else {
                    Log.d("VideosData", "get failed with ", task.getException());
                }
            }
        });

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = SearchInput.getText().toString().trim();
                for (VideoFragment fragment : videoFragments) {
                    fragment.highlightTitleIfMatches(searchText);
                }
            }
        });
        return view;
    }

    private class FetchYoutubeDataTask extends AsyncTask<String, Void, Map<SearchResult, String>> {
        @Override
        protected Map<SearchResult, String> doInBackground(String... params) {
            // params[0] is the channelId
            List<SearchResult> videos = YoutubeAPI.getChannelVideos(params[0]);
            if (videos != null && !videos.isEmpty()) {
                List<String> videoIds = new ArrayList<>();
                for (SearchResult video : videos) {
                    videoIds.add(video.getId().getVideoId());
                }
                Map<String, String> videoDurations = YoutubeAPI.getVideoDurations(videoIds);
                Map<SearchResult, String> videosWithDurations = new HashMap<>();
                for (SearchResult video : videos) {
                    videosWithDurations.put(video, videoDurations.get(video.getId().getVideoId()));
                }
                return videosWithDurations;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<SearchResult, String> videosWithDurations) {
            // Get a reference to the "cactus" layout
            try {
                View cactusLayout = getView().findViewById(R.id.cactus);
                if (videosWithDurations != null && !videosWithDurations.isEmpty()) {
                    FragmentManager fragmentManager = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    for (Map.Entry<SearchResult, String> entry : videosWithDurations.entrySet()) {
                        SearchResult video = entry.getKey();
                        String videoId = video.getId().getVideoId();  // Get the video id

                        String videoName = video.getSnippet().getTitle();
                        String description = video.getSnippet().getDescription(); // Get the video description
                        String videoImageUrl = video.getSnippet().getThumbnails().getDefault().getUrl();
                        String videoDuration = convertISO8601Duration(entry.getValue());

                        Log.d("VideosData", "Video name: " + videoName + ", Description: " + description + ", Image URL: " + videoImageUrl + ", Duration: " + videoDuration);

                        // Create a new VideoFragment for this video, pass videoId as argument
                        VideoFragment videoFragment = new VideoFragment(videoName, videoImageUrl, videoDuration, videoId, description);
                        // Add the fragment to the container
                        fragmentTransaction.add(R.id.videos_container, videoFragment);
                        videoFragments.add(videoFragment);
                    }
                    fragmentTransaction.commit();

                    // Since we have videos, make sure "cactus" layout is hidden
                    cactusLayout.setVisibility(View.GONE);
                } else {
                    // If no videos were fetched, make "cactus" layout visible
                    cactusLayout.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.d("VideosData", "err: " + e);
            }
        }
    }
    private String convertISO8601Duration(String duration) {
        String time = duration.substring(2);
        String hour = "0", minute = "0", second = "0";
        if (time.contains("H")) {
            String[] split = time.split("H");
            hour = split[0];
            time = split.length > 1 ? split[1] : "";
        }
        if (time.contains("M")) {
            String[] split = time.split("M");
            minute = split[0];
            time = split.length > 1 ? split[1] : "";
        }
        if (time.contains("S")) {
            second = time.split("S")[0];
        }

        return String.format("%s:%s:%s", hour, minute, second);
    }
}
