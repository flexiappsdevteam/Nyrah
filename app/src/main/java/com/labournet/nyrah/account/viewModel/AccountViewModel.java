package com.labournet.nyrah.account.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccountViewModel extends ViewModel {
    MutableLiveData<Integer> apiStatus = new MutableLiveData<>();

    public AccountViewModel() {
    }

    public MutableLiveData<Integer> getApiStatus() {
        apiStatus.setValue(1);
        return apiStatus;
    }
}
