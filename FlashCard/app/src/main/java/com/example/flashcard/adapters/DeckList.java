package com.example.flashcard.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.flashcard.R;
import com.example.flashcard.models.Deck;

import java.util.List;

public class DeckList extends ArrayAdapter<Deck> {
    private Activity context;
    List<Deck> decks;

    public DeckList(Activity context, List<Deck> decks) {
        super(context, R.layout.layout_deck_list,decks);
        this.context = context;
        this.decks = decks;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_deck_list, null,true);

        TextView tvNameDeck = (TextView)listViewItem.findViewById(R.id.tvNameDeck);
        TextView tvQuantityOfCard = (TextView)listViewItem.findViewById(R.id.tvQuantityOfCard);
        TextView tvPercent = (TextView)listViewItem.findViewById(R.id.tvPercent);

        Deck artist = decks.get(position);
        tvNameDeck.setText(artist.getDeckName());
        // tvQuantityOfCard ????

        return listViewItem;
    }
}
