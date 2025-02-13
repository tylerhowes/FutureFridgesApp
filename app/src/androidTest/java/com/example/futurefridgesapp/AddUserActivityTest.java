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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AddUserActivityTest {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Rule
    public ActivityScenarioRule<AddUserActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AddUserActivity.class);

    @Before
    public void setUp() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Test
    public void testActivityLaunch() {
        ActivityScenario<AddUserActivity> scenario = ActivityScenario.launch(AddUserActivity.class);
        onView(withId(R.id.main)).check(matches(isDisplayed())); // Check if main layout is visible
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.user_email)).check(matches(isDisplayed()));
        onView(withId(R.id.user_password)).check(matches(isDisplayed()));
        onView(withId(R.id.user_passcode)).check(matches(isDisplayed()));
        onView(withId(R.id.user_role_spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testEnteringUserDataAndSaving() {
        onView(withId(R.id.user_email))
                .perform(replaceText("testuser@example.com"), closeSoftKeyboard());
        onView(withId(R.id.user_password))
                .perform(replaceText("TestPass123"), closeSoftKeyboard());
        onView(withId(R.id.user_passcode))
                .perform(replaceText("123"), closeSoftKeyboard());
        onView(withId(R.id.save_button)).perform(click());

    }


    @Test
    public void testCancelButton() {
        onView(withId(R.id.cancel_button)).perform(click());
    }

    @Test
    public void testFirebaseUserCreation() {
        auth.createUserWithEmailAndPassword("testuser@example.com", "TestPass123")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        Log.d("FirebaseAuth", "User created: " + user.getUid());
                    } else {
                        Log.e("FirebaseAuth", "User creation failed: " + task.getException().getMessage());
                    }
                });
    }

    @Test
    public void testFirestoreUserStorage() {
        String testUserId = "testUserId123";
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("email", "testuser@example.com");
        userDetails.put("password", "TestPass123");
        userDetails.put("passcode", "123");
        userDetails.put("role", "chef");
        userDetails.put("userId", testUserId);

        db.collection("Users").document(testUserId).set(userDetails)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User stored successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error storing user: " + e.getMessage()));
    }
}
