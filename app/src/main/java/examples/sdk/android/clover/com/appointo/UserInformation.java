package examples.sdk.android.clover.com.appointo;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class UserInformation {

    private final String userName;
    private final String email;
    private final String imageUri;

    public UserInformation(GoogleSignInAccount account) {
        this.userName = account.getDisplayName();
        this.email = account.getEmail();
        this.imageUri = account.getPhotoUrl() == null ?
                null : account.getPhotoUrl().toString();
    }

    public UserInformation(final String userName,
                           final String email,
                           final String imageUri) {
        this.userName = userName;
        this.email = email;
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUri() {
        return imageUri;
    }
}
