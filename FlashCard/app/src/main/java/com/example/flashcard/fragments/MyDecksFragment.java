package com.example.flashcard.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.flashcard.DeckDetailActivity;
import com.example.flashcard.ManageFlashcardsActivity;
import com.example.flashcard.R;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.adapters.DeckList;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.ListItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyDecksFragment extends Fragment {
    int count = 0;
    List<Integer> totalCardOfDeckID = new ArrayList<>();
    List<Integer> numberOfRedCard = new ArrayList<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    ListView listViewDecks;
    List<Deck> decks;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseDecks = database.getReference("DBFlashCard");

    //
    private SearchView searchViewDeck;
    private DeckList deckAdapter;
    //

    ValueEventListener valueEventListenerDeck;
    ValueEventListener valueEventListenerDeckDetail;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_decks, null);
        searchViewDeck = rootView.findViewById(R.id.searchViewDeck);
        listViewDecks = rootView.findViewById(R.id.lvDecks);
        // initial
        decks = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDeckDialog();
            }
        });

        listViewDecks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Deck deck = decks.get(position);

                Intent intent = new Intent(getContext(), DeckDetailActivity.class);
                // put data to intent
                intent.putExtra(ConstantVariable.DECK_NAME, deck.getDeckName());
                intent.putExtra(ConstantVariable.DECK_ID, deck.getDeckId());
                intent.putExtra(ConstantVariable.USER_ID, userId);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(valueEventListenerDeck != null) {
            databaseDecks.child("decks").child(userId).removeEventListener(valueEventListenerDeck);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        valueEventListenerDeck = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                decks.clear();
                count = 0;
                totalCardOfDeckID.clear();
                numberOfRedCard.clear();
                // iterating through all the nodes
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Deck deck = postSnapshot.getValue(Deck.class);
                    Log.i(ConstantVariable.TAG_DECK_LIST, deck.getDeckName());
                    decks.add(deck);
                }
                for(int i=0;i<decks.size();i++){
                    Log.i(ConstantVariable.TAG_DECK_LIST, ""+ decks.size());
                    databaseDecks.child("deckdetails").child(decks.get(i).getDeckId())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    count++;
                                    Log.i(ConstantVariable.TAG_DECK_LIST, ""+ count);
                                    Log.i(ConstantVariable.TAG_DECK_LIST, "dataSnapshot == " + dataSnapshot.getChildren().toString());
                                    int countTotal = 0;
                                    int countRed = 0;

                                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                        Card card = postSnapshot.getValue(Card.class);
                                        if(card.getCardStatus().equals(CardColor.RED.name())){
                                            countRed++;
                                        }
                                        countTotal++;

                                    }
                                    Log.i(ConstantVariable.TAG_DECK_LIST, "countTotal = " + countTotal + "--countRed = " + countRed);
                                    totalCardOfDeckID.add(countTotal);
                                    numberOfRedCard.add(countRed);
                                    if(count == decks.size()){
                                        if(getActivity()!=null){
                                            List<ListItem> listDeck = new ArrayList<>();
                                            for(int i=0;i<decks.size();i++){
                                                String s = "" + i + "--" + decks.get(i).getDeckName() + " - " + totalCardOfDeckID.get(i) + " - " + numberOfRedCard.get(i);
                                                Log.i(ConstantVariable.TAG_DECK_LIST, s);
                                                ListItem item = new ListItem(decks.get(i).getDeckId(),decks.get(i).getDeckName()
                                                        ,totalCardOfDeckID.get(i),numberOfRedCard.get(i));
                                                listDeck.add(item);
                                            }
                                            deckAdapter = new DeckList(getActivity(), listDeck);
                                            listViewDecks.setAdapter(null);
                                            listViewDecks.setAdapter(deckAdapter);
                                            setupSearchViewDeck();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                if(decks.size() == 0){
                    listViewDecks.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseDecks.child("decks").child(userId).addValueEventListener(valueEventListenerDeck);
    }

    private void showCreateDeckDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_deck_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelDeck);
        final Button buttonOk = (Button) dialogView.findViewById(R.id.buttonOkDeck);

        dialogBuilder.setTitle("Create new deck");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    // create a unique id as the PK for Artist
                    String id = databaseDecks.child("decks").child(userId).push().getKey();
                    Deck deck = new Deck(id,name);
                    // save to db
                    databaseDecks.child("decks").child(userId).child(id).setValue(deck);
                    // set blank for name
                    editTextName.setText("");

                    // notify success
                    b.dismiss();
                    Toast.makeText(getContext(), "Deck added", Toast.LENGTH_LONG).show();
                } else {
                    //updateArtist(artistId, artistName, genre);
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

    private void setupSearchViewDeck(){
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchViewDeck.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchViewDeck.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchViewDeck.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                deckAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                deckAdapter.getFilter().filter(query);
                return false;
            }
        });
    }
}
