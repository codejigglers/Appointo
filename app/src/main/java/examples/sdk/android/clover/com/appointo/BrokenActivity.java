package examples.sdk.android.clover.com.appointo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BrokenActivity extends AppCompatActivity {

  Button button;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_broken);
    button = findViewById(R.id.retry);

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String action;
        Intent intent = new Intent(BrokenActivity.this, CalendarSampleActivity.class);
        startActivity(intent);
      }
    });
  }
}
