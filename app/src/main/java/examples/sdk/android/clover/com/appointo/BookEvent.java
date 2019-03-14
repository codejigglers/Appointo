package examples.sdk.android.clover.com.appointo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class BookEvent extends AppCompatActivity {

  static TextView startTime;
  static TextView endTime;
  private TextView description;
  private TextView location;
  private TextView profileName;
  private TextView professor;
  static TextView selectDate;
  private List<ProfDataObject> profData;
  String email;
  static Button confirmButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_book_event);
    try {
      this.getSupportActionBar().hide();
    } catch (NullPointerException e) {
    }

    profData = (List<ProfDataObject>) getIntent().getSerializableExtra(Intents.PROF_DATA);

    startTime = findViewById(R.id.startTime);
    endTime = findViewById(R.id.endTime);
    description = findViewById(R.id.description);
    location = findViewById(R.id.location);
    profileName = findViewById(R.id.profileName);
    professor = findViewById(R.id.professor);
    selectDate = findViewById(R.id.selectDate);
    confirmButton = findViewById(R.id.confirmButton);

    confirmButton.setEnabled(false);

    confirmButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createEventActivity();
      }
    });

    selectDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showDatePickerDialog();
      }
    });

    startTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog();
      }
    });

    endTime.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showTimePickerDialog2();
      }
    });

  }


  public void createEventActivity() {
    email = professor.getText().toString();
    ProfDataObject m = null;

    for (ProfDataObject p : profData) {
      if (p.email.equals(email)) {
        m = p;
      }
    }

    String descrip = description.getText().toString();
    String start = startTime.getText().toString();
    String end = endTime.getText().toString();
    String title = "APPOINTO: Appointment with " + m.email;
    String date = selectDate.getText().toString();

    Intent intent = new Intent(BookEvent.this, CalendarSampleActivity.class);
    intent.putExtra(Intents.DESCRIPTION, descrip);
    intent.putExtra(Intents.DATE, date);
    intent.putExtra(Intents.START_TIME, start);
    intent.putExtra(Intents.END_TIME, end);
    intent.putExtra(Intents.TITLE, title);
    intent.putExtra(Intents.EMAIL, m.email);
    startActivity(intent);
  }


  public void showTimePickerDialog() {
    TimePickerFragment newFragment = new TimePickerFragment();
    newFragment.show(getSupportFragmentManager(), "timePicker");
  }

  public void showTimePickerDialog2() {
    DialogFragment newFragment = new TimePickerFragment2();
    newFragment.show(getSupportFragmentManager(), "timePicker");
  }


  public void showDatePickerDialog() {
    DialogFragment newFragment = new DatePickerFragment();
    newFragment.show(getSupportFragmentManager(), "datePicker");
  }


  public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String time = "";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current time as the default values for the picker
      final Calendar c = Calendar.getInstance();
      int hour = c.get(Calendar.HOUR_OF_DAY);
      int minute = c.get(Calendar.MINUTE);

      // Create a new instance of TimePickerDialog and return it
      return new TimePickerDialog(getActivity(), this, hour, minute,
          DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      String hours;
      String minutes;

      if(hourOfDay <= 9) {
        hours = "0" + String.valueOf(hourOfDay);
      } else {
        hours = String.valueOf(hourOfDay);
      }

      if(minute <= 9) {
        minutes = "0" + String.valueOf(minute);
      } else {
        minutes = String.valueOf(minute);
      }
      time = hours + ":" + minutes;
      startTime.setText(time);
      if(isCompleted()) {
        confirmButton.setEnabled(true);
      }
    }

    public String getTime() {
      return time;
    }
  }

  public static class TimePickerFragment2 extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String time = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current time as the default values for the picker
      final Calendar c = Calendar.getInstance();
      int hour = c.get(Calendar.HOUR_OF_DAY);
      int minute = c.get(Calendar.MINUTE);

      // Create a new instance of TimePickerDialog and return it
      return new TimePickerDialog(getActivity(), this, hour, minute,
          DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      String hours;
      String minutes;

      if(hourOfDay <= 9) {
        hours = "0" + String.valueOf(hourOfDay);
      } else {
        hours = String.valueOf(hourOfDay);
      }

      if(minute <= 9) {
        minutes = "0" + String.valueOf(minute);
      } else {
        minutes = String.valueOf(minute);
      }
      time = hours + ":" + minutes;

      time = hours + ":" + minutes;
      String[] date2 = time.split(":");
      String[] date1 = startTime.getText().toString().split(":");

      if (Integer.parseInt(date1[0]) > Integer.parseInt(date2[0])) {
        Toast.makeText(getContext(), "Invalid Selection of time. End time should be smaller than start time", Toast.LENGTH_LONG).show();
      } else if ((Integer.parseInt(date1[0]) == Integer.parseInt(date2[0])) && Integer.parseInt(date1[1]) > Integer.parseInt(date2[1])) {
        Toast.makeText(getContext(), "Invalid Selection of time. End time should be smaller than start time", Toast.LENGTH_LONG).show();
      } else {
        endTime.setText(time);
        if(isCompleted()) {
          confirmButton.setEnabled(true);
        }
      }

    }

    public String getTime() {
      return time;
    }
  }

  public static class DatePickerFragment extends DialogFragment
      implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Use the current date as the default date in the picker
      final Calendar c = Calendar.getInstance();
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      int day = c.get(Calendar.DAY_OF_MONTH);
      // Create a new instance of DatePickerDialog and return it
      return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
      String years;
      String months;
      String days;

      years = String.valueOf(year);
      month = month + 1;
      if(month <= 9) {
        months = "0" + String.valueOf(month);
      } else {
        months = String.valueOf(month);
      }

      if(day <= 9) {
        days = "0" + String.valueOf(day);
      } else {
        days = String.valueOf(day);
      }

      selectDate.setText(months + "-" + days + "-" + years);
      if(isCompleted()) {
        confirmButton.setEnabled(true);
      }
    }
  }

  public static boolean isCompleted() {
    if(startTime.getText().toString().contains("--")) {
      return false;
    } else if(endTime.getText().toString().contains("--")) {
      return false;
    } else if(selectDate.getText().toString().contains("Select")) {
      return false;
    }
    return true;
  }
}
