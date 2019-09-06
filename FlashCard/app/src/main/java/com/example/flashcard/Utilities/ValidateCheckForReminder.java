package com.example.flashcard.Utilities;

import com.example.flashcard.models.Reminder;

public class ValidateCheckForReminder {
    public static boolean isFinishLearn = false;
    public static boolean isFinishTest = false;

    public static boolean isTriggerFromLearnTotalButton = false;
    public static boolean isTriggerFromTestTotalButton = false;

    public static Reminder reminderSave = null;

    public static void setDefault(){
        isTriggerFromLearnTotalButton = false;
        isTriggerFromTestTotalButton = false;
        isFinishLearn = false;
        isFinishTest = false;
        reminderSave = null;
    }
}
