package com.example.flashcard.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.flashcard.R;
import com.example.flashcard.fragments.PlaceholderFragmentForResultPage;
import com.example.flashcard.models.QuizResult;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapterForResultPage extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    QuizResult quizResult;

    public SectionsPagerAdapterForResultPage(Context context, FragmentManager fm,
                                             QuizResult quizResult) {
        super(fm);
        mContext = context;
        this.quizResult = quizResult;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragmentForResultPage.newInstance(position + 1, quizResult);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}
