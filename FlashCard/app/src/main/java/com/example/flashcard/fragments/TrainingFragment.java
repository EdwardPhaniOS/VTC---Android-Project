package com.example.flashcard.fragments;


import android.content.Intent;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.LearnActivity;
import com.example.flashcard.R;
import com.example.flashcard.SurveyActivity;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.Utilities.ValidateCheckForReminder;
import com.example.flashcard.adapters.DeckList;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {
    public List<Card> cards;
    public List<Card> cardsBlue;
    public List<Card> cardsYellow;
    public List<Card> cardsGreen;
    public List<Card> cardsRed;

    public int numberOfBlueCards = 0;
    public int numberOfYellowCards = 0;
    public int numberOfGreenCards = 0;
    public int numberOfRedCards = 0;


    private Button buttonLearnBlue;
    private Button buttonTestBlue;
    private Button buttonTrainingBlue;

    private Button buttonLearnTotal;
    private Button buttonTestTotal;
    private Button buttonTrainingTotal;

    private Button buttonLearnYellow;
    private Button buttonTestYellow;
    private Button buttonTrainingYellow;

    private Button buttonLearnGreen;
    private Button buttonTestGreen;
    private Button buttonTrainingGreen;

    private Button buttonLearnRed;
    private Button buttonTestRed;
    private Button buttonTrainingRed;

    private ImageButton buttonSpeak;
    private TextToSpeech textToSpeech;
    private TextView textViewShowSpeaking;

    private OnButtonTestClickListener listener;

    public TrainingFragment(OnButtonTestClickListener listener) {
        // Required empty public constructor
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_training, null);

        cards = new ArrayList<>();
        cardsBlue = new ArrayList<>();
        cardsYellow = new ArrayList<>();
        cardsGreen = new ArrayList<>();
        cardsRed = new ArrayList<>();

        final String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
        final String deckName = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME);

        textViewShowSpeaking = (TextView) view.findViewById(R.id.textViewShowSpeaking);
        textViewShowSpeaking.setText("");

        buttonSpeak = (ImageButton) view.findViewById(R.id.buttonSpeak);


        buttonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                    textViewShowSpeaking.setText("");
                    //textToSpeech.shutdown();
                    return;
                }
                //textToSpeech.stop();
