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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

public class BookEvent extends AppCompatActivity {

  static TextView startTime;
  static TextView endTime;
  private TextView description;
  private TextView location;
  private TextView profileName;
  private TextView professor;
  static TextView selectDate;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_book_event);
    try {
      this.getSupportActionBar().hide();
    } catch (NullPointerException e) {
    }

    startTime = findViewById(R.id.startTime);
    endTime = findViewById(R.id.endTime);
    description = findViewById(R.id.description);
    location = findViewById(R.id.location);
    profileName = findViewById(R.id.profileName);
    professor = findViewById(R.id.profName);
    selectDate = findViewById(R.id.selectDate);

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
      time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
      startTime.setText(time);
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
      time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
      String[] date2 = time.split(":");
      String[] date1 = startTime.getText().toString().split(":");

      if(Integer.parseInt(date1[0]) > Integer.parseInt(date2[0])) {
        Toast.makeText(getContext(), "Invalid Selection of time. End time should be smaller than start time", Toast.LENGTH_LONG).show();
      } else if( (Integer.parseInt(date1[0]) == Integer.parseInt(date2[0])) && Integer.parseInt(date1[1]) > Integer.parseInt(date2[1])) {
        Toast.makeText(getContext(), "Invalid Selection of time. End time should be smaller than start time", Toast.LENGTH_LONG).show();
      } else {
        endTime.setText(time);
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
      selectDate.setText(String.valueOf(month) + "-" +  String.valueOf(day) + "-" +  String.valueOf(year));
    }
  }
}
