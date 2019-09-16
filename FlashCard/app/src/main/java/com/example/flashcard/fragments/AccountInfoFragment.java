package com.example.flashcard.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.MainActivity;
import com.example.flashcard.R;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.models.Card;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class AccountInfoFragment extends Fragment
{
    //widgets
    private TextView userEmail;
    private Button logOutButton;

    //temp widgets
    private Button uploadButton;
    private EditText vocabInput;
    private EditText defInput;

    //firebase
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    //temp
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View rootView = inflater.inflate(R.layout.fragment_account_info, null);
        userEmail = rootView.findViewById(R.id.user_email_info);
        logOutButton = rootView.findViewById(R.id.log_out_button);

        //temp
        uploadButton = rootView.findViewById(R.id.upload_button);
        vocabInput = rootView.findViewById(R.id.vocabulary_input);
        defInput = rootView.findViewById(R.id.definition_input);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final String emailInfo = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        userEmail.setText(emailInfo);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mAuth.signOut();
                removeLinkAccount();

                Toast.makeText(getActivity(), "Log Out", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String vocab = String.valueOf(vocabInput.getText());
                final String definition = String.valueOf(defInput.getText());


                //Add a card to a deck (1000 common words) in Library
                if (vocab.isEmpty() || definition.isEmpty()) {
                    Toast.makeText(getContext(),
                            "ERROR: Please input Vocab and Definition",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String id = mDatabase.child("DBFlashCard").child("Library")
                            .child(ConstantVariable.ONE_THOUSAND_COMMON_PHRASES)
                            .push().getKey();

                    final Card card = new Card(id, vocab, definition);

                    mDatabase.child("DBFlashCard").child("Library")
                            .child(ConstantVariable.ONE_THOUSAND_COMMON_PHRASES)
                            .push().setValue(card);
                    }

                    Toast.makeText(getContext(),
                        "Upload success",
                        Toast.LENGTH_SHORT).show();
                }
            }
        );

        return rootView;
    }

    private void removeLinkAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
}
