package examples.sdk.android.clover.com.appointo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CalenderActivity extends AppCompatActivity implements View.OnClickListener {


  private int RC_SIGN_IN = 1;
  private GoogleSignInClient mGoogleSignInClient;

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_activity);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build();

    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    SignInButton signInButton = findViewById(R.id.sign_in_button);
    signInButton.setSize(SignInButton.SIZE_STANDARD);
    updateUI();

    findViewById(R.id.sign_in_button).setOnClickListener(this);
  }


  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.sign_in_button:
        signIn();
        break;
    }
  }

  private void signIn() {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  private void signOut() {
    mGoogleSignInClient.signOut()
        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
          }
        });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      handleSignInResult(task);
    }
    finish();
  }

  private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
    try {
      GoogleSignInAccount account = completedTask.getResult(ApiException.class);
      updateUI();
    } catch (ApiException e) {
    }
  }

  private void updateUI() {
    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    if (acct != null) {
      String personName = acct.getDisplayName();
      Toast.makeText(getApplicationContext(), personName, Toast.LENGTH_LONG).show();
      Intent intent = new Intent(CalenderActivity.this, CalendarSampleActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivityForResult(intent, 2);
    }
  }
}
