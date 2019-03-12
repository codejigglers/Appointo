package examples.sdk.android.clover.com.appointo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MainActivity extends BaseActivity implements AppointmentClickListener{

  private static final Level LOGGING_LEVEL = Level.OFF;

  private static final String PREF_ACCOUNT_NAME = "accountName";

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
  private LinearLayout appointmentLayout;
  private RecyclerView appointmentRv;
  CalenderRepository calenderRepository;

  @Override
  public int getLayoutResourceId() {
    return R.layout.activity_main;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    noAppointmentLayout = findViewById(R.id.no_appointment_view);
    appointmentLayout = findViewById(R.id.appointment_view);
    navigationView = (NavigationView) findViewById(R.id.nav_view);
    navHeader = navigationView.getHeaderView(0);
    profile = (ImageView) navHeader.findViewById(R.id.img_header_bg);
    drawerLayout = findViewById(R.id.drawer_layout);
    drawerLayout.getBackground().setAlpha(127);
    loadingFragment = LoadingFragment.newInstance();
    fm = findViewById(R.id.loader);
    appointmentRv = findViewById(R.id.appointment_recyclerview);
    ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.loader, loadingFragment, "MyLoader").commit();

    Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcToe-PSAektDgBsXLsdybQW6F1wGDdpw2mbm3SaReRPuQ0ec0ns")
        .crossFade()
        .thumbnail(0.5f)
        .bitmapTransform(new CircleTransform(this))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(profile);

    List<String> scopes = new ArrayList<>();
    scopes.add(CalendarScopes.CALENDAR);
    scopes.add(CalendarScopes.CALENDAR_EVENTS);

    credential =
        GoogleAccountCredential.usingOAuth2(this, scopes);
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
    // Calendar client
    client = new com.google.api.services.calendar.Calendar.Builder(
        transport, jsonFactory, credential).setApplicationName("Google-CalendarAndroidSample/1.0")
        .build();


    fetchData();
  }


  public void fetchEvents(com.google.api.services.calendar.Calendar service) {
    // Iterate over the events in the specified calendar

    com.google.api.services.calendar.Calendar[] a = new com.google.api.services.calendar.Calendar[]{service};
    new AsyncTask<com.google.api.services.calendar.Calendar, Void, Void>() {
      @Override
      protected Void doInBackground(com.google.api.services.calendar.Calendar... calendars) {
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
    }.execute(a);
  }


  public void removeLoader() {
    getSupportFragmentManager().beginTransaction().remove(loadingFragment).commitAllowingStateLoss();
    fm.setVisibility(View.GONE);
    drawerLayout.getBackground().setAlpha(0);
  }

  public void setupUI() {
    removeLoader();
//    if (data.size() > 0) {
//      noAppointmentLayout.setVisibility(View.GONE);
//      appointmentLayout.setVisibility(View.VISIBLE);
//      AppointmentAdapter appointmentAdapter = new AppointmentAdapter(data, this, );
//      appointmentRv.setLayoutManager(new LinearLayoutManager(this));
//      appointmentRv.setAdapter(appointmentAdapter);
//    } else {
//      appointmentLayout.setVisibility(View.GONE);
//      noAppointmentLayout.setVisibility(View.VISIBLE);
//    }

  }

  public void fetchData() {
    new AsyncTask<Void, Void, Integer>() {

      @Override
      protected Integer doInBackground(Void... voids) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return 1;
      }

      @Override
      protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        setupUI();
      }
    }.execute();

    AppointmentModel dataItem = new AppointmentModel();
    dataItem.date = "26, Jan, 2016";
    dataItem.time = "11:00 AM - 2:00 PM";
    dataItem.name = "Prof. Eskafi";

    data.add(dataItem);

    AppointmentModel dataItem2 = new AppointmentModel();
    dataItem2.date = "26, Feb, 2016";
    dataItem2.time = "1:00 AM - 2:00 PM";
    dataItem2.name = "Prof. Moataghed";

    data.add(dataItem2);

    AppointmentModel dataItem3 = new AppointmentModel();
    dataItem3.date = "26, Feb, 2016";
    dataItem3.time = "1:00 AM - 2:00 PM";
    dataItem3.name = "Prof. Moataghed";

    data.add(dataItem3);
    data.clear();

    AppointmentModel dataItem4 = new AppointmentModel();
    dataItem4.date = "26, Feb, 2016";
    dataItem4.time = "1:00 AM - 2:00 PM";
    dataItem4.name = "Prof. Moataghed";
    CalenderRepository calenderRepository = new CalenderRepository(this);
    List<Event> allData = calenderRepository.fetchEvents();
    for(Event e : allData) {
      AppointmentModel item = new AppointmentModel();
      item.name = e.getAttendees().get(0).getDisplayName();
      item.date = e.getOriginalStartTime().getDate().toString();
      item.time = e.getOriginalStartTime().getDateTime().toString();

      data.add(item);
    }
//    data.add(dataItem4);
//    data.clear();
  }

  @Override
  protected void onResume() {
    super.onResume();
    calenderRepository = new CalenderRepository(this);
    if (calenderRepository.checkGooglePlayServicesAvailable()) {
      calenderRepository.haveGooglePlayServices();
    }
  }

  @Override
  public void onDeletePress(String id) {

  }

  @Override
  public void setImage(String url, ImageView v) {

  }
}
