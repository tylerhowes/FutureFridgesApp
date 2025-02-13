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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String testPasscode = "123";

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        ensureFirebaseUser();
    }

    private void ensureFirebaseUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            CountDownLatch latch = new CountDownLatch(1);
            auth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FirebaseAuth", "Test User signed in");
                        } else {
                            Log.e("FirebaseAuth", "Failed to sign in test user: " + task.getException().getMessage());
                        }
                        latch.countDown();
                    });

            try {
                latch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testActivityLaunch() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.passcodeText)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonLogin)).check(matches(isDisplayed()));
    }

    @Test
    public void testPasscodeEntryAndLogin() {
        onView(withId(R.id.passcodeText))
                .perform(replaceText(testPasscode), closeSoftKeyboard());
        onView(withId(R.id.buttonLogin)).perform(click());
    }

    @Test
    public void testValidUserLoginWithPasscode() {
        CountDownLatch latch = new CountDownLatch(1);

        db.collection("Users").whereEqualTo("passcode", testPasscode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (!snapshot.isEmpty()) {
                            DocumentSnapshot document = snapshot.getDocuments().get(0);
                            String email = document.getString("email");
                            String password = document.getString("password");
                            String role = document.getString("role");

                            if (email != null && password != null && role != null) {
                                auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(authTask -> {
                                            if (authTask.isSuccessful()) {
                                                Log.d("LoginTest", "Login successful: " + email);
                                            } else {
                                                Log.e("LoginTest", "Login failed: " + authTask.getException().getMessage());
                                            }
                                            latch.countDown();
                                        });
                            }
                        }
                    } else {
                        Log.e("Firestore", "Error fetching test user data", task.getException());
                    }
                    latch.countDown();
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
