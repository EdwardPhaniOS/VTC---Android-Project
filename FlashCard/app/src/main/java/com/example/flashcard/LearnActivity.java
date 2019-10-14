package com.example.flashcard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.Utilities.DataAll;
import com.example.flashcard.Utilities.OnGetDataListener;
import com.example.flashcard.Utilities.ValidateCheckForReminder;
import com.example.flashcard.adapters.FlashcardsFragmentAdapter;
import com.example.flashcard.fragments.FlashcardLearnFragment;
import com.example.flashcard.fragments.MyDecksFragment;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LearnActivity extends AppCompatActivity{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
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

    private ImageButton buttonSpeakerVocabulary;
    private TextToSpeech textToSpeech;

    private int sizeOfCards;
    private int lastPosition = 0;
    private int currentPosition = 0;
    private boolean learnByBackCard = false;

    DatabaseReference databaseReference;


    Toolbar toolbarLearn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        setupToolbar();

        String deckID = getIntent().getStringExtra(ConstantVariable.DECK_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference("DBFlashCard/deckdetails").child(deckID);
        String deckName = getIntent().getStringExtra(ConstantVariable.DECK_NAME);

        setTitle(deckName);

        learnByBackCard = getIntent().getStringExtra("LEARN-BY-BACK-CARD").isEmpty() ? false : true;


        cards = new ArrayList<>();

        textViewProgress = findViewById(R.id.textViewProgress);
        progressBar = findViewById(R.id.progressBar);

        textToSpeech = new TextToSpeech(LearnActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.80f);
                    textToSpeech.setPitch(1.050f);
                }
            }
        },"com.google.android.tts");

        buttonSpeakerVocabulary = findViewById(R.id.buttonSpeakerVocabulary);
        buttonSpeakerVocabulary.bringToFront();
        buttonSpeakerVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                    return;
                }
                String toSpeak = cards.get(currentPosition).getVocabulary();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null,null);
                } else {
                    textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        buttonSpeakerVocabulary.setOnLongClickListener(speakButtonLongClickListener);

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
            if(textToSpeech.isSpeaking()){
                textToSpeech.stop();
            }
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
                List<Card> allCards = new ArrayList<>();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "LOOP - GETDATA");
                    Card card = postSnapshot.getValue(Card.class);
                    Log.d(TAG, card.getVocabulary());
                    allCards.add(card);
                }
                List<Card> cardsAfterFilter = filterCardByColor(allCards,getIntent().getStringExtra(ConstantVariable.CARD_COLOR));
                cards = cardsAfterFilter;
                //
                Collections.shuffle(cards);
                //
                mAdapter = new FlashcardsFragmentAdapter(getSupportFragmentManager(),cards,learnByBackCard);
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

                Fragment fragment = (Fragment) mAdapter.getRegisteredFragment(currentPosition);

                if (fragment != null && fragment instanceof FlashcardLearnFragment) {
                    Log.d(ConstantVariable.TAG_COLOR, "Vo duoc fragment");
                    ((FlashcardLearnFragment) fragment).changeColorBackground("BLUE");
                }
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
                Fragment fragment = (Fragment) mAdapter.instantiateItem(viewPagerLearn, currentPosition);

                if (fragment != null && fragment instanceof FlashcardLearnFragment) {
                    ((FlashcardLearnFragment) fragment).changeColorBackground("RED");
                }
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
                Fragment fragment = (Fragment) mAdapter.instantiateItem(viewPagerLearn, currentPosition);

                if (fragment != null && fragment instanceof FlashcardLearnFragment) {
                    ((FlashcardLearnFragment) fragment).changeColorBackground("GREEN");
                }
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
                Fragment fragment = (Fragment) mAdapter.instantiateItem(viewPagerLearn, currentPosition);

                if (fragment != null && fragment instanceof FlashcardLearnFragment) {
                    ((FlashcardLearnFragment) fragment).changeColorBackground("YELLOW");
                }
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

    private void setupToolbar(){
        toolbarLearn = (Toolbar)findViewById(R.id.toolbarLearn);
        setSupportActionBar(toolbarLearn);

        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarLearn.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // process for validate check reminder
                if(ValidateCheckForReminder.isFinishLearn){
                    //ValidateCheckForReminder.reminderSave.setIsActivated("true"); // wrong
                    Reminder reminderChecked = new Reminder(ValidateCheckForReminder.reminderSave.getReminderId()
                                                            ,ValidateCheckForReminder.reminderSave.getName()
                                                            ,ValidateCheckForReminder.reminderSave.getNameDay()
                                                            ,ValidateCheckForReminder.reminderSave.getDate()
                                                            ,ValidateCheckForReminder.reminderSave.getDeckId());
                    reminderChecked.setIsActivated("true");
                    FirebaseDatabase.getInstance().getReference("DBFlashCard/reminders").child(userId)
                            .child(ValidateCheckForReminder.reminderSave.getDeckId())
                            .child(ValidateCheckForReminder.reminderSave.getReminderId())
                            .setValue(reminderChecked);
                    Toast.makeText(LearnActivity.this, "The reminder of "
                                                            + reminderChecked.getName() + " is done", Toast.LENGTH_LONG).show();
                    //ValidateCheckForReminder.setDefault();
                }else {
                    if(ValidateCheckForReminder.reminderSave == null){
                        ValidateCheckForReminder.setDefault();
                    }else {
                        ValidateCheckForReminder.isTriggerFromLearnTotalButton = false;
                    }

                }
                finish();
                //onBackPressed();
            }
        });
    }

    private List<Card> filterCardByColor(List<Card> list,String color){
        if("Total".equals(color))
            return list;

        List<Card> res = new ArrayList<Card>();
        for(Card c : list){
            if(c.getCardStatus().equals(color))
                res.add(c);
        }
        return res;
    }


    View.OnLongClickListener speakButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showRepeatSpeakerDialog();
            return true;
        }
    };

    private void showRepeatSpeakerDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LearnActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.numberoftimes_repeat_speaker_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etRepetitionSpeaker = (EditText)dialogView.findViewById(R.id.etRepetitionSpeaker);
        final Button buttonOk = (Button)dialogView.findViewById(R.id.buttonOkRepeat);
        final Button buttonCancel = (Button)dialogView.findViewById(R.id.buttonCancelRepeat);

        dialogBuilder.setTitle("Repetition");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String input = etRepetitionSpeaker.getText().toString().trim();
                if(!TextUtils.isEmpty(input)){
                    int times = Integer.parseInt(input);
                    if(times<1){
                        return;
                    }
                    b.dismiss();
                    for(int j=0;j<times;j++){
                        String toSpeak = cards.get(currentPosition).getVocabulary();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textToSpeech.speak(toSpeak + "...\n", TextToSpeech.QUEUE_ADD, null,null);
                        } else {
                            textToSpeech.speak(toSpeak + "...\n", TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                }
                else {
                    etRepetitionSpeaker.setError("Cannot empty");
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }
}
