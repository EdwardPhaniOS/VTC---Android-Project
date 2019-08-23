package com.example.flashcard.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.flashcard.LearnActivity;
import com.example.flashcard.R;
import com.example.flashcard.SurveyActivity;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.models.Card;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {
    private Button buttonLearnBlue;
    private Button buttonTestBlue;

    private OnButtonTestClickListener listener;

    public TrainingFragment(OnButtonTestClickListener listener) {
        // Required empty public constructor
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_training, null);

        final String deckId = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_ID);
        final String deckName = getActivity().getIntent().getStringExtra(ConstantVariable.DECK_NAME);

        buttonLearnBlue = (Button) view.findViewById(R.id.buttonLearnBlue);
        buttonTestBlue = (Button) view.findViewById(R.id.buttonTestBlue);

        buttonLearnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LearnActivity.class);
                // put extra
                intent.putExtra(ConstantVariable.DECK_ID, deckId);
                intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                startActivity(intent);
            }
        });

        buttonTestBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SurveyActivity.class);
                // put extra
                intent.putExtra(ConstantVariable.DECK_ID, deckId);
                intent.putExtra(ConstantVariable.DECK_NAME, deckName);
                startActivity(intent);
                listener.OnButtonTestClick();
            }
        });

        return view;
    }

    public interface OnButtonTestClickListener {
        void OnButtonTestClick();
    }
}
