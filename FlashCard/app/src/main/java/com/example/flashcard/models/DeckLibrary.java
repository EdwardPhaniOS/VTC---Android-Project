package com.example.flashcard.models;

import java.io.Serializable;

public class DeckLibrary implements Serializable {

    private String deckName;
    private String deckType;

    private boolean isDownloaded;

    public DeckLibrary(String deckName, String deckType)  {
        this.deckName = deckName;
        this.deckType = deckType;
        this.isDownloaded= true;
    }

    public DeckLibrary(String deckName, String deckType, boolean active)  {
        this.deckName = deckName;
        this.deckType = deckType;
        this.isDownloaded= active;
    }

    public String getDeckType() {
        return deckType;
    }

    public void setDeckType(String deckType) {
        this.deckType = deckType;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(boolean active) {
        this.isDownloaded = active;
    }

    @Override
    public String toString() {
        return this.deckName +" ("+ this.deckType +")";
    }

}