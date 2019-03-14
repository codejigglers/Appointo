package examples.sdk.android.clover.com.appointo;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import examples.sdk.android.clover.com.appointo.sqlite.SQLiteHelper;
import examples.sdk.android.clover.com.appointo.sqlite.User;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private UserInformation userInformation;
    private static String TAG = "ProfileActivity";
    List<UserLocation> userLocationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFirebase();
        initializeSQLite();
        setContentView(R.layout.activity_main);
        login();
        Button signOutButton = findViewById(R.id.signoutButton);
        signOutButton.setOnClickListener(this);
    }

    private void initializeSQLite() {
        String pic = "pic";
        final User dummyUser = new User("user_name",
                "email_id", "college_Id", pic.getBytes());
        SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(this);
        sqLiteHelper.addOrUpdateUser(dummyUser);
        List<User> users = sqLiteHelper.getAllData();
        for (User user: users) {
            Log.d(TAG, "userName: " + user.getUserName());
            Log.d(TAG, "emailId: " + user.getEmailId());
        }
    }

    private void initializeFirebase() {
        FirebaseApp.initializeApp(this);
        final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference professorLocationDBReference = mDatabase.getReference();
        professorLocationDBReference.child("Professors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange");
                updateUserLocationDataFromFirebase(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUserLocationDataFromFirebase(DataSnapshot dataSnapshot) {
        Log.d(TAG, "children count: " + dataSnapshot.getChildrenCount());
        userLocationList = new ArrayList<>();

        for (DataSnapshot ds: dataSnapshot.getChildren()) {
            Map<String, Object> valueMap = (HashMap) ds.getValue();
            UserLocation userLocation = new UserLocation();
            userLocation.setProfessorName((String)valueMap.get("ProfessorName"));
            userLocation.setLocationName((String)valueMap.get("LocationName"));
            userLocation.setLongitude((Long)valueMap.get("Longitude"));
            userLocation.setLatitude((Long)valueMap.get("Latitude"));
            userLocationList.add(userLocation);
        }

        Log.d(TAG, "userLocationList size: " + userLocationList.size());
        printProfessorNames();
    }

    public void printProfessorNames() {
        Log.d(TAG, "Professor Names");
        for (UserLocation userLocation: userLocationList) {
            Log.d(TAG, userLocation.professorName);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
