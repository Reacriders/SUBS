package com.reacriders.subs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.reacriders.subs.databinding.ActivityGeneralBinding;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import androidx.lifecycle.MutableLiveData;

public class GeneralActivity extends AppCompatActivity implements YourSettingsFragment.OnYourSettingsFragmentInteractionListener {

    private TextView channelNameTextView;
    private ExecutorService executorService;
    private Handler mainThreadHandler;

    // Firebase Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // TextView for displaying score
    private TextView starValue;

    private BottomNavigationView bottomNavigationView;

    private ProgressBar pg;

    private ChannelWarningListener channelWarningListener;

    private boolean isChannelIdNone;
    private String channelName;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnableCode;

    private boolean fetchYTData;

    private YouTube youtube;

    private String channelId;

    private String lastVideoId = null;

    private MutableLiveData<String> channelNameLiveData = new MutableLiveData<>();
    private MutableLiveData<String> profileImageUrlLiveData = new MutableLiveData<>();
    private GoogleAccountCredential googleAccountCredential;

    public void setChannelWarningListener(ChannelWarningListener channelWarningListener) {
        this.channelWarningListener = channelWarningListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        starValue = findViewById(R.id.star_value);
        pg = findViewById(R.id.Value_loader);
        channelNameTextView = findViewById(R.id.channelName);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());
        fetchYTData = false;

        fetchChannelName();

        // If the user is logged in, get the score
        FirestoreHelper.updateScore(currentUser, starValue, pg);

        // Define the task to be run here
        runnableCode = new Runnable() {
            @Override
            public void run() {
                FirestoreHelper.updateScore(currentUser, starValue, pg);
                if (fetchYTData) {
                    fetchChannelName();
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runnableCode);


        SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String accountName = sharedPref.getString("accountName", null);

        if (accountName != null) {
            googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                    this, Arrays.asList("https://www.googleapis.com/auth/youtube.readonly", "https://www.googleapis.com/auth/youtube.force-ssl"));
            googleAccountCredential.setSelectedAccountName(accountName);

            youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    googleAccountCredential)
                    .setApplicationName("YourAppName")
                    .build();
        } else {
            // Account not logged in
        }


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String tag;

                switch (item.getItemId()) {
                    case R.id.navigation_tasks:
                        selectedFragment = new ContentFragment();
                        tag = "ContentFragment";
                        break;
                    case R.id.navigation_account:
                        selectedFragment = new ProfileFragment();
                        tag = "ProfileFragment";
                        break;
                    case R.id.navigation_notifications:
                        selectedFragment = new NotificationsFragment();
                        tag = "NotificationsFragment";
                        break;
                    default:
                        tag = "";
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, tag).commit();
                }

                return true;
            }
        });

        // Check which fragment to display
        String fragmentName = getIntent().getStringExtra("fragment");
        Fragment selectedFragment;
        String tag;
        if ("profile".equals(fragmentName)) {
            selectedFragment = new ProfileFragment();
            tag = "ProfileFragment";
        } else {
            selectedFragment = new ContentFragment();  // default to ContentFragment
            tag = "ContentFragment";
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, tag).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
        executorService.shutdown();
    }

    public boolean isChannelIdNone() {
        return isChannelIdNone;
    }

    private void fetchChannelName() {
        String uid = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String channel = document.getString("channel");
                    channelId = channel != null ? channel : "none";
                    if ("none".equals(channelId)) {
                        Log.d("channelId", "fetchChannelName: It is none");
                        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
                        fetchYTData = true;
                        if (profileFragment != null) {
                            isChannelIdNone = true; //// karevor mas
                            Log.d("channelId", "fetchChannelName: It's not null");
                            profileFragment.showChannelWarning();


                        } else if (channelWarningListener != null) {
                            channelWarningListener.onChannelWarning();
                        }
                    } else {
                        isChannelIdNone = false; //// karevor mas
                        Future<String> future = executorService.submit(() -> YoutubeAPI.getChannelName(channelId));
                        if (fetchYTData) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                        executorService.submit(() -> {
                            try {
                                final String profileImageUrl = YoutubeAPI.getProfileImageUrl(channelId);
                                final String channelName = future.get();
                                // Adding or updating the fields profileImageUrl and channelName in Firestore
                                Map<String, Object> data = new HashMap<>();
                                data.put("profileImageUrl", profileImageUrl);
                                data.put("channelName", channelName);


                                docRef.set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Firestore", "Document has been saved!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Firestore", "Document was not saved!", e);
                                    }
                                });

                                mainThreadHandler.post(() -> {
                                    if (channelName != null) {
                                        channelNameTextView.setText(channelName);
                                        channelNameLiveData.setValue(channelName); // Set the value of MutableLiveData
                                    } else {
                                        channelNameTextView.setText("Channel not found");
                                    }
                                    if (profileImageUrl != null) {
                                        profileImageUrlLiveData.setValue(profileImageUrl); // Set the value of MutableLiveData
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        //Find Youtube profile image from YoutubeAPI.java and give it to Profile fragment
                    }
                }
            } else {
                Log.d("fail_111", "get failed with ", task.getException());
            }
        });
    }


    public MutableLiveData<String> getChannelNameLiveData() {
        return channelNameLiveData;
    }

    public MutableLiveData<String> getProfileImageUrlLiveData() {
        return profileImageUrlLiveData;
    }

    @Override
    public void onLogoutClicked() {
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
        if (profileFragment != null) {
            profileFragment.handleLogoutClicked();
        }
    }

    @Override
    public void onSwitchAccountClicked() {
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
        if (profileFragment != null) {
            profileFragment.handleSwitchAccountClicked();
        }
    }

    @Override
    public void onUpgradeClicked() {
        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
        if (profileFragment != null) {
            profileFragment.handleUpgradeClicked();
        }
    }

    public interface ChannelWarningListener {
        void onChannelWarning();
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public TextView getStarValue() {
        return starValue;
    }

    public ProgressBar getPg() {
        return pg;
    }


    public String getLastVideoId() {
        return lastVideoId;
    }

    @SuppressLint("LongLogTag")
    public void retrieveLikedVideosPlaylist(String channelId) {
        try {
            YouTube.Channels.List channelsListRequest = youtube.channels().list("contentDetails");
            channelsListRequest.setId(channelId);

            ChannelListResponse channelListResponse = channelsListRequest.execute();
            List<Channel> channels = channelListResponse.getItems();

            if (channels != null && channels.size() > 0) {
                Channel channel = channels.get(0);
                String likedVideosPlaylistId = channel.getContentDetails().getRelatedPlaylists().getLikes();

                YouTube.PlaylistItems.List playlistItemsListRequest = youtube.playlistItems().list("snippet");
                playlistItemsListRequest.setPlaylistId(likedVideosPlaylistId);
                playlistItemsListRequest.setMaxResults(1L); // Change this value if you want to retrieve more videos

                PlaylistItemListResponse playlistItemListResponse = playlistItemsListRequest.execute();
                List<PlaylistItem> playlistItems = playlistItemListResponse.getItems();

                if (playlistItems != null && playlistItems.size() > 0) {
                    PlaylistItem playlistItem = playlistItems.get(0);
                    PlaylistItemSnippet snippet = playlistItem.getSnippet();
                    String videoTitle = snippet.getTitle();
                    String videoId = snippet.getResourceId().getVideoId();

                    lastVideoId = videoId;

                    // Log the video details
                    Log.d("YouTubeLikedVideosPlaylist", "Title: " + videoTitle + ", Video ID: " + videoId);

                } else {
                    Log.d("YouTubeLikedVideosPlaylist", "No videos found in the liked videos playlist.");
                }
            } else {
                Log.d("YouTubeLikedVideosPlaylist", "No channel found.");
            }
        } catch (IOException e) {
            Log.e("YouTubeLikedVideosPlaylist", "Error retrieving liked videos playlist: " + e.getMessage());
        }
    }

    public interface SubscriptionCheckListener {
        void onSubscriptionCheckCompleted(boolean isSubscribed);
    }

    @SuppressLint("StaticFieldLeak")
    public void retrieveSubscriptions(String subscriberId, String subscribedToId, SubscriptionCheckListener listener) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean isSubscribed = false;

                try {
                    YouTube.Subscriptions.List subscriptionsListRequest = youtube.subscriptions().list("snippet");
                    subscriptionsListRequest.setChannelId(subscriberId);
                    SubscriptionListResponse subscriptionListResponse = subscriptionsListRequest.execute();
                    List<Subscription> subscriptions = subscriptionListResponse.getItems();

                    if (subscriptions != null && subscriptions.size() > 0) {
                        for (Subscription subscription : subscriptions) {
                            String channelId = subscription.getSnippet().getResourceId().getChannelId();
                            if (channelId.equals(subscribedToId)) {
                                isSubscribed = true;
                                break;
                            }
                        }
                    } else {
                        Log.d("YouTubeSubscriptions", "No subscriptions found for this user.");
                    }

                    if (isSubscribed) {
                        Log.d("YouTubeSubscriptions", "User is subscribed to the channel.");
                    } else {
                        Log.d("YouTubeSubscriptions", "User is not subscribed to the channel.");
                    }
                } catch (IOException e) {
                    Log.e("YouTubeSubscriptions", "Error retrieving subscriptions: " + e.getMessage());
                }

                return isSubscribed; // Return the result
            }

            @Override
            protected void onPostExecute(Boolean isSubscribed) {
                if (listener != null) {
                    listener.onSubscriptionCheckCompleted(isSubscribed);
                }
            }
        }.execute();
    }
}

