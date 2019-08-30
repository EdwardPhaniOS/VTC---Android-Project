package com.example.flashcard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.example.flashcard.models.DeckLibrary;


public class DownloadCardsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_download_cards, container, false);
        final ListView listView = (ListView) rootView.findViewById(R.id.listView);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        DeckLibrary commonWords = new DeckLibrary("1000 Common English Words ","free");
        DeckLibrary commonPhrases = new DeckLibrary("1000 Common English Phrases","free");

        DeckLibrary[] deckLibraries = new DeckLibrary[]{commonWords, commonPhrases};

        ArrayAdapter<DeckLibrary> arrayAdapter
                = new ArrayAdapter<DeckLibrary>(getActivity(), android.R.layout.simple_list_item_checked , deckLibraries);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Clicked", "onItemClick: " +position);
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                DeckLibrary deckLibrary = (DeckLibrary)listView.getItemAtPosition(position);
                deckLibrary.setIsDownloaded(!currentCheck);
                Log.i("Clicked", "is dowloaded: : " +deckLibrary.isDownloaded());
            }
        });

        return rootView;
    }
}
