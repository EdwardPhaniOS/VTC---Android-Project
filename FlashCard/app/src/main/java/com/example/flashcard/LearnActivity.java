package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.flashcard.adapters.FlashcardsFragmentAdapter;

public class LearnActivity extends AppCompatActivity {
    private ViewPager viewPagerLearn;
    private FlashcardsFragmentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        viewPagerLearn = findViewById(R.id.pagerLearnAcivity);
        mAdapter = new FlashcardsFragmentAdapter(getSupportFragmentManager());
        viewPagerLearn.setAdapter(mAdapter);
    }
}
