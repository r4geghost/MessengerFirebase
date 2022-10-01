package com.diusov.messengerfirebase.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.diusov.messengerfirebase.R;
import com.diusov.messengerfirebase.adapters.UsersAdapter;
import com.diusov.messengerfirebase.models.User;
import com.diusov.messengerfirebase.viewmodels.UsersViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private static final String TAG = "UsersActivity";
    private final static String EXTRA_CURRENT_USER_ID = "current_user_id";


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference = firebaseDatabase.getReference("Messages");

    private UsersViewModel viewModel;

    private RecyclerView recyclerViewUsers;
    private UsersAdapter usersAdapter;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        initViews();
        // get current user id
        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        observeViewModel();
        setUpClickListeners();
    }

    private void initViews() {
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        usersAdapter = new UsersAdapter();
        recyclerViewUsers.setAdapter(usersAdapter);
    }

    private void observeViewModel() {
        viewModel.getFirebaseUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                // if user is null, go back to login screen
                if (firebaseUser == null) {
                    Intent intent = LoginActivity.newIntent(UsersActivity.this);
                    startActivity(intent);
                    // close this screen
                    finish();
                }
            }
        });
        viewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                usersAdapter.setUsers(users);
            }
        });
    }

    private void setUpClickListeners() {
        usersAdapter.setOnUserClickListener(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User otherUser) {
                Intent intent = ChatActivity.newIntent(
                        UsersActivity.this,
                        currentUserId,
                        otherUser.getId()
                );
                startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_sign_out) {
            // log out
            viewModel.signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    public static Intent newIntent(Context context, String currentUserId) {
        Intent intent = new Intent(context, UsersActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        return intent;
    }
}