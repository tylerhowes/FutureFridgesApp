package com.example.futurefridgesapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
public class OrdersActivityTest {

    private FirebaseFirestore db;
    private String testOrderId;

    private static final String TEST_SUPPLIER = "Iceland";
    private static final String TEST_STATUS = "Open";

    @Rule
    public ActivityScenarioRule<OrdersActivity> activityScenarioRule =
            new ActivityScenarioRule<>(OrdersActivity.class);

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
    }

    private void addTestOrder() {
        CountDownLatch latch = new CountDownLatch(1);

        Map<String, Object> testOrder = new HashMap<>();
        testOrder.put("supplier", TEST_SUPPLIER);
        testOrder.put("status", TEST_STATUS);

        db.collection("Orders").add(testOrder)
                .addOnSuccessListener(documentReference -> {
                    testOrderId = documentReference.getId();
                    Log.d("FirestoreTest", "Test order added: " + testOrderId);
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreTest", "Failed to add test order", e);
                    latch.countDown();
                });

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testActivityLaunch() {
        ActivityScenario<OrdersActivity> scenario = ActivityScenario.launch(OrdersActivity.class);
        onView(withId(R.id.ordersTable)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddNewOrder() {
        ActivityScenario<OrdersActivity> scenario = ActivityScenario.launch(OrdersActivity.class);
        onView(withId(R.id.createOrderButton)).perform(click());
        addTestOrder();
    }

    @Test
    public void testDeleteOrder() {
        if (testOrderId != null) {
            DocumentReference testDoc = db.collection("Orders").document(testOrderId);
            testDoc.delete().addOnSuccessListener(aVoid -> Log.d("FirestoreTest", "Test order deleted"))
                    .addOnFailureListener(e -> Log.e("FirestoreTest", "Error deleting test order", e));
        }
    }
}
