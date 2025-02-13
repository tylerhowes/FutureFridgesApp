package com.example.futurefridgesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class DashboardActivityTest {

    private FirebaseFirestore db;

    private static final String TEST_PASSCODE = "123";
    private static final String TEST_EMAIL = "admin@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_ROLE = "admin";
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        addTestUser();
    }

    private void addTestUser() {
        CountDownLatch latch = new CountDownLatch(1);

        Map<String, Object> testUser = new HashMap<>();
        testUser.put("email", TEST_EMAIL);
        testUser.put("password", TEST_PASSWORD);
        testUser.put("role", TEST_ROLE);
        testUser.put("passcode", TEST_PASSCODE);

        db.collection("Users").document("testUser")
                .set(testUser)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreTest", "Test user added");
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreTest", "Failed to add test user", e);
                    latch.countDown();
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginAndNavigateToDashboard() throws InterruptedException {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.passcodeText)).perform(replaceText(TEST_PASSCODE), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());

        Thread.sleep(5000);

        // Verify we are in DashboardActivity
        onView(withId(R.id.dashboard_button)).check(matches(isDisplayed()));
    }

}
