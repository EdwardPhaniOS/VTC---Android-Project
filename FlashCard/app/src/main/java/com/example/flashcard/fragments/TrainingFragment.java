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

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {
    private Button buttonLearnBlue;

    public TrainingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_training, null);
        buttonLearnBlue = (Button) view.findViewById(R.id.buttonLearnBlue);

        buttonLearnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LearnActivity.class);
                // put extra userLogin

                startActivity(intent);
            }
        });

        return view;
    }

}
