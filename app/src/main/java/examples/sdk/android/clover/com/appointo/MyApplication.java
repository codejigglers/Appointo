package examples.sdk.android.clover.com.appointo;

import android.app.Application;

public class MyApplication extends Application {

  private static MyApplication mInstance;

  public static boolean activityVisible; // Variable that will check the
  // current activity state

  public static boolean isActivityVisible() {
    return activityVisible; // return true or false
  }

  public static void activityResumed() {
    activityVisible = true;// this will set true when activity resumed
  }

  public static void activityPaused() {
    activityVisible = false;// this will set false when activity paused

  }
}