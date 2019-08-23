package com.example.flashcard.Utilities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.LearnActivity;
import com.example.flashcard.R;
import com.example.flashcard.adapters.FlashcardsFragmentAdapter;
import com.example.flashcard.adapters.QuestionTestFragmentAdapter;
import com.example.flashcard.fragments.FlashcardLearnFragment;
import com.example.flashcard.fragments.TestQuestionFragment;
import com.example.flashcard.models.Card;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    public List<Card> cards;
    private ViewPager viewpagerTest;
    private QuestionTestFragmentAdapter mAdapter;

    private Button buttonSubmitTest;
    private TextView textViewProgressTest;

    ProgressDialog mProgressDialog;
    DatabaseReference databaseReference;

    private int sizeOfCards;
    private String deckID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        cards = new ArrayList<>();

        deckID = getIntent().getStringExtra(ConstantVariable.DECK_ID);
        databaseReference = FirebaseDatabase.getInstance().getReference("DBFlashCard/deckdetails").child(deckID);

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
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Card card = postSnapshot.getValue(Card.class);
                    cards.add(card);
                }
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
            //
            if(!checkAnswer())
                return;
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
            if(((TestQuestionFragment) fragment).checkAnswer(deckID).size() > 0){
                return true;
            }else {
                Toast.makeText(this, "Please choose your answer", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }
}
