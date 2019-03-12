package examples.sdk.android.clover.com.appointo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ProfileActivity extends AppCompatActivity {
//  @Override
//  public void onBackPressed() {
//    Intent intent = new Intent(ProfileActivity.this, CalendarSampleActivity.class);
//    startActivity(intent);
//  }

  TextView fullName;
  TextView email;
  TextView collegeId;
  ImageView imageView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.profile);
    fullName = findViewById(R.id.fullName);
    email = findViewById(R.id.email);
    collegeId = findViewById(R.id.collegeId);
    imageView = findViewById(R.id.imageView10);
    updateData();
  }

  public void updateData() {
    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    if (acct != null) {
      fullName.setText(acct.getDisplayName());
      email.setText(acct.getEmail());
      if(acct.getPhotoUrl() != null) {
        Glide.with(this).load(acct.getPhotoUrl().toString())
            .crossFade()
            .override(300, 300)
            .thumbnail(0.5f)
            .bitmapTransform(new CircleTransform(this))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView);
      }
      Uri personPhoto = acct.getPhotoUrl();
    }

  }

}
