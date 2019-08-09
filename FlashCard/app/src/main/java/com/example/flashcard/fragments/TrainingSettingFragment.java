package com.example.flashcard.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.ManageFlashcardsActivity;
import com.example.flashcard.R;
import com.example.flashcard.models.Deck;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingSettingFragment extends Fragment {
    private TextView textViewAddEditFlashcards;
    private TextView textViewRenameDeck;
    private TextView textViewDeleteDeck;

    public TrainingSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training_setting, container, false);
        textViewAddEditFlashcards = (TextView) view.findViewById(R.id.tvAddEdit);
        textViewRenameDeck = (TextView) view.findViewById(R.id.tvRenameDeck);
        textViewDeleteDeck = (TextView) view.findViewById(R.id.tvDelete);

        textViewAddEditFlashcards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ManageFlashcardsActivity.class);
                String name = getActivity().getIntent().getStringExtra(MyDecksFragment.DECK_NAME);
                String id = getActivity().getIntent().getStringExtra(MyDecksFragment.DECK_ID);
                intent.putExtra(MyDecksFragment.DECK_NAME, name);
                intent.putExtra(MyDecksFragment.DECK_ID, id);
                startActivity(intent);
            }
        });
        textViewRenameDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDeckNameDialog(getActivity().getIntent().getStringExtra(MyDecksFragment.DECK_ID)
                                        ,getActivity().getIntent().getStringExtra(MyDecksFragment.DECK_NAME)
                                        ,getActivity().getIntent().getStringExtra(MyDecksFragment.USER_ID));
            }
        });
        textViewDeleteDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete confirmation")
                        .setMessage("Do you really want to delete" + getActivity().getIntent().getStringExtra(MyDecksFragment.DECK_NAME) +" deck?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteDeck(getActivity().getIntent().getStringExtra(MyDecksFragment.DECK_ID)
                                            ,getActivity().getIntent().getStringExtra(MyDecksFragment.USER_ID));

                                // back to my deck list
                                // TODO: Chưa biết làm khi xóa thì back lại activity chứa fragment
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
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
        drFlashcards.removeValue();
        Toast.makeText(getContext(), "Delete completed", Toast.LENGTH_SHORT).show();
        return true;
    }
}
