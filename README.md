# Appointment app integration with Gmail

# Flow of of login/ logout user case in app
* Profile activity is launcher activity
* If user has not signed in with gmail ProfileActivity redirects to LoginActivity for Login with Gmail.
* Once users is signedIn with gmail SignInActivity returns following userInformation to Profile Activity
    ** UserName
    ** Email
    ** Profile picture (Not that this value is nullable)
* Profile Activity has signout button. If user clicks on sign out button users is signed out with gmail 
and Profile activity redirects to Login Activity for prompting user to signin.