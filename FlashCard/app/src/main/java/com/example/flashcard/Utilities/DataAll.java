package com.example.flashcard.Utilities;

import android.widget.Toast;

import com.example.flashcard.models.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataAll {

    public static void CardsOfDeckID_AsyncTask(String deckId,final OnGetDataListener listener) {
        listener.onStart();
        FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails").child(deckId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    public static void updateColorWhenMakeTest(String deckId,Card card){
        DatabaseReference databaseDeckDetails = FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails")
                .child(deckId);
        databaseDeckDetails.child(card.getCardId()).setValue(card);
    }
}
