package examples.sdk.android.clover.com.appointo;






import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Arrays;
import java.util.Calendar;

import static java.util.Calendar.MONDAY;

public class CalendarQuickstart {


  @RequiresApi(api = Build.VERSION_CODES.O)
  public void setEvent() {
    Calendar cal = new Calendar.Builder().setCalendarType("iso8601")
        .setWeekDate(2013, 1, MONDAY).build();

  }




}