package com.example.flashcard.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.example.flashcard.adapters.ReminderListAdapter;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.ListItem;
import com.example.flashcard.models.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MyDecksFragment extends Fragment {
    public interface OnClickDeckItemListener {
        void handleGoToDeckDetail(String name, String id);
    }

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
    //
    int countForReminder = 0;
    private String dateCurrent = "";
    private List<String> listDeckIdHaveReminder;
    int lastIndex;
    int lastTop;
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
        listDeckIdHaveReminder = new ArrayList<>();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final Calendar calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        dateCurrent = df.format(d);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateDeckDialog();
            }
        });

//        listViewDecks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Deck deck = decks.get(position);
//
//                Intent intent = new Intent(getContext(), DeckDetailActivity.class);
//                // put data to intent
//                intent.putExtra(ConstantVariable.DECK_NAME, deck.getDeckName());
//                intent.putExtra(ConstantVariable.DECK_ID, deck.getDeckId());
//                intent.putExtra(ConstantVariable.USER_ID, userId);
//                startActivity(intent);
//            }
//        });

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
        if(searchViewDeck.getQuery().length() > 0){
            return;
        }
        valueEventListenerDeck = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                decks.clear();
                listDeckIdHaveReminder.clear();
                count = 0;
                countForReminder = 0;
                totalCardOfDeckID.clear();
                numberOfRedCard.clear();
                // iterating through all the nodes
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Deck deck = postSnapshot.getValue(Deck.class);
                    Log.i(ConstantVariable.TAG_DECK_LIST, deck.getDeckName());
                    decks.add(deck);
                }
                for(int i=0;i<decks.size();i++){
                    databaseDecks.child("reminders")
                            .child(userId).child(decks.get(i).getDeckId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            countForReminder++;
                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                Reminder reminder = postSnapshot.getValue(Reminder.class);
                                if(reminder.getDate().equals(dateCurrent)){
                                    if(!listDeckIdHaveReminder.contains(reminder.getDeckId())){
                                        listDeckIdHaveReminder.add(reminder.getDeckId());
                                    }
                                }
                            }
                            if(countForReminder == decks.size()){
                                for(int i=0;i<decks.size();i++){
                                    databaseDecks.child("deckdetails").child(decks.get(i).getDeckId())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    count++;
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
                                                                ListItem item = new ListItem(decks.get(i).getDeckId(),decks.get(i).getDeckName()
                                                                        ,totalCardOfDeckID.get(i),numberOfRedCard.get(i));
                                                                listDeck.add(item);
                                                            }
                                                            deckAdapter = new DeckList(getActivity(), listDeck, listDeckIdHaveReminder, new OnClickDeckItemListener() {
                                                                @Override
                                                                public void handleGoToDeckDetail(String name, String id) {
                                                                    //
                                                                    lastIndex = listViewDecks.getFirstVisiblePosition();
                                                                    View v = listViewDecks.getChildAt(0);
                                                                    lastTop = (v == null) ? 0 : (v.getTop() - listViewDecks.getPaddingTop());
                                                                    //
                                                                    Intent intent = new Intent(getContext(), DeckDetailActivity.class);
                                                                    // put data to intent
                                                                    intent.putExtra(ConstantVariable.DECK_NAME, name);
                                                                    intent.putExtra(ConstantVariable.DECK_ID, id);
                                                                    intent.putExtra(ConstantVariable.USER_ID, userId);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                            listViewDecks.setAdapter(null);
                                                            listViewDecks.setAdapter(deckAdapter);
                                                            setupSearchViewDeck();
                                                            listViewDecks.setSelectionFromTop(lastIndex, lastTop);
                                                            listViewDecks.requestFocus();
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
                                //
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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
                    // push new item to last position
                    lastIndex = decks.size();
                    searchViewDeck.setQuery("", true);

                    //
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
