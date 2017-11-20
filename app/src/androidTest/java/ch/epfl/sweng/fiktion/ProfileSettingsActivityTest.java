package ch.epfl.sweng.fiktion;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.providers.AuthSingleton;
import ch.epfl.sweng.fiktion.providers.DatabaseProvider;
import ch.epfl.sweng.fiktion.providers.DatabaseSingleton;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.LocalDatabaseProvider;
import ch.epfl.sweng.fiktion.views.SettingsActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * This class tests if the profile editing happens correctly
 * Created by Rodrigo on 25.10.2017.
 */

public class ProfileSettingsActivityTest {


    private User user;

    @Rule
    public final ActivityTestRule<SettingsActivity> editProfileActivityRule =
            new ActivityTestRule<>(SettingsActivity.class);

    @BeforeClass
    public static void setAuth() {
        AuthSingleton.auth = new LocalAuthProvider();
        DatabaseSingleton.database = new LocalDatabaseProvider();
    }

    @Before
    public void setVariables() {
        AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User currUser) {
                user = currUser;
            }

            @Override
            public void onDoesntExist() {
                user = null;
            }

            @Override
            public void onFailure() {
                user = null;
            }
        });
    }

    @After
    public void resetAuth() {
        AuthSingleton.auth = new LocalAuthProvider();
        DatabaseSingleton.database = new LocalDatabaseProvider();
        //wait until all toasts disappear
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void changeUserInfos_newValues() {
        //change name
        final String newName = "new name";
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());
        //change email
        final String newEmail = "new@email.ch";
        onView(withId(R.id.emailEdit)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        assertThat(user.getName(), is("new name"));

        AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                assertThat(AuthSingleton.auth.getEmail(), is(newEmail));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

    }

    @Test
    public void changeUserEmail_invalid() {
        String newEmail = "";
        onView(withId(R.id.emailEdit)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                //assert that we can only write 15 characters
                assertThat(AuthSingleton.auth.getEmail(), is("default@email.ch"));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }


            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

    }

    @Test
    public void changeUserName_invalid() {
        String newName = "";
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());
        newName = "thishasmorethan15characters";
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                //assert that we can only write 15 characters
                assertThat(user.getName(), is("thishasmorethan"));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }


            @Override
            public void onFailure() {
                Assert.fail();
            }
        });

    }

    @Test
    public void changeUserInfos_sameValues() {
        //change name
        final String newName = user.getName();
        onView(withId(R.id.usernameEdit)).perform(typeText(newName), closeSoftKeyboard());


        //change email
        final String newEmail = AuthSingleton.auth.getEmail();
        onView(withId(R.id.emailEdit)).perform(typeText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.saveAccountSettingsButton)).perform(click());

        onView(withId(R.id.usernameEdit)).check(matches(hasErrorText("Please type a new and valid username")));
        onView(withId(R.id.emailEdit)).check(matches(hasErrorText("Please type a new and valid email")));


        AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
            @Override
            public void onSuccess(User user) {
                assertThat(user.getName(), is(newName));
                assertThat(AuthSingleton.auth.getEmail(), is(newEmail));
            }

            @Override
            public void onDoesntExist() {
                Assert.fail();
            }


            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void successDeleteAccount() {
        onView(withId(R.id.deleteAccountButton)).perform(click());
        //check that list of user that by default only has one user is now empty
        onView(withText("Delete"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        AuthSingleton.auth.signIn(AuthSingleton.auth.getEmail(), "testing", new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                AuthSingleton.auth.getCurrentUser(DatabaseSingleton.database, new DatabaseProvider.GetUserListener() {
                    @Override
                    public void onSuccess(User user) {
                        Assert.fail();
                    }

                    @Override
                    public void onDoesntExist() {
                        Assert.fail();
                    }

                    @Override
                    public void onFailure() {
                        //success
                    }
                });
            }
        });
    }

    @Test
    public void cancelDeleteAccount() {
        onView(withId(R.id.deleteAccountButton)).perform(click());
        //check that list of user that by default only has one user is now empty
        onView(withText("Cancel"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.accountSettingsTitle)).check(matches(isDisplayed()));

    }


    @Test
    public void failNoUserSignedInDeleteAccount() {
        AuthSingleton.auth.signOut();
        //we try to delete the same account with no user currently connected -> failure, toast should appear
        onView(withId(R.id.deleteAccountButton)).perform(click());
        onView(withText("Delete"))
                .inRoot(withDecorView(not(is(editProfileActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withId(R.id.accountLoginButton)).check(matches(isDisplayed()));

    }

    @Test
    public void alreadyVerifiedSendEmailVerification() {
        onView(withId(R.id.verifiedButton)).check(matches(not(isDisplayed())));
        //should send an email verification since the user is already connected (default user)
    }

    @Test
    public void successAndNotSignedInSendEmailVerification() {
        //in our local auth we have only one user with a verified account,
        //we must delete this account and create a new one
        //without a verified email
        AuthSingleton.auth.deleteAccount(new AuthProvider.AuthListener() {
            @Override
            public void onSuccess() {
                //we succeeded in deleting in firebase
            }

            @Override
            public void onFailure() {
                //should be able to delete current account because there is one connected by default
                Assert.fail();
            }
        }, new DatabaseProvider.DeleteUserListener() {
            @Override
            public void onSuccess() {
                //we successfully deleted the account on the database
                AuthSingleton.auth.createUserWithEmailAndPassword(DatabaseSingleton.database, "new@email", "newpassword", new AuthProvider.AuthListener() {
                    @Override
                    public void onSuccess() {
                        //we try to send an email to a unverified account,
                        //this account was just created successfully
                        final Activity testActivity = editProfileActivityRule.getActivity();
                        getInstrumentation().runOnMainSync(new Runnable() {
                            @Override
                            public void run() {
                                testActivity.recreate();
                            }
                        });
                        onView(withId(R.id.verifiedButton)).perform(click());

                        AuthSingleton.auth.signOut();
                        onView(withId(R.id.verifiedButton)).perform(click());
                        onView(withId(R.id.accountLoginButton)).check(matches(isDisplayed()));
                    }

                    @Override
                    public void onFailure() {
                        //should be able to create account with given paramaters
                        Assert.fail();
                    }
                });
            }


            @Override
            public void onDoesntExist() {
                Assert.fail();
            }

            @Override
            public void onFailure() {
                Assert.fail();
            }
        });
    }

    @Test
    public void successResetPassword() {
        onView(withId(R.id.passwordReset)).perform(click());
        //should send an email verification since the user is already connected (default user)
    }

    @Test
    public void failResetPassword() {
        AuthSingleton.auth.signOut();
        onView(withId(R.id.passwordReset)).perform(click());
        //should send an email verification since the user is already connected (default user)
        onView(withId(R.id.accountLoginButton)).check(matches(isDisplayed()));
    }
}
