package com.scau.beyondboy.idgoods;

import android.support.test.espresso.contrib.PickerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.DatePicker;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Author:beyondboy
 * Gmail:xuguoli.scau@gmail.com
 * Date: 2015-10-25
 * Time: 16:21
 */
//设置测试运行环境
@RunWith(AndroidJUnit4.class)
public class PersonInforTest
{
    private static final String TAG = PersonInforTest.class.getName();
    //设置启动的Activity
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule=new ActivityTestRule<MainActivity>(MainActivity.class);
    //模拟用户的点击行为
    private void clicktest(final int id)
    {
        onView(withId(id)).perform(click());
    }
    //改变View的文本显示
    private String changetexttest(final int id,String text)
    {
        onView(withId(id)).perform(clearText(),typeText(text),closeSoftKeyboard());
        return text;
    }
    //检查View文本变化是否正确
    private void checktexttest(final int id,String text)
    {
        onView(withId(id)).check(matches(withText(text)));
    }

    //测试方法
   @Test
    public void NickNameTest()
   {
       clicktest(R.id.menu_toggle);
      // clicktest(R.id.header_image);
       clicktest(R.id.nickname_layout);
       final String text=changetexttest(R.id.nickname,"test");
       clicktest(R.id.save);
       checktexttest(R.id.nickname, text);
   }


    @Test
    public void birthdayTest()
    {
        int year = 2020;
        int month = 11;
        int day = 15;
        onView(withId(R.id.menu_toggle)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.header_image)).perform(click());
        clicktest(R.id.birthday_layout);
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month + 1, day));
        clicktest(R.id.comfirm);
        checktexttest(R.id.birthday, String.format("%d年%d月%d日", year, month + 1, day));
        clicktest(R.id.sex_layout);
        clicktest(R.id.female);
        clicktest(R.id.comfirm);
        Log.i(TAG,"输出内容" );
        checktexttest(R.id.sex,"女");
    }

    @Test
    public void sexTest()
    {
        onView(withId(R.id.menu_toggle)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.header_image)).perform(click());
        clicktest(R.id.sex_layout);
        clicktest(R.id.female);
        clicktest(R.id.comfirm);
        checktexttest(R.id.sex,"女");
    }
}
