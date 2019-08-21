package com.example.flashcard.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.R;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.models.Card;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.example.flashcard.Utilities.DataAll;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestQuestionFragment extends Fragment {
    TextView tvQuestionTest;
    RadioButton rbAnswer1;
    RadioButton rbAnswer2;
    RadioButton rbAnswer3;
    RadioButton rbAnswer4;

    private Card card;

    public TestQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_question, container, false);

        tvQuestionTest = (TextView) view.findViewById(R.id.tvQuestionTest);
        rbAnswer1 = (RadioButton) view.findViewById(R.id.rbAnswer1);
        rbAnswer2 = (RadioButton) view.findViewById(R.id.rbAnswer2);
        rbAnswer3 = (RadioButton) view.findViewById(R.id.rbAnswer3);
        rbAnswer4 = (RadioButton) view.findViewById(R.id.rbAnswer4);

        setupAnswer();

        return view;
    }

    private void setupAnswer(){
        card = new Card();
        ArrayList<String> wrongAnswers = new ArrayList<String>();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("question_data")) {
            card = (Card)arguments.getSerializable("question_data");
        }
        if (arguments != null && arguments.containsKey("wrong_answer_data")) {
            wrongAnswers = (ArrayList<String>)arguments.getSerializable("wrong_answer_data");
        }
        // add CORRECT ANSWER to list wrong answer, then shuffle
        wrongAnswers.add(card.getDefinition());
        Collections.shuffle(wrongAnswers,new Random());

        tvQuestionTest.setText(card.getVocabulary());
        rbAnswer1.setText(wrongAnswers.get(0));
        rbAnswer2.setText(wrongAnswers.get(1));
        rbAnswer3.setText(wrongAnswers.get(2));
        rbAnswer4.setText(wrongAnswers.get(3));
    }

    public List<String> checkAnswer(String deckId){
        List<String> result = new ArrayList<String>();
        if(rbAnswer1.isChecked()){
            return markQuestion(rbAnswer1,card,deckId);
        }else if(rbAnswer2.isChecked()){
            return markQuestion(rbAnswer2,card,deckId);
        }if(rbAnswer3.isChecked()){
            return markQuestion(rbAnswer3,card,deckId);
        }else if(rbAnswer4.isChecked()){
            return markQuestion(rbAnswer4,card,deckId);
        }
        return result;
    }

    private Card upGradeColor(Card _card){
        if(_card.getCardStatus().equals(CardColor.BLUE.name())){
            _card.setCardStatus(CardColor.YELLOW.toString());
        }else if(_card.getCardStatus().equals(CardColor.YELLOW.name())){
            _card.setCardStatus(CardColor.GREEN.toString());
        }else if(_card.getCardStatus().equals(CardColor.GREEN.name())){
            _card.setCardStatus(CardColor.RED.toString());
        }
        return _card;
    }

    private Card downGradeColor(Card _card){
        if(_card.getCardStatus().equals(CardColor.RED.name())){
            _card.setCardStatus(CardColor.GREEN.toString());
        }else if(_card.getCardStatus().equals(CardColor.GREEN.name())){
            _card.setCardStatus(CardColor.YELLOW.toString());
        }else if(_card.getCardStatus().equals(CardColor.YELLOW.name())){
            _card.setCardStatus(CardColor.BLUE.toString());
        }
        return _card;
    }

    public List<String> markQuestion(RadioButton rb,Card card,String deckId){
        List<String> result = new ArrayList<String>();
        if(rb.getText().toString() == card.getDefinition()){
            // upgrade color ( if red -> not change )
            if(card.getCardStatus() != CardColor.RED.toString()){
                upGradeColor(card);
                DataAll.updateColorWhenMakeTest(deckId,card);
            }
        }else {
            // downgrade color ( if blue -> not change )
            if(card.getCardStatus() != CardColor.BLUE.toString()){
                downGradeColor(card);
                DataAll.updateColorWhenMakeTest(deckId,card);
            }
        }
        result.add(card.getVocabulary());
        result.add(card.getDefinition());
        result.add(card.getCardStatus());
        result.add(rb.getText().toString());

        return result;
    }

}
