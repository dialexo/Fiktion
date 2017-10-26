package ch.epfl.sweng.fiktion;

import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.models.User;
import ch.epfl.sweng.fiktion.providers.LocalAuthProvider;
import ch.epfl.sweng.fiktion.providers.Providers;
import ch.epfl.sweng.fiktion.views.UserDetailsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**This activity will test the UI part of the User Details Activity
 * Created by Rodrigo on 22.10.2017.
 */


public class UserDetailsActivityTest {

    private User user;
    @Rule
    public final ActivityTestRule<UserDetailsActivity> userDetActivityRule =
            new ActivityTestRule<>(UserDetailsActivity.class);

    @BeforeClass
    public static void setAuth(){
        Providers.auth = new LocalAuthProvider();
    }

    @Before
    public void setVariables(){
        user = Providers.auth.getCurrentUser();
    }

    @After
    public void resetAuth(){
        Providers.auth = new LocalAuthProvider();
    }

    @Test
    public void seeDefaultUserInformations(){
        onView(withId(R.id.detail_user_email)).check(matches(withText(user.getEmail())));
        onView(withId(R.id.detail_user_name)).check(matches(withText(user.getName())));
    }

    @Test
    public void clickEditAccount(){
        onView(withId(R.id.detail_edit_account)).perform(click());

        //check we see the ProfileSettingsActivity
        onView(withId(R.id.update_confirm_email)).check(matches(isDisplayed()));
    }

    @Test
    public void clickSignOut(){
        onView(withId(R.id.detail_signout)).perform(click());
        //check we come back to sign in activity
        onView(withId(R.id.User_Email)).check(matches(isDisplayed()));
    }

}
