package com.reacriders.subs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.reacriders.subs.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

public class YourSettingsFragment extends Fragment {


    public interface OnYourSettingsFragmentInteractionListener {
        void onLogoutClicked();
        void onSwitchAccountClicked();
        void onUpgradeClicked();
    }

    private OnYourSettingsFragmentInteractionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnYourSettingsFragmentInteractionListener) {
            mListener = (OnYourSettingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnYourSettingsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String email, uid, channelId;
    private final String TAG = "Settings";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_settings_fragment, container, false);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get user email and UID
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();
            Log.d(TAG, "onCreateView: "+ uid + "\n"+ email);
        }else{
            email = "null";
            uid = "null";
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);





        Switch switch1 = view.findViewById(R.id.switch1);
        TextView switchOn = view.findViewById(R.id.switch_on);
        TextView switchOff = view.findViewById(R.id.switch_off);
        TextView switchText1 = view.findViewById(R.id.text_switch1);

        if (switch1.isChecked()) {
            switchOn.setVisibility(View.VISIBLE);
            switchOff.setVisibility(View.GONE);
            switchText1.setTextColor(Color.rgb(33,150, 243));
        } else {
            switchOn.setVisibility(View.GONE);
            switchOff.setVisibility(View.VISIBLE);
            switchText1.setTextColor(Color.GRAY);
        }
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setThumbTintColor(switch1, isChecked);

            if (isChecked) {
                switchOn.setVisibility(View.VISIBLE);
                switchOff.setVisibility(View.GONE);
                switchText1.setTextColor(Color.rgb(33,150, 243));

                // Write to Firestore when the switch is turned on
                Map<String, Object> data = new HashMap<>();
                data.put("account", email);
                docRef.set(data, SetOptions.merge());
            } else {
                switchOn.setVisibility(View.GONE);
                switchOff.setVisibility(View.VISIBLE);
                switchText1.setTextColor(Color.GRAY);

                // Delete from Firestore when the switch is turned off
                Map<String, Object> updates = new HashMap<>();
                updates.put("account", FieldValue.delete());
                docRef.update(updates);
            }
        });




        Button logoutBtn = view.findViewById(R.id.logoutBtn);
        Button switchAccountBtn = view.findViewById(R.id.switch_account_btn_2);
        Button upgradeBtn = view.findViewById(R.id.upgrade_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onLogoutClicked();
                }
            }
        });

        switchAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSwitchAccountClicked();
                }
            }
        });

        upgradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onUpgradeClicked();
                }
            }
        });

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(document.getString("account") != null) {
                            switch1.setChecked(true);
                        } else {
                            switch1.setChecked(false);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return view;
    }


    //on off color
    private void setThumbTintColor(Switch switchView, boolean isChecked) {
        int colorOn = ContextCompat.getColor(getActivity(), R.color.blue);
        int colorOff = ContextCompat.getColor(getActivity(), R.color.gray);
        int colorToUse = isChecked ? colorOn : colorOff;

        ColorStateList thumbTintList = ColorStateList.valueOf(colorToUse);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switchView.setThumbTintList(thumbTintList);
        }
    }

}

