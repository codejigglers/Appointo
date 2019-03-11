package examples.sdk.android.clover.com.appointo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserLocation {
    public String professorName;
    public String locationName;
    public long longitude;
    public long latitude;


    public UserLocation() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserLocation(String professorName, String locationName, long longitude, long latitude) {
        this.professorName = professorName;
        this.locationName = locationName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }
}
