package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.flashcard.fragments.AccountInfoFragment;
import com.example.flashcard.fragments.DownloadCardsFragment;
import com.example.flashcard.fragments.MyDecksFragment;
import com.example.flashcard.fragments.RemindersFragment;
import com.example.flashcard.fragments.UserProgressFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class UserProgress extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadDefaultTab()
    {
        if (user == null) {
            Intent intent = new Intent(UserProgress.this, MainActivity.class);
            startActivity(intent);
        } else {
            BottomNavigationView navigation = findViewById(R.id.nav_view);

            navigation.getMenu().getItem(0).setChecked(true);

            onNavigationItemSelected(navigation.getMenu().getItem(0));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_progress);

        // use toolbar as appbarLayout
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar();

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(this);

        loadDefaultTab();

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
            case R.id.navigation_reminder:
                fragment = new RemindersFragment();
                setTitle("Reminders");
                break;
            case R.id.navigation_download:
                fragment = new DownloadCardsFragment();
                setTitle("Downloads");
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
