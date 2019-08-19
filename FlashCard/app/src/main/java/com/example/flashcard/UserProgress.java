package com.example.flashcard;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.flashcard.fragments.MyDecksFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class UserProgress extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_progress);
        // use toolbar as appbarLayout
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar();
        //loading the default fragment
        loadFragment(new UserProgressFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(this);

        Fragment fragment = new UserProgressFragment();
        loadFragment(fragment);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()) {
            case R.id.navigation_progress:
                fragment = new UserProgressFragment();
                setTitle("Progress");
                break;
            case R.id.navigation_account_info:
                fragment = new AccountInfoFragment();
                setTitle("Account information");
                break;
            case R.id.navigation_my_decks:
                fragment = new MyDecksFragment();
                setTitle("My Decks");
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
