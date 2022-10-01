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
import com.diusov.messengerfirebase.viewmodels.ResetPasswordViewModel;

public class ResetPasswordActivity extends AppCompatActivity {

    private static final String EXTRA_EMAIL = "email";

    private ResetPasswordViewModel viewModel;

    private EditText editTextEmail;
    private Button buttonResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initViews();
        setUpViews();
        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        observeViewModel();
        setUpClickListeners();
    }

    private void observeViewModel() {
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    Toast.makeText(
                            ResetPasswordActivity.this,
                            error,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
        viewModel.isSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (isSuccess) {
                    Toast.makeText(
                            ResetPasswordActivity.this,
                            R.string.reset_link_success,
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
    }

    private void setUpClickListeners() {
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                viewModel.resetPassword(email);
            }
        });
    }

    private void setUpViews() {
        // get email from login activity
        String email = getIntent().getStringExtra(EXTRA_EMAIL);
        // set email to EditText
        editTextEmail.setText(email);
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
    }

    public static Intent newIntent(Context context, String email) {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        intent.putExtra(EXTRA_EMAIL, email);
        return intent;
    }
}