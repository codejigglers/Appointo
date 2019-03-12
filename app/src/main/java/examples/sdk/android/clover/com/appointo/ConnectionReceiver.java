package examples.sdk.android.clover.com.appointo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ConnectionReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    try {

      boolean isVisible = MyApplication.isActivityVisible();// Check if
      Log.i("Activity is Visible ", "Is activity visible : " + isVisible);

      if (isVisible == true) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
            .getActiveNetworkInfo();

        // Check internet connection and accrding to state change the
        // text of activity by calling method
        if (networkInfo != null && networkInfo.isConnected()) {

          new CalendarSampleActivity().changeTextStatus(true);
        } else {
          new CalendarSampleActivity().changeTextStatus(false);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
