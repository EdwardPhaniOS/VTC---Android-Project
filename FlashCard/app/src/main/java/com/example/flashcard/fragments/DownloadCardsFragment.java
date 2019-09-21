package com.example.flashcard.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.flashcard.R;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.DeckLibrary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DownloadCardsFragment extends Fragment {

    private DatabaseReference downloadDeckRef;
    private DeckLibrary selectedDeck;

    //fire base
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference rootDatabase = database.getReference("DBFlashCard");
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user != null ? user.getUid() : null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_download_cards, container, false);
        final ListView listView = rootView.findViewById(R.id.listView);
        final Button downloadButton = rootView.findViewById(R.id.download_button);

        downloadDeckRef = FirebaseDatabase.getInstance().getReference()
                .child("DBFlashCard").child("Library").getRef();

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        DeckLibrary commonWords = new DeckLibrary(ConstantVariable.ONE_THOUSAND_COMMON_WORDS,"free");
        DeckLibrary commonPhrases = new DeckLibrary(ConstantVariable.ONE_THOUSAND_COMMON_PHRASES,"free");

        final DeckLibrary[] deckLibraries = new DeckLibrary[]{commonWords, commonPhrases};

        ArrayAdapter<DeckLibrary> arrayAdapter
                = new ArrayAdapter<DeckLibrary>(getActivity(), android.R.layout.simple_list_item_checked , deckLibraries);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.i("Clicked", "onItemClick: " + position);
                selectedDeck = deckLibraries[position];
            }
        });


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: download cards
                if (selectedDeck != null) {

                    Toast.makeText(getContext(),
                            "Downloading",
                            Toast.LENGTH_SHORT).show();

                    final String deckId = selectedDeck.getDeckName();

                    Deck deck = new Deck(deckId, deckId);
                    rootDatabase.child("decks").child(userId).child(deckId).setValue(deck);

                    downloadDeckRef.child(deckId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                Card card = postSnapshot.getValue(Card.class);
                                rootDatabase.child("deckdetails").child(deckId)
                                        .child(card.getCardId()).setValue(card);
                            }

                            Toast.makeText(getContext(),
                                    "Download has completed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //ERROR:
                        }
                    });
                }


            }
        });

        return rootView;
    }
}
