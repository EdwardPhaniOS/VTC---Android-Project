package com.example.flashcard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.example.flashcard.adapters.DeckList;
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
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;


public class UserProgressFragment extends Fragment {

    private TextView totalDeck;
    private TextView totalCard;

    private View rootView;
    private AnyChartView anyChartView;
    private Pie pie;

    List<Deck> decks;
    List<Card> cards;
    ArrayList<String> deckIds = new ArrayList<String>();

    //Fire base
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseDecks = database.getReference("DBFlashCard");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();

    private String[] settings = {
            "#FF0000", "#FFFF00", "#00CC00", "#0099FF"
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_progress, null);
        anyChartView = rootView.findViewById(R.id.any_chart_view);

        decks = new ArrayList<>();
        cards = new ArrayList<>();
        cards.clear();

        totalDeck = rootView.findViewById(R.id.total_decks_number);
        totalCard = rootView.findViewById(R.id.total_cards_number);

        AsyncTask task = new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return rootView;
    }

    private void updateUI() {
        totalDeck.setText(String.valueOf(decks.size()));
        totalCard.setText(String.valueOf(cards.size()));
    }


    private class MyTask extends AsyncTask<Pie, Integer, Pie> {

        public MyTask() {
        }


        @Override
        protected Pie doInBackground(Pie... pie) {

            //get all decks, deckIds and all cards
            databaseDecks.child("decks").child(userId).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            decks.clear();

                            // iterating through all the nodes
                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                Deck deck = postSnapshot.getValue(Deck.class);

                                decks.add(deck);
                                
                                final String deckId = deck.getDeckId();
                                deckIds.add(deckId);
                            }

                            getAllCard();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            //create a chart
            Pie pieChart = createPieChart();

            pieChart.normal().outline().enabled(true);
            pieChart.normal().outline().width("5%");
            pieChart.hovered().outline().width("10%");
            pieChart.selected().outline().width("3");
            pieChart.selected().outline().fill("#455a64");
            pieChart.selected().outline().stroke(null);
            pieChart.selected().outline().offset(2);
            pieChart.draw(false);

            return pieChart;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Pie p) {

            //add chart to view then update it
            anyChartView.setChart(p);
            updateChart();
        }

        private void getAllCard() {

            for (String id : deckIds)
            {
                databaseDecks.child("deckdetails").child(id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                // iterating through all the nodes
                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                {
                                    Card card = postSnapshot.getValue(Card.class);
                                    cards.add(card);
                                }
                                updateUI();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
            }

        }

        private Pie createPieChart()
        {
            int blueCards = 1;
            int redCards = 1;
            int yellowCards = 1;
            int greenCards = 1;

            pie = AnyChart.pie();

            List<DataEntry> data = new ArrayList<>();
            data.add(new ValueDataEntry("Thuộc lòng", redCards));
            data.add(new ValueDataEntry("Sơ sơ", yellowCards));
            data.add(new ValueDataEntry("Đã quên", greenCards));
            data.add(new ValueDataEntry("Chưa thuộc", blueCards));

            pie.data(data);

            pie.palette(settings);

            pie.title("Progression Report");

            pie.labels().position("outside");

            pie.legend()
                    .position("center-bottom")
                    .itemsLayout(LegendLayout.HORIZONTAL)
                    .align(Align.CENTER);

            return pie;
        }

        private void updateChart() {

            int blueCards = 0;
            int redCards = 0;
            int yellowCards = 0;
            int greenCards = 0;

            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getCardStatus().matches("BLUE")) {
                    blueCards++;

                } else  if (cards.get(i).getCardStatus().matches("RED")) {
                    redCards++;

                } else  if (cards.get(i).getCardStatus().matches("YELLOW")) {
                    yellowCards++;

                }  else {
                    greenCards++;
                }
            }

            List<DataEntry> data = new ArrayList<>();
            data.add(new ValueDataEntry("Thuộc lòng", redCards));
            data.add(new ValueDataEntry("Sơ sơ", yellowCards));
            data.add(new ValueDataEntry("Đã quên", greenCards));
            data.add(new ValueDataEntry("Chưa thuộc", blueCards));

            pie.data(data);
            pie.draw(false);
            anyChartView.setChart(pie);

            //TODO: reload activity
        }
    }
}
