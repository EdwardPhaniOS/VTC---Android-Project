package com.example.flashcard.models;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Deck {
    private String deckId;
    private String deckName;

    public Deck(){

    }

    public Deck(String deckId, String deckName) {
        this.deckId = deckId;
        this.deckName = deckName;
    }

    public String getDeckId() {
        return deckId;
    }

    public String getDeckName() {
        return deckName;
    }
}
