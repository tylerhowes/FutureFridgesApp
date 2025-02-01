package com.example.futurefridgesapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        LinearLayout notificationContainer = findViewById(R.id.notificationContainer);

        ArrayList<Notification> notificationArrayList = new ArrayList<Notification>();

        db.collection("Notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Notification notification = new Notification(document.getString("title"), document.getString("date"), document.getString("message"), document.getString("action"));
                        notificationArrayList.add(notification);

                    }

                    // Add cards dynamically
                    for (Notification notif : notificationArrayList) {
                        LinearLayout card = createNotificationCard(notif, notificationContainer);
                        notificationContainer.addView(card); // Add the card to the container
                    }

                    Button dashboardButton = findViewById(R.id.dashboardButton);
                    dashboardButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(NotificationActivity.this, DashboardActivity.class);

                            startActivity(intent);
                        }
                    });

                } else{
                    Log.d("InventoryManager", "Error getting Inventory document: ", task.getException());
                }
            }
        });





    }

    /**
     * Creates a notification card.
     *
     * @param notification The notification data to populate the card.
     * @param container    The parent container for handling actions like removing the card.
     * @return A LinearLayout representing the notification card.
     */
    private LinearLayout createNotificationCard(Notification notification, LinearLayout container) {
        // Card container
        LinearLayout card = createCardContainer();

        // Add the header (title and close icon)
        card.addView(createCardHeader(notification.getTitle(), notification.getDate(), container, card));

        // Add the message
        card.addView(createCardMessage(notification.getMessage()));

        // Add the action button
        card.addView(createActionButton(notification.getAction(), container, card));

        return card;
    }

    /**
     * Creates the container for the card.
     *
     * @return A LinearLayout representing the card container.
     */
    private LinearLayout createCardContainer() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.notification_card_background);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 16, 0, 16);
        card.setLayoutParams(cardParams);
        card.setPadding(16, 16, 16, 16);
        return card;
    }

    /**
     * Creates the header for the card (title and close icon).
     *
     * @param titleText The title of the notification.
     * @param container The parent container for handling actions.
     * @param card      The card to remove when the close icon is clicked.
     * @return A LinearLayout representing the card header.
     */
    private LinearLayout createCardHeader(String titleText, String date, LinearLayout container, LinearLayout card) {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView dateTV = new TextView(this);
        dateTV.setText(date);
        dateTV.setTextSize(16);
        dateTV.setTextColor(Color.DKGRAY);
        dateTV.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        header.addView(dateTV);


        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(16);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));
        header.addView(title);



        TextView closeIcon = new TextView(this);
        closeIcon.setText("✖");
        closeIcon.setGravity(Gravity.END);
        closeIcon.setTextSize(18);
        closeIcon.setTextColor(Color.RED);
        closeIcon.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));;
        closeIcon.setOnClickListener(v -> container.removeView(card));
        header.addView(closeIcon);

        return header;
    }

    /**
     * Creates the message text for the card.
     *
     * @param messageText The message of the notification.
     * @return A TextView representing the card message.
     */
    private TextView createCardMessage(String messageText) {
        TextView message = new TextView(this);
        message.setText(messageText);
        message.setGravity(Gravity.CENTER);
        message.setTextColor(Color.DKGRAY);
        message.setPadding(0, 8, 0, 8);
        return message;
    }

    /**
     * Creates the action button for the card.
     *
     * @param buttonText The text of the button.
     * @param container  The parent container for handling actions.
     * @param card       The card to remove when the button is clicked.
     * @return A Button representing the action button.
     */
    private Button createActionButton(String buttonText, LinearLayout container, LinearLayout card) {
        Button actionButton = new Button(this);
        actionButton.setText(buttonText);
        actionButton.setBackgroundTintList(getColorStateList(R.color.black));
        actionButton.setTextColor(Color.WHITE);
        actionButton.setOnClickListener(v -> {
            // Handle button action (e.g., remove the card)
            container.removeView(card);
        });
        return actionButton;
    }

    // Notification model class
    static class Notification {
        private final String title;
        private final String date;
        private final String message;
        private final String action;

        public Notification(String title, String date, String message, String action) {
            this.title = title;
            this.date = date;
            this.message = message;
            this.action = action;
        }

        public String getDate()
        {
            return date;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getAction() {
            return action;
        }
    }
}
