package com.example.flashcard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flashcard.R;
import com.example.flashcard.models.Card;

import java.util.List;


public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.ViewHolder>{


    private Context context;
    private List<Card> cards;
    private final OnCardClickListener cardListener;

    public CardRecyclerAdapter(Context context, List<Card> cards, OnCardClickListener cardListener) {
        this.context = context;
        this.cards = cards;
        this.cardListener = cardListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_cardview_flashcard, viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.bind(cards.get(position),cardListener);
        final Card card = cards.get(position);

        viewHolder.textViewVocabulary.setText(card.getVocabulary());
        viewHolder.textViewDefinition.setText(card.getDefinition());

//        viewHolder.textViewVocabulary.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                openEditFlashCardDialog(card);
//            }
//        });
    }

    private void openEditFlashCardDialog(Card card) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        // Goi inflater trong adapter ???
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.edit_flashcard_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etVocabulary = (EditText) dialogView.findViewById(R.id.etEditVocabulary);
        final EditText etDefinition = (EditText) dialogView.findViewById(R.id.etEditDefinition);
        final ImageView imageViewFlashCard = (ImageView) dialogView.findViewById(R.id.imageViewFlashCard);
        final LinearLayout imageViewDefault = (LinearLayout) dialogView.findViewById(R.id.imageViewDefault);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelEditFlashcard);
        final Button buttonOk = (Button) dialogView.findViewById(R.id.buttonOkEditFlashcard);


        if(card.getVocabularyUrl() != null && card.getVocabularyUrl() != ""){
            Glide.with(context).load(card.getVocabularyUrl()).into(imageViewFlashCard);
            imageViewFlashCard.setVisibility(View.VISIBLE);
            imageViewDefault.setVisibility(View.GONE);
        }
        else {
            imageViewFlashCard.setVisibility(View.GONE);
            imageViewDefault.setVisibility(View.VISIBLE);
        }

        etVocabulary.setText(card.getVocabulary());
        etDefinition.setText(card.getDefinition());

        dialogBuilder.setTitle("Edit flashcard");
        final AlertDialog b = dialogBuilder.create();
        b.show();

//        buttonOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String vocabulary = etVocabulary.getText().toString().trim();
//                String definition = etDefinition.getText().toString().trim();
//                if (!TextUtils.isEmpty(vocabulary)) {
//                    // create a unique id as the PK for Artist
//                    String id = databaseDeckDetails.push().getKey();
//                    Card card = new Card(id,vocabulary,definition);
//                    // save to db
//                    databaseDeckDetails.child(id).setValue(card);
//                    // set blank for name
//                    etVocabulary.setText("");
//                    etDefinition.setText("");
//                    // notify success
//                    b.dismiss();
//                    Toast.makeText(ManageFlashcardsActivity.this, "Card added", Toast.LENGTH_LONG).show();
//                } else {
//                    //updateArtist(artistId, artistName, genre);
//                    etVocabulary.setError("Cannot empty");
//                }
//            }
//        });
//
        buttonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public interface OnCardClickListener {
        void onCardClick(Card item);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewVocabulary;
        public TextView textViewDefinition;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewVocabulary = (TextView)itemView.findViewById(R.id.textViewVocabulary);
            textViewDefinition = (TextView)itemView.findViewById(R.id.textViewDefinition);
        }

        public void bind(final Card item, final OnCardClickListener listener) {
            textViewVocabulary.setText(item.getVocabulary());
            textViewDefinition.setText(item.getDefinition());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onCardClick(item);
                }
            });
        }

    }
}
