package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.adapters.DeckList;
import com.example.flashcard.fragments.MyDecksFragment;
import com.example.flashcard.fragments.TrainingFragment;
import com.example.flashcard.fragments.TrainingSettingFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeckDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean statusSwitchOfSettingFragment = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);
        // use offline
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //
        Intent intent = getIntent();

        setTitle(intent.getStringExtra(ConstantVariable.DECK_NAME));

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        checkStatusSwitchButton();  // quan trọng - liên kết với onDataChange
    }

    private void setupAfterGetData(){
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout)findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager); // dòng này quan trọng kết nối với viewpager
    }

    private void checkStatusSwitchButton(){
        String userId = getIntent().getStringExtra(ConstantVariable.USER_ID);
        String deckName = getIntent().getStringExtra(ConstantVariable.DECK_NAME);
        String deckId = getIntent().getStringExtra(ConstantVariable.DECK_ID);
        FirebaseDatabase.getInstance().getReference("DBFlashCard").child("reminders")
                .child(userId).child(deckId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    statusSwitchOfSettingFragment = false;
                    setupAfterGetData();
                }
                else {
                    statusSwitchOfSettingFragment = true;
                    setupAfterGetData();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        // listen for button Test
        TrainingFragment.OnButtonTestClickListener listener = new TrainingFragment.OnButtonTestClickListener() {
            @Override
            public void OnButtonTestClick() {
                // transition
                //finish();
                overridePendingTransition(R.anim.slide_in_up,R.anim.slide_out_up);
            }
        };
        //
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TrainingFragment(listener), "TRAINING");
        adapter.addFragment(new TrainingSettingFragment(statusSwitchOfSettingFragment), "SETTING");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
