package ch.epfl.sweng.fiktion.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.fiktion.R;
import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;

/**
 * This activity displays the user's information and allows him to apply changes to its profile
 *
 * @author Rodrigo
 */
public class UserDetailsActivity extends AppCompatActivity {

    //constants
    //LOGCAT
    private static final String TAG = "UserDetails";

    //UI modes
    private enum UIMode {
        defaultMode,
        userSignedOut
    }

    //Authenticator initiation

    private final AuthProvider auth = Providers.auth;

    //views
    private TextView user_name_view;
    private TextView user_email_view;
    private TextView user_verify_view;

    //Buttons
    //Strings
    private String email;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Log.d(TAG, "Initialising User Details activity");

        //initialise views
        user_name_view = (TextView) findViewById(R.id.detail_user_name);
        user_email_view = (TextView) findViewById(R.id.detail_user_email);
        user_verify_view = (TextView) findViewById(R.id.detail_user_verify);


        //initialise button

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Started UserDetailsActivity");

        //initialise user details and firebase authentication

        if (auth.isConnected()) {
            Log.d(TAG, "User signed in");
            // Name, email address, and profile photo Url
            User user = auth.getCurrentUser();
            name = user.getName();
            email = user.getEmail();
            if (user.isEmailVerified()) {
                user_verify_view.setText("Email is verified");
            } else {
                user_verify_view.setText("Email is not verified");
            }

            //Uri photoUrl = user.getPhotoUrl();
            //String uid = user.getID();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead. [I will keep this advice for now]
            updateUI(UIMode.defaultMode);
        } else {
            //this case will probably never happen
            Log.d(TAG, "Could not initialise user details, user is not signed in");
        }

    }

    /**
     * This method signs the user out from Fiktion
     */
    private void signOut() {

        if (auth.isConnected()) {
            auth.signOut();
            //firebase authentication listener will see
            // that user signed out and call onAuthStateChanged
            updateUI(UIMode.userSignedOut);
            Log.d(TAG, "User is signed out");
        }
    }


    /**
     * this method will set the UI according to the mode it is.
     * It will prompt a sign in if the user is not currently signed in
     * It will display user's informations if he is signed in
     *
     * @param mode UIMode that we want to set for the UI
     */
    private void updateUI(UIMode mode) {
        if (mode.equals(UIMode.defaultMode)) {
            //UI default mode
            //initialise views and buttons
            user_name_view.setText(name);
            user_email_view.setText(email);
        } else if (mode.equals(UIMode.userSignedOut)) {
            Log.d(TAG, "Return to signIn activity");
            Intent login = new Intent(this, SignInActivity.class);
            finish();
            startActivity(login);
        }
    }


    /**
     * Starts the sign out request
     *
     */
    public void clickSignOut(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Signing Out");
        signOut();
    }

    /**
     * Starts and activity where the user can edit his profile
     */
    public void clickEditAccount(@SuppressWarnings("UnusedParameters") View v) {
        Log.d(TAG, "Start Edit Account Activity");
        Intent editAccountActivity = new Intent(this, ProfileSettingsActivity.class);
        startActivity(editAccountActivity);
    }
}
