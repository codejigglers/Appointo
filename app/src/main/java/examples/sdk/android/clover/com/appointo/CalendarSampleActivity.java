/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package examples.sdk.android.clover.com.appointo;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class CalendarSampleActivity extends AppCompatActivity implements AppointmentClickListener, DialogFragmentListener {

  private static final Level LOGGING_LEVEL = Level.OFF;

  private static final String PREF_ACCOUNT_NAME = "vardan";

  static final String TAG = "CalendarSampleActivity";

  private static final int CONTEXT_EDIT = 0;

  private static final int CONTEXT_DELETE = 1;

  private static final int CONTEXT_BATCH_ADD = 2;

  static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

  static final int REQUEST_AUTHORIZATION = 1;

  static final int REQUEST_ACCOUNT_PICKER = 2;

  private final static int ADD_OR_EDIT_CALENDAR_REQUEST = 3;

  final HttpTransport transport = AndroidHttp.newCompatibleTransport();

  final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

  GoogleAccountCredential credential;

  CalendarModel model = new CalendarModel();

  ArrayAdapter<CalendarInfo> adapter;

  com.google.api.services.calendar.Calendar client;

  int numAsyncTasks;

  private ListView listView;

  public FragmentTransaction ft;
  FrameLayout fm;
  public Fragment loadingFragment;
  protected DrawerLayout drawerLayout;
  protected ImageView profile;
  private View navHeader;
  private NavigationView navigationView;
  private ArrayList<AppointmentModel> data = new ArrayList<>();
  private LinearLayout noAppointmentLayout;
  private SwipeRefreshLayout appointmentLayout;
  private RecyclerView appointmentRv;
  List<Event> allEvents = new ArrayList<>();
  FloatingActionButton f1;
  FloatingActionButton f2;
  List<ProfDataObject> profData = new ArrayList<>();
  HashMap<String, ProfDataObject> profDataMap = new HashMap<>();
  TextView navName;
  Call<List<ProfDataObject>> call;
  public boolean isConnected = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // enable logging
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    // view and menu
    setContentView(R.layout.activity_main);
//    registerForContextMenu(listView);
    // Google Accounts

    noAppointmentLayout = findViewById(R.id.no_appointment_view);
    appointmentLayout = findViewById(R.id.appointment_view);
    navigationView = (NavigationView) findViewById(R.id.nav_view);
    navHeader = navigationView.getHeaderView(0);
    navName = navHeader.findViewById(R.id.navName);


    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.nav_profile:
            Intent intent = new Intent(CalendarSampleActivity.this, ProfileActivity.class);
            startActivityForResult(intent, 22);
            return true;
          case R.id.nav_sign_out:
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
              ft.remove(prev);
            }
            ft.addToBackStack(null);
            DialogFragment dialogFragment = new ConfirmFragment();
            dialogFragment.show(ft, "dialog");
            return true;
        }
        return true;
      }
    });

    profile = (ImageView) navHeader.findViewById(R.id.img_header_bg);
    drawerLayout = findViewById(R.id.drawer_layout);
    //drawerLayout.getBackground().setAlpha(127);
    loadingFragment = LoadingFragment.newInstance();
    fm = findViewById(R.id.loader);
    f1 = findViewById(R.id.fab);
    f2 = findViewById(R.id.fab2);
    appointmentRv = findViewById(R.id.appointment_recyclerview);


    Endpoints service = RetrofitInstance.getRetrofitInstance().create(Endpoints.class);
    call = service.getAllProfs();

//    ft = getSupportFragmentManager().beginTransaction();
//    ft.add(R.id.loader, loadingFragment, "MyLoader").commit();


    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      handleInitialUi();
    } else {
      isConnected = false;
      changeTextStatus(false);
    }

  }

  public void handleInitialUi() {
    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    navName.setText(acct.getDisplayName());
    if (acct.getPhotoUrl() == null) {
      Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcToe-PSAektDgBsXLsdybQW6F1wGDdpw2mbm3SaReRPuQ0ec0ns")
          .crossFade()
          .thumbnail(0.5f)
          .bitmapTransform(new CircleTransform(this))
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(profile);
    } else {
      Glide.with(this).load(acct.getPhotoUrl().toString())
          .centerCrop()
          .crossFade()
          .override(150, 150)
          .thumbnail(0.5f)
          .bitmapTransform(new CircleTransform(this))
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(profile);
    }


    List<String> scopes = new ArrayList<>();
    scopes.add(CalendarScopes.CALENDAR);
    scopes.add(CalendarScopes.CALENDAR_EVENTS);

    credential =
        GoogleAccountCredential.usingOAuth2(this, scopes);
    final SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, "vardaan.gupta27@gmail.com"));
    // Calendar client
    client = new com.google.api.services.calendar.Calendar.Builder(
        transport, jsonFactory, credential).setApplicationName("Google-CalendarAndroidSample/1.0")
        .build();

    call.enqueue(new Callback<List<ProfDataObject>>() {
      @Override
      public void onResponse(Call<List<ProfDataObject>> call, Response<List<ProfDataObject>> response) {
        generateProfData(response.body());
        fetchEvents(client);
      }

      @Override
      public void onFailure(Call<List<ProfDataObject>> call, Throwable t) {
        Toast.makeText(CalendarSampleActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
      }
    });

    f1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(CalendarSampleActivity.this, BookEvent.class);
        intent.putExtra(Intents.PROF_DATA, (Serializable) profData);
        startActivity(intent);
//        addEvent(client);
      }
    });

    f2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(CalendarSampleActivity.this, BookEvent.class);
        intent.putExtra(Intents.PROF_DATA, (Serializable) profData);
        startActivity(intent);
