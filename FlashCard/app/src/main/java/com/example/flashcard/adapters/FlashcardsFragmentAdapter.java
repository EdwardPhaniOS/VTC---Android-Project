package com.example.flashcard.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.flashcard.fragments.FlashcardLearnFragment;

public class FlashcardsFragmentAdapter extends FragmentStatePagerAdapter {

    public FlashcardsFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        FlashcardLearnFragment cardFragment = new FlashcardLearnFragment();
        Bundle bundle = new Bundle();
        position = position + 1;

        bundle.putString("message", "Hello from page: " + position);
        cardFragment.setArguments(bundle);

        return cardFragment;
    }

    @Override
    public int getCount() {
        return 10;
    }
}
