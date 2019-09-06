package com.example.flashcard.models;

public class Reminder {
    private String reminderId;
    private String name;
    private String nameDay;
    private String date;
    private String isActivated;
    private String deckId;

    public Reminder(String reminderId, String name,String nameDay, String date,String deckId) {
        this.reminderId = reminderId;
        this.name = name;
        this.nameDay = nameDay;
        this.date = date;
        this.isActivated = "false";
        this.deckId = deckId;
    }

    public Reminder(){}

    public String getNameDay() {
        return nameDay;
    }

    public String getReminderId() {
        return reminderId;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getIsActivated() {
        return isActivated;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setIsActivated(String isActivated) {
        this.isActivated = isActivated;
    }
}
