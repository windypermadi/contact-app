package com.windypermadi.yestodopersonal.ui.grup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GrupViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GrupViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}