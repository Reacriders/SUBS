package com.reacriders.subs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class GeneralActivity extends AppCompatActivity {

    // Firebase Firestore instance
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // TextView for displaying score
    private TextView starValue;

    private BottomNavigationView bottomNavigationView;

    private ProgressBar pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        // Initialization
        starValue = findViewById(R.id.star_value);
        pg = findViewById(R.id.Value_loader);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // If the user is logged in, get the score
        FirestoreHelper.updateScore(currentUser, starValue, pg);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_tasks:
                        selectedFragment = new ContentFragment();
                        break;
                    case R.id.navigation_account:
                        selectedFragment = new ProfileFragment();
                        break;
                    case R.id.navigation_notifications:
                        selectedFragment = new NotificationsFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                return true;
            }
        });

        // Check which fragment to display
        String fragmentName = getIntent().getStringExtra("fragment");
        Fragment selectedFragment;
        if ("profile".equals(fragmentName)) {
            selectedFragment = new ProfileFragment();
        } else {
            selectedFragment = new ContentFragment();  // default to ContentFragment
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }
}

