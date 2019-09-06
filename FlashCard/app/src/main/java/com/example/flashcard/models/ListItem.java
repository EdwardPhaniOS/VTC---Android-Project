package com.example.flashcard.models;
import java.util.List;

public class ListItem {
    private String deckId;
    private String deckName;
    private int totalCard;
    private int redCard;

    public ListItem(){

    }

    public ListItem(String deckId, String deckName,int totalCard,int redCard) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.totalCard = totalCard;
        this.redCard = redCard;
    }

    public String getDeckId() {
        return deckId;
    }

    public String getDeckName() {
        return deckName;
    }

    public int getTotalCard() {
        return totalCard;
    }

    public int getRedCard() {
        return redCard;
    }
}
