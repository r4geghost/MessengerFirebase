package com.diusov.messengerfirebase.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diusov.messengerfirebase.R;
import com.diusov.messengerfirebase.viewmodels.RegistrationViewModel;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    private RegistrationViewModel viewModel;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextAge;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();
        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        observeViewModel();
        setUpClickListeners();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }

    private void observeViewModel() {
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                // launch users activity + provide current user id
                if (firebaseUser != null) {
                    Intent intent = UsersActivity.newIntent(
                            RegistrationActivity.this,
                            firebaseUser.getUid()
                    );
                    startActivity(intent);
                    finish();
                }
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    Toast.makeText(
                            RegistrationActivity.this,
                            error, Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private void setUpClickListeners() {
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = getTrimmedValue(editTextEmail);
                String password = getTrimmedValue(editTextPassword);
                String name = getTrimmedValue(editTextName);
                String lastName = getTrimmedValue(editTextLastName);
                int age = Integer.parseInt(getTrimmedValue(editTextAge));
                viewModel.signUp(email, password, name, lastName, age);
            }
        });
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextAge = findViewById(R.id.editTextAge);
        buttonSignUp = findViewById(R.id.buttonSignUp);
    }

    // helper method to get info from EditText
    private String getTrimmedValue(EditText editText) {
        return editText.getText().toString().trim();
    }
}