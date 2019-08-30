package com.example.flashcard.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.flashcard.LearnActivity;
import com.example.flashcard.R;
import com.example.flashcard.SurveyActivity;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.models.Card;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {
    private Button buttonLearnBlue;
    private Button buttonTestBlue;
    private Button buttonTrainingBlue;

    private Button buttonLearnTotal;
    private Button buttonTestTotal;
    private Button buttonTrainingTotal;

    private Button buttonLearnYellow;
    private Button buttonTestYellow;
    private Button buttonTrainingYellow;

    private Button buttonLearnGreen;
    private Button buttonTestGreen;
    private Button buttonTrainingGreen;

    private Button buttonLearnRed;
    private Button buttonTestRed;
    private Button buttonTrainingRed;

    private OnButtonTestClickListener listener;

    public TrainingFragment(OnButtonTestClickListener listener) {
        // Required empty public constructor
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_training, null);

        final String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
        final String deckName = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME);

        buttonLearnBlue = (Button) view.findViewById(R.id.buttonLearnBlue);
        buttonTestBlue = (Button) view.findViewById(R.id.buttonTestBlue);
        buttonTrainingBlue = (Button) view.findViewById(R.id.buttonTrainingBlue);

        buttonLearnTotal = (Button) view.findViewById(R.id.buttonLearnTotal);
        buttonTestTotal = (Button) view.findViewById(R.id.buttonTestTotal);
        buttonTrainingTotal = (Button) view.findViewById(R.id.buttonTrainingTotal);

        buttonLearnYellow = (Button) view.findViewById(R.id.buttonLearnYellow);
        buttonTestYellow = (Button) view.findViewById(R.id.buttonTestYellow);
        buttonTrainingYellow = (Button) view.findViewById(R.id.buttonTrainingYellow);

        buttonLearnGreen = (Button) view.findViewById(R.id.buttonLearnGreen);
        buttonTestGreen = (Button) view.findViewById(R.id.buttonTestGreen);
        buttonTrainingGreen = (Button) view.findViewById(R.id.buttonTrainingGreen);

        buttonLearnRed = (Button) view.findViewById(R.id.buttonLearnRed);
        buttonTestRed = (Button) view.findViewById(R.id.buttonTestRed);
        buttonTrainingRed = (Button) view.findViewById(R.id.buttonTrainingRed);


        buttonTrainingTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllButtonsExcept(buttonLearnTotal);
                hideAndShowButtons(buttonLearnTotal,buttonTestTotal);
            }
        });

        buttonTrainingBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllButtonsExcept(buttonLearnBlue);
                hideAndShowButtons(buttonLearnBlue,buttonTestBlue);
            }
        });

        buttonTrainingYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllButtonsExcept(buttonLearnYellow);
                hideAndShowButtons(buttonLearnYellow,buttonTestYellow);
            }
        });

        buttonTrainingGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllButtonsExcept(buttonLearnGreen);
                hideAndShowButtons(buttonLearnGreen,buttonTestGreen);
            }
        });

        buttonTrainingRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllButtonsExcept(buttonLearnRed);
                hideAndShowButtons(buttonLearnRed,buttonTestRed);
            }
        });

        buttonLearnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LearnActivity.class);
                // put extra
                intent.putExtra(ConstantVariable.DECK_ID, deckId);
                intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                startActivity(intent);
            }
        });

        buttonTestBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SurveyActivity.class);
                // put extra
                intent.putExtra(ConstantVariable.DECK_ID, deckId);
                intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                startActivity(intent);
                listener.OnButtonTestClick();
            }
        });

        return view;
    }

    public interface OnButtonTestClickListener {
        void OnButtonTestClick();
    }

    private void hideAndShowButtons(Button buttonLearn, Button buttonTest){
        if(buttonLearn.getVisibility() == View.GONE){
            buttonLearn.setVisibility(View.VISIBLE);
            buttonTest.setVisibility(View.VISIBLE);
        }
        else {
            buttonLearn.setVisibility(View.GONE);
            buttonTest.setVisibility(View.GONE);
        }
    }

    private void hideAllButtonsExcept(Button button){
        if(button == buttonLearnTotal){
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnBlue){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnYellow){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnGreen){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnRed){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
        }


    }
}
