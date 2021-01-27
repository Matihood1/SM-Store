package com.example.projektsklep;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditUserActivity extends AppCompatActivity {
    private User selectedUser;
    private User currentUser;
    private TextView editUserTextView;
    private TextInputLayout firstNameLayout;
    private TextInputEditText firstNameEditText;
    private TextInputLayout lastNameLayout;
    private TextInputEditText lastNameEditText;
    private TextInputLayout emailLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;
    private CheckBox adminCheckBox;
    private Button saveUserButton;
    private LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        editUserTextView = findViewById(R.id.edit_user_title);
        firstNameLayout = findViewById(R.id.user_firstname_layout);
        firstNameEditText = findViewById(R.id.user_firstname);
        lastNameLayout = findViewById(R.id.user_lastname_layout);
        lastNameEditText = findViewById(R.id.user_lastname);
        emailLayout = findViewById(R.id.user_email_layout);
        emailEditText = findViewById(R.id.user_email);
        passwordLayout = findViewById(R.id.user_password_layout);
        passwordEditText = findViewById(R.id.user_password);
        adminCheckBox = findViewById(R.id.user_admin_checkbox);
        saveUserButton = findViewById(R.id.edit_user_save);

        lightSensor = LoginActivity.lightSensor;

        if (getIntent().hasExtra(LoginActivity.EXTRA_LOGIN_USER)) {
            currentUser = (User) getIntent().getSerializableExtra(LoginActivity.EXTRA_LOGIN_USER);
        }
        if (getIntent().hasExtra(UsersFragment.EXTRA_USER_DATA)) {
            editUserTextView.setText(getString(R.string.edit_user));
            selectedUser = (User) getIntent().getSerializableExtra(UsersFragment.EXTRA_USER_DATA);
            firstNameEditText.setText(selectedUser.getFirstName());
            lastNameEditText.setText(String.valueOf(selectedUser.getLastName()));
            emailEditText.setText(selectedUser.getEmail());
            passwordEditText.setText(selectedUser.getPassword());
            adminCheckBox.setChecked(selectedUser.getAdmin());
        }
        else {
            editUserTextView.setText(getString(R.string.add_user));
        }

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

        adminCheckBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    adminCheckBox.setChecked(!adminCheckBox.isChecked());
                }
            }
        });

        if(selectedUser != null && selectedUser.getEmail().equals(currentUser.getEmail())) {
            adminCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(adminCheckBox.isChecked() != selectedUser.getAdmin()) {
                        adminCheckBox.setError(getString(R.string.changing_admin_for_yourself));
                    }
                    else {
                        adminCheckBox.setError(null);
                    }
                }
            });
        }

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(passwordEditText.getText().toString().length() <= 0) {
                    passwordLayout.setErrorEnabled(true);
                    passwordEditText.setError(getString(R.string.required_field));
                }
                else {
                    passwordLayout.setErrorEnabled(false);
                }
            }
        });

        saveUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if(firstNameLayout.getError() == null && lastNameLayout.getError() == null &&
                        emailLayout.getError() == null && passwordLayout.getError() == null &&
                        adminCheckBox.getError() == null) {
                    if(TextUtils.isEmpty(firstNameEditText.getText()) || TextUtils.isEmpty(lastNameEditText.getText()) ||
                            TextUtils.isEmpty((emailEditText.getText())) || TextUtils.isEmpty((passwordEditText.getText()))) {
                        setResult(RESULT_CANCELED, replyIntent);
                    }
                    else {
                        if (selectedUser != null) {
                            selectedUser.setFirstName(firstNameEditText.getText().toString());
                            selectedUser.setLastName(lastNameEditText.getText().toString());
                            selectedUser.setEmail(emailEditText.getText().toString());
                            selectedUser.setPassword(passwordEditText.getText().toString());
                            selectedUser.setAdmin(adminCheckBox.isChecked());
                        }
                        else {
                            selectedUser = new User(emailEditText.getText().toString(),
                                    passwordEditText.getText().toString(), adminCheckBox.isChecked(),
                                    firstNameEditText.getText().toString(), lastNameEditText.getText().toString());
                        }
                        replyIntent.putExtra(UsersFragment.EXTRA_USER_DATA, selectedUser);
                        setResult(RESULT_OK, replyIntent);
                    }
                    finish();
                }
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
}