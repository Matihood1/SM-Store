package com.example.projektsklep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationActivity extends AppCompatActivity {

    public static final String EXTRA_FIRST_NAME = "EXTRA_FIRST_NAME";
    public static final String EXTRA_LAST_NAME = "EXTRA_LAST_NAME";
    public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
    public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";

    private TextInputLayout firstNameLayout;
    private TextInputEditText firstNameEditText;
    private TextInputLayout lastNameLayout;
    private TextInputEditText lastNameEditText;
    private TextInputLayout emailLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;
    private TextInputLayout passwordConfirmLayout;
    private TextInputEditText passwordConfirmEditText;
    private Button registerButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstNameLayout = findViewById(R.id.registration_firstname_layout);
        firstNameEditText = findViewById(R.id.registration_firstname);
        lastNameLayout = findViewById(R.id.registration_lastname_layout);
        lastNameEditText = findViewById(R.id.registration_lastname);
        emailLayout = findViewById(R.id.registration_email_layout);
        emailEditText = findViewById(R.id.registration_email);
        passwordLayout = findViewById(R.id.registration_password_layout);
        passwordEditText = findViewById(R.id.registration_password);
        passwordConfirmLayout = findViewById(R.id.registration_password_confirm_layout);
        passwordConfirmEditText = findViewById(R.id.registration_password_confirm);
        registerButton = findViewById(R.id.register_button);
        cancelButton = findViewById(R.id.registration_cancel_button);

        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(firstNameEditText.getText().toString().length() <= 0) {
                    firstNameLayout.setErrorEnabled(true);
                    firstNameEditText.setError(getString(R.string.required_field));
                }
                else {
                    firstNameLayout.setErrorEnabled(false);
                }
            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(lastNameEditText.getText().toString().length() <= 0) {
                    lastNameLayout.setErrorEnabled(true);
                    lastNameEditText.setError(getString(R.string.required_field));
                }
                else {
                    lastNameLayout.setErrorEnabled(false);
                }
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(emailEditText.getText().toString().length() <= 0) {
                    emailLayout.setErrorEnabled(true);
                    emailEditText.setError(getString(R.string.required_field));
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
                    emailLayout.setErrorEnabled(true);
                    emailEditText.setError(getString(R.string.incorrect_format));
                }
                else {
                    emailLayout.setErrorEnabled(false);
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(passwordEditText.getText().toString().length() < 6) {
                    passwordLayout.setErrorEnabled(true);
                    if(passwordEditText.getText().toString().length() <= 0) {
                        passwordEditText.setError(getString(R.string.required_field));
                    }
                    else {
                        passwordEditText.setError(getString(R.string.password_too_short));
                    }
                }
                else {
                    passwordLayout.setErrorEnabled(false);
                }
            }
        });

        passwordConfirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(emailEditText.getText().toString().length() <= 0) {
                    emailLayout.setErrorEnabled(true);
                    emailEditText.setError(getString(R.string.required_field));
                }
                else if(!passwordConfirmEditText.getText().toString().equals(passwordEditText.getText().toString())) {
                    passwordConfirmLayout.setErrorEnabled(true);
                    passwordConfirmEditText.setError(getString(R.string.passwords_not_matching));
                }
                else {
                    passwordConfirmLayout.setErrorEnabled(false);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if(firstNameLayout.getError() == null && lastNameLayout.getError() == null && emailLayout.getError() == null &&
                passwordLayout.getError() == null && passwordConfirmLayout.getError() == null) {
                    if(TextUtils.isEmpty(firstNameEditText.getText()) || TextUtils.isEmpty(lastNameEditText.getText()) ||
                            TextUtils.isEmpty((emailEditText.getText())) || TextUtils.isEmpty((passwordEditText.getText())) ||
                            TextUtils.isEmpty((passwordConfirmEditText.getText()))) {
                        setResult(RESULT_CANCELED, replyIntent);
                    } else {
                        String firstName = firstNameEditText.getText().toString();
                        replyIntent.putExtra(EXTRA_FIRST_NAME, firstName);
                        String lastName = lastNameEditText.getText().toString();
                        replyIntent.putExtra(EXTRA_LAST_NAME, lastName);
                        String email = emailEditText.getText().toString();
                        replyIntent.putExtra(EXTRA_EMAIL, email);
                        String password = passwordEditText.getText().toString();
                        replyIntent.putExtra(EXTRA_PASSWORD, password);
                        setResult(RESULT_OK, replyIntent);
                    }
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}