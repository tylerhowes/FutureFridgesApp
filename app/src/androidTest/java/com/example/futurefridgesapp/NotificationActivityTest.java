package com.example.futurefridgesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentReference;
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
public class NotificationActivityTest {

    private FirebaseFirestore db;
    private String testNotificationId;
    private static final String TEST_TITLE = "Test Notification";
    private static final String TEST_MESSAGE = "This is a test notification.";

    @Rule
    public ActivityScenarioRule<NotificationActivity> activityScenarioRule =
            new ActivityScenarioRule<>(NotificationActivity.class);

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        addTestNotification();
    }

    private void addTestNotification() {
        CountDownLatch latch = new CountDownLatch(1);

        Map<String, Object> testNotification = new HashMap<>();
        testNotification.put("title", TEST_TITLE);
        testNotification.put("date", "2025-02-12");
        testNotification.put("message", TEST_MESSAGE);
        testNotification.put("action", "Dismiss");

        db.collection("Notifications").add(testNotification)
                .addOnSuccessListener(documentReference -> {
                    testNotificationId = documentReference.getId();
                    Log.d("FirestoreTest", "Test notification added: " + testNotificationId);
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreTest", "Failed to add test notification", e);
                    latch.countDown();
                });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testActivityLaunch() {
        ActivityScenario<NotificationActivity> scenario = ActivityScenario.launch(NotificationActivity.class);
        onView(withId(R.id.notificationContainer)).check(matches(isDisplayed()));

    }


    @Test
    public void testDeleteNotification() {
        if (testNotificationId != null) {
            DocumentReference testDoc = db.collection("Notifications").document(testNotificationId);
            testDoc.delete().addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test notification deleted"))
                    .addOnFailureListener(e -> Log.e("FirestoreTest", "Error deleting test notification", e));
        }
    }
}
