package com.example.flashcard.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.DeckDetailActivity;
import com.example.flashcard.ManageFlashcardsActivity;
import com.example.flashcard.R;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.Utilities.ValidateCheckForReminder;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.Reminder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingSettingFragment extends Fragment {
    private TextView textViewAddEditFlashcards;
    private TextView textViewRenameDeck;
    private TextView textViewDeleteDeck;
    private TextView tvAddListCards;
    private TextView tvActiveReminders;
    private TextView tvGenerateVocabAudio;
    private Switch switchButton;
    private String dateActivated;
    private List<Card> cards;

    private TextToSpeech textToSpeech;


    private boolean statusSwitch = false;
    public TrainingSettingFragment(boolean statusSwitchOfSettingFragment, String _dateActivated) {
        // Required empty public constructor
        this.statusSwitch = statusSwitchOfSettingFragment;
        this.dateActivated = _dateActivated;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadData();
    }

    private void setupTextToSpeech(){
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //buttonSpeak.setEnabled(status == TextToSpeech.SUCCESS);
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(0.80f);
                    textToSpeech.setPitch(1.050f);
                    textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                        @Override
                        public void onUtteranceCompleted(String utteranceId) {
                            Toast.makeText(getContext(), "Finished", Toast.LENGTH_LONG).show();
                        }
                    });
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Toast.makeText(getContext(), "Generating", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Toast.makeText(getContext(), "Finished", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });
                }
            }
        },"com.google.android.tts");
    }

    private void loadData(){
        String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
        FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails").child(deckId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cards.clear();
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            Card card = postSnapshot.getValue(Card.class);
                            cards.add(card);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_setting, container, false);

        verifyStoragePermissions(getActivity());
        cards = new ArrayList<>();
        textViewAddEditFlashcards = (TextView) view.findViewById(R.id.tvAddEdit);
        textViewRenameDeck = (TextView) view.findViewById(R.id.tvRenameDeck);
        tvActiveReminders = (TextView) view.findViewById(R.id.tvActiveReminders);
        textViewDeleteDeck = (TextView) view.findViewById(R.id.tvDelete);
        tvAddListCards = (TextView) view.findViewById(R.id.tvAddListCards);
        tvGenerateVocabAudio = (TextView) view.findViewById(R.id.tvGenerateVocabAudio);
        switchButton = (Switch) view.findViewById(R.id.switchActiveReminders);
        switchButton.setChecked(statusSwitch);

        textViewAddEditFlashcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ManageFlashcardsActivity.class);
                String name = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME);
                String id = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
                intent.putExtra(ConstantVariable.DECK_NAME, name);
                intent.putExtra(ConstantVariable.DECK_ID, id);
                startActivity(intent);
            }
        });
        textViewRenameDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDeckNameDialog(getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID)
                                        ,getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME)
                                        ,getActivity().getIntent().getStringExtra(ConstantVariable.USER_ID));
            }
        });
        tvActiveReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dateActivated.isEmpty()){
                    Toast.makeText(getContext(), "Activated: " + dateActivated, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(), "Status: Inactivated", Toast.LENGTH_LONG).show();
                }
            }
        });
        textViewDeleteDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete confirmation")
                        .setMessage("Do you really want to delete deck \'" + getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME) +"\' ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteDeck(getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID)
                                            ,getActivity().getIntent().getStringExtra(ConstantVariable.USER_ID));

                                // back to my deck list
                                // TODO: Chưa biết làm khi xóa thì back lại activity chứa fragment

                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        tvAddListCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTextListCardDialog(getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID));
            }
        });
        setupTextToSpeech();
        tvGenerateVocabAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deckName = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME);
                String filename = deckName + ".wav";
                if (Build.VERSION.SDK_INT >= 23) {
                    String speakTextTxt                  = generateTextFromVocabularyCards(cards);
                    String exStoragePath                = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File appTmpPath                     = new File(exStoragePath + "/sounds/");
                    appTmpPath.mkdirs();
                    //File file = new File(appTmpPath,"generate-audio-vocab.wav");
                    File file = new File(appTmpPath,filename);

//                    String tempFilename                 = "tmpaudio.wav";
//                    String tempDestFile                 = appTmpPath.getAbsolutePath() + "/" + tempFilename;
//                    textToSpeech.synthesizeToFile(speakTextTxt, myHashRender, tempDestFile);
                    if(file.exists()){
//                        Toast.makeText(getContext(), "Check path:\n" + file.getPath(),Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
//                                + "/sounds/");
//                        intent.setDataAndType(uri, "audio/x-wav");
//                        startActivity(intent);
                        showConfirmPlayBackgroudDialog(filename);
                    }
                    else {
                        new YourAsyncTask((DeckDetailActivity) getActivity(),speakTextTxt,file).execute();
                        //int test = textToSpeech.synthesizeToFile((CharSequence) speakTextTxt, null, file, file.getAbsolutePath());
                    }

                } else {
                    Toast.makeText(getContext(), "Cannot generate!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(switchButton.isChecked()){
                    showConfirmActiveReminderDialog();

                }
                else {
                    showConfirmInActiveReminderDialog();

                }
            }
        });

        return view;
    }

    private String generateTextFromVocabularyCards(List<Card> cards) {
        String res = "";
        for(Card card:cards){
            res += card.getVocabulary() + "...\n";
        }
        return res;
    }

    private void showUpdateDeckNameDialog(final String deckId,final String deckName, final String userId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.rename_deck_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText)dialogView.findViewById(R.id.etRenameDeck);
        editTextName.setText(deckName);
        final Button buttonOk = (Button)dialogView.findViewById(R.id.buttonOkRenameDeck);
        final Button buttonCancel = (Button)dialogView.findViewById(R.id.buttonCancelRenameDeck);

        dialogBuilder.setTitle("Deck: " + deckName);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                if(!TextUtils.isEmpty(name)){
                    renameDeck(deckId,name,userId);
                    getActivity().setTitle(name);
                    b.dismiss();
                }
                else {
                    editTextName.setError("Cannot empty");
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

    private void showAddTextListCardDialog(final String deckId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_multi_text_list_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etTextListCards = (EditText)dialogView.findViewById(R.id.etTextListCards);
        final Button buttonAddListCards = (Button)dialogView.findViewById(R.id.buttonAddListCards);
        final Button buttonCancelAddListCards = (Button)dialogView.findViewById(R.id.buttonCancelAddListCards);

        dialogBuilder.setTitle("Add multiple cards");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonAddListCards.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String text = etTextListCards.getText().toString().trim();
                if(!TextUtils.isEmpty(text)){
                    try {
                        addMultipleCardToFirebase(text,deckId);
                        Toast.makeText(getContext(), "List card is added.\nCheck your flashcard tab.", Toast.LENGTH_LONG).show();
                        b.dismiss();
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Something is wrong.\nPlease check your input and try again.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    etTextListCards.setError("Cannot empty");
                }
            }
        });

        buttonCancelAddListCards.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    private void showConfirmActiveReminderDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.confirm_active_reminders_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonOKConfirmActiveReminder = (Button)dialogView.findViewById(R.id.buttonOKConfirmActiveReminder);
        final Button buttonCancelConfirmActiveReminder = (Button)dialogView.findViewById(R.id.buttonCancelConfirmActiveReminder);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);
        dialogBuilder.setTitle("Explanation");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOKConfirmActiveReminder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String userId = getActivity().getIntent().getStringExtra(ConstantVariable.USER_ID);
                String deckName = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME);
                String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
                DatabaseReference databaseReminders = FirebaseDatabase.getInstance().getReference("DBFlashCard").child("reminders")
                        .child(userId).child(deckId);

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                // For the 1st day
                for(int i=1;i<=9;i++){
                    Calendar calendar = Calendar.getInstance();
                    Date d = calendar.getTime();
                    String id = databaseReminders.push().getKey();
                    String name = deckName + "  [" + i + "]";
                    String nameDay = "The 1st day";
                    String date = df.format(d);
                    dateActivated = date;
                    Reminder reminder = new Reminder(id,name,nameDay,date,deckId);
                    databaseReminders.child(id).setValue(reminder);
                }
                // For the 2nd day
                for(int i=1;i<=3;i++){
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    Date d = calendar.getTime();
                    String id = databaseReminders.push().getKey();
                    String name = deckName + "  [" + i + "]";
                    String nameDay = "The 2nd day";
                    String date = df.format(d);
                    Reminder reminder = new Reminder(id,name,nameDay,date,deckId);
                    databaseReminders.child(id).setValue(reminder);
                }
                // For the 7th day
                for(int i=1;i<=3;i++){
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_MONTH, 6);
                    Date d = calendar.getTime();
                    String id = databaseReminders.push().getKey();
                    String name = deckName + "  [" + i + "]";
                    String nameDay = "The 7th day";
                    String date = df.format(d);
                    Reminder reminder = new Reminder(id,name,nameDay,date,deckId);
                    databaseReminders.child(id).setValue(reminder);
                }
                // For the 30th day
                for(int i=1;i<=3;i++){
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.add(Calendar.DAY_OF_MONTH, 29);
                    Date d = calendar.getTime();
                    String id = databaseReminders.push().getKey();
                    String name = deckName + "  [" + i + "]";
                    String nameDay = "The 30th day";
                    String date = df.format(d);
                    Reminder reminder = new Reminder(id,name,nameDay,date,deckId);
                    databaseReminders.child(id).setValue(reminder);
                }

                b.dismiss();
                Toast.makeText(getContext(), "The reminder is activated.\nPlease check your reminder tab.", Toast.LENGTH_LONG).show();
            }
        });

        buttonCancelConfirmActiveReminder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switchButton.setChecked(false);
                b.dismiss();
            }
        });
    }
    private void showConfirmInActiveReminderDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.confirm_inactive_reminders_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonOKConfirmInActiveReminder = (Button)dialogView.findViewById(R.id.buttonOKConfirmInActiveReminder);
        final Button buttonCancelConfirmInActiveReminder = (Button)dialogView.findViewById(R.id.buttonCancelConfirmInActiveReminder);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        dialogBuilder.setTitle("Warning!");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOKConfirmInActiveReminder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // remove all reminders
                String userId = getActivity().getIntent().getStringExtra(ConstantVariable.USER_ID);
                String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
                DatabaseReference databaseReminders = FirebaseDatabase.getInstance().getReference("DBFlashCard").child("reminders")
                        .child(userId);
                databaseReminders.child(deckId).removeValue();
                b.dismiss();
                dateActivated = "";
                Toast.makeText(getContext(), "The reminder is inactivated.", Toast.LENGTH_LONG).show();
            }
        });

        buttonCancelConfirmInActiveReminder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switchButton.setChecked(true);
                b.dismiss();
            }
        });
    }

    private void showConfirmPlayBackgroudDialog(final String nameFile) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.confirm_play_background_dialog, null);
        dialogBuilder.setView(dialogView);

        final Button buttonOKConfirmPlayBackground = (Button)dialogView.findViewById(R.id.buttonOKConfirmPlayBackground);
        final Button buttonCancelConfirmPlayBackground = (Button)dialogView.findViewById(R.id.buttonCancelConfirmPlayBackground);
        final TextView tvPathAudioFile = (TextView)dialogView.findViewById(R.id.tvPathAudioFile);
        tvPathAudioFile.setText("Path: /sounds/" + nameFile);
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOKConfirmPlayBackground.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                        + "/sounds/" + nameFile);
                intent.setDataAndType(uri, "audio/x-wav");
                startActivity(intent);
                b.dismiss();
                Toast.makeText(getContext(), "Playing background...", Toast.LENGTH_LONG).show();
            }
        });

        buttonCancelConfirmPlayBackground.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    private boolean renameDeck(String deckId, String deckName, String userId) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("DBFlashCard/decks").child(userId).child(deckId);

        Deck deck = new Deck(deckId,deckName);
        dR.setValue(deck);

        Toast.makeText(getContext(), "Deck name updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteDeck(String id, String userId){
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("DBFlashCard/decks").child(userId).child(id);

        dR.removeValue();

        // must remove flashcards related to deck
        DatabaseReference drFlashcards = FirebaseDatabase.getInstance().getReference("DBFlashCard/deckdetails").child(id);
        if(drFlashcards!=null){
            drFlashcards.removeValue();
        }

        // must remove remiders related to deck
        DatabaseReference drReminders = FirebaseDatabase.getInstance().getReference("DBFlashCard/reminders").child(userId).child(id);
        if(drReminders!=null){
            drReminders.removeValue();
        }
        Toast.makeText(getContext(), "Delete completed", Toast.LENGTH_SHORT).show();
        getActivity().finish();
        return true;
    }

    private void addMultipleCardToFirebase(String input, String deckID){
        DatabaseReference dbAddCard = FirebaseDatabase.getInstance().getReference("DBFlashCard/deckdetails").child(deckID);
        String[] lines = input.split("\n");
        for(int i=0;i<lines.length;i++){
            String[] sep = lines[i].split("=");
            if(sep.length == 2){
                String _id = dbAddCard.push().getKey();
                Card card = new Card(_id,sep[0].trim(),sep[1].trim());
                dbAddCard.child(_id).setValue(card);
            }
            else if(sep.length == 1){
                if(!sep[0].trim().isEmpty()) {
                    String _id = dbAddCard.push().getKey();
                    Card card = new Card(_id, sep[0].trim(), sep[0].trim());
                    dbAddCard.child(_id).setValue(card);
                }
            }
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    private class YourAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String input;
        private File path;

        public YourAsyncTask(DeckDetailActivity activity,String _input, File _path) {
            dialog = new ProgressDialog(activity);
            input = _input;
            path = _path;

        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Generating");
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... args) {
            int test = textToSpeech.synthesizeToFile(input, null, path, "tts");
            // do background work here
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // do UI work here
            if (dialog.isShowing()) {
                dialog.dismiss();
                Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
