package com.example.smishingdetectionapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_news, R.id.nav_settings)
                .build();

        if (!areNotificationsEnabled()) {
            showNotificationPermissionDialog();
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        Button debug_btn = findViewById(R.id.debug_btn);
        debug_btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DebugActivity.class)));

        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });

        // Database connection
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();

        // Setting counter from the result
        TextView total_count = findViewById(R.id.total_counter);
        total_count.setText("" + databaseAccess.getCounter());

        // Closing the connection
        databaseAccess.close();

        // Contact numbers retrieval and SMS analysis
//        Set<String> contactNumbers = getContactNumbers();
//        String analyzedMessages = analyzeSms(contactNumbers);
//        total_count.setText(analyzedMessages);
    }

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "notificationPermission");
    }

//    private Set<String> getContactNumbers() {
//        Set<String> contactNumbers = new HashSet<>();
//        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                null, null, null, null);
//
//        if (cursor != null) {
//            int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//
//            while (cursor.moveToNext()) {
//                if (numberIndex != -1) {
//                    String number = cursor.getString(numberIndex);
//                    contactNumbers.add(number.replaceAll("[^0-9]", ""));  // Normalize phone numbers
//                }
//            }
//            cursor.close();
//        }
//        return contactNumbers;
//    }

//    private String analyzeSms(Set<String> contactNumbers) {
//        StringBuilder analyzedMessages = new StringBuilder();
//        Uri uri = Uri.parse("content://sms/inbox");
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            int addressColumnIndex = cursor.getColumnIndex("address");
//            int bodyColumnIndex = cursor.getColumnIndex("body");
//
//            if (addressColumnIndex != -1 && bodyColumnIndex != -1) {
//                do {
//                    String address = cursor.getString(addressColumnIndex);
//                    String body = cursor.getString(bodyColumnIndex);
//                    if (!contactNumbers.contains(address.replaceAll("[^0-9]", ""))) {
//                        boolean isSmishing = SmishingDetector.isSmishingMessage(body.toLowerCase());
//                        analyzedMessages.append(body)
//                                .append("\nSmishing: ").append(isSmishing).append("\n\n");
//                    }
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        }
//
//        return analyzedMessages.toString();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
