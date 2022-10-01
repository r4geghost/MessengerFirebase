package com.diusov.messengerfirebase.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.diusov.messengerfirebase.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {

    private static final String USERS_TABLE = "Users";
    private static final String TAG = "UsersViewModel";

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersReference;

    // store Firebase user
    private MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();

    public LiveData<FirebaseUser> getFirebaseUser() {
        return firebaseUser;
    }

    // store users from database
    private MutableLiveData<List<User>> users = new MutableLiveData<>();

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public UsersViewModel() {
        auth = FirebaseAuth.getInstance();
        // check if user is already logged in
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // add current firebase user to Livedata
                firebaseUser.setValue(firebaseAuth.getCurrentUser());
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference(USERS_TABLE);
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get current user
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    return;
                }
                // create list to store users from database
                List<User> usersFromDb = new ArrayList<>();
                // use "for each" cycle to read all table entries
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        return;
                    }
                    // if user is not current user, add new user to LiveData
                    if (!user.getId().equals(currentUser.getUid())) {
                        usersFromDb.add(user);
                    }
                }
                // add users to LiveData
                users.setValue(usersFromDb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // logging error
                Log.d(TAG, error.getMessage());
            }
        });
    }

    public void setUserOnline(boolean isOnline) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return;
        }
        // set online status to user
        usersReference.child(user.getUid()).child("online").setValue(isOnline);
    }

    public void signOut() {
        // before sign out change online status to "offline"
        setUserOnline(false);
        // sign out from app
        auth.signOut();
    }
}
