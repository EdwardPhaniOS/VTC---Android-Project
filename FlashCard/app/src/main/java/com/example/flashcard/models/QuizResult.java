package com.example.flashcard.models;

import java.util.ArrayList;
import java.util.List;

public class QuizResult
{
    private List<String> result_question = new ArrayList<String>();
    private List<String> result_answer_right = new ArrayList<String>();
    private List<String> result_user_answer = new ArrayList<String>();
    private List<String> result_color = new ArrayList<String>();

    private List<String> indexNumberInFirstPage = new ArrayList<String>();;
    private List<String> questionTitlesForFirstPage = new ArrayList<String>();;
    private List<String> yourAnswersForFirstPage = new ArrayList<String>();;
    private List<String> correctAnswersForFirstPage = new ArrayList<String>();;

    private List<String> indexNumberInSecondPage = new ArrayList<String>();;
    private List<String> questionTitlesForSecondPage = new ArrayList<String>();;
    private List<String> yourAnswersForSecondPage = new ArrayList<String>();;
    private List<String> correctAnswersForSecondPage = new ArrayList<String>();;


    public QuizResult(List<String> result_question, List<String> result_answer_right,
                      List<String> result_user_answer, List<String> result_color)
    {
        this.result_question = result_question;
        this.result_answer_right = result_answer_right;
        this.result_user_answer = result_user_answer;
        this.result_color = result_color;
    }

    public QuizResult(List<String> indexNumberInFirstPage, List<String> questionTitlesForFirstPage,
                      List<String> yourAnswersForFirstPage, List<String> correctAnswersForFirstPage,
                      List<String> indexNumberInSecondPage, List<String> questionTitlesForSecondPage,
                      List<String> yourAnswersForSecondPage, List<String> correctAnswersForSecondPage)
    {
        this.indexNumberInFirstPage = indexNumberInFirstPage;
        this.questionTitlesForFirstPage = questionTitlesForFirstPage;
        this.yourAnswersForFirstPage = yourAnswersForFirstPage;
        this.correctAnswersForFirstPage = correctAnswersForFirstPage;

        this.indexNumberInSecondPage = indexNumberInSecondPage;
        this.questionTitlesForSecondPage = questionTitlesForSecondPage;
        this.yourAnswersForSecondPage = yourAnswersForSecondPage;
        this.correctAnswersForSecondPage = correctAnswersForSecondPage;
    }

    public List<String> getResult_question() {
        return  result_question;
    }

    public List<String> getResult_answer_right() {
        return  result_answer_right;
    }

    public List<String> getResult_user_answer() {
        return result_user_answer;
    }

    public List<String> getResult_color() {
        return result_color;
    }

    public List<String> getIndexNumberInFirstPage() {
        return indexNumberInFirstPage;
    }

    public List<String> getQuestionTitlesForFirstPage() {
        return questionTitlesForFirstPage;
    }

    public List<String> getYourAnswersForFirstPage() {
        return yourAnswersForFirstPage;
    }

    public List<String> getCorrectAnswersForFirstPage() {
        return correctAnswersForFirstPage;
    }

    public List<String> getIndexNumberInSecondPage() {
        return indexNumberInSecondPage;
    }

    public List<String> getQuestionTitlesForSecondPage() {
        return questionTitlesForSecondPage;
    }

    public List<String> getYourAnswersForSecondPage() {
        return yourAnswersForSecondPage;
    }

    public List<String> getCorrectAnswersForSecondPage() {
        return correctAnswersForSecondPage;
    }
}
