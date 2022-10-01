package com.diusov.messengerfirebase.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.diusov.messengerfirebase.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationViewModel extends ViewModel {

    private static final String USERS_TABLE = "Users";

    private FirebaseAuth auth;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersReference;

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

    public RegistrationViewModel() {
        auth = FirebaseAuth.getInstance();
        // check if user is already logged in
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // if current user exists, add current user to Livedata
                if (firebaseAuth.getCurrentUser() != null) {
                    user.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference(USERS_TABLE);
    }

    public void signUp(
            String email,
            String password,
            String name,
            String lastName,
            int age
    ) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // get user id created by Firebase
                        FirebaseUser firebaseUser = authResult.getUser();
                        if (firebaseUser == null) {
                            return;
                        }
                        // create new User
                        User user = new User(
                                firebaseUser.getUid(),
                                name,
                                lastName,
                                age,
                                false
                        );
                        // add new User to database
                        // use child() to set our id to id in database table entry
                        // because we want "user id" match its table entry id
                        usersReference.child(user.getId()).setValue(user);
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
