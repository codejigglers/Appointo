package examples.sdk.android.clover.com.appointo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    private final int GOOGLE_SIGN_IN_REQUEST = 1;
    private final int REQUEST_LOAD_IMG = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeFirebase();
        initializeSQLite();

        setContentView(R.layout.activity_main);
        initializePhotoPicker();
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

    private void initializePhotoPicker() {
        Button photoPickerButtom = findViewById(R.id.photoPickerButton);
        photoPickerButtom.setOnClickListener(this);
    }

    private void selectImageFromGalary() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_LOAD_IMG);
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
            case R.id.photoPickerButton:
                selectImageFromGalary();
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
        startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch(requestCode) {
            case GOOGLE_SIGN_IN_REQUEST:
                handleSignInActivityResult(data);
                break;
            case REQUEST_LOAD_IMG:
                handleImageLoadActivityResult(data);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImageLoadActivityResult(Intent data) {
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            byte imageInByte[] =compressImage(selectedImage);

            //save selected image in sqlite db
            final User dummyUser = new User("user_name",
                    "email_id", "college_Id", imageInByte);
            SQLiteHelper sqLiteHelper = SQLiteHelper.getInstance(this);
            sqLiteHelper.addOrUpdateUser(dummyUser);

            ImageView image_view = findViewById(R.id.imageView);
            image_view.setImageBitmap(deCompressImage(imageInByte));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong when selecting image", Toast.LENGTH_LONG).show();
        }
    }

    private byte[] compressImage(Bitmap image) {
        // convert bitmap to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap deCompressImage(byte[] imageInByte) {
        //convert byte to bitmap take from contact class
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageInByte);
        return BitmapFactory.decodeStream(imageStream);
    }

    private void handleSignInActivityResult(Intent data) {
        final String userName = data.getStringExtra(LoginActivity.USER_NAME);
        final String email = data.getStringExtra(LoginActivity.EMAIL);
        final String imageUri = data.getStringExtra(LoginActivity.IMAGE_URL);
        userInformation = new UserInformation(userName, email, imageUri);
        displayAccountDetails(userInformation);
    }

    public void displayAccountDetails(final UserInformation userInformation) {
        final String message = String.format("Username: %s email: %s, ImageUri: %s",
                userInformation.getUserName(),
                userInformation.getEmail(),
                userInformation.getImageUri());
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
