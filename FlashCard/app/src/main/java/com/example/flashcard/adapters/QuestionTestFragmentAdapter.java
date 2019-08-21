package com.example.flashcard.adapters;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.flashcard.fragments.TestQuestionFragment;
import com.example.flashcard.models.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionTestFragmentAdapter extends FragmentStatePagerAdapter {

    public List<Card> cards;
    ArrayList<ArrayList<String>> wrongAnswers;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public QuestionTestFragmentAdapter(FragmentManager fm, List<Card> cards) {
        super(fm);
        this.cards = cards;
        this.wrongAnswers = generateWrongAnswers(cards);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        TestQuestionFragment questionFragment = new TestQuestionFragment();
        Bundle bundle = new Bundle();

//        bundle.putString("textVocabulary", cards.get(position).getVocabulary());
//        bundle.putString("textDefinition", cards.get(position).getDefinition());
//        bundle.putString("backgroundColor", cards.get(position).getCardStatus());

        bundle.putSerializable("question_data",cards.get(position));
        bundle.putStringArrayList("wrong_answer_data",wrongAnswers.get(position));
        questionFragment.setArguments(bundle);
        return questionFragment;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    private ArrayList<ArrayList<String>> generateWrongAnswers(List<Card> cards){
        // Tham khao https://www.baeldung.com/java-random-list-element
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        Random random = new Random();
        for(int i = 0; i<cards.size();i++){
            List<Card> copy_to_exclude = new ArrayList<Card>(cards);
            copy_to_exclude.remove(cards.get(i));

            int numbersOfAnswers = 3;
            ArrayList<String> list_wrong_answer_one_card = new ArrayList<String>();
            for (int j = 0; j < numbersOfAnswers; j++) {
                int randomIndex = random.nextInt(copy_to_exclude.size());
                Card randomElement = copy_to_exclude.get(randomIndex);

                list_wrong_answer_one_card.add(randomElement.getDefinition());

                copy_to_exclude.remove(randomIndex);
            }
            result.add(list_wrong_answer_one_card);
            copy_to_exclude.clear();
        }
        return result;
    }
}