//        addEvent(client);
      }
    });

    appointmentLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        fetchEvents(client);
        appointmentLayout.setRefreshing(false);
      }
    });

  }

  public void generateProfData(List<ProfDataObject> profDataObjects) {
    for (ProfDataObject p : profDataObjects) {
      profData.add(p);
      profDataMap.put(p.getEmail(), p);
    }
  }

  public void fetchEvents(com.google.api.services.calendar.Calendar service) {
    // Iterate over the events in the specified calendar
    //data.clear();
    com.google.api.services.calendar.Calendar[] a = new com.google.api.services.calendar.Calendar[]{service};

    FetchEvents fetchEvents = new FetchEvents(allEvents, this);
    fetchEvents.execute(a);
  }

  public void removeLoader() {
    getSupportFragmentManager().beginTransaction().remove(loadingFragment).commitAllowingStateLoss();
    //drawerLayout.getBackground().setAlpha(0);
    fm.setVisibility(View.GONE);
  }

  public void showLoader() {
    //drawerLayout.getBackground().setAlpha(127);
    if (loadingFragment.isAdded()) {
      return;
    }
    fm.setVisibility(View.VISIBLE);
    ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.loader, loadingFragment, "MyLoader").commit();
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void setupUI() {

    HashMap<String, String> dayMap = new HashMap<>();
    dayMap.put("Mon", "Monday");
    dayMap.put("Tue", "Tuesday");
    dayMap.put("Wed", "Wed");
    dayMap.put("Thu", "Thursday");
    dayMap.put("Fri", "Friday");
    dayMap.put("Sat", "Saturday");
    dayMap.put("Sun", "Sunday");
    data.clear();
    for (Event e : allEvents) {
      try {
        if (e.getSummary().contains("APPOINTO")) {
          AppointmentModel item = new AppointmentModel();
          try {
            item.name = e.getAttendees().get(0).getEmail();
          } catch (Exception e1) {
            item.name = "";
          }

          try {
            item.setTime(e.getStart().getDateTime().toString().substring(11, 16).concat("-").concat(e.getEnd().getDateTime().toString().substring(11, 16)));
            String s = item.getTime();
          } catch (Exception e1) {
            item.time = "";
          }

          try {
            item.date = e.getStart().getDateTime().toString().substring(0, 10);
          } catch (Exception e1) {
            item.date = "";
          }

          try {
            DateTime dt = e.getStart().getDateTime();
            String dtStr = dt.toString();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

            Date dt1 = format1.parse(dtStr);
            item.setDay(dayMap.get(dt1.toString().substring(0, 3)));
          } catch (Exception e1) {
            item.date = "";
          }

          try {
            String id = e.getId();
            item.setId(id);
          } catch (Exception p) {

          }
          try {
            if (e.getSummary().contains("APPOINTO")) {
              item.setImageUrl("https://www.thedailymash.co.uk/wp-content/uploads/40-something-man-2-1.jpg");
              data.add(item);
            }
          } catch (Exception es) {

          }
        }
      } catch (Exception em) {
      }
    }
    allEvents.clear();

    if (data.size() > 0) {
      noAppointmentLayout.setVisibility(View.GONE);
      appointmentLayout.setVisibility(View.VISIBLE);
      AppointmentAdapter appointmentAdapter = new AppointmentAdapter(data, this, profDataMap);
      appointmentRv.setLayoutManager(new LinearLayoutManager(this));
      appointmentRv.setAdapter(appointmentAdapter);
    } else {
      appointmentLayout.setVisibility(View.GONE);
      noAppointmentLayout.setVisibility(View.VISIBLE);
    }
    removeLoader();
  }

  @Override
  public void onDeletePress(String id) {
    deleteEvent(client, id);
  }

  @Override
  public void setImage(String url, ImageView v) {
    Glide.with(this).load(url)
        .crossFade()
        .thumbnail(0.5f)
        .bitmapTransform(new CircleTransform(this))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(v);
  }

  @Override
  public void onYesClick() {
    signOut();
  }

  @Override
  public void onNoClick() {

  }

  // Method to change the text status
  public void changeTextStatus(boolean isConnected) {
    if (isConnected) {
      String action;

    } else {
      Intent intent = new Intent(CalendarSampleActivity.this, BrokenActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
      startActivity(intent);
      finish();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    MyApplication.activityPaused();
    setIntent(new Intent());
  }

  class FetchEvents extends AsyncTask<com.google.api.services.calendar.Calendar, Void, List<Event>> {

    private List<Event> allEvents;
    private CalendarSampleActivity activity;

    public FetchEvents(List<Event> allEvents, CalendarSampleActivity activity) {
      this.allEvents = allEvents;
      this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      activity.showLoader();
    }

    @Override
    protected List<Event> doInBackground(com.google.api.services.calendar.Calendar... calendars) {
      String pageToken = null;
      Events events = null;
      java.util.Calendar c = java.util.Calendar.getInstance();
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String myDate = df.format(c.getTime());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      java.util.Date utilDate = new java.util.Date();

      try {
        utilDate = sdf.parse(myDate);
      } catch (ParseException pe) {
        pe.printStackTrace();
      }

      DateTime dateTime = new DateTime(utilDate);
      try {
        events = calendars[0].events().list("primary").setTimeMin(dateTime).setPageToken(null).execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
      List<Event> items = events.getItems();
      List<Event> a = new ArrayList<>();
      for (Event event : items) {
        a.add(event);
      }
      return a;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(List<Event> a) {
      super.onPostExecute(a);
      for (Event e : a) {
        allEvents.add(e);
      }
      activity.setupUI();
    }

  }

  public void deleteEvent(final com.google.api.services.calendar.Calendar service, final String eventId) {
    com.google.api.services.calendar.Calendar[] a = new com.google.api.services.calendar.Calendar[]{service};
    showLoader();
    new AsyncTask<com.google.api.services.calendar.Calendar, Void, Void>() {

      @Override
      protected Void doInBackground(com.google.api.services.calendar.Calendar... calendars) {
        try {
          calendars[0].events().delete("primary", eventId).execute();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        removeLoader();
        Toast.makeText(getApplicationContext(), "Event Deleted", Toast.LENGTH_LONG).show();
        fetchEvents(service);

      }
    }.execute(a);
  }


  public void addEvent(final com.google.api.services.calendar.Calendar service, String desc, String summary, String startDateTimes, String endDateTimes, String email) {
    final Event event = new Event()
        .setSummary(summary)
        .setLocation("Santa Clara University")
        .setDescription(desc);

    DateTime startDateTime = new DateTime(startDateTimes);
    EventDateTime start = new EventDateTime()
        .setDateTime(startDateTime)
        .setTimeZone("America/Los_Angeles");
    event.setStart(start);

    DateTime endDateTime = new DateTime(endDateTimes);
    EventDateTime end = new EventDateTime()
        .setDateTime(endDateTime)
        .setTimeZone("America/Los_Angeles");
    event.setEnd(end);

    EventAttendee[] attendees = new EventAttendee[]{
        new EventAttendee().setEmail(email),
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

    com.google.api.services.calendar.Calendar[] a = new com.google.api.services.calendar.Calendar[]{service};

    new AsyncTask<com.google.api.services.calendar.Calendar, Void, Void>() {

      String calendarId = "primary";

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        showLoader();
      }

      @Override
      protected Void doInBackground(com.google.api.services.calendar.Calendar... calendars) {
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

      @RequiresApi(api = Build.VERSION_CODES.N)
      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        removeLoader();
        fetchEvents(service);
      }
    }.execute(a);
  }

  void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    runOnUiThread(new Runnable() {
      public void run() {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
            connectionStatusCode, CalendarSampleActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
      }
    });
  }

  void refreshView() {
    adapter = new ArrayAdapter<CalendarInfo>(
        this, android.R.layout.simple_list_item_1, model.toSortedArray()) {

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        // by default it uses toString; override to use summary instead
        TextView view = (TextView) super.getView(position, convertView, parent);
        CalendarInfo calendarInfo = getItem(position);
        view.setText(calendarInfo.summary);
        return view;
      }
    };
    //listView.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    MyApplication.activityResumed();
    super.onResume();
    if (checkGooglePlayServicesAvailable() && isConnected) {
      haveGooglePlayServices();
    }
    if (getIntent().hasExtra(Intents.END_TIME)) {
      String descrip = getIntent().getStringExtra(Intents.DESCRIPTION);
      String start = getIntent().getStringExtra(Intents.START_TIME);
      String end = getIntent().getStringExtra(Intents.END_TIME);
      String title = getIntent().getStringExtra(Intents.TITLE);
      String date = getIntent().getStringExtra(Intents.DATE);
      String email = getIntent().getStringExtra(Intents.EMAIL);

      //2019-03-11T09:00:00-07:00
      String startDateTime = date.split("-")[2] + "-" + date.split("-")[0] + "-" + date.split("-")[1] +
                             "T09:00:00-" + start;
      String endDateTime = date.split("-")[2] + "-" + date.split("-")[0] + "-" + date.split("-")[1] +
                           "T09:00:00-" + end;
      removeLoader();
      addEvent(client, descrip, title, startDateTime, endDateTime, email);

    }
  }


  public void signOut() {
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build();

    GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
      @Override
      public void onComplete(@NonNull Task<Void> task) {
        Toast.makeText(getApplicationContext(), "Successfully Logged Out", Toast.LENGTH_LONG).show();
        String action;
        Intent intent = new Intent(CalendarSampleActivity.this, CalenderActivity.class);
        startActivity(intent);
      }
    });
    ;
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode == Activity.RESULT_OK) {
          haveGooglePlayServices();
        } else {
          checkGooglePlayServicesAvailable();
        }
        break;
      case REQUEST_AUTHORIZATION:
        if (resultCode == Activity.RESULT_OK) {
          AsyncLoadCalendars.run(this);
        } else {
          chooseAccount();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            credential.setSelectedAccountName(accountName);
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.commit();
            AsyncLoadCalendars.run(this);
          }
        }
        break;
      case ADD_OR_EDIT_CALENDAR_REQUEST:
        if (resultCode == Activity.RESULT_OK) {
          Calendar calendar = new Calendar();
          calendar.setSummary(data.getStringExtra("summary"));
          String id = data.getStringExtra("id");
          if (id == null) {
            new AsyncInsertCalendar(this, calendar).execute();
          } else {
            calendar.setId(id);
            new AsyncUpdateCalendar(this, id, calendar).execute();
          }
        }
        break;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_refresh:
        AsyncLoadCalendars.run(this);
        break;
      case R.id.menu_accounts:
        chooseAccount();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, CONTEXT_EDIT, 0, R.string.edit);
    menu.add(0, CONTEXT_DELETE, 0, R.string.delete);
    menu.add(0, CONTEXT_BATCH_ADD, 0, R.string.batchadd);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    int calendarIndex = (int) info.id;
    if (calendarIndex < adapter.getCount()) {
      final CalendarInfo calendarInfo = adapter.getItem(calendarIndex);
      switch (item.getItemId()) {
        case CONTEXT_EDIT:
          startAddOrEditCalendarActivity(calendarInfo);
          return true;
        case CONTEXT_DELETE:
          new AlertDialog.Builder(this).setTitle(R.string.delete_title)
              .setMessage(calendarInfo.summary)
              .setCancelable(false)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                  new AsyncDeleteCalendar(CalendarSampleActivity.this, calendarInfo).execute();
                }
              })
              .setNegativeButton(R.string.no, null)
              .create()
              .show();
          return true;
        case CONTEXT_BATCH_ADD:
          List<Calendar> calendars = new ArrayList<Calendar>();
          for (int i = 0; i < 3; i++) {
            Calendar cal = new Calendar();
            cal.setSummary(calendarInfo.summary + " [" + (i + 1) + "]");
            calendars.add(cal);
          }
          new AsyncBatchInsertCalendars(this, calendars).execute();
          return true;
      }
    }
    return super.onContextItemSelected(item);
  }

  public void onAddClick(View view) {
    startAddOrEditCalendarActivity(null);
  }

  /**
   * Check that Google Play services APK is installed and up to date.
   */
  private boolean checkGooglePlayServicesAvailable() {
    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
      return false;
    }
    return true;
  }

  private void haveGooglePlayServices() {
    // check if there is already an account selected
    if (credential.getSelectedAccountName() == null) {
      // ask user to choose account
      chooseAccount();
    } else {
      // load calendars
      AsyncLoadCalendars.run(this);
    }
  }

  private void chooseAccount() {
    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
  }

  private void startAddOrEditCalendarActivity(CalendarInfo calendarInfo) {
    Intent intent = new Intent(this, AddOrEditCalendarActivity.class);
    if (calendarInfo != null) {
      intent.putExtra("id", calendarInfo.id);
      intent.putExtra("summary", calendarInfo.summary);
    }
    startActivityForResult(intent, ADD_OR_EDIT_CALENDAR_REQUEST);
  }
}
