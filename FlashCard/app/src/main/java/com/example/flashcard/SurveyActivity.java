package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SurveyActivity extends AppCompatActivity {

    private EditText number_of_cards_input;
    private TextView txt_max_cards;
    private EditText time_input;
    private Button unlimitedButton;
    private TextView txt_cards_response;
    private TextView txt_time_response;
    private Button survey_confirm_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        setUp();
    }


    private void setUp() {
        number_of_cards_input = findViewById(R.id.number_of_cards_input);
        txt_max_cards = findViewById(R.id.txt_max_cards);
        time_input = findViewById(R.id.time_input);
        unlimitedButton = findViewById(R.id.unlimitedButton);
        txt_cards_response = findViewById(R.id.txt_cards_response);
        txt_time_response = findViewById(R.id.txt_time_response);
        survey_confirm_button = findViewById(R.id.survey_confirm_button);
    }
}
