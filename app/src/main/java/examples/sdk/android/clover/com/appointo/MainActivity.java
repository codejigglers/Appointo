package examples.sdk.android.clover.com.appointo;

import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

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

    fetchData();
  }

  public void removeLoader() {
    getSupportFragmentManager().beginTransaction().remove(loadingFragment).commitAllowingStateLoss();
    fm.setVisibility(View.GONE);
    drawerLayout.getBackground().setAlpha(0);
  }

  public void setupUI() {
    removeLoader();
    if (data.size() > 0) {
      noAppointmentLayout.setVisibility(View.GONE);
      appointmentLayout.setVisibility(View.VISIBLE);
      AppointmentAdapter appointmentAdapter = new AppointmentAdapter(data);
      appointmentRv.setLayoutManager(new LinearLayoutManager(this));
      appointmentRv.setAdapter(appointmentAdapter);
    } else {
      appointmentLayout.setVisibility(View.GONE);
      noAppointmentLayout.setVisibility(View.VISIBLE);
    }

  }

  public void fetchData() {
    new AsyncTask<Void, Void, Integer>() {

      @Override
      protected Integer doInBackground(Void... voids) {
        try {
          Thread.sleep(5000);
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

    AppointmentModel dataItem4 = new AppointmentModel();
    dataItem4.date = "26, Feb, 2016";
    dataItem4.time = "1:00 AM - 2:00 PM";
    dataItem4.name = "Prof. Moataghed";

    data.add(dataItem4);
    data.clear();
  }

}
