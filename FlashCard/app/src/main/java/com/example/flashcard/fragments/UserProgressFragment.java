package com.example.flashcard.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.flashcard.R;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserProgressFragment extends Fragment {

    private TextView totalDeck;
    private TextView totalCard;

    private AnyChartView anyChartView;

    private int blueCards = 0;
    private int redCards = 0;
    private int yellowCards = 0;
    private int greenCards = 0;

    private List<Deck> decks;
    private List<Card> cards;
    private ArrayList<String> deckIds = new ArrayList<String>();

    //Fire base
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseDecks = database.getReference("DBFlashCard");
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user != null ? user.getUid() : null;
    private int count = 0;

    private ValueEventListener userDecksListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_user_progress, null);

        anyChartView = rootView.findViewById(R.id.any_chart_view);

        totalDeck = rootView.findViewById(R.id.total_decks_number);
        totalCard = rootView.findViewById(R.id.total_cards_number);

        decks = new ArrayList<>();
        cards = new ArrayList<>();

        //AsyncTask task = new MyTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        showProgress();

        return rootView;
    }
    @Override
    public void onStop() {
        super.onStop();
        if(userDecksListener != null) {
            databaseDecks.child("decks").child(userId).removeEventListener(userDecksListener);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        userDecksListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                decks.clear();
                cards.clear();
                blueCards = 0;
                redCards = 0;
                yellowCards = 0;
                greenCards = 0;
                count = 0;
                // iterating through all the nodes
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Deck deck = postSnapshot.getValue(Deck.class);

                    decks.add(deck);

                    assert deck != null;
                    final String deckId = deck.getDeckId();
                    deckIds.add(deckId);
                }

                for (String id : deckIds)
                {

                    ValueEventListener deckListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            count++;
                            // iterating through all the nodes
                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                Card card = postSnapshot.getValue(Card.class);

                                if (card.getCardStatus().matches("BLUE")) {
                                    blueCards++;
                                } else  if (card.getCardStatus().matches("RED")) {
                                    redCards++;

                                } else  if (card.getCardStatus().matches("YELLOW")) {
                                    yellowCards++;

                                }  else {
                                    greenCards++;
                                }

                                cards.add(card);
                            }
                            if(count == deckIds.size()){
//                                for (int i = 0; i < cards.size(); i++) {
//                                    if (cards.get(i).getCardStatus().matches("BLUE")) {
//                                        blueCards++;
//                                    } else  if (cards.get(i).getCardStatus().matches("RED")) {
//                                        redCards++;
//
//                                    } else  if (cards.get(i).getCardStatus().matches("YELLOW")) {
//                                        yellowCards++;
//
//                                    }  else {
//                                        greenCards++;
//                                    }
//                                }
                                //anyChartView.setChart(null);
                                //anyChartView.clear();
                                Pie pie = createPieChart();
                                updateUI();
                                anyChartView.setChart(pie);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    };

                    databaseDecks.child("deckdetails").child(id).addValueEventListener(deckListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseDecks.child("decks").child(userId).addValueEventListener(userDecksListener);
    }

    private Pie createPieChart() {
        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry(getResources().getString(R.string.textButton_Red), redCards));
        data.add(new ValueDataEntry(getResources().getString(R.string.textButton_Green), greenCards));
        data.add(new ValueDataEntry(getResources().getString(R.string.textButton_Yellow), yellowCards));
        data.add(new ValueDataEntry(getResources().getString(R.string.textButton_Blue), blueCards));

        pie.data(data);

        String[] settings = {
                "#FF0000", "#00CC00", "#FFFF00", "#0099FF"
        };

        pie.palette(settings);

        pie.title("Progression Report");
        pie.labels().position("outside");
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        pie.normal().outline().enabled(true);
        pie.normal().outline().width("5%");
        pie.hovered().outline().width("10%");
        pie.selected().outline().width("3");
        pie.selected().outline().fill("#455a64");
        pie.selected().outline().stroke(null);
        pie.selected().outline().offset(2);

        return pie;
    }

    private void updateUI()
    {
        totalDeck.setText(String.valueOf(decks.size()));
        totalCard.setText(String.valueOf(cards.size()));
    }

    private void showProgress()
    {
        totalDeck.setText("Loading...");
        totalCard.setText("Loading...");
    }

//    private class MyTask extends AsyncTask<Void, Integer, Pie> {
//
//        MyTask() { }
//
//        @Override
//        protected void onPreExecute() {
//            //Thong bao len man hinh la dang load du lieu
//        }
//
//        @Override
//        protected Pie doInBackground(Void... param)
//        {
//            //set event Listener for decks of user to get all deckIds and all decks
//
//            ValueEventListener userDecksListener = new ValueEventListener()
//            {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    // iterating through all the nodes
//                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                        Deck deck = postSnapshot.getValue(Deck.class);
//
//                        decks.add(deck);
//
//                        assert deck != null;
//                        final String deckId = deck.getDeckId();
//                        deckIds.add(deckId);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            };
//
//            databaseDecks.child("decks").child(userId).addValueEventListener(userDecksListener);
//
//            //give thread of listener time to download user decks
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            //Set event listener for each deck to get all cards
//            for (String id : deckIds)
//            {
//                ValueEventListener deckListener = new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//                    {
//                        // iterating through all the nodes
//                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
//                        {
//                            Card card = postSnapshot.getValue(Card.class);
//                            cards.add(card);
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                    }
//                };
//
//                databaseDecks.child("deckdetails").child(id).addValueEventListener(deckListener);
//            }
//
//            //give thread of listener time to download all cards
//            final int time = decks.size() * 100;
//            try {
//                Thread.sleep(time);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            //find color in each card
//            for (int i = 0; i < cards.size(); i++) {
//                if (cards.get(i).getCardStatus().matches("BLUE")) {
//                    blueCards++;
//
//                } else  if (cards.get(i).getCardStatus().matches("RED")) {
//                    redCards++;
//
//                } else  if (cards.get(i).getCardStatus().matches("YELLOW")) {
//                    yellowCards++;
//
//                }  else {
//                    greenCards++;
//                }
//            }
//
//            Pie pie = createPieChart();
//
//            try {
//                Thread.sleep(time);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return pie;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            showProgress();
//        }
//
//        @Override
//        protected void onPostExecute(Pie pie)
//        {
//            updateUI();
//            anyChartView.setChart(pie);
//        }
//
//
//        private Pie createPieChart()
//        {
//            Pie pie = AnyChart.pie();
//
//            List<DataEntry> data = new ArrayList<>();
//            data.add(new ValueDataEntry("Thuộc lòng", redCards));
//            data.add(new ValueDataEntry("Sơ sơ", greenCards));
//            data.add(new ValueDataEntry("Đã quên", yellowCards));
//            data.add(new ValueDataEntry("Chưa thuộc", blueCards));
//
//            pie.data(data);
//
//            String[] settings = {
//                    "#FF0000", "#00CC00", "#FFFF00", "#0099FF"
//            };
//
//            pie.palette(settings);
//
//            pie.title("Progression Report");
//            pie.labels().position("outside");
//            pie.legend()
//                    .position("center-bottom")
//                    .itemsLayout(LegendLayout.HORIZONTAL)
//                    .align(Align.CENTER);
//
//            pie.normal().outline().enabled(true);
//            pie.normal().outline().width("5%");
//            pie.hovered().outline().width("10%");
//            pie.selected().outline().width("3");
//            pie.selected().outline().fill("#455a64");
//            pie.selected().outline().stroke(null);
//            pie.selected().outline().offset(2);
//
//            return pie;
//        }
//    }
}