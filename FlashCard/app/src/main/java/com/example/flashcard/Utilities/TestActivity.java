package com.example.flashcard.Utilities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.flashcard.LearnActivity;
import com.example.flashcard.R;
import com.example.flashcard.adapters.QuestionTestFragmentAdapter;
import com.example.flashcard.fragments.TestQuestionFinishFragment;
import com.example.flashcard.fragments.TestQuestionFragment;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Reminder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();
    public List<Card> cards;
    private ViewPager viewpagerTest;
    private QuestionTestFragmentAdapter mAdapter;

    private Button buttonSubmitTest;
    private TextView textViewProgressTest;

    ProgressDialog mProgressDialog;
    DatabaseReference databaseReference;

    private int sizeOfCards;
    private boolean lastQuestion = false;
    private String deckID;
    private int numberOfCards;
    private int time;
    long timer = 60000; // 60 secs

    private List<String> result_question = new ArrayList<>();
    private List<String> result_answer_right = new ArrayList<>();
    private List<String> result_answer_wrong = new ArrayList<>();
    private List<String> result_color = new ArrayList<>();

    private Toolbar toolbarTest;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // use offline
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //

        setupToolbar();

        cards = new ArrayList<>();

        deckID = getIntent().getStringExtra(ConstantVariable.DECK_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference("DBFlashCard/deckdetails").child(deckID);

        numberOfCards = getIntent().getIntExtra(ConstantVariable.INPUT_CARDS,0);
        time = getIntent().getIntExtra(ConstantVariable.INPUT_TIME,0);
        timer = timer * time;

        buttonSubmitTest = findViewById(R.id.buttonSubmitTest);
        buttonSubmitTest.setOnClickListener(submitButtonListener);

        textViewProgressTest = findViewById(R.id.textViewProgressTest);

        viewpagerTest = findViewById(R.id.viewpagerTest);
        viewpagerTest.setOnTouchListener(disableSwipeViewPager);

        getCardOfDeckId(deckID);
    }

    private void getCardOfDeckId(String deckId) {
        DataAll.CardsOfDeckID_AsyncTask(deckId,new OnGetDataListener() {
            @Override
            public void onStart() {
                //DO SOME THING WHEN START GET DATA HERE
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(TestActivity.this);
                    mProgressDialog.setTitle("Wait ... ");
                    mProgressDialog.setMessage("Loading cards");
                    mProgressDialog.setIndeterminate(true);
                }
                mProgressDialog.show();
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                //DO SOME THING WHEN GET DATA SUCCESS HERE
                List<Card> getAllCards = new ArrayList<Card>();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Card card = postSnapshot.getValue(Card.class);
                    getAllCards.add(card);
                }
                List<Card> cardsAfterFilter = filterCardByColor(getAllCards
                                                                ,getIntent().getStringExtra(ConstantVariable.CARD_COLOR));
                Collections.shuffle(cardsAfterFilter);

                cards = new ArrayList<Card>(cardsAfterFilter.subList(0, numberOfCards));

                mAdapter = new QuestionTestFragmentAdapter(getSupportFragmentManager(),cards);
                sizeOfCards = cards.size();
                textViewProgressTest.setText("1/" + sizeOfCards);
                viewpagerTest.setAdapter(mAdapter);

                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });

    }

    View.OnClickListener submitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(lastQuestion){
                return;
            }
            //
            if(!checkAnswer())
                return;

            if(getItem(+1) == sizeOfCards){
                if(ValidateCheckForReminder.isTriggerFromTestTotalButton && ValidateCheckForReminder.reminderSave!=null){
                    ValidateCheckForReminder.isFinishTest = true;
                }
                lastQuestion = true;

                buttonSubmitTest.setVisibility(View.GONE);
                textViewProgressTest.setVisibility(View.GONE);
                viewpagerTest.setVisibility(View.GONE);
                addTestFinishFragment(result_question,result_answer_right,result_answer_wrong,result_color,userId);
            }

            // go to next page
            viewpagerTest.setCurrentItem(getItem(+1), true);
            // set progress
            textViewProgressTest.setText(getItem(+1) + "/" + sizeOfCards);
            //


        }
    };

    private int getItem(int i) {
        return viewpagerTest.getCurrentItem() + i;
    }

    View.OnTouchListener disableSwipeViewPager = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    private boolean checkAnswer(){
        Fragment fragment = (Fragment) mAdapter.instantiateItem(viewpagerTest, viewpagerTest.getCurrentItem());

        if (fragment != null && fragment instanceof TestQuestionFragment) {
            List<String> result = ((TestQuestionFragment) fragment).checkAnswer(deckID);
            if(result.size() > 0){
                result_question.add(result.get(0));
                result_answer_right.add(result.get(1));
                result_color.add(result.get(2));
                result_answer_wrong.add(result.get(3));
                return true;
            }else {
                Toast.makeText(this, "Please choose your answer", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu_countdown, menu);
//
//        final MenuItem counter = menu.findItem(R.id.countdownTimer);
        if(time > 0){
            countDownTimer = new CountDownTimer(timer, 1000) {

                public void onTick(long millisUntilFinished) {
                    long millis = millisUntilFinished;
//                String  hms =  (TimeUnit.MILLISECONDS.toHours(millis))+":"
//                        +(TimeUnit.MILLISECONDS.toMinutes(millis) -TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))+":"
//                        + (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));


                    long m = TimeUnit.MILLISECONDS.toMinutes(millis);
                    long s = (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));



                    String m_String = m < 10 ? "0" + m : String.valueOf(m);
                    String s_String = s < 10 ? "0" + s : String.valueOf(s);

                    String ms_String = m_String + ":" + s_String;


                    //counter.setTitle(ms_String);
                    setTitle(ms_String);
                    timer = millis;

                }

                public void onFinish() {
                    //counter.setTitle("00:00");
                    setTitle("00:00");
                    buttonSubmitTest.setVisibility(View.GONE);
                    textViewProgressTest.setVisibility(View.GONE);
                    viewpagerTest.setVisibility(View.GONE);

                    if(result_question.size() < sizeOfCards){
                        for(int i=result_question.size();i<sizeOfCards;i++){
                            result_question.add(cards.get(i).getVocabulary());
                            result_answer_right.add(cards.get(i).getDefinition());
                            result_answer_wrong.add(ConstantVariable.UNSUBMIITED_QUESTION);
                            result_color.add(cards.get(i).getCardStatus());
                        }
                    }
                    if(ValidateCheckForReminder.isTriggerFromTestTotalButton && ValidateCheckForReminder.reminderSave!=null){
                        ValidateCheckForReminder.isFinishTest = true;
                    }
                    addTestFinishFragment(result_question,result_answer_right,result_answer_wrong,result_color,userId);
                }
            }.start();
        }
        else {
            setTitle("No time limit");
        }
        return  true;

    }

    private List<Card> filterCardByColor(List<Card> list,String color){
        if("Total".equals(color))
            return list;

        List<Card> res = new ArrayList<>();
        for(Card c : list){
            if(c.getCardStatus().equals(color))
                res.add(c);
        }
        return res;
    }

    private void setupToolbar(){
        toolbarTest = (Toolbar)findViewById(R.id.toolbarTest);
        setSupportActionBar(toolbarTest);

        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbarTest.setNavigationOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(TestActivity.this, "The reminder of "
                            + reminderChecked.getName() + " is done", Toast.LENGTH_LONG).show();
                    //ValidateCheckForReminder.setDefault();
                }else {
                    if(ValidateCheckForReminder.reminderSave == null){
                        ValidateCheckForReminder.setDefault();
                    }else {
                        ValidateCheckForReminder.isTriggerFromTestTotalButton = false;
                    }
                }
                //
                finish();
                //onBackPressed();
            }
        });
    }

    private void addTestFinishFragment(List<String> __result_question
                                        ,List<String> __result_answer_right
                                        ,List<String> __result_answer_wrong
                                        ,List<String> __result_color
                                        ,String _userId){
        // Create new fragment and transaction
        TestQuestionFinishFragment newFragment = new TestQuestionFinishFragment(__result_question,__result_answer_right
                                                                                ,__result_answer_wrong,__result_color, _userId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.replace_frame, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        //transaction.commit();
        transaction.commitAllowingStateLoss();
        if(countDownTimer != null){
            countDownTimer.cancel();

        }
//        String s = "";
//
//        for(int i = 0 ; i< result_question.size();i++){
//            s += result_question.get(i).toString() + "\n";
//        }
//
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        // https://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        super.onSaveInstanceState(outState);
    }

    public Toolbar getToolbarTest(){
        return this.toolbarTest;
    }
}
