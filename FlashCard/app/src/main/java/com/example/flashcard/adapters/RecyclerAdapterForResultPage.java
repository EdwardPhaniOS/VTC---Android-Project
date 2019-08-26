package com.example.flashcard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;

public class RecyclerAdapterForResultPage extends RecyclerView.Adapter<RecyclerAdapterForResultPage.ViewHolder> {

    private boolean isShowWrongAnswer;

    private String[] indexNumberInFirstPage = {"1", "3", "6"};
    private String[] questionTitlesForFirstPage = {"Chapter One", "Chapter Two", "Chapter Three"};
    private String[] yourAnswersForFirstPage = {"Item one details",
            "Item two details", "Item three details",};
    private String[] correctAnswersForFirstPage = {"Item one details",
            "Item two details", "Item three details",};

    private String[] indexNumberInSecondPage = {"2", "4", "5"};
    private String[] questionTitlesForSecondPage = {"Chapter One", "Chapter Two", "Chapter Three"};
    private String[] yourAnswersForSecondPage = {"Item one details",
            "Item two details", "Item three details",};
    private String[] correctAnswersForSecondPage = {"Item one details",
            "Item two details", "Item three details",};

    public RecyclerAdapterForResultPage(boolean isShowWrongAnswer) {
        this.isShowWrongAnswer = isShowWrongAnswer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.result_cell_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if (isShowWrongAnswer) {
            viewHolder.questionNumber.setText("Question " + indexNumberInFirstPage[i]);
            viewHolder.questionTitle.setText(questionTitlesForFirstPage[i]);
            viewHolder.correctAnswer.setText(correctAnswersForFirstPage[i]);
            viewHolder.yourAnswer.setText(yourAnswersForFirstPage[i]);
        } else {
            viewHolder.questionNumber.setText("Question " + indexNumberInSecondPage[i]);
            viewHolder.questionTitle.setText(questionTitlesForSecondPage[i]);
            viewHolder.correctAnswer.setText(correctAnswersForSecondPage[i]);
            viewHolder.yourAnswer.setText(yourAnswersForSecondPage[i]);
        }
    }

    @Override
    public int getItemCount() {
        if (isShowWrongAnswer) {
            return correctAnswersForFirstPage.length;
        } else {
            return correctAnswersForSecondPage.length;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView questionNumber;
        public TextView questionTitle;
        public TextView yourAnswer;
        public TextView correctAnswer;
        public TextView currentCardStatus;
        public TextView newCardStatus;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            questionNumber = itemView.findViewById(R.id.question_number);
            questionTitle = itemView.findViewById(R.id.question_title);
            yourAnswer = itemView.findViewById(R.id.your_answer);
            correctAnswer = itemView.findViewById(R.id.correct_answer);
            currentCardStatus = itemView.findViewById(R.id.current_card_status);
            newCardStatus = itemView.findViewById(R.id.new_card_status);
        }
    }
}