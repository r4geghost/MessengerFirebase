package com.diusov.messengerfirebase.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private FirebaseAuth auth;

    // store error message
    private MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<String> getError() {
        return error;
    }

    // store Firebase user
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();

    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();
        // check if user is already logged in
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    user.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });
    }

    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // auth listener handles all changes
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // save error message to LiveData
                        error.setValue(e.getMessage());
                    }
                });
    }
}
