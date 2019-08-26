package com.example.flashcard.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcard.R;
import com.example.flashcard.adapters.RecyclerAdapterForResultPage;
import com.example.flashcard.models.PageViewModelForResultPage;

public class PlaceholderFragmentForResultPage extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModelForResultPage pageViewModelForResultPage;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    public static PlaceholderFragmentForResultPage newInstance(int index) {
        PlaceholderFragmentForResultPage fragment = new PlaceholderFragmentForResultPage();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModelForResultPage = ViewModelProviders.of(this).get(PageViewModelForResultPage.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModelForResultPage.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_show_result, container, false);

        recyclerView = root.findViewById(R.id.showResultRecycleView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        pageViewModelForResultPage.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String index) {
                if (index.matches("1")) {
                    adapter = new RecyclerAdapterForResultPage(true);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter = new RecyclerAdapterForResultPage(false);
                    recyclerView.setAdapter(adapter);
                }

            }
        });

        return root;
    }
}