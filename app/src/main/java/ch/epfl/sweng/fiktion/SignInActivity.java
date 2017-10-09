package ch.epfl.sweng.fiktion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "SignIn";
    private EditText UserEmail;
    private EditText UserPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Log.d(TAG, "Initialising sign in activity");

        //Initialise user content

        //Views
        UserEmail = (EditText) findViewById(R.id.User_Email);
        UserPassword = (EditText) findViewById(R.id.User_Password);

        //Firebase Authenticator
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // If User is signed in, UI will adapt, if User is null , UI will prompt a sign in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
        updateUI(currentUser);
    }

    private boolean validateCredentials() {
        boolean validEmail = false;
        boolean validPassword = false;
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        Log.d(TAG, "Validating credentials");

        if (password.isEmpty()) {
            UserPassword.setError("Password is required");
            Log.d(TAG, "Password validation failed");
        } else {
            if (password.length() >= 6) {
                validPassword = true;
                UserPassword.setError(null);
            } else {
                UserPassword.setError("Password must be of at least 6 characters");
                Log.d(TAG, "Password validation failed");
            }
        }
        if (email.contains("@")) {
            validEmail = true;
            UserEmail.setError(null);
        } else {
            UserEmail.setError("Require a valid email");
            Log.d(TAG, "Email validation failed");

        }


        return validEmail && validPassword;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateCredentials()) {
            Log.d(TAG, "Not valid credentials");
            findViewById(R.id.SignInFailedView).setVisibility(View.VISIBLE);
            return;
        }
        Log.d(TAG, "signIn:" + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            findViewById(R.id.SignInFailedView).setVisibility(View.VISIBLE);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //start details activity
            Intent user_details_activity = new Intent(this, UserDetailsActivity.class);
            startActivity(user_details_activity);
            findViewById(R.id.SignInFailedView).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        //user clicks on signin button
        if (i == R.id.SignInButton) {
            signIn(UserEmail.getText().toString(), UserPassword.getText().toString());
        }
        //user clicks on register button
        else if (i == R.id.RegisterButton) {
            Intent registerActivity = new Intent(this, RegisterActivity.class);
            startActivityForResult(registerActivity,1);
        }
    }


}
