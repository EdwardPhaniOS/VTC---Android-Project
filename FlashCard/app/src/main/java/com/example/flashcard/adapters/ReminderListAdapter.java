package com.example.flashcard.adapters;

import android.app.Activity;
import android.content.Intent;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.flashcard.DeckDetailActivity;
import com.example.flashcard.R;
import com.example.flashcard.Utilities.ConstantVariable;
import com.example.flashcard.models.Card;
import com.example.flashcard.models.Deck;
import com.example.flashcard.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderListAdapter extends ArrayAdapter<Reminder> {


    private Activity context;
    private List<Reminder> reminders;
    private OnButtonAlarmClickListener listener;
    private String chosenDate;

    public interface OnButtonAlarmClickListener {
        void onButtonAlarmClick(String nameReminder);
    }

    public ReminderListAdapter(Activity _context, List<Reminder> _reminders,OnButtonAlarmClickListener _listener,String _chosenDate) {
        super(_context, R.layout.lv_reminders_item,_reminders);
        this.context = _context;
        this.reminders = _reminders;
        this.listener = _listener;
        this.chosenDate = _chosenDate;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();
        final View listViewItem = inflater.inflate(R.layout.lv_reminders_item, null,true);

        TextView tvNameReminder = (TextView)listViewItem.findViewById(R.id.tvNameReminder);
        TextView tvNameDayReminder = (TextView)listViewItem.findViewById(R.id.tvNameDayReminder);
        CheckBox cbActivated = (CheckBox) listViewItem.findViewById(R.id.cbActivated);
        ImageButton imageButtonSetAlarm = (ImageButton)listViewItem.findViewById(R.id.imageButtonSetAlarm);
        cbActivated.setEnabled(false);
        cbActivated.setFocusable(false);
        cbActivated.setFocusableInTouchMode(false);
        imageButtonSetAlarm.setFocusable(false);
        imageButtonSetAlarm.setFocusableInTouchMode(false);

        final Reminder reminder = reminders.get(position);
        tvNameReminder.setText(reminder.getName());

        tvNameDayReminder.setText(reminder.getNameDay() + " - " + reminder.getDate());

        if(reminder.getIsActivated().equals("true")){
            cbActivated.setChecked(true);
            imageButtonSetAlarm.setVisibility(View.GONE);
        }

        if(!chosenDate.equals("Today")){
            imageButtonSetAlarm.setVisibility(View.GONE);
        }

        imageButtonSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onButtonAlarmClick(reminder.getName());
            }
        });

        return listViewItem;
    }
}
