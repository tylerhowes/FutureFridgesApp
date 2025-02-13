package com.example.futurefridgesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.TableLayout;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class AddItemActivityTest {

    @Rule
    public ActivityScenarioRule<AddItemActivity> activityRule = new ActivityScenarioRule<>(AddItemActivity.class);

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> {
            TableLayout tableLayout = activity.findViewById(R.id.item_table);
            FridgeItem mockItem = new FridgeItem("Milk", null, "123", "2025-02-15", 2, null);
            ArrayList<FridgeItem> items = new ArrayList<>();
            items.add(mockItem);
            activity.refreshTable(items);
        });
    }

    @Test
    public void testActivityLaunch() {
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void testTableDisplayed() {
        onView(withId(R.id.item_table)).check(matches(isDisplayed()));
    }

    @Test
    public void testItemDisplayedInTable() {
        onView(withText("Name")).check(matches(isDisplayed()));
        onView(withText("Quantity")).check(matches(isDisplayed()));
        onView(withText("Actions")).check(matches(isDisplayed()));
        onView(withText("Milk")).check(matches(isDisplayed()));
        onView(withText("2")).check(matches(isDisplayed()));
    }

    @Test
    public void testAddToOrderButtonClick() {
        onView(withText("Add to Order")).perform(click());
    }
}