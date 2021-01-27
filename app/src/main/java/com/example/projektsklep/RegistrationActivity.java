package com.example.projektsklep;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationActivity extends AppCompatActivity {

    public static final String EXTRA_FIRST_NAME = "EXTRA_FIRST_NAME";
    public static final String EXTRA_LAST_NAME = "EXTRA_LAST_NAME";
    public static final String EXTRA_EMAIL = "EXTRA_EMAIL";
    public static final String EXTRA_PASSWORD = "EXTRA_PASSWORD";

    /*public static final String KEY_USER_FIRSTNAME_ERROR = "KEY_USER_FIRSTNAME_ERROR";
    public static final String KEY_USER_LASTNAME_ERROR = "KEY_USER_LASTNAME_ERROR";
    public static final String KEY_USER_EMAIL_ERROR = "KEY_USER_EMAIL_ERROR";
    public static final String KEY_USER_PASSWORD_ERROR = "KEY_USER_PASSWORD_ERROR";
    public static final String KEY_USER_PASSWORD_CONFIRM_ERROR = "KEY_USER_PASSWORD_CONFIRM_ERROR";
    public static final String KEY_USER_ADMIN_ERROR = "KEY_USER_ADMIN_ERROR";*/

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
    private LightSensor lightSensor;

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

        lightSensor = LoginActivity.lightSensor;

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
                if(passwordConfirmEditText.getText().toString().length() <= 0) {
                    passwordConfirmLayout.setErrorEnabled(true);
                    passwordConfirmEditText.setError(getString(R.string.required_field));
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

    @Override
    protected void onStart() {
        super.onStart();
        if(lightSensor != null) {
            lightSensor.onStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(lightSensor != null) {
            lightSensor.onPause();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            firstNameEditText.setText(savedInstanceState.getString(LoginActivity.KEY_USER_FIRSTNAME));
            lastNameEditText.setText(savedInstanceState.getString(LoginActivity.KEY_USER_LASTNAME));
            emailEditText.setText(savedInstanceState.getString(LoginActivity.KEY_USER_EMAIL));
            passwordEditText.setText(savedInstanceState.getString(LoginActivity.KEY_USER_PASSWORD));
            passwordConfirmEditText.setText(savedInstanceState.getString(LoginActivity.KEY_USER_PASSWORD_CONFIRM));
            /*firstNameLayout.setErrorEnabled(savedInstanceState.getBoolean(KEY_USER_FIRSTNAME_ERROR));
            lastNameLayout.setErrorEnabled(savedInstanceState.getBoolean(KEY_USER_LASTNAME_ERROR));
            emailLayout.setErrorEnabled(savedInstanceState.getBoolean(KEY_USER_EMAIL_ERROR));
            passwordLayout.setErrorEnabled(savedInstanceState.getBoolean(KEY_USER_PASSWORD_ERROR));
            passwordConfirmLayout.setErrorEnabled(savedInstanceState.getBoolean(KEY_USER_PASSWORD_CONFIRM_ERROR));*/
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString(LoginActivity.KEY_USER_FIRSTNAME, firstNameEditText.getText().toString());
        outState.putString(LoginActivity.KEY_USER_LASTNAME, lastNameEditText.getText().toString());
        outState.putString(LoginActivity.KEY_USER_EMAIL, emailEditText.getText().toString());
        outState.putString(LoginActivity.KEY_USER_PASSWORD, passwordEditText.getText().toString());
        outState.putString(LoginActivity.KEY_USER_PASSWORD_CONFIRM, passwordConfirmEditText.getText().toString());
        /*outState.putBoolean(KEY_USER_FIRSTNAME_ERROR, firstNameLayout.isErrorEnabled());
        outState.putBoolean(KEY_USER_LASTNAME_ERROR, lastNameLayout.isErrorEnabled());
        outState.putBoolean(KEY_USER_EMAIL_ERROR, emailLayout.isErrorEnabled());
        outState.putBoolean(KEY_USER_PASSWORD_ERROR, passwordLayout.isErrorEnabled());
        outState.putBoolean(KEY_USER_PASSWORD_CONFIRM_ERROR, passwordConfirmLayout.isErrorEnabled());*/

        super.onSaveInstanceState(outState);
    }
}