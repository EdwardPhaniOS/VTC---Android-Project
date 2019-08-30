package com.example.flashcard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.adapters.RecyclerAdapterForResultPage;
import com.example.flashcard.models.PageViewModelForResultPage;
import com.example.flashcard.models.QuizResult;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderFragmentForResultPage extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModelForResultPage pageViewModelForResultPage;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    QuizResult quizResult;
    QuizResult quizResultAfterAnalysis;

    private List<String> result_question = new ArrayList<String>();
    private List<String> result_answer_right = new ArrayList<String>();
    private List<String> result_user_answer = new ArrayList<String>();
    private List<String> result_color = new ArrayList<String>();

    private List<String> indexNumberInFirstPage = new ArrayList<String>();
    private List<String> questionTitlesForFirstPage = new ArrayList<String>();
    private List<String> yourAnswersForFirstPage = new ArrayList<String>();
    private List<String> correctAnswersForFirstPage = new ArrayList<String>();
    private List<String> resultColorForFirstPage = new ArrayList<String>();

    private List<String> indexNumberInSecondPage = new ArrayList<String>();
    private List<String> questionTitlesForSecondPage = new ArrayList<String>();
    private List<String> yourAnswersForSecondPage = new ArrayList<String>();
    private List<String> correctAnswersForSecondPage = new ArrayList<String>();
    private List<String> resultColorForSecondPage = new ArrayList<String>();

    public PlaceholderFragmentForResultPage(QuizResult quizResult) {
        this.quizResult = quizResult;
    }

    public static PlaceholderFragmentForResultPage newInstance(int index, QuizResult quizResult) {
        PlaceholderFragmentForResultPage fragment =
                new PlaceholderFragmentForResultPage(quizResult);
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModelForResultPage = ViewModelProviders.of(this).get(PageViewModelForResultPage.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }

        //Phan tich so lieu
        result_question = quizResult.getResult_question();
        result_answer_right = quizResult.getResult_answer_right();
        result_user_answer = quizResult.getResult_user_answer();
        result_color = quizResult.getResult_color();

        for (int i = 0; i < result_question.size(); i++)
        {
            if (result_answer_right.get(i).matches(result_user_answer.get(i))) {
                indexNumberInSecondPage.add("" + (i + 1));
                questionTitlesForSecondPage.add(result_question.get(i));
                correctAnswersForSecondPage.add(result_answer_right.get(i));
                yourAnswersForSecondPage.add(result_user_answer.get(i));
                resultColorForSecondPage.add(result_color.get(i));

            } else {
                indexNumberInFirstPage.add("" + (i + 1));
                questionTitlesForFirstPage.add(result_question.get(i));
                correctAnswersForFirstPage.add(result_answer_right.get(i));
                yourAnswersForFirstPage.add(result_user_answer.get(i));
                resultColorForFirstPage.add(result_color.get(i));
            }
        }

        quizResultAfterAnalysis = new QuizResult(indexNumberInFirstPage, questionTitlesForFirstPage,
                yourAnswersForFirstPage, correctAnswersForFirstPage, indexNumberInSecondPage, questionTitlesForSecondPage,
                yourAnswersForSecondPage, correctAnswersForSecondPage, resultColorForFirstPage, resultColorForSecondPage);


        pageViewModelForResultPage.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_show_result, container, false);

        recyclerView = root.findViewById(R.id.showResultRecycleView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        pageViewModelForResultPage.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String index) {
                if (index.matches("1")) {
                    adapter = new RecyclerAdapterForResultPage(true,
                            quizResultAfterAnalysis);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter = new RecyclerAdapterForResultPage(false,
                            quizResultAfterAnalysis);
                    recyclerView.setAdapter(adapter);
                }

            }
        });

        return root;
    }
}