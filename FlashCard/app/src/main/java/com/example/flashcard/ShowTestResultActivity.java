package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.Utilities.TestActivity;
import com.example.flashcard.Utilities.ValidateCheckForReminder;
import com.example.flashcard.adapters.SectionsPagerAdapterForResultPage;
import com.example.flashcard.models.QuizResult;
import com.example.flashcard.models.Reminder;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ShowTestResultActivity extends AppCompatActivity
{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();

    private List<String> result_question = new ArrayList<String>();
    private List<String> result_answer_right = new ArrayList<String>();
    private List<String> result_user_answer = new ArrayList<String>();
    private List<String> result_color = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_test_result);

        ImageButton cancelButton = findViewById(R.id.cancel_button_in_result_page);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // process for validate check reminder
                if(ValidateCheckForReminder.isFinishTest){
                    Reminder reminderChecked = new Reminder(ValidateCheckForReminder.reminderSave.getReminderId()
                            ,ValidateCheckForReminder.reminderSave.getName()
                            ,ValidateCheckForReminder.reminderSave.getNameDay()
                            ,ValidateCheckForReminder.reminderSave.getDate()
                            ,ValidateCheckForReminder.reminderSave.getDeckId());
                    reminderChecked.setIsActivated("true");
                    FirebaseDatabase.getInstance().getReference("DBFlashCard/reminders").child(userId)
                            .child(ValidateCheckForReminder.reminderSave.getDeckId())
                            .child(ValidateCheckForReminder.reminderSave.getReminderId())
                            .setValue(reminderChecked);
                    Toast.makeText(ShowTestResultActivity.this, "The reminder of "
                            + reminderChecked.getName() + " is done", Toast.LENGTH_LONG).show();
                    //ValidateCheckForReminder.setDefault();
                }else {
                    if(ValidateCheckForReminder.reminderSave == null){
                        ValidateCheckForReminder.setDefault();
                    }else {
                        ValidateCheckForReminder.isTriggerFromTestTotalButton = false;
                    }
                }
                finish();
            }
        });

        result_question = getIntent()
                .getStringArrayListExtra(ConstantVariable.RESULT_QUESTION_FOR_SHOWTESTRESULT);
        result_answer_right = getIntent()
                .getStringArrayListExtra(ConstantVariable.RESULT_ANSWER_RIGHT_FOR_SHOWTESTRESULT);
        result_user_answer = getIntent()
                .getStringArrayListExtra(ConstantVariable.RESULT_ANSWER_WRONG_FOR_SHOWTESTRESULT);
        result_color = getIntent()
                .getStringArrayListExtra(ConstantVariable.RESULT_COLOR_FOR_SHOWTESTRESULT);

        QuizResult quizResult = new QuizResult(result_question,
                result_answer_right, result_user_answer, result_color);

        SectionsPagerAdapterForResultPage sectionsPagerAdapterForResultPage =
                new SectionsPagerAdapterForResultPage(this,
                        getSupportFragmentManager(), quizResult);

        ViewPager viewPager = findViewById(R.id.view_pager_result);
        viewPager.setAdapter(sectionsPagerAdapterForResultPage);
        TabLayout tabs = findViewById(R.id.tabsQuizResult);
        tabs.setupWithViewPager(viewPager);
    }
}
