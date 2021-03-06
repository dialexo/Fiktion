package ch.epfl.sweng.fiktion.providers;

import ch.epfl.sweng.fiktion.listeners.Failure;
import ch.epfl.sweng.fiktion.listeners.Success;
import ch.epfl.sweng.fiktion.utils.Config;

/**
 * Authentication provider
 * Created by rodri on 17.10.2017.
 */

public abstract class AuthProvider {

    private static AuthProvider auth;

    /**
     * @return an instance of an authentication provider
     */
    public static AuthProvider getInstance() {
        if (auth == null) {
            if (Config.TEST_MODE) {
                auth = new LocalAuthProvider();
            } else {
                auth = new FirebaseAuthProvider();
            }
        }
        return auth;
    }

    /**
     * Sets the current instance to the given authentication instance
     *
     * @param authInstance authentication instance
     */
    public static void setInstance(AuthProvider authInstance) {
        auth = authInstance;
    }

    /**
     * Destroys current authentication instance
     */
    public static void destroyInstance() {
        auth = null;
    }

    /**
     * Defines what actions to take on auth op callback
     */
    public interface AuthListener extends Success, Failure {
    }

    /**
     * Signs in a user with an email, a password and what to do afterwards
     *
     * @param email    user email
     * @param password user password
     * @param listener what to do after login
     */
    public abstract void signIn(String email, String password, AuthListener listener);

    /**
     * Signs the user out of the application
     */
    public abstract void signOut();

    /**
     * Validate the email provided by the user.
     *
     * @param email provided by the user
     * @return empty string if valid, error message otherwise
     */
    public abstract String validateEmail(String email);

    /**
     * Validate the password provided by the user.
     *
     * @param password provided by the user
     * @return empty string if valid, error message otherwise
     */
    public abstract String validatePassword(String password);

    /**
     * Creates a new account using the provided informations
     *
     * @param password used to create the account
     */
    public abstract void createUserWithEmailAndPassword(String email, String password, final AuthListener listener);


    /**
     * Sends a password reset mail, defines what to do afterwards
     *
     * @param listener what to do after email attempt
     */
    public abstract void sendPasswordResetEmail(AuthListener listener);

    /**
     * Sends an email verification to the current user connected
     *
     * @param listener awaits the result and acts accordingly
     */
    public abstract void sendEmailVerification(AuthListener listener);

    /**
     * Verifies if the user is currently connected or not
     *
     * @return true if user is signed in, false otherwise
     */
    public abstract Boolean isConnected();

    /**
     * starts a request to database to have currently signed in User or null if there is not any
     *
     * @param listener handles what to do after the request
     */
    public abstract void getCurrentUser(DatabaseProvider.GetUserListener listener);

    /*
     * Enables the user to change his primary email
     * @param email new email provided by the user
     * @param listener actions to be done in case of failure or success
     */
    public abstract void changeEmail(String email, final AuthListener listener);

    /**
     * Enables the user to delete his account if he has signed in recently
     *
     * @param listener    actions to be done in case of failure or success in firebase authentication
     * @param delListener actions to be done in case of failure, sucess or inexistant in database
     */
    public abstract void deleteAccount(AuthListener listener, DatabaseProvider.DeleteUserListener delListener);

    /**
     * @return true if user is email verified, false otherwise
     */
    public abstract Boolean isEmailVerified();

    /**
     * @return email of the current signed in user, null if there is not any user connected
     */
    public abstract String getEmail();
}
