package com.example.flashcard.adapters;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.flashcard.fragments.FlashcardLearnFragment;
import com.example.flashcard.models.Card;

import java.util.List;

public class FlashcardsFragmentAdapter extends FragmentStatePagerAdapter {
    public final static String TAG = "CheckFlowAsync";
    public List<Card> cards;
    public FlashcardsFragmentAdapter(FragmentManager fm,List<Card> cards){
        super(fm);
        this.cards = cards;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem");
        FlashcardLearnFragment cardFragment = new FlashcardLearnFragment();
        Bundle bundle = new Bundle();


        bundle.putString("textVocabulary", cards.get(position).getVocabulary());
        bundle.putString("textDefinition", cards.get(position).getDefinition());
        bundle.putString("backgroundColor", cards.get(position).getCardStatus());

        cardFragment.setArguments(bundle);

        return cardFragment;
    }

    @Override
    public int getCount() {
        Log.d(TAG, "getCount");
        return cards.size();
    }


}
