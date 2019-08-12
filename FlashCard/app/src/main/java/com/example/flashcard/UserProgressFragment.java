package com.example.flashcard;

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


public class UserProgressFragment extends Fragment {

    private TextView totalDeck;
    private TextView totalCard;

    List<Deck> decks;
    List<Card> cards;
    ArrayList<String> deckIds = new ArrayList<String>();

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseDecks = database.getReference("DBFlashCard");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();

    private String[] settings = {
            "#FF0000", "#FFFF00", "#00CC00", "#0099FF"
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_progress, null);

        totalDeck = rootView.findViewById(R.id.total_decks_number);
        totalCard = rootView.findViewById(R.id.total_cards_number);

        decks = new ArrayList<>();
        cards = new ArrayList<>();

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

        //adding chart
        AnyChartView anyChartView = rootView.findViewById(R.id.any_chart_view);
        Pie pieChart = createPieChart();
        anyChartView.setChart(pieChart);

        return rootView;
    }

    private Pie createPieChart() {
        Pie pie = AnyChart.pie();

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Thuộc lòng", 10000));
        data.add(new ValueDataEntry("Sơ sơ", 12000));
        data.add(new ValueDataEntry("Đã quên", 18000));
        data.add(new ValueDataEntry("Chưa thuộc", 18000));

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

    private void getAllCard() {
        //TODO: need to loop through all deck ids at child of deckdetails
        cards.clear();
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

    private void updateUI() {
        totalDeck.setText(String.valueOf(decks.size()));
        totalCard.setText(String.valueOf(cards.size()));
    }

}
