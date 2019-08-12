package com.example.flashcard.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flashcard.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlashcardLearnFragment extends Fragment {
    LinearLayout layout1;
    LinearLayout layout2;
    Button buttonFlip;
    Button buttonFlip2;
    private TextView textViewFront;


    public FlashcardLearnFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_flashcard_learn, container, false);

        textViewFront = view.findViewById(R.id.textViewFront);
        String message = getArguments().getString("message");
        textViewFront.setText(message);

        //
        layout1 = (LinearLayout) view.findViewById(R.id.layoutFront);
        layout2 = (LinearLayout) view.findViewById(R.id.layoutBack);

        buttonFlip = (Button) view.findViewById(R.id.buttonFlip);
        buttonFlip2 = (Button) view.findViewById(R.id.buttonFlip2);

        buttonFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout1.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.flip_in_right));
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout2.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.flip_in_left));
            }
        });

        buttonFlip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(layout1.getVisibility() == View.VISIBLE){
                    final ObjectAnimator oa1 = ObjectAnimator.ofFloat(layout1, "scaleX", 1f, 0f);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(layout1, "scaleX", 0f, 1f);
                    oa1.setDuration(500);
                    oa2.setDuration(500);
                    oa1.setInterpolator(new DecelerateInterpolator());
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //imageView.setImageResource(R.drawable.frontSide);
                            layout1.setVisibility(View.GONE);
                            layout2.setVisibility(View.VISIBLE);
                            oa2.start();
                        }
                    });
                    oa1.start();
                }
                else {
                    final ObjectAnimator oa1 = ObjectAnimator.ofFloat(layout2, "scaleX", 1f, 0f);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(layout2, "scaleX", 0f, 1f);
                    oa1.setDuration(500);
                    oa2.setDuration(500);
                    oa1.setInterpolator(new DecelerateInterpolator());
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //imageView.setImageResource(R.drawable.frontSide);
                            layout2.setVisibility(View.GONE);
                            layout1.setVisibility(View.VISIBLE);
                            oa2.start();
                        }
                    });
                    oa1.start();
                }

            }
        });
        //

        return view;
    }

}
