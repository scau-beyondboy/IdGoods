package com.scau.beyondboy.idgoods;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.scau.beyondboy.idgoods.view.SlideListView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-12-14
 * Time: 20:16
 */
@RunWith(AndroidJUnit4.class)
public class ProductTest
{
    private static final String TAG = ProductTest.class.getName();
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule=new ActivityTestRule<MainActivity>(MainActivity.class);
    private void clicktest(final int id)
    {
        onView(withId(id)).perform(click());
    }

    private String changetexttest(final int id, String text)
    {
        onView(withId(id)).perform(clearText(),typeText(text),closeSoftKeyboard());
        return text;
    }

    private void checktexttest(final int id,String text)
    {
        onView(withId(id)).check(matches(withText(text)));
    }
    @Test
    public void testcout()
    {
        //int i=0;
        for (int i = 0; i < 1; i++)
        {
            testClickOnItem();
        }
    }
    @Test
    public void testClickOnItem()
    {
        clicktest(R.id.menu_toggle);
        clicktest(R.id.myproduct);
        int waitingTime=1000;
        //设置测试超时时间
        IdlingPolicies.setMasterPolicyTimeout(
                waitingTime * 10, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(
                waitingTime * 10, TimeUnit.MILLISECONDS);
        IdlingResource idlingResource=new ListAdapterIdlingResource(1000,(SlideListView)mActivityTestRule.getActivity().findViewById(R.id.product_slidelistview));
        //等待后台ListView加载完数据后执行后面的代码
        Espresso.registerIdlingResources(idlingResource);
        //onData(withItemContent("CABF42677fdsaf1"))；
        /*try
        {
            Thread.sleep(2000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/
        /*onData(withItemContent("3D4179CAFCC20"))
                .perform(click());*/
        onData(anything()).inAdapterView(withId(R.id.product_slidelistview)).atPosition(10).perform(click());
        //释放对其异步空闲处理类
        Espresso.unregisterIdlingResources(idlingResource);
        clicktest(R.id.header_image);
        checktexttest(R.id.product_name, "其他的商品");
        pressBack();
        pressBack();
        //mActivityTestRule.getActivityIntent();
        //.perform(click());
    }

    public  Matcher<Object> withItemContent(String expectedText) {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        //checkNotNull(expectedText);
        System.out.println("expectedText = [" + expectedText + "]");
        return withItemContent(equalTo(expectedText));
    }
    @SuppressWarnings("rawtypes")
    public  Matcher<Object> withItemContent(final Matcher<String> itemTextMatcher) {
        // use preconditions to fail fast when a test is creating an invalid matcher.
        //checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, Map>(Map.class) {
            @Override
            public boolean matchesSafely(Map map) {
                return hasEntry(equalTo("STR"), itemTextMatcher).matches(map);
            }

            @Override
            public void describeTo(Description description) {
                Log.i(TAG,"description:  "+description );
                description.appendText("with item content: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }
}
