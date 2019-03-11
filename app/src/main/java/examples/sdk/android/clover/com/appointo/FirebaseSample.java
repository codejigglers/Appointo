package examples.sdk.android.clover.com.appointo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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

public class FirebaseSample {
    private FirebaseDatabase mDatabase;
    DatabaseReference mUserLocationInformationDBReference;

    private final static String TAG = "FirebaseSample";

    public FirebaseSample(Context context) {
        FirebaseApp.initializeApp(context);
        mDatabase = FirebaseDatabase.getInstance();
        mUserLocationInformationDBReference = mDatabase.getReference();
        // Read from the database
        mUserLocationInformationDBReference.child("Professors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateUserLocationDataFromFirebase(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserLocationDataFromFirebase(DataSnapshot dataSnapshot) {
        Log.d(TAG, "children count: " + dataSnapshot.getChildrenCount());
        List<UserLocation> userLocationList = new ArrayList<>();

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
        printProfessorNames(userLocationList);
    }

    private void printProfessorNames(List<UserLocation> userLocationList) {
        Log.d(TAG, "Professor Names");
        for (UserLocation userLocation: userLocationList) {
            Log.d(TAG, userLocation.professorName);
        }
    }

}