//                    String toSpeak = generateTextFromVocabularyCards(cards);
//                    Toast.makeText(getContext(), toSpeak,Toast.LENGTH_LONG).show();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null,null);
//                    } else {
//                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                    }
                int size = cards.size();
                for(int i = 0;i<size;i++){
                    int count = i + 1;
                     String toSpeak = cards.get(i).getVocabulary();
                    String showText = "" + count + "/" + size + "\n" + "[ " + toSpeak + " ]";
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                         textToSpeech.speak(toSpeak + "...\n", TextToSpeech.QUEUE_ADD, null,showText);
                     } else {
                         textToSpeech.speak(toSpeak + "...\n", TextToSpeech.QUEUE_ADD, null);
                     }
                 }
            }
        });

        buttonSpeak.setOnLongClickListener(speakButtonLongClickListener);

        buttonLearnTotal = (Button) view.findViewById(R.id.buttonLearnTotal);
        buttonTestTotal = (Button) view.findViewById(R.id.buttonTestTotal);
        buttonTrainingTotal = (Button) view.findViewById(R.id.buttonTrainingTotal);

        buttonLearnBlue = (Button) view.findViewById(R.id.buttonLearnBlue);
        buttonTestBlue = (Button) view.findViewById(R.id.buttonTestBlue);
        buttonTrainingBlue = (Button) view.findViewById(R.id.buttonTrainingBlue);

        buttonLearnYellow = (Button) view.findViewById(R.id.buttonLearnYellow);
        buttonTestYellow = (Button) view.findViewById(R.id.buttonTestYellow);
        buttonTrainingYellow = (Button) view.findViewById(R.id.buttonTrainingYellow);

        buttonLearnGreen = (Button) view.findViewById(R.id.buttonLearnGreen);
        buttonTestGreen = (Button) view.findViewById(R.id.buttonTestGreen);
        buttonTrainingGreen = (Button) view.findViewById(R.id.buttonTrainingGreen);

        buttonLearnRed = (Button) view.findViewById(R.id.buttonLearnRed);
        buttonTestRed = (Button) view.findViewById(R.id.buttonTestRed);
        buttonTrainingRed = (Button) view.findViewById(R.id.buttonTrainingRed);


        buttonTrainingBlue.setOnLongClickListener(blueButtonLongClickListener);
        buttonTrainingYellow.setOnLongClickListener(yellowButtonLongClickListener);
        buttonTrainingGreen.setOnLongClickListener(greenButtonLongClickListener);
        buttonTrainingRed.setOnLongClickListener(redButtonLongClickListener);


        buttonTrainingTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonSpeak.getVisibility() == View.GONE){
                    buttonSpeak.setVisibility(View.VISIBLE);
                }else {
                    buttonSpeak.setVisibility(View.GONE);
                }
                hideAllButtonsExcept(buttonLearnTotal);
                hideAndShowButtons(buttonLearnTotal,buttonTestTotal);

            }
        });



        buttonTrainingBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSpeak.setVisibility(View.GONE);
                hideAllButtonsExcept(buttonLearnBlue);
                hideAndShowButtons(buttonLearnBlue,buttonTestBlue);
            }
        });

        buttonTrainingYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSpeak.setVisibility(View.GONE);
                hideAllButtonsExcept(buttonLearnYellow);
                hideAndShowButtons(buttonLearnYellow,buttonTestYellow);
            }
        });

        buttonTrainingGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSpeak.setVisibility(View.GONE);
                hideAllButtonsExcept(buttonLearnGreen);
                hideAndShowButtons(buttonLearnGreen,buttonTestGreen);
            }
        });

        buttonTrainingRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSpeak.setVisibility(View.GONE);
                hideAllButtonsExcept(buttonLearnRed);
                hideAndShowButtons(buttonLearnRed,buttonTestRed);
            }
        });



        buttonLearnTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLearn(cards.size())){
                    //
                    ValidateCheckForReminder.isTriggerFromLearnTotalButton = true;
                    //
                    Intent intent = new Intent(getContext(), LearnActivity.class);
                    // put extra
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, "Total");
                    startActivity(intent);
                }
            }
        });

        buttonTestTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTest(cards.size())){
                    //
                    ValidateCheckForReminder.isTriggerFromTestTotalButton = true;
                    //
                    Intent intent = new Intent(getContext(), SurveyActivity.class);
                    // put extra
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, "Total");
                    startActivity(intent);
                    listener.OnButtonTestClick();
                }
            }
        });

        buttonLearnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLearn(numberOfBlueCards)) {
                    Intent intent = new Intent(getContext(), LearnActivity.class);
                    // put extra
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.BLUE.name());
                    startActivity(intent);
                }
            }
        });

        buttonTestBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTest(numberOfBlueCards)) {
                    Intent intent = new Intent(getContext(), SurveyActivity.class);
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.BLUE.name());
                    startActivity(intent);
                    listener.OnButtonTestClick();
                }
            }
        });

        buttonLearnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLearn(numberOfYellowCards)) {
                    Intent intent = new Intent(getContext(), LearnActivity.class);
                    // put extra
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.YELLOW.name());
                    startActivity(intent);
                }
            }
        });

        buttonTestYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTest(numberOfYellowCards)) {
                    Intent intent = new Intent(getContext(), SurveyActivity.class);
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.YELLOW.name());
                    startActivity(intent);
                    listener.OnButtonTestClick();
                }
            }
        });

        buttonLearnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLearn(numberOfGreenCards)) {
                    Intent intent = new Intent(getContext(), LearnActivity.class);
                    // put extra
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.GREEN.name());
                    startActivity(intent);
                }
            }
        });

        buttonTestGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTest(numberOfGreenCards)) {
                    Intent intent = new Intent(getContext(), SurveyActivity.class);
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.GREEN.name());
                    startActivity(intent);
                    listener.OnButtonTestClick();
                }
            }
        });

        buttonLearnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLearn(numberOfRedCards)) {
                    Intent intent = new Intent(getContext(), LearnActivity.class);
                    // put extra
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.RED.name());
                    startActivity(intent);
                }
            }
        });

        buttonTestRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTest(numberOfRedCards)) {
                    Intent intent = new Intent(getContext(), SurveyActivity.class);
                    intent.putExtra(ConstantVariable.DECK_ID, deckId);
                    intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                    intent.putExtra(ConstantVariable.CARD_COLOR, CardColor.RED.name());
                    startActivity(intent);
                    listener.OnButtonTestClick();
                }
            }
        });



