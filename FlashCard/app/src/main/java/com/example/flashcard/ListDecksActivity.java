package com.example.flashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.flashcard.adapters.DeckList;
import com.example.flashcard.models.Deck;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListDecksActivity extends AppCompatActivity {
    ListView listViewDecks;
    List<Deck> decks;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseDecks = database.getReference("DBFlashCard");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_decks);

        listViewDecks = findViewById(R.id.lvDecks);
        // initial
        decks = new ArrayList<>();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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

                Intent intent = new Intent(getApplicationContext(),DeckDetailActivity.class);
                // put data to intent
                //intent.putExtra(ARTIST_NAME, artist.getArtistName());
                //intent.putExtra(ARTIST_ID, artist.getArtistId());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        databaseDecks.child("decks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                decks.clear();

                // iterating through all the nodes
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Deck deck = postSnapshot.getValue(Deck.class);
                    decks.add(deck);
                }
                DeckList deckAdapter = new DeckList(ListDecksActivity.this, decks);
                listViewDecks.setAdapter(deckAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showCreateDeckDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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
                    String id = databaseDecks.push().getKey();
                    Deck deck = new Deck(id,name);
                    // save to db
                    databaseDecks.child("decks").child(id).setValue(deck);
                    // set blank for name
                    editTextName.setText("");
                    // notify success
                    b.dismiss();
                    Toast.makeText(ListDecksActivity.this, "Deck added", Toast.LENGTH_LONG).show();
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

}
