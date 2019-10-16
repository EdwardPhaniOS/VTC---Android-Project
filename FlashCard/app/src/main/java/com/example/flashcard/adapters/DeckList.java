package com.example.flashcard.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.example.flashcard.R;
import com.example.flashcard.fragments.MyDecksFragment;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.ListItem;
import com.example.flashcard.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class DeckList extends ArrayAdapter<ListItem> implements Filterable {
    private Activity context;
    private List<ListItem> decks;
    private List<ListItem> copyAlldecks;
    private List<String> listDeckIdHaveReminder;
    private MyDecksFragment.OnClickDeckItemListener onClickDeckItemListener;


//    private List<Deck> decks = new ArrayList<Deck>();
//    private List<Integer> totalCardOfDeckID = new ArrayList<Integer>();
//    private List<Integer> numberOfRedCard = new ArrayList<Integer>();

    public DeckList(Activity context, List<ListItem> _deckDetail, List<String> listDeckIdHaveReminder, MyDecksFragment.OnClickDeckItemListener onClickDeckItemListener) {
        super(context, R.layout.layout_deck_list,_deckDetail);
        this.context = context;
        this.decks = _deckDetail;
        this.copyAlldecks = _deckDetail;
        this.listDeckIdHaveReminder = listDeckIdHaveReminder;
        this.onClickDeckItemListener = onClickDeckItemListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_deck_list, null,true);

        TextView tvNameDeck = (TextView)listViewItem.findViewById(R.id.tvNameDeck);
        TextView tvQuantityOfCard = (TextView)listViewItem.findViewById(R.id.tvQuantityOfCard);
        TextView tvPercent = (TextView)listViewItem.findViewById(R.id.tvPercent);

        final ListItem deck = decks.get(position);
        tvNameDeck.setText(deck.getDeckName());

        int total = deck.getTotalCard();
        int red = deck.getRedCard();
        int percentageOfRed = (int)Math.round((float)red*100/total);
        if(listDeckIdHaveReminder.contains(deck.getDeckId())){
            listViewItem.setBackgroundColor(Color.WHITE);
            tvNameDeck.setTextColor(Color.BLACK);
            tvQuantityOfCard.setTextColor(Color.BLACK);
            tvQuantityOfCard.setText( total + " flashcard(s) - " + "⏰");
        } else {
            tvQuantityOfCard.setText( total + " flashcard(s)");
        }
        tvPercent.setText(percentageOfRed + "%");

        listViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDeckItemListener.handleGoToDeckDetail(deck.getDeckName(),deck.getDeckId());
            }
        });

        return listViewItem;
    }

    @Override
    public int getCount() {
        return decks.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    decks = copyAlldecks;
                } else {
                    List<ListItem> filteredList = new ArrayList<>();
                    for (ListItem row : copyAlldecks) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDeckName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    decks = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = decks;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                decks = (ArrayList<ListItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
