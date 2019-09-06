package com.example.flashcard.fragments;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashcard.DeckDetailActivity;
import com.example.flashcard.R;
import com.example.flashcard.Utilities.CardColor;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.Utilities.ValidateCheckForReminder;
import com.example.flashcard.adapters.DeckList;
import com.example.flashcard.adapters.ReminderListAdapter;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemindersFragment extends Fragment {
    ListView lvReminders;
    private TextView tvDateChosen;
    List<Reminder> reminders;
    List<String> deckIds;
    private int count = 0;
    private String dateCurrent = "";
    private String dateChosen = "";

    private boolean getDateFromPicker = false;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userId = user.getUid();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReminders = database.getReference("DBFlashCard");



    public RemindersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminders,null);
        // use offline
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //
        tvDateChosen = view.findViewById(R.id.tvDateChosen);
        lvReminders = view.findViewById(R.id.lvReminders);
        lvReminders.setItemsCanFocus(false);
        // initial
        reminders = new ArrayList<>();
        deckIds = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final Calendar calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        dateCurrent = df.format(d);

        final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                getDateFromPicker = true;
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date d = calendar.getTime();
                dateChosen = df.format(d);

                if(dateChosen.equals(dateCurrent)){
                    tvDateChosen.setText("Today");
                }else {
                    tvDateChosen.setText(dateChosen);
                }

                onStart();
            }
        };

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabChooseDate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), dateSetListener, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        lvReminders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!dateChosen.isEmpty()){
                    if(!checkValidTimeReminder(dateCurrent,dateChosen)){
                        return;
                    }
                }
                Reminder reminder = reminders.get(position);
                if(!checkIsActivated(reminder)){
                    // save id reminder for check
                    ValidateCheckForReminder.reminderSave = reminder;
                    //
                    Intent intent = new Intent(getContext(), DeckDetailActivity.class);
                    // put data to intent
                    intent.putExtra(ConstantVariable.DECK_NAME, reminder.getName());
                    intent.putExtra(ConstantVariable.DECK_ID, reminder.getDeckId());
                    intent.putExtra(ConstantVariable.USER_ID, userId);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), "You've done this reminder.\nPlease choose another.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReminders.child("reminders")
                .child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reminders.clear();
                        deckIds.clear();
                        count = 0;
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            String deckId = postSnapshot.getKey();
                            deckIds.add(deckId);
                        }

                        for(int i=0;i<deckIds.size();i++){
                            databaseReminders.child("reminders")
                                    .child(userId).child(deckIds.get(i)).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            count++;
                                            for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                                Reminder reminder = postSnapshot.getValue(Reminder.class);
                                                if(getDateFromPicker){
                                                    if(reminder.getDate().equals(dateChosen)){
                                                        reminders.add(reminder);
                                                    }
                                                }
                                                else {
                                                    if(reminder.getDate().equals(dateCurrent)){
                                                        reminders.add(reminder);
                                                    }
                                                }
                                            }
                                            if(count == deckIds.size()){
                                                if(getActivity()!=null){
                                                    ReminderListAdapter reminderListAdapterAdapter = new ReminderListAdapter(getActivity()
                                                            , reminders, new ReminderListAdapter.OnButtonAlarmClickListener() {
                                                        @Override
                                                        public void onButtonAlarmClick(String nameReminder) {
                                                            Calendar now = Calendar.getInstance();
                                                            int hour = now.get(Calendar.HOUR_OF_DAY);
                                                            int minute = now.get(Calendar.MINUTE);
                                                            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                                                            intent.putExtra(AlarmClock.EXTRA_HOUR, hour + 1);
                                                            intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
                                                            intent.putExtra(AlarmClock.EXTRA_MESSAGE, nameReminder);
                                                            startActivity(intent);
                                                            // https://www.youtube.com/watch?v=W07OAiJCHa0&t=s
                                                        }
                                                    },tvDateChosen.getText().toString());
                                                    lvReminders.setAdapter(reminderListAdapterAdapter);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private boolean checkIsActivated(Reminder reminder){
        if(reminder.getIsActivated().equals("true")){
            return true;
        }
        return false;
    }

    private boolean checkValidTimeReminder(String current, String chosen){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
            Date date1 = sdf.parse(current);
            Date date2 = sdf.parse(chosen);
            if (date1.compareTo(date2) > 0) {
                Toast.makeText(getContext(), "The reminder has passed.", Toast.LENGTH_SHORT).show();
                return false;
            } else if (date1.compareTo(date2) < 0) {
                Toast.makeText(getContext(), "The reminder has not come yet.", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
