package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.DataAll;
import com.example.flashcard.Utilities.OnGetDataListener;
import com.example.flashcard.adapters.FlashcardsFragmentAdapter;
import com.example.flashcard.fragments.MyDecksFragment;
import com.example.flashcard.models.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

public class LearnActivity extends AppCompatActivity{
    public final static String TAG = "CheckFlowAsync";
    private ViewPager viewPagerLearn;
    private FlashcardsFragmentAdapter mAdapter;

    public List<Card> cards;
    ProgressDialog mProgressDialog;
    TextView textViewProgress;
    ProgressBar progressBar;

    Button buttonRed;
    Button buttonBlue;
    Button buttonGreen;
    Button buttonYellow;

    private int sizeOfCards;
    private int lastPosition = 0;
    private int currentPosition = 0;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        String deckID = getIntent().getStringExtra(MyDecksFragment.DECK_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference("DBFlashCard/deckdetails").child(deckID);
        cards = new ArrayList<>();

        textViewProgress = findViewById(R.id.textViewProgress);
        progressBar = findViewById(R.id.progressBar);


        viewPagerLearn = findViewById(R.id.pagerLearnAcivity);
        viewPagerLearn.addOnPageChangeListener(swipeListener);

        getCardOfDeckId(deckID);


    }

    ViewPager.OnPageChangeListener swipeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (lastPosition > position) {
                updateUI(position);
            }else if (lastPosition < position) {
                updateUI(position);
            }
            lastPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void getCardOfDeckId(String deckId) {
            DataAll.CardsOfDeckID_AsyncTask(deckId,new OnGetDataListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "onStart");
                //DO SOME THING WHEN START GET DATA HERE
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(LearnActivity.this);
                    mProgressDialog.setTitle("Wait ... ");
                    mProgressDialog.setMessage("Loading cards");
                    mProgressDialog.setIndeterminate(true);
                }
                mProgressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                //DO SOME THING WHEN GET DATA SUCCESS HERE
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "LOOP - GETDATA");
                    Card card = postSnapshot.getValue(Card.class);
                    Log.d(TAG, card.getVocabulary());
                    cards.add(card);
                }
                mAdapter = new FlashcardsFragmentAdapter(getSupportFragmentManager(),cards);
                sizeOfCards = cards.size();
                progressBar.setMax(sizeOfCards);
                viewPagerLearn.setAdapter(mAdapter);


                configureButtons();

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
                Toast.makeText(LearnActivity.this, "Something wrong with Database Firebase", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void configureButtons(){
        // button
        buttonRed = findViewById(R.id.buttonRed);
        buttonBlue = findViewById(R.id.buttonBlue);
        buttonGreen = findViewById(R.id.buttonGreen);
        buttonYellow = findViewById(R.id.buttonYellow);


        buttonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonBlue.setBackgroundResource(R.drawable.border_button_blue_selected);
                buttonGreen.setBackgroundResource(R.drawable.border_button_green_default);
                buttonYellow.setBackgroundResource(R.drawable.border_button_yellow_default);
                buttonRed.setBackgroundResource(R.drawable.border_button_red_default);
                updateColor(CardColor.BLUE);
            }
        });
        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRed.setBackgroundResource(R.drawable.border_button_red_selected);
                buttonBlue.setBackgroundResource(R.drawable.border_button_blue_default);
                buttonGreen.setBackgroundResource(R.drawable.border_button_green_default);
                buttonYellow.setBackgroundResource(R.drawable.border_button_yellow_default);
                updateColor(CardColor.RED);
            }
        });
        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonGreen.setBackgroundResource(R.drawable.border_button_green_selected);
                buttonRed.setBackgroundResource(R.drawable.border_button_red_default);
                buttonBlue.setBackgroundResource(R.drawable.border_button_blue_default);
                buttonYellow.setBackgroundResource(R.drawable.border_button_yellow_default);
                updateColor(CardColor.GREEN);
            }
        });
        buttonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonYellow.setBackgroundResource(R.drawable.border_button_yellow_selected);
                buttonGreen.setBackgroundResource(R.drawable.border_button_green_default);
                buttonRed.setBackgroundResource(R.drawable.border_button_red_default);
                buttonBlue.setBackgroundResource(R.drawable.border_button_blue_default);
                updateColor(CardColor.YELLOW);
            }
        });
        updateUI(0);
        //
    }

    private void updateUI(int position){
        currentPosition = position;
        // set progress textView
        textViewProgress.setText(position+1 + "/" + sizeOfCards);
        // set progress bar
        progressBar.setProgress(position+1);
        // set color button
        String color = cards.get(position).getCardStatus();
        CardColor card = CardColor.valueOf(CardColor.class,color);
        switch(card) {
            case BLUE:
                buttonBlue.performClick();
                break;
            case RED:
                buttonRed.performClick();
                break;
            case GREEN:
                buttonGreen.performClick();
                break;
            case YELLOW:
                buttonYellow.performClick();
                break;
        }

    }

    private void updateColor(CardColor color){
        Card currentCard = cards.get(currentPosition);
        if(currentCard.getCardStatus() != color.toString()){
            currentCard.setCardStatus(color.toString());
            databaseReference.child(currentCard.getCardId()).setValue(currentCard);
            viewPagerLearn.setCurrentItem(currentPosition);
        }
    }


}
