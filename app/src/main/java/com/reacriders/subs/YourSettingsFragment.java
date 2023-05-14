package com.reacriders.subs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.reacriders.subs.ProfileFragment;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_settings_fragment, container, false);

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
            saveSwitchState(isChecked);
            setThumbTintColor(switch1, isChecked);

            if (isChecked) {
                switchOn.setVisibility(View.VISIBLE);
                switchOff.setVisibility(View.GONE);
                switchText1.setTextColor(Color.rgb(33,150, 243));
            } else {
                switchOn.setVisibility(View.GONE);
                switchOff.setVisibility(View.VISIBLE);
                switchText1.setTextColor(Color.GRAY);
            }
        });
        boolean switchState = getSwitchState();
        switch1.setChecked(switchState);
        setThumbTintColor(switch1, switchState);

        if (switchState) {
            switchOn.setVisibility(View.VISIBLE);
            switchOff.setVisibility(View.GONE);
            switchText1.setTextColor(Color.rgb(33,150, 243));
        } else {
            switchOn.setVisibility(View.GONE);
            switchOff.setVisibility(View.VISIBLE);
            switchText1.setTextColor(Color.GRAY);
        }



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
        return view;
    }



    //switchi Hishox
    private void saveSwitchState(boolean isChecked) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("switch1_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("state", isChecked);
        editor.apply();
    }

    private boolean getSwitchState() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("switch1_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("state", false);
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

