package com.example.flashcard.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.flashcard.R;
import com.example.flashcard.Utilities.DataAll;
import com.example.flashcard.models.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlashcardLearnFragment extends Fragment {
    public final static String TAG = "Color";
    LinearLayout layoutFront;
    LinearLayout layoutBack;
    ScrollView scrollViewFront;
    ScrollView scrollViewBack;

    private TextView textViewFront;
    private TextView textViewBack;

    public FlashcardLearnFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_flashcard_learn, container, false);

        textViewFront = view.findViewById(R.id.textViewFront);
        textViewBack = view.findViewById(R.id.textViewBack);
        String textVocabulary = getArguments().getString("textVocabulary");
        String textDefinition = getArguments().getString("textDefinition");
        String backgroundColor = getArguments().getString("backgroundColor");


        layoutFront = (LinearLayout) view.findViewById(R.id.layoutFront);
        layoutBack = (LinearLayout) view.findViewById(R.id.layoutBack);

        textViewFront.setText(textVocabulary);
        textViewBack.setText(textDefinition);
        Log.d(TAG,backgroundColor);
        if(backgroundColor == "BLUE"){
            layoutFront.setBackgroundResource(R.drawable.border_flashcard_learn_blue);
            layoutBack.setBackgroundResource(R.drawable.border_flashcard_learn_blue);
        }
        if(backgroundColor == "RED"){
            layoutFront.setBackgroundResource(R.drawable.border_flashcard_learn_red);
            layoutBack.setBackgroundResource(R.drawable.border_flashcard_learn_red);
        }
        if(backgroundColor == "GREEN"){
            layoutFront.setBackgroundResource(R.drawable.border_flashcard_learn_green);
            layoutBack.setBackgroundResource(R.drawable.border_flashcard_learn_green);
        }
        if(backgroundColor == "YELLOW"){
            layoutFront.setBackgroundResource(R.drawable.border_flashcard_learn_yellow);
            layoutBack.setBackgroundResource(R.drawable.border_flashcard_learn_yellow);
        }



        scrollViewFront = (ScrollView) view.findViewById(R.id.scrollViewFront);
        scrollViewBack = (ScrollView) view.findViewById(R.id.scrollViewBack);

        // animate flip for LinearLayout
        scrollViewFront.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "scrollViewFront.setOnClickListener");
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(layoutFront, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(layoutFront, "scaleX", 0f, 1f);
                oa1.setDuration(500);
                oa2.setDuration(500);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //imageView.setImageResource(R.drawable.frontSide);
                        layoutFront.setVisibility(View.GONE);
                        layoutBack.setVisibility(View.VISIBLE);
                        oa2.start();
                    }
                });
                oa1.start();
            }
        });

        scrollViewBack.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "scrollViewBack.setOnClickListener");
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(layoutBack, "scaleX", 1f, 0f);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(layoutBack, "scaleX", 0f, 1f);
                oa1.setDuration(500);
                oa2.setDuration(500);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //imageView.setImageResource(R.drawable.frontSide);
                        layoutBack.setVisibility(View.GONE);
                        layoutFront.setVisibility(View.VISIBLE);
                        oa2.start();
                    }
                });
                oa1.start();
            }
        });

        return view;
    }


}
