package com.example.projektsklep;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_LOGIN_USER = "LOGIN_USER";
    public static final String EXTRA_LIGHT_SENSOR = "LIGHT_SENSOR";
    public static final int REGISTER_USER_ACTIVITY_REQUEST_CODE = 1;

    private TextInputLayout emailLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordLayout;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private StoreViewModel storeViewModel;
    public static LightSensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLayout = findViewById(R.id.login_email_layout);
        emailEditText = findViewById(R.id.login_email);
        passwordLayout = findViewById(R.id.login_password_layout);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.login_register_button);

        lightSensor = new LightSensor((SensorManager) getSystemService(Context.SENSOR_SERVICE));

        storeViewModel = new ViewModelProvider(this).get(StoreViewModel.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = storeViewModel.findUserByCredentials(emailEditText.getText().toString(), passwordEditText.getText().toString());
                if(user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_LOGIN_USER, user);
                    startActivity(intent);
                    finish();
                }
                else {
                    Snackbar.make(findViewById(R.id.login_linear_layout), getString(R.string.user_not_found), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivityForResult(intent, REGISTER_USER_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REGISTER_USER_ACTIVITY_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                User user = new User(data.getStringExtra(RegistrationActivity.EXTRA_EMAIL), data.getStringExtra(RegistrationActivity.EXTRA_PASSWORD),
                        false, data.getStringExtra(RegistrationActivity.EXTRA_FIRST_NAME), data.getStringExtra(RegistrationActivity.EXTRA_LAST_NAME));
                storeViewModel.insert(user);
                Snackbar.make(findViewById(R.id.login_linear_layout), R.string.user_created, Snackbar.LENGTH_LONG).show();
            }
        }
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