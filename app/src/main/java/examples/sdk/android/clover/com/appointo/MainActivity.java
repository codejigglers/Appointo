package examples.sdk.android.clover.com.appointo;

import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MainActivity extends BaseActivity {

  public FragmentTransaction ft;
  FrameLayout fm;
  public Fragment loadingFragment;
  protected DrawerLayout drawerLayout;
  protected ImageView profile;
  private View navHeader;
  private NavigationView navigationView;

  @Override
  public int getLayoutResourceId() {
    return R.layout.activity_main;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    navigationView = (NavigationView) findViewById(R.id.nav_view);
    navHeader = navigationView.getHeaderView(0);
    profile = (ImageView) navHeader.findViewById(R.id.img_header_bg);
    drawerLayout = findViewById(R.id.drawer_layout);
    drawerLayout.getBackground().setAlpha(127);
    loadingFragment = LoadingFragment.newInstance();
    fm = findViewById(R.id.loader);
    ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.loader, loadingFragment, "MyLoader").commit();

    Glide.with(this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcToe-PSAektDgBsXLsdybQW6F1wGDdpw2mbm3SaReRPuQ0ec0ns")
        .crossFade()
        .thumbnail(0.5f)
        .bitmapTransform(new CircleTransform(this))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(profile);

    new AsyncTask<Void, Void, Integer>() {

      @Override
      protected Integer doInBackground(Void... voids) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        return 1;
      }

      @Override
      protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        removeLoader();
      }
    }.execute();

  }

  public void removeLoader() {
    getSupportFragmentManager().beginTransaction().remove(loadingFragment).commitAllowingStateLoss();
    fm.setVisibility(View.GONE);
    drawerLayout.getBackground().setAlpha(0);
  }


}
