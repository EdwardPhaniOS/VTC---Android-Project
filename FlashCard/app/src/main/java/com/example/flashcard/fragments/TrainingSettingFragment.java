package com.example.flashcard.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

import com.example.flashcard.ManageFlashcardsActivity;
import com.example.flashcard.R;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.Reminder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingSettingFragment extends Fragment {
    private TextView textViewAddEditFlashcards;
    private TextView textViewRenameDeck;
    private TextView textViewDeleteDeck;
    private TextView tvAddListCards;
    private Switch switchButton;

    private boolean statusSwitch = false;
    public TrainingSettingFragment(boolean statusSwitchOfSettingFragment) {
        // Required empty public constructor
        this.statusSwitch = statusSwitchOfSettingFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_setting, container, false);
        // use offline
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //
        textViewAddEditFlashcards = (TextView) view.findViewById(R.id.tvAddEdit);
        textViewRenameDeck = (TextView) view.findViewById(R.id.tvRenameDeck);
        textViewDeleteDeck = (TextView) view.findViewById(R.id.tvDelete);
        tvAddListCards = (TextView) view.findViewById(R.id.tvAddListCards);
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
        textViewDeleteDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete confirmation")
                        .setMessage("Do you really want to delete" + getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME) +" deck?")
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
                    addMultipleCardToFirebase(text,deckId);
                    Toast.makeText(getContext(), "List card is added.\nCheck your flashcard tab.", Toast.LENGTH_LONG).show();
                    b.dismiss();
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
                String _id = dbAddCard.push().getKey();
                Card card = new Card(_id,sep[0].trim(),sep[0].trim());
                dbAddCard.child(_id).setValue(card);
            }
        }
    }

}
