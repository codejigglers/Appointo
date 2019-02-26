package examples.sdk.android.clover.com.appointo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private UserInformation userInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login();
        Button signOutButton = findViewById(R.id.signoutButton);
        signOutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signoutButton:
                signOut();
                break;
        }
    }

    private void signOut() {
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        redirectToLoginPage();
                    }
                });
    }

    public void login() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            userInformation = new UserInformation(account);
            displayAccountDetails(userInformation);
        } else {
            //redirect to login page
            redirectToLoginPage();
        }
    }

    public void redirectToLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) {
            return;
        }
        final String userName = data.getStringExtra(LoginActivity.USER_NAME);
        final String email = data.getStringExtra(LoginActivity.EMAIL);
        final String imageUri = data.getStringExtra(LoginActivity.IMAGE_URL);
        userInformation = new UserInformation(userName, email, imageUri);
        displayAccountDetails(userInformation);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void displayAccountDetails(final UserInformation userInformation) {
        final String message = String.format("Username: %s email: %s, ImageUri: %s",
                userInformation.getUserName(),
                userInformation.getEmail(),
                userInformation.getImageUri());
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
