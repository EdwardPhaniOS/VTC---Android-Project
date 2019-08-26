package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.flashcard.fragments.MyDecksFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserProgress extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private int tabIndex;

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView navigation = findViewById(R.id.nav_view);

        int tabIndex = navigation.getSelectedItemId();
        loadDataOfCurrentTab(tabIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();

        BottomNavigationView navigation = findViewById(R.id.nav_view);

        int tabIndex = navigation.getSelectedItemId();
        Log.i("tabIndex", String.valueOf(tabIndex));
        loadDataOfCurrentTab(tabIndex);
    }

    private void loadDefaultTab()
    {
        if (user == null) {
            Intent intent = new Intent(UserProgress.this, MainActivity.class);
            startActivity(intent);
        } else {
            //loading the default fragment
            Fragment userProgressFragment = new UserProgressFragment();
            loadFragment(userProgressFragment);
        }
    }

    private void loadDataOfCurrentTab(int Index)
    {
        if (Index == 2131362083) //2131362083 la index tuong ung voi tab MyDecksFragment
        {
            Fragment myDeckFragment = new MyDecksFragment();
            loadFragment(myDeckFragment);
        } else {
            loadDefaultTab();
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

        int tabIndex = navigation.getSelectedItemId();
        Log.i("tabIndex", String.valueOf(tabIndex));

        loadDataOfCurrentTab(tabIndex);
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
