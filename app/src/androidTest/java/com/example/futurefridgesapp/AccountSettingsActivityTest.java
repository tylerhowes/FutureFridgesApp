package com.example.futurefridgesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AccountSettingsActivityTest {

    @Rule
    public ActivityScenarioRule<AccountSettingsActivity> activityRule =
            new ActivityScenarioRule<>(AccountSettingsActivity.class);

    @Test
    public void testActivityLaunch() {
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }
}