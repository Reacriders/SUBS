package com.reacriders.subs;
import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reacriders.subs.databinding.ActivityMainBinding;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;
    private TextView checkText;
    private com.google.android.gms.common.SignInButton sgnBtn;


    private ImageView mode;
    private ImageView cloud;

    private ProgressBar loader;
    private ProgressBar loader1;

    private ActivityMainBinding binding;

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private static final String TAG1 = "GOOGLE_SIGN_IN_TAG";

    private static final int REQUEST_AUTHORIZATION = 101;
    private GoogleAccountCredential googleAccountCredential;
    private YouTube youtube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());


        setContentView(binding.getRoot());
        checkText = findViewById(R.id.checking_text);
        loader = findViewById(R.id.loader);
        loader1   = findViewById(R.id.loader1);
        mode = findViewById(R.id.sun_image);
        cloud = findViewById(R.id.cloud_image);

        boolean isNightModeEnabled = isNightModeEnabled(this);

        if (isNightModeEnabled) {
            mode.setImageResource(R.drawable.nightmode);
            cloud.setImageResource(R.drawable.cloud);
        } else {
            mode.setImageResource(R.drawable.lightmode);
            cloud.setImageResource(0);
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken( getString(R.string.default_web_client_id)) // animast error
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);


        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG1, "onClick: begain google sign in");
                Intent intent = googleSignInClient.getSignInIntent();
                loader1.setVisibility(View.VISIBLE);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });


        sgnBtn = findViewById(R.id.googleSignInBtn);




        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            checkText.setText("The device is connected to the Internet.");
        } else {
            checkText.setText("Checking internet connection!");
            loader.setVisibility(View.VISIBLE);
            loader1.setVisibility(View.GONE);
        }

        // Initialize network callback
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d(TAG, "Network available");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkText.setText("Sign in with Google");
                        sgnBtn.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.d(TAG, "Network lost");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkText.setText("Checking internet connection!");
                        sgnBtn.setVisibility(View.GONE);
                        loader.setVisibility(View.VISIBLE);
                        loader1.setVisibility(View.GONE);
                    }
                });
            }
        };

        // Register network callback
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);


    }

    private void checkUser() {
        // ete arten sign in a exe
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            Log.d(TAG1, "checkUser: Already logged in");
            Intent intent = new Intent(this, GeneralActivity.class);
            intent.putExtra("fragment", "profile");
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //google sign in Success
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);

                googleAccountCredential = GoogleAccountCredential.usingOAuth2(
                        this, Collections.singleton("https://www.googleapis.com/auth/youtube.readonly"));
                googleAccountCredential.setSelectedAccountName(account.getEmail());
                youtube = new YouTube.Builder(
                        new NetHttpTransport(),
                        new GsonFactory(),
                        googleAccountCredential)
                        .setApplicationName("YourAppName")
                        .build();

            } catch (Exception e) {
                //failed google sign in
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        }

        if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == Activity.RESULT_OK) {
                fetchYoutubeChannelId();
            } else {
                Log.d("IDK Error", "onActivityResult: error 'I don't know'");
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG1, "firebaseAuthWithGoogleAccount: begin firebase auth with google acc");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG1, "onSuccess: Logged In");

                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();

                        Log.d(TAG1, "onSuccess:EMAIL " + email);
                        Log.d(TAG, "onSuccess:UID " + uid);

                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            Log.d(TAG, "onSuccess: Account Created...\n" + email);
                            Toast.makeText(MainActivity.this, "Account Created...\n" + email, Toast.LENGTH_SHORT).show();
                            fetchYoutubeChannelId();

                            // Create a new user with a score of 0
                            Map<String, Object> user = new HashMap<>();
                            user.put("score", 0);

                            // Add a new document with a UID
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users")
                                    .document(uid)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });

                            // Check if the document with the user's email exists in the FriendRequests collection
                            db.collection("FriendRequests")
                                    .document(email)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                // Retrieve and log the "inviter" field value
                                                String inviter = documentSnapshot.getString("inviter");
                                                Log.d("Inviter", "Inviter: " + inviter);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error getting document", e);
                                        }
                                    });

                        } else {
                            Log.d(TAG, "onSuccess: Logged In...\n" + email);
                            Toast.makeText(MainActivity.this, "Logged In...\n" + email, Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(MainActivity.this, GeneralActivity.class);
                        intent.putExtra("fragment", "profile");
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG1, "onFailure: Loggin Failed " + e.getMessage());
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister network callback
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }
    private boolean isNightModeEnabled(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        return uiModeManager != null && uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES;
    }
    private void fetchYoutubeChannelId() {
        new Thread(() -> {
            try {
                YouTube.Channels.List channelsList = youtube.channels().list("id");
                channelsList.setMine(true);
                ChannelListResponse response = channelsList.execute();
                String channelId = response.getItems().get(0).getId();

                Log.d("Youtube_problem_111", "Channel ID: " + channelId);

                // Save channelId to Firestore
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Update the "channel" field in the document with the UID
                    db.collection("Users")
                            .document(uid)
                            .update("channel", channelId)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated with channel ID!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document with channel ID", e);
                                }
                            });
                }else{
                    Log.d("Youtube_problem_111", "fetchYoutubeChannelId: firebaseUser = null ");
                }

            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (Exception e) {
                Log.e("Youtube_problem_111", "Error fetching channel ID", e);
            }
        }).start();
    }
}

