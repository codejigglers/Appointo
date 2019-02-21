package examples.sdk.android.clover.com.appointo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends AppCompatActivity {


  private View mContentView;
  Intent intent;

  private final Runnable mShowPart2Runnable = new Runnable() {
    @Override
    public void run() {
      // Delayed display of UI elements
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
        actionBar.show();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_splash_screen);
    intent = new Intent(this, MainActivity.class);

    animate();
    new AsyncTask<String, String, Integer>() {
      @SuppressLint("WrongThread")
      @Override
      protected Integer doInBackground(String... strings) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        return 3;
      }

      @Override
      protected void onPostExecute(Integer aVoid) {
        startActivity(intent);
        finish();
      }
    }.execute();

  }

  @Override
  protected void onResume() {
    super.onResume();

  }

  public void animate() {
    ImageView img = (ImageView)findViewById(R.id.imgview);
    Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
    img.startAnimation(aniFade);
  }

}
