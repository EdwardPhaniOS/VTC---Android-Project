package com.example.flashcard.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.flashcard.R;
import com.example.flashcard.SurveyActivity;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.Utilities.DataAll;
import com.example.flashcard.models.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestQuestionFinishFragment extends Fragment {

    private List<String> result_question = new ArrayList<String>();
    private List<String> result_answer_right = new ArrayList<String>();
    private List<String> result_answer_wrong = new ArrayList<String>();
    private List<String> result_color = new ArrayList<String>();

    private Button buttonTestViewResult;
    private Button buttonTestExit;
    private Button buttonTestRetake;
    private TextView textViewShowScore;


    public TestQuestionFinishFragment(List<String> _result_question,List<String> _result_answer_right
    ,List<String> _result_answer_wrong,List<String> _result_color) {
        // Required empty public constructor
        this.result_question = _result_question;
        this.result_answer_right = _result_answer_right;
        this.result_answer_wrong = _result_answer_wrong;
        this.result_color = _result_color;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_question_finish, container, false);

        buttonTestViewResult = view.findViewById(R.id.buttonTestViewResult);
        buttonTestExit = view.findViewById(R.id.buttonTestExit);
        buttonTestRetake = view.findViewById(R.id.buttonTestRetake);

        textViewShowScore = view.findViewById(R.id.textViewShowScore);

        int numberofCorrectAnswers = numberOfCorrectAnswer();
        int numberofQuestions = result_question.size();

        String numberResult = numberofCorrectAnswers + "/" + numberofQuestions;
        String percentageResult = Math.round((float)numberofCorrectAnswers* 100/numberofQuestions) + "%";
        textViewShowScore.setText(numberResult + " for " + percentageResult);

        buttonTestViewResult.setOnClickListener(onClickListener_buttonTestViewResult);
        buttonTestExit.setOnClickListener(onClickListener_buttonTestExit);
        buttonTestRetake.setOnClickListener(onClickListener_buttonTestRetake);

        return view;
    }

    View.OnClickListener onClickListener_buttonTestViewResult = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), SurveyActivity.class);
            intent.putStringArrayListExtra(ConstantVariable.RESULT_QUESTION_FOR_SHOWTESTRESULT, (ArrayList<String>) result_question);
            intent.putStringArrayListExtra(ConstantVariable.RESULT_ANSWER_RIGHT_FOR_SHOWTESTRESULT, (ArrayList<String>) result_answer_right);
            intent.putStringArrayListExtra(ConstantVariable.RESULT_ANSWER_WRONG_FOR_SHOWTESTRESULT, (ArrayList<String>) result_answer_wrong);
            intent.putStringArrayListExtra(ConstantVariable.RESULT_COLOR_FOR_SHOWTESTRESULT, (ArrayList<String>) result_color);
            //ArrayList<String> test = getIntent().getStringArrayListExtra("test");
            startActivity(intent);
        }
    };
    View.OnClickListener onClickListener_buttonTestExit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().finish();
        }
    };
    View.OnClickListener onClickListener_buttonTestRetake = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private int numberOfCorrectAnswer(){
        int res = 0;
        for(int i=0;i<result_question.size();i++){
            if(result_answer_right.get(i).equals(result_answer_wrong.get(i))){
                res++;
            }
        }
        return res;
    }


}
