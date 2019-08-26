package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.flashcard.adapters.SectionsPagerAdapterForResultPage;
import com.google.android.material.tabs.TabLayout;

public class ShowTestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test_result);

        SectionsPagerAdapterForResultPage sectionsPagerAdapterForResultPage =
                new SectionsPagerAdapterForResultPage(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager_result);
        viewPager.setAdapter(sectionsPagerAdapterForResultPage);
        TabLayout tabs = findViewById(R.id.tabsQuizResult);
        tabs.setupWithViewPager(viewPager);
    }
}
