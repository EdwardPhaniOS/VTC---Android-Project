package com.example.flashcard.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Card {
    private String cardId;
    private String vocabulary;
    private String definition;

    private String vocabularyUrl;
    private String definitionUrl;

    private String cardStatus;

    public Card() {

    }

    public Card(String cardId, String vocabulary, String definition) {
        this.cardId = cardId;
        this.vocabulary = vocabulary;
        this.definition = definition;
        this.vocabularyUrl = "";
        this.cardStatus = "BLUE"; // default
    }

    public Card(String cardId, String vocabulary, String definition, String vocabularyUrl) {
        this.cardId = cardId;
        this.vocabulary = vocabulary;
        this.definition = definition;
        this.vocabularyUrl = vocabularyUrl;
        this.cardStatus = "BLUE"; // return BLUE when edit
    }

    public String getCardId() {
        return cardId;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public String getDefinition() {
        return definition;
    }

    public String getVocabularyUrl() {
        return vocabularyUrl;
    }

    public String getDefinitionUrl() {
        return definitionUrl;
    }

    public void setVocabularyUrl(String vocabularyUrl) {
        this.vocabularyUrl = vocabularyUrl;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }
}
