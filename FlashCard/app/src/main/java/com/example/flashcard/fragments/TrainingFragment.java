package com.example.flashcard.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.flashcard.LearnActivity;
import com.example.flashcard.R;
import com.example.flashcard.SurveyActivity;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.adapters.DeckList;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {
    public List<Card> cards;

    public int numberOfBlueCards = 0;
    public int numberOfYellowCards = 0;
    public int numberOfGreenCards = 0;
    public int numberOfRedCards = 0;


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

        cards = new ArrayList<>();

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



        buttonLearnTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LearnActivity.class);
                // put extra
                intent.putExtra(ConstantVariable.DECK_ID, deckId);
                intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                startActivity(intent);
            }
        });

        buttonTestTotal.setOnClickListener(new View.OnClickListener() {
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

    private void loadData(){
        String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
        FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails").child(deckId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cards.clear();
                numberOfBlueCards=0;
                numberOfYellowCards=0;
                numberOfGreenCards=0;
                numberOfRedCards=0;

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Card card = postSnapshot.getValue(Card.class);
                    cards.add(card);
                }
                for(int i = 0; i<cards.size();i++){
                    if(cards.get(i).getCardStatus().equals(CardColor.BLUE.name())){
                        numberOfBlueCards++;
                    }else if(cards.get(i).getCardStatus().equals(CardColor.YELLOW.name())){
                        numberOfYellowCards++;
                    }else if(cards.get(i).getCardStatus().equals(CardColor.GREEN.name())){
                        numberOfGreenCards++;
                    }else if(cards.get(i).getCardStatus().equals(CardColor.RED.name())){
                        numberOfRedCards++;
                    }
                }
                int total = cards.size();
                buttonTrainingTotal.setText("Total: " + total + " card(s) for 100%");
                int percentageOfBlue = (int)Math.round((float)numberOfBlueCards*100/total);
                buttonTrainingBlue.setText(numberOfBlueCards + " card(s) for " + percentageOfBlue + "%");
                int percentageOfYellow = (int)Math.round((float)numberOfYellowCards*100/total);
                buttonTrainingYellow.setText(numberOfYellowCards + " card(s) for " + percentageOfYellow + "%");
                int percentageOfGreen = (int)Math.round((float)numberOfGreenCards*100/total);
                buttonTrainingGreen.setText(numberOfGreenCards + " card(s) for " + percentageOfGreen + "%");
                int percentageOfRed = 100 - percentageOfBlue - percentageOfYellow - percentageOfGreen;
                buttonTrainingRed.setText(numberOfRedCards + " card(s) for " + percentageOfRed + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        loadData();
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
