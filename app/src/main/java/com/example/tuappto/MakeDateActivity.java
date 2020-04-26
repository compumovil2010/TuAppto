package com.example.tuappto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

public class MakeDateActivity extends AppCompatActivity {

    TimePickerDialog timePickerDialog;
    Calendar calendar = Calendar.getInstance();
    ConstraintLayout constraintLayout = findViewById(R.id.prueba);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_date);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            }
        },calendar.get(Calendar.HOUR),calendar.get(Calendar.MINUTE),android.text.format.DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }
}
