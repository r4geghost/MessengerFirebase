package com.diusov.messengerfirebase.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordViewModel extends ViewModel {

    private FirebaseAuth auth;

    private MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<String> getError() {
        return error;
    }

    private MutableLiveData<Boolean> success = new MutableLiveData<>();

    public MutableLiveData<Boolean> isSuccess() {
        return success;
    }

    public ResetPasswordViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public void resetPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        success.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });
    }
}
