package com.example.flashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.adapters.CardRecyclerAdapter;
import com.example.flashcard.fragments.MyDecksFragment;
import com.example.flashcard.models.Card;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageFlashcardsActivity extends AppCompatActivity {
    //constant to track image chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;
    //uri to store file
    private Uri filePath;
    //
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    //firebase objects
    private StorageReference storageReference;
    DatabaseReference databaseDeckDetails;
    //
    private CardRecyclerAdapter adapter;
    private List<Card> cards;
    // Image from dialog alert builder
    ImageView imageViewFlashCard;
    LinearLayout imageViewDefault;
    //
    Card card_To_UploadImage;
    //
    private SearchView searchViewCard;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_flashcards);
        // GET INTENT TO RECEIVE DATA FROM PREVIOUS ACTION
        Intent intent = getIntent();
        // create note for storage image
        storageReference = FirebaseStorage.getInstance().getReference();
        // CREATE NOTE BY NAME OF DECK ( DECK_ID )
        databaseDeckDetails = FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails")
                .child(intent.getStringExtra(ConstantVariable.DECK_ID));
        // SET TITLE FOR TOOLBAR APPBARLAYOUT
        setTitle(intent.getStringExtra(ConstantVariable.DECK_NAME));
        ///////// USING TOOLBAR AS ACTION BAR ( CUSTOM PURPOSE )
        toolbar = (Toolbar)findViewById(R.id.toolbarEditFlashCard);
        setSupportActionBar(toolbar);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //
        // BACK TO PREVIOUS ACTIVITY ( LIKE BACK PHYSICAL BUTTON
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateFlashCardDialog();
            }
        });
        //
        // intial setup for RecyclerView androidX
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewCards);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        //
        //
        searchViewCard = (SearchView) findViewById(R.id.searchViewCard);
        //
        cards = new ArrayList<>();
        //
        //databaseDeckDetails = FirebaseDatabase.getInstance().getReference("DBFlashCard").child("deckdetails");
        databaseDeckDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                cards.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Card card = postSnapshot.getValue(Card.class);
                    cards.add(card);
                }

                adapter = new CardRecyclerAdapter(ManageFlashcardsActivity.this, cards, new CardRecyclerAdapter.OnCardClickListener() {
                    @Override
                    public void onCardClick(Card item) {
                        //Toast.makeText(ManageFlashcardsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        showEditFlashCardDialog(item);
                    }
                });

                recyclerView.setAdapter(adapter);
                setupSearchViewCard();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }



    private void showCreateFlashCardDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_flashcard_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etVocabulary = (EditText) dialogView.findViewById(R.id.etVocabulary);
        final EditText etDefinition = (EditText) dialogView.findViewById(R.id.etDefinition);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelFlashcard);
        final Button buttonOk = (Button) dialogView.findViewById(R.id.buttonOkFlashcard);

        dialogBuilder.setTitle("Create a new flashcard");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vocabulary = etVocabulary.getText().toString().trim();
                String definition = etDefinition.getText().toString().trim();
                if (!TextUtils.isEmpty(vocabulary)) {
                    // create a unique id as the PK for Artist
                    String id = databaseDeckDetails.push().getKey();
                    Card card = new Card(id,vocabulary,definition);
                    // save to db
                    databaseDeckDetails.child(id).setValue(card);
                    // set blank for name
                    etVocabulary.setText("");
                    etDefinition.setText("");
                    // notify success
                    b.dismiss();
                    Toast.makeText(ManageFlashcardsActivity.this, "Card added", Toast.LENGTH_LONG).show();
                } else {
                    //updateArtist(artistId, artistName, genre);
                    etVocabulary.setError("Cannot empty");
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    private void showEditFlashCardDialog(final Card card) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_flashcard_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText etVocabulary = (EditText) dialogView.findViewById(R.id.etEditVocabulary);
        final EditText etDefinition = (EditText) dialogView.findViewById(R.id.etEditDefinition);
         imageViewFlashCard = (ImageView) dialogView.findViewById(R.id.imageViewFlashCard);
         imageViewDefault = (LinearLayout) dialogView.findViewById(R.id.imageViewDefault);
        final Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancelEditFlashcard);
        final Button buttonOk = (Button) dialogView.findViewById(R.id.buttonOkEditFlashcard);
        final ImageButton buttonDelete = (ImageButton) dialogView.findViewById(R.id.buttonDeleteFlashcard);

        imageViewDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_To_UploadImage = card;
                showFileChooser();
            }
        });

        imageViewFlashCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_To_UploadImage = card;
                showFileChooser();
            }
        });

        if(card.getVocabularyUrl() != null && !card.getVocabularyUrl().isEmpty()){
            Glide.with(this).load(card.getVocabularyUrl()).into(imageViewFlashCard);
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

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vocabulary = etVocabulary.getText().toString().trim();
                String definition = etDefinition.getText().toString().trim();
                if (!TextUtils.isEmpty(vocabulary)) {
                    // create a unique id as the PK for Artist
                    //String id = databaseDeckDetails.push().getKey();
                    Card newCard = new Card(card.getCardId(),vocabulary,definition,card.getVocabularyUrl());
                    // save to db
                    databaseDeckDetails.child(card.getCardId()).setValue(newCard);
                    // set blank for name
                    etVocabulary.setText("");
                    etDefinition.setText("");

                    // notify success
                    b.dismiss();
                    if(filePath != null){
                        card_To_UploadImage = newCard;
                        uploadFile();
                    }
                    else {
                        Toast.makeText(ManageFlashcardsActivity.this, "Card edited", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //updateArtist(artistId, artistName, genre);
                    etVocabulary.setError("Cannot empty");
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                databaseDeckDetails.child(card.getCardId()).removeValue();
                b.dismiss();

                Toast.makeText(getApplicationContext(), "Card deleted", Toast.LENGTH_LONG).show();
            }
        });
    }

    // file chooser
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // remember
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    // selected file extension
    public String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //upload file to StorageFirebase and Url's file to DatabaseFirebase
    private void uploadFile(){
        if(filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            // resize

            // create node file to store
            StorageReference sRef = storageReference.child("uploads/"
                    + System.currentTimeMillis() + "." + getFileExtension(filePath));
            // add the file to ref
            sRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(taskSnapshot.getMetadata() != null){
                        if(taskSnapshot.getMetadata().getReference() != null){
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {


                                    Toast.makeText(ManageFlashcardsActivity.this, "Card edited", Toast.LENGTH_LONG).show();
//                                    // get url to assign to object Upload
//                                    Card card = new Card(editTextName.getText().toString().trim(),uri.toString());
//                                    // Resolve taskSnapshot.getDownloadUrl() cannot found
//                                    // https://stackoverflow.com/questions/50585334/tasksnapshot-getdownloadurl-method-not-working
//
//                                    // add url to firebase database
//                                    String uploadId = mDatabase.push().getKey();
//                                    mDatabase.child(uploadId).setValue(upload);
                                    card_To_UploadImage.setVocabularyUrl(uri.toString());
                                    databaseDeckDetails.child(card_To_UploadImage.getCardId()).setValue(card_To_UploadImage);
                                    filePath = null;
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            filePath = null;
                            Toast.makeText(ManageFlashcardsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // display percentage of the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + ((int)progress) + "%...");
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please choose an image to upload", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageViewFlashCard.setImageBitmap(bitmap);
                imageViewFlashCard.setVisibility(View.VISIBLE);
                imageViewDefault.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupSearchViewCard(){
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchViewCard.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchViewCard.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchViewCard.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                (ManageFlashcardsActivity.this).adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                (ManageFlashcardsActivity.this).adapter.getFilter().filter(query);
                return false;
            }
        });
    }

}
