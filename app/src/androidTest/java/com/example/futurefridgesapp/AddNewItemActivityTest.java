package com.example.futurefridgesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddNewItemActivityTest {

    @Rule
    public ActivityScenarioRule<AddNewItemActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AddNewItemActivity.class);

    @Test
    public void testActivityLaunch() {
        ActivityScenario<AddNewItemActivity> scenario = ActivityScenario.launch(AddNewItemActivity.class);
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.name_text)).check(matches(isDisplayed()));
        onView(withId(R.id.expiry_text)).check(matches(isDisplayed()));
        onView(withId(R.id.quantity_text)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testEnteringDataAndSaving() {
        onView(withId(R.id.name_text)).perform(replaceText("Milk"), closeSoftKeyboard());
        onView(withId(R.id.expiry_text)).perform(replaceText("7"), closeSoftKeyboard());
        onView(withId(R.id.quantity_text)).perform(replaceText("10"), closeSoftKeyboard());

        onView(withId(R.id.save_button)).perform(click());
    }

    @Test
    public void testCancelButton() {
        onView(withId(R.id.cancel_button)).perform(click());
    }
}