//        buttonSpeak.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                    String speakTextTxt                  = "Good morning,have a nice day";
////                    HashMap<String, String> myHashRender = new HashMap<String, String>();
////                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speakTextTxt);
////                    String exStoragePath                = Environment.getExternalStorageDirectory().getAbsolutePath();
////                    File appTmpPath                     = new File(exStoragePath + "/sounds/");
////                    appTmpPath.mkdirs();
////                    String tempFilename                 = "tmpaudio.mp3";
////                    String tempDestFile                 = appTmpPath.getAbsolutePath() + "/" + tempFilename;
//                    //textToSpeech.synthesizeToFile(speakTextTxt, myHashRender, tempDestFile);
////                    Toast.makeText(getContext(), "Long Click",Toast.LENGTH_SHORT).show();
//
//                String state = Environment.getExternalStorageState();
//                boolean mExternalStorageWriteable = false;
//                boolean mExternalStorageAvailable = false;
//                if (Environment.MEDIA_MOUNTED.equals(state)) {
//                    // Can read and write the media
//                    mExternalStorageAvailable = mExternalStorageWriteable = true;
//
//                } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//                    // Can only read the media
//                    mExternalStorageAvailable = true;
//                    mExternalStorageWriteable = false;
//                } else {
//                    // Can't read or write
//                    mExternalStorageAvailable = mExternalStorageWriteable = false;
//                }
//                File root = android.os.Environment.getExternalStorageDirectory();
//                File dir = new File(root.getAbsolutePath() + "/download");
//                dir.mkdirs();
//                File file = new File(dir, "myData.mp3");
//                int test = textToSpeech.synthesizeToFile((CharSequence) speakTextTxt, null, file,
//                        "tts");
//                Toast.makeText(getContext(), "Long Click",Toast.LENGTH_SHORT).show();
//
//                return true;
//            }
//        });

        return view;
    }

    private String generateTextFromVocabularyCards(List<Card> cards) {
        String res = "";
        for(Card card:cards){
            res += card.getVocabulary() + "...\n";
        }
        return res;
    }

    private void loadData(){
        String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
        FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails").child(deckId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cards.clear();
                cardsBlue.clear();
                cardsYellow.clear();
                cardsGreen.clear();
                cardsRed.clear();
                numberOfBlueCards=0;
                numberOfYellowCards=0;
                numberOfGreenCards=0;
                numberOfRedCards=0;

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Card card = postSnapshot.getValue(Card.class);
                    cards.add(card);
                }
                for(int i = 0; i<cards.size();i++){
                    if(cards.get(i).getCardStatus().equals(CardColor.BLUE.name())){
                        numberOfBlueCards++;
                        cardsBlue.add(cards.get(i));
                    }else if(cards.get(i).getCardStatus().equals(CardColor.YELLOW.name())){
                        numberOfYellowCards++;
                        cardsYellow.add(cards.get(i));
                    }else if(cards.get(i).getCardStatus().equals(CardColor.GREEN.name())){
                        numberOfGreenCards++;
                        cardsGreen.add(cards.get(i));
                    }else if(cards.get(i).getCardStatus().equals(CardColor.RED.name())){
                        numberOfRedCards++;
                        cardsRed.add(cards.get(i));
                    }
                }
                int total = cards.size();
                buttonTrainingTotal.setText("Total: " + total + " card(s) for 100%");
                int percentageOfBlue = (int)Math.round((float)numberOfBlueCards*100/total);
                buttonTrainingBlue.setText(numberOfBlueCards + " card(s) for " + percentageOfBlue + "%");
                int percentageOfYellow = (int)Math.round((float)numberOfYellowCards*100/total);
                buttonTrainingYellow.setText(numberOfYellowCards + " card(s) for " + percentageOfYellow + "%");
                int percentageOfRed = (int)Math.round((float)numberOfRedCards*100/total);
                buttonTrainingRed.setText(numberOfRedCards + " card(s) for " + percentageOfRed + "%");
                int percentageOfGreen = 100 - percentageOfBlue - percentageOfYellow - percentageOfRed;
                buttonTrainingGreen.setText(numberOfGreenCards + " card(s) for " + percentageOfGreen + "%");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        // finish activity to return to Reminders Activity
        if(ValidateCheckForReminder.isFinishLearn || ValidateCheckForReminder.isFinishTest){
            ValidateCheckForReminder.setDefault();
            getActivity().finish();
        }
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //buttonSpeak.setEnabled(status == TextToSpeech.SUCCESS);
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.80f);
                    textToSpeech.setPitch(1.050f);
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            textViewShowSpeaking.setText(utteranceId);
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            textViewShowSpeaking.setText("");
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                }
            }
        },"com.google.android.tts");
        loadData();
    }
    @Override
    public void onPause(){
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    public interface OnButtonTestClickListener {
        void OnButtonTestClick();
    }

    private void hideAndShowButtons(Button buttonLearn, Button buttonTest){
        if(buttonLearn.getVisibility() == View.GONE){
            buttonLearn.setVisibility(View.VISIBLE);
            buttonTest.setVisibility(View.VISIBLE);
        }
        else {
            buttonLearn.setVisibility(View.GONE);
            buttonTest.setVisibility(View.GONE);
        }
    }

    private void hideAllButtonsExcept(Button button){
        if(button == buttonLearnTotal){
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnBlue){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnYellow){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnGreen){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnRed.setVisibility(View.GONE);
            buttonTestRed.setVisibility(View.GONE);
        }
        if(button == buttonLearnRed){
            buttonLearnTotal.setVisibility(View.GONE);
            buttonTestTotal.setVisibility(View.GONE);
            buttonLearnBlue.setVisibility(View.GONE);
            buttonTestBlue.setVisibility(View.GONE);
            buttonLearnYellow.setVisibility(View.GONE);
            buttonTestYellow.setVisibility(View.GONE);
            buttonLearnGreen.setVisibility(View.GONE);
            buttonTestGreen.setVisibility(View.GONE);
        }
    }

    private boolean checkTest(int quantity){
        if(quantity < 4){
            Toast.makeText(getContext(), "Must have 4 cards to submit", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkLearn(int quantity){
        if(quantity < 1){
            Toast.makeText(getContext(), "Must have at least 1 cards to submit", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    View.OnLongClickListener speakButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showRepeatSpeakerDialog(cards);
            return true;
        }
    };

    View.OnLongClickListener blueButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showRepeatSpeakerDialog(cardsBlue);
            return true;
        }
    };
    View.OnLongClickListener yellowButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showRepeatSpeakerDialog(cardsYellow);
            return true;
        }
    };
    View.OnLongClickListener greenButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showRepeatSpeakerDialog(cardsGreen);
            return true;
        }
    };
    View.OnLongClickListener redButtonLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showRepeatSpeakerDialog(cardsRed);
            return true;
        }
    };

    private void showRepeatSpeakerDialog(final List<Card> _cards) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.numberoftimes_repeat_speaker_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etRepetitionSpeaker = (EditText)dialogView.findViewById(R.id.etRepetitionSpeaker);
        final CheckBox cbShuffleCards = (CheckBox)dialogView.findViewById(R.id.cbShuffleCards);
        final Button buttonOk = (Button)dialogView.findViewById(R.id.buttonOkRepeat);
        final Button buttonCancel = (Button)dialogView.findViewById(R.id.buttonCancelRepeat);

        dialogBuilder.setTitle("Repetition");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(textToSpeech.isSpeaking()){
                    textToSpeech.stop();
                    textViewShowSpeaking.setText("");
                }
                if(cbShuffleCards.isChecked()){
                    Collections.shuffle(_cards);
                }
                String input = etRepetitionSpeaker.getText().toString().trim();
                if(!TextUtils.isEmpty(input)){
                    int times = Integer.parseInt(input);
                    if(times<1){
                        return;
                    }
                    b.dismiss();
                    for(int j=1;j<=times;j++){
                        int size = _cards.size();
                        for(int i = 0;i<size;i++){
                            int count = i + 1;
                            String toSpeak = _cards.get(i).getVocabulary();
                            String showText = "" + count + "/" + size + " - (" + j + "|" +times + ")" + "\n" + "<[ " + toSpeak + " ]>";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak(toSpeak + "...\n", TextToSpeech.QUEUE_ADD, null,showText);
                            } else {
                                textToSpeech.speak(toSpeak + "...\n", TextToSpeech.QUEUE_ADD, null);
                            }
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
