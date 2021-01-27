package com.example.projektsklep;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FrameLayout fragmentFrameLayout;
    protected StoreViewModel storeViewModel;
    protected User currentUser;
    protected LightSensor lightSensor;
    private Fragment currentFragment;
    private static final String ACCURACY_TAG = "AccuracyChange";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.topAppBar));

        topAppBar = findViewById(R.id.topAppBar);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        storeViewModel = new ViewModelProvider(this).get(StoreViewModel.class);

        lightSensor = LoginActivity.lightSensor;

        if (getIntent().hasExtra(LoginActivity.EXTRA_LOGIN_USER)) {
            currentUser = (User)getIntent().getSerializableExtra(LoginActivity.EXTRA_LOGIN_USER);
        }

        View headerView = navigationView.getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.header_user_name)).setText(currentUser.getFirstName() +
                " " + currentUser.getLastName());
        ((TextView) headerView.findViewById(R.id.header_user_email)).setText(currentUser.getEmail());

        if(currentUser.getAdmin() != true) {
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.item_users);
            menuItem.setVisible(false);
        }

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        try {
            currentFragment = (Fragment) ProductsFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_framelayout, currentFragment).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Class fragmentClass;
                switch(item.getItemId()) {
                    case R.id.item_products:
                        fragmentClass = ProductsFragment.class;
                        break;
                    case R.id.item_users:
                        fragmentClass = UsersFragment.class;
                        break;
                    case R.id.item_logout:
                        fragmentClass = null;
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        fragmentClass = ProductsFragment.class;
                }

                if(fragmentClass != null) {
                    try {
                        currentFragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_framelayout, currentFragment).commit();
                }

                item.setChecked(true);
                //setTitle(item.getTitle());
                drawerLayout.close();
                return true;
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

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getIntent().hasExtra(LoginActivity.EXTRA_LOGIN_USER)) {
            currentUser = (User) getIntent().getSerializableExtra(LoginActivity.EXTRA_LOGIN_USER);
        }
    }*/
}