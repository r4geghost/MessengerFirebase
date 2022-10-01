package com.diusov.messengerfirebase.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.diusov.messengerfirebase.R;
import com.diusov.messengerfirebase.adapters.MessagesAdapter;
import com.diusov.messengerfirebase.models.Message;
import com.diusov.messengerfirebase.models.User;
import com.diusov.messengerfirebase.viewmodels.ChatViewModel;
import com.diusov.messengerfirebase.viewmodels.ChatViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private final static String EXTRA_CURRENT_USER_ID = "current_user_id";
    private final static String EXTRA_OTHER_USER_ID = "other_user_id";

    private TextView textViewTitle;
    private View onlineStatus;
    private EditText editTextMessage;
    private ImageView imageViewSendMessage;

    private RecyclerView recyclerViewMessages;
    private MessagesAdapter messagesAdapter;

    private ChatViewModel viewModel;
    private ChatViewModelFactory viewModelFactory;

    private String currentUserId;
    private String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();

        // getting current user id and user with who we are chatting id
        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        otherUserId = getIntent().getStringExtra(EXTRA_OTHER_USER_ID);

        // init adapter with current user id
        messagesAdapter = new MessagesAdapter(currentUserId);
        recyclerViewMessages.setAdapter(messagesAdapter);

        // init ChatViewModelFactory with current user id and other user id
        viewModelFactory = new ChatViewModelFactory(currentUserId, otherUserId);
        // init ChatViewModel with viewModelFactory
        viewModel = new ViewModelProvider(this, viewModelFactory).get(ChatViewModel.class);

        observeViewModel();
        setUpClickListeners();
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.textViewTitle);
        onlineStatus = findViewById(R.id.onlineStatus);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
    }

    private void setUpClickListeners() {
        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get message from EditText
                Message message = new Message(
                        editTextMessage.getText().toString().trim(),
                        currentUserId,
                        otherUserId
                );
                // send message via view model
                viewModel.sendMessage(message);
            }
        });
    }


    private void observeViewModel() {
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                messagesAdapter.setMessages(messages);
            }
        });
        viewModel.getMessageSent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean messageIsSent) {
                if (messageIsSent) {
                    // if message is sent, clear EditText
                    editTextMessage.setText("");
                }
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getOtherUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // set user name and last name to label
                String userInfo = String.format("%s %s", user.getName(), user.getLastName());
                textViewTitle.setText(userInfo);
                // save background resource id
                int bgResId;
                if (user.getOnline()) {
                    bgResId = R.drawable.circle_green;
                } else {
                    bgResId = R.drawable.circle_red;
                }
                // make drawable from our resource id
                Drawable onlineStatusBg = ContextCompat.getDrawable(ChatActivity.this, bgResId);
                // set background to view
                onlineStatus.setBackground(onlineStatusBg);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if user closes app, set his status to "online"
        viewModel.setUserOnline(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if user opens app, set his status to "online"
        viewModel.setUserOnline(true);
    }

    public static Intent newIntent(Context context, String currentUserId, String otherUserId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId);
        return intent;
    }
}