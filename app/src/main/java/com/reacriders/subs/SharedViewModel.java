package com.reacriders.subs;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    // Create MutableLiveData for each method you want to call
    public MutableLiveData<Void> triggerUpdateP = new MutableLiveData<>();
    public MutableLiveData<Void> triggerUpdateCH = new MutableLiveData<>();

    // Call these methods from FragmentA when you want to call updateP() and updateCH()
    public void callUpdateP() {
        triggerUpdateP.setValue(null);
    }

    public void callUpdateCH() {
        triggerUpdateCH.setValue(null);
    }
}