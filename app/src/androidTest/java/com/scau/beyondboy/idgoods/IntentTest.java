package com.scau.beyondboy.idgoods;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2016-01-07
 * Time: 12:57
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntentTest
{
//    @Rule
//    public final ActivityTestRule<MainActivity> rule =
//            new ActivityTestRule<>(MainActivity.class);
    @Rule
    public IntentsTestRule<MainActivity> mRule = new IntentsTestRule<>(MainActivity.class);
    @Test
    public void intentTest()
    {
       // Intents.init();
        clicktest(R.id.menu_toggle);
        clicktest(R.id.header_image);
        intended(hasComponent(PersonInfoActivity.class.getName()));
        clicktest(R.id.email_layout);
        intended(hasComponent(ChangeEmailActivity.class.getName()));
        //Intents.release();
    }
    private void clicktest(final int id)
    {
        onView(withId(id)).perform(click());
    }
}
