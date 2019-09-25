package com.matchstick.brightlife.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.fragments.FragmentHome;
import com.matchstick.brightlife.fragments.FragmentMessage;
import com.matchstick.brightlife.fragments.FragmentNotification;
import com.matchstick.brightlife.fragments.FragmentProfile;
import com.matchstick.brightlife.fragments.FragmentSale;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
                    break;
                case R.id.nav_sale:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentSale()).commit();
                    break;
                case R.id.nav_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentProfile()).commit();
                    break;
                case R.id.nav_notification:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentNotification()).commit();
                    break;
//                case R.id.nav_messages:
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentMessage()).commit();
//                    break;
            }

            return true;
        }
    };
}
