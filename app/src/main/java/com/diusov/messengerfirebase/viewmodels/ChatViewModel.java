package com.diusov.messengerfirebase.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.diusov.messengerfirebase.models.Message;
import com.diusov.messengerfirebase.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "ChatViewModel";
    private static final String USERS_TABLE = "Users";
    private static final String MESSAGES_TABLE = "Messages";

    private String currentUserId;
    private String otherUserId;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference referenceUsers = firebaseDatabase.getReference(USERS_TABLE);
    private DatabaseReference referenceMessages = firebaseDatabase.getReference(MESSAGES_TABLE);

    public ChatViewModel(String currentUserId, String otherUserId) {
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;
        // get other user (via child())
        referenceUsers.child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                otherUser.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
            }
        });
        // listening to new messages
        referenceMessages
                .child(currentUserId)
                .child(otherUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Message> messageList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            // add message to our list
                            Message message = dataSnapshot.getValue(Message.class);
                            messageList.add(message);
                        }
                        // set messages to LiveData
                        messages.setValue(messageList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
    }

    private MutableLiveData<List<Message>> messages = new MutableLiveData<>();

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    private MutableLiveData<User> otherUser = new MutableLiveData<>();

    public LiveData<User> getOtherUser() {
        return otherUser;
    }

    private MutableLiveData<Boolean> messageSent = new MutableLiveData<>();

    public LiveData<Boolean> getMessageSent() {
        return messageSent;
    }

    private MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<String> getError() {
        return error;
    }

    public void setUserOnline(boolean isOnline) {
        // set online status to user
        referenceUsers.child(currentUserId).child("online").setValue(isOnline);
    }

    public void sendMessage(Message message) {
        // save message in sender
        referenceMessages
                .child(message.getSenderId())
                .child(message.getReceiverId())
                .push()
                .setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // save message in receiver
                        referenceMessages
                                .child(message.getReceiverId())
                                .child(message.getSenderId())
                                .push()
                                .setValue(message)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // if everything went well, change state to
                                        // "sent" in LiveData object
                                        messageSent.setValue(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                });
    }

}
