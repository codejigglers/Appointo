package examples.sdk.android.clover.com.appointo;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalenderRepository {

  private final static int ADD_OR_EDIT_CALENDAR_REQUEST = 3;
  static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
  static final int REQUEST_AUTHORIZATION = 1;
  static final int REQUEST_ACCOUNT_PICKER = 2;

  Activity activity;
  private static final String PREF_ACCOUNT_NAME = "accountName";
  GoogleAccountCredential credential;
  com.google.api.services.calendar.Calendar client;
  final HttpTransport transport = AndroidHttp.newCompatibleTransport();
  MutableLiveData<List<Event>> events = new MutableLiveData<>();

  final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

  public CalenderRepository(Activity activity) {
    this.activity = activity;
    List<String> scopes = new ArrayList<>();
    scopes.add(CalendarScopes.CALENDAR);
    Logger.getLogger("com.google.api.client").setLevel(Level.OFF);
    scopes.add(CalendarScopes.CALENDAR_EVENTS);
    credential = GoogleAccountCredential.usingOAuth2(activity, scopes);
    SharedPreferences settings = activity.getPreferences(Context.MODE_PRIVATE);
    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
    client = new com.google.api.services.calendar.Calendar.Builder(
        transport, jsonFactory, credential).setApplicationName("Google-CalendarAndroidSample/1.0")
        .build();
    if (checkGooglePlayServicesAvailable()) {
      haveGooglePlayServices();
    }
  }

  public List<Event> fetchEvents() {
    com.google.api.services.calendar.Calendar[] a = new com.google.api.services.calendar.Calendar[]{client};
    final List<Event> allEvents = new ArrayList<>();
    FetchEvents fetchEvents = new FetchEvents(allEvents);
    fetchEvents.execute(a);
    return allEvents;

  }

  class FetchEvents extends AsyncTask<Calendar, Void, List<Event>> {

    private List<Event> allEvents;
    public FetchEvents(List<Event> allEvents) {
      this.allEvents = allEvents;
    }

    @Override
    protected List<Event> doInBackground(com.google.api.services.calendar.Calendar... calendars) {
      String pageToken = null;
      Events events = null;
      try {
        events = calendars[0].events().list("primary").setPageToken(null).execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
      List<Event> items = events.getItems();
      for (Event event : items) {
        System.out.println(event.getSummary());
      }
      pageToken = events.getNextPageToken();
      return null;
    }

    @Override
    protected void onPostExecute(List<Event> a) {
      super.onPostExecute(a);
      allEvents = a;
    }

  }

  public void addEvent(String summary, String desc, DateTime startDT, DateTime endDT, String attendee, String location) {
    final Event event = new Event()
        .setSummary(summary)
        .setLocation(location)
        .setDescription(desc);

//    DateTime startDateTime = new DateTime("2019-02-23T09:00:00-07:00");
    EventDateTime start = new EventDateTime()
        .setDateTime(startDT)
        .setTimeZone("America/Los_Angeles");
    event.setStart(start);

//    DateTime endDateTime = new DateTime("2019-02-23T17:00:00-07:00");
    EventDateTime end = new EventDateTime()
        .setDateTime(endDT)
        .setTimeZone("America/Los_Angeles");
    event.setEnd(end);

//    String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
//    event.setRecurrence(Arrays.asList(recurrence));

    EventAttendee[] attendees = new EventAttendee[]{
        new EventAttendee().setEmail(attendee),
    };
    event.setAttendees(Arrays.asList(attendees));

    EventReminder[] reminderOverrides = new EventReminder[]{
        new EventReminder().setMethod("email").setMinutes(24 * 60),
        new EventReminder().setMethod("popup").setMinutes(10),
    };
    Event.Reminders reminders = new Event.Reminders()
        .setUseDefault(false)
        .setOverrides(Arrays.asList(reminderOverrides));
    event.setReminders(reminders);

    com.google.api.services.calendar.Calendar[] a = new com.google.api.services.calendar.Calendar[]{client};

    new AsyncTask<com.google.api.services.calendar.Calendar, Void, List<Event>>() {

      String calendarId = "primary";

      @Override
      protected List<Event> doInBackground(com.google.api.services.calendar.Calendar... calendars) {
        try {
          Event event2 = new Event();
          event2 = calendars[0].events().insert(calendarId, event).execute();
          if (event2 == null) {
            throw new NullPointerException();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected void onPostExecute(List<Event> event) {
        super.onPostExecute(event);
        events.setValue(event);
      }
    }.execute(a);
  }

  public boolean checkGooglePlayServicesAvailable() {
    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
      return false;
    }
    return true;
  }

  public void haveGooglePlayServices() {
    // check if there is already an account selected
    if (credential.getSelectedAccountName() == null) {
      // ask user to choose account
      chooseAccount();
    } else {
      // load calendars
    }
  }

  private void chooseAccount() {
    activity.startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
  }

  private void startAddOrEditCalendarActivity(CalendarInfo calendarInfo) {
    Intent intent = new Intent(activity, AddOrEditCalendarActivity.class);
    if (calendarInfo != null) {
      intent.putExtra("id", calendarInfo.id);
      intent.putExtra("summary", calendarInfo.summary);
    }
    activity.startActivityForResult(intent, ADD_OR_EDIT_CALENDAR_REQUEST);
  }

  void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    activity.runOnUiThread(new Runnable() {
      public void run() {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
            connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
      }
    });
  }


}
