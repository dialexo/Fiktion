package ch.epfl.sweng.fiktion;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.fiktion.providers.AuthProvider;
import ch.epfl.sweng.fiktion.utils.Config;
import ch.epfl.sweng.fiktion.views.AddPOIActivity;
import ch.epfl.sweng.fiktion.views.HomeActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/** Tests that verification is required when trying to contribute
 * Created by Rodrigo on 30.11.2017.
 */

public class SignedInButNotVerifiedAuthCheckAddPoiTest {

    private final ViewInteraction homeMainLayout = onView(withId(R.id.home_main_layout));
    private final ViewInteraction menuDrawer = onView(withId(R.id.menu_drawer));


    @Rule
    public final ActivityTestRule<HomeActivity> mActivityRule =
            new ActivityTestRule<>(HomeActivity.class);

    @BeforeClass
    public static void setConfig(){
        Config.TEST_MODE = true;
    }

    @After
    public void resetAuth(){
        AuthProvider.destroyInstance();
    }
    @Before
    public void resetAuthAfter(){
        AuthProvider.destroyInstance();
    }


    private static ViewAction swipeRightFast() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }
    @Test
    public void ContributeVerifyCheckTest(){


        homeMainLayout.perform(swipeRightFast());
        menuDrawer.check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());


        onView(withText("Verify"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());

        onView(withText("Refresh"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.add_poi_fiction_button)).check(matches(isDisplayed()));
    }

    @Test
    public void ContributeVerifyCheckCancelTest(){

        homeMainLayout.perform(swipeRightFast());
        menuDrawer.check(matches(isDisplayed()));
        onData(anything()).inAdapterView(withId(R.id.menu_drawer)).atPosition(5).perform(click());

        onView(withText("Return"))
                .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.home_main_layout)).check(matches(isDisplayed()));
    }
}
