package com.example.futurefridgesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class DeliveryActivityTest {

    private static final String TEST_PASSCODE = "345";

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Log.d("Test", "Setting up DeliveryActivityTest...");
    }

    @Test
    public void testLoginAndNavigateToDelivery() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Enter passcode and log in
        onView(withId(R.id.passcodeText)).perform(replaceText(TEST_PASSCODE), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        // Wait for login and transition to DeliveryActivity
        TimeUnit.SECONDS.sleep(3); // Allow UI update

        // Verify DeliveryActivity is displayed
        onView(withId(R.id.confirmDeliveryButton)).check(matches(isDisplayed())); // Ensure delivery button exists
    }
}
