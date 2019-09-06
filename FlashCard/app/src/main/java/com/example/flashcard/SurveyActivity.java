package com.example.flashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.Utilities.TestActivity;
import com.example.flashcard.adapters.CardRecyclerAdapter;
import com.example.flashcard.models.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SurveyActivity extends AppCompatActivity {

    private int max = 0;
    private boolean unlimitButtonClicked = false;
    private EditText number_of_cards_input;
    private TextView txt_max_cards;
    private EditText time_input;
    private Button unlimitedButton;
    private TextView txt_cards_response;
    private TextView txt_time_response;
    private Button survey_confirm_button;
    private Toolbar toolbarSurvey;

    private TextWatcher  listener_Number_of_cards_input = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(ConstantVariable.TAG_TEXTCHANGE, s.toString());
            Log.d(ConstantVariable.TAG_TEXTCHANGE, String.valueOf(start));
            Log.d(ConstantVariable.TAG_TEXTCHANGE, String.valueOf(before));
            Log.d(ConstantVariable.TAG_TEXTCHANGE, String.valueOf(count));
            if(s.length() != 0)
                txt_cards_response.setText("Confirm: " + s + " cards ");
            else
                txt_cards_response.setText("");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher  listener_Time_input = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(ConstantVariable.TAG_TEXTCHANGE, "unlimitButtonClicked = " +  String.valueOf(unlimitButtonClicked));
            if(s.length() != 0)
                txt_time_response.setText("in " + s + " minute(s)");
            else
                txt_time_response.setText("");
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    View.OnClickListener listener_UnlimitedButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            time_input.setText("No limit time");
            txt_time_response.setText("(no limit time)");
        }
    };

    View.OnClickListener listener_Survey_confirm_button = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (number_of_cards_input.getText().toString().equals("")){
                number_of_cards_input.setError("Cannot empty");
                return;
            }
            if(time_input.getText().toString().equals("")){
                time_input.setError("Cannot empty");
                return;
            }

            int numberOfCards = Integer.valueOf(number_of_cards_input.getText().toString());
            int time = -1;

            if(!time_input.getText().toString().equals("No limit time")){
                time = Integer.valueOf(time_input.getText().toString());
            }


            if(checkNumberLowerThanFour(numberOfCards) && checkNumberLowerThanOne(time)){
                number_of_cards_input.setError("Must have 4 cards to test!!!");
                time_input.setError("Time must greater than 0");
                return;
            }
            else {
                if(checkNumberLowerThanFour(numberOfCards)) {
                    number_of_cards_input.setError("Must have 4 cards to test!!!");
                    return;
                }
                if(checkNumberLowerThanOne(time)) {
                    time_input.setError("Time must greater than 0");
                    return;
                }
            }

            numberOfCards = numberOfCards > max ? max : numberOfCards;


            time = time > 60 ? 60 : time;

            Intent intent = new Intent(SurveyActivity.this,TestActivity.class);
            intent.putExtra(ConstantVariable.DECK_ID, getIntent().getStringExtra(ConstantVariable.DECK_ID));
            intent.putExtra(ConstantVariable.CARD_COLOR, getIntent().getStringExtra(ConstantVariable.CARD_COLOR));
            intent.putExtra(ConstantVariable.INPUT_CARDS,numberOfCards);
            intent.putExtra(ConstantVariable.INPUT_TIME,time);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        initialGetViewComponents();
        setupToolBar();

        getData_Wait_Continue(); // continue flow after onDataChange

    }

    private void execute_After_onDataChange(){
        Log.d(ConstantVariable.TAG_TEXTCHANGE, "End loop");
        txt_max_cards.setText("/" + String.valueOf(max));
        number_of_cards_input.addTextChangedListener(listener_Number_of_cards_input);
        time_input.addTextChangedListener(listener_Time_input);
        unlimitedButton.setOnClickListener(listener_UnlimitedButton);
        survey_confirm_button.setOnClickListener(listener_Survey_confirm_button);
    }

    private void getData_Wait_Continue() {
        Log.d(ConstantVariable.TAG_TEXTCHANGE, "S -- onStart");
        DatabaseReference databaseDeckDetails = FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails")
                .child(getIntent().getStringExtra(ConstantVariable.DECK_ID));
        databaseDeckDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    if("Total".equals(getIntent().getStringExtra(ConstantVariable.CARD_COLOR))){
                        count++;
                    }
                    else {
                        Card card = postSnapshot.getValue(Card.class);
                        if(card.getCardStatus().equals(getIntent().getStringExtra(ConstantVariable.CARD_COLOR))){
                            count++;
                        }
                    }
                }
                max = count;
                execute_After_onDataChange();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Log.d(ConstantVariable.TAG_TEXTCHANGE, "S -- onStart");
    }

    private void initialGetViewComponents() {
        number_of_cards_input = findViewById(R.id.number_of_cards_input);
        txt_max_cards = findViewById(R.id.txt_max_cards);
        time_input = findViewById(R.id.time_input);
        unlimitedButton = findViewById(R.id.unlimitedButton);
        txt_cards_response = findViewById(R.id.txt_cards_response);
        txt_time_response = findViewById(R.id.txt_time_response);
        survey_confirm_button = findViewById(R.id.survey_confirm_button);
        toolbarSurvey = (Toolbar) findViewById(R.id.toolbarSurvey);

    }

    private void setupToolBar() {

        if (toolbarSurvey == null) return;
        setTitle("Test settings");
        setSupportActionBar(toolbarSurvey);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        // BACK TO PREVIOUS ACTIVITY ( LIKE BACK PHYSICAL BUTTON
        toolbarSurvey.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean checkNumberLowerThanFour(int input){
        if(input < 4){
            return true;
        }
        return false;
    }
    private boolean checkNumberLowerThanOne(int input){
        if(input < 1 && input >=0){
            return true;
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_down,R.anim.slide_out_down);
    }
    // animate top to bottom back pressed
    @Override
    public void onBackPressed() {
        finish();
    }
}
