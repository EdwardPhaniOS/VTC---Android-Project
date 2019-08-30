package com.example.flashcard.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.models.QuizResult;

public class RecyclerAdapterForResultPage extends RecyclerView.Adapter<RecyclerAdapterForResultPage.ViewHolder> {

    private boolean isShowWrongAnswer;
    QuizResult quizResult;

    public RecyclerAdapterForResultPage(boolean isShowWrongAnswer, QuizResult quizResult) {
        this.isShowWrongAnswer = isShowWrongAnswer;
        this.quizResult = quizResult;
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

        if (isShowWrongAnswer)
        {   //First Page

            int indexNumberOfFirstPage = Integer
                    .parseInt(quizResult.getIndexNumberInFirstPage().get(i));

            viewHolder.questionNumber.setText("Question "
                    + indexNumberOfFirstPage);
            viewHolder.questionTitle.setText(quizResult.getQuestionTitlesForFirstPage().get(i));
            viewHolder.correctAnswer.setText(quizResult.getCorrectAnswersForFirstPage().get(i));

            viewHolder.descriptionArrow.setImageResource(R.drawable.icon_arrow_down);

            String newColor = String.valueOf(quizResult.getResultColorForFirstPage().get(i));

            //UN-SUBMIT ANSWER
            if (quizResult.getYourAnswersForFirstPage().get(i)
                    .matches(ConstantVariable.UNSUBMIITED_QUESTION))
            {
                viewHolder.yourAnswer.setText("--Unsubmitted--");
                setCardStatusColor(newColor, viewHolder, false);

            } else {
                //SUBMITTED ANSWER
                viewHolder.yourAnswer.setText(quizResult.getYourAnswersForFirstPage().get(i));
                setCardStatusColor(newColor, viewHolder, true);
            }

        } else {
            //Second Page

            viewHolder.questionNumber.setText("Question "
                    + quizResult.getIndexNumberInSecondPage().get(i));
            viewHolder.questionTitle.setText(quizResult.getQuestionTitlesForSecondPage().get(i));
            viewHolder.correctAnswer.setText(quizResult.getCorrectAnswersForSecondPage().get(i));
            viewHolder.yourAnswer.setText(quizResult.getYourAnswersForSecondPage().get(i));
            viewHolder.descriptionArrow.setImageResource(R.drawable.icon_arrow_up);

            String newColor = String.valueOf(quizResult.getResultColorForSecondPage().get(i));

            setCardStatusColor(newColor, viewHolder, true);

        }
    }

    private void setCardStatusColor(String newColor, ViewHolder viewHolder, boolean isSubmitted)
    {
        if (!isSubmitted)
        {
            //For unsubmitted card

            viewHolder.descriptionArrow.setVisibility(View.INVISIBLE);

            if (newColor.matches("RED")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.RED);

            } else if (newColor.matches("YELLOW")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.YELLOW);

            } else if (newColor.matches("GREEN")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.GREEN);

            } else if (newColor.matches("BLUE")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.BLUE);
            }

        } else {

            //For submitted card
            if (newColor.matches("RED")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.RED);

            } else if (newColor.matches("YELLOW")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.YELLOW);

            } else if (newColor.matches("GREEN")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.GREEN);

            } else if (newColor.matches("BLUE")) {
                viewHolder.newCardStatus.setBackgroundResource(R.color.BLUE);
            }

        }
    }

    @Override
    public int getItemCount() {
        if (isShowWrongAnswer) {
            return quizResult.getCorrectAnswersForFirstPage().size();
        } else {
            return quizResult.getCorrectAnswersForSecondPage().size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView questionNumber;
        public TextView questionTitle;
        public TextView yourAnswer;
        public TextView correctAnswer;
        public TextView newCardStatus;
        public ImageView descriptionArrow;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            questionNumber = itemView.findViewById(R.id.question_number);
            questionTitle = itemView.findViewById(R.id.question_title);
            yourAnswer = itemView.findViewById(R.id.your_answer);
            correctAnswer = itemView.findViewById(R.id.correct_answer);
            newCardStatus = itemView.findViewById(R.id.new_card_status);
            descriptionArrow = itemView.findViewById(R.id.description_arrow);
        }
    }
}