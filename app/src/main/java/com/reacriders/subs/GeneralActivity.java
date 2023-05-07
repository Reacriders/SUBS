package com.reacriders.subs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reacriders.subs.databinding.ActivityGeneralBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GeneralActivity extends AppCompatActivity {

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

    public void setChannelWarningListener(ChannelWarningListener channelWarningListener) {
        this.channelWarningListener = channelWarningListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView starValue = findViewById(R.id.star_value);
        ProgressBar pg = findViewById(R.id.Value_loader);
        channelNameTextView = findViewById(R.id.channelName);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        fetchChannelName();

        // If the user is logged in, get the score
        FirestoreHelper.updateScore(currentUser, starValue, pg);

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
        executorService.shutdown();
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
                    String channelId = channel != null ? channel : "none";
                    if ("none".equals(channelId)) {
                        Log.d("channelId", "fetchChannelName: It is none");
                        ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("ProfileFragment");
                        if (profileFragment != null) {
                            Log.d("channelId", "fetchChannelName: It's not null");
                            profileFragment.showChannelWarning();

                        } else if (channelWarningListener != null) {
                            channelWarningListener.onChannelWarning();
                        }
                    } else {
                        Future<String> future = executorService.submit(() -> YoutubeAPI.getChannelName(channelId));
                        executorService.submit(() -> {
                            try {
                                final String channelName = future.get();
                                mainThreadHandler.post(() -> {
                                    if (channelName != null) {
                                        channelNameTextView.setText(channelName);
                                    } else {
                                        channelNameTextView.setText("Channel not found");
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            } else {
                Log.d("fail_111", "get failed with ", task.getException());
            }
        });
    }

    public interface ChannelWarningListener {
        void onChannelWarning();
    }
}

