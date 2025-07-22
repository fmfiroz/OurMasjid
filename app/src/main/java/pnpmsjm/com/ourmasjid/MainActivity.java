package pnpmsjm.com.ourmasjid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Prayer time TextViews
    private TextView fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime, jumaTime;

    // Your AnalogClockView references (assuming you have this custom view)
    private AnalogClockView analogueFajrTime, analogueDhuhrTime, analogueAsrTime,
            analogueMaghribTime, analogueIshaTime, analogueJummaTime;

    // Login UI
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private FirebaseAuth mAuth;

    // Monthly report TextViews
    private TextView monthly_chada, first_week, second_week, third_week, fourth_week, misc_danbox, total_income;
    private TextView imam_salary, muajjin_salary, electricity_bill, misc_expence, total_expence;
    private TextView cash_in_hand, dev_fund_income, kollan_fund_income, current_balance;

    // Firebase references
    private DatabaseReference prayerTimesRef, monthlyReportRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize database references
        prayerTimesRef = FirebaseDatabase.getInstance().getReference("prayer_times");
        monthlyReportRef = FirebaseDatabase.getInstance().getReference("monthly_report");

        // Initialize Views
        initViews();

        // Listen for prayer times changes
        prayerTimesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setTimeFromSnapshot(dataSnapshot, "fajr", fajrTime, analogueFajrTime);
                    setTimeFromSnapshot(dataSnapshot, "dhuhr", dhuhrTime, analogueDhuhrTime);
                    setTimeFromSnapshot(dataSnapshot, "asr", asrTime, analogueAsrTime);
                    setTimeFromSnapshot(dataSnapshot, "maghrib", maghribTime, analogueMaghribTime);
                    setTimeFromSnapshot(dataSnapshot, "isha", ishaTime, analogueIshaTime);
                    setTimeFromSnapshot(dataSnapshot, "juma", jumaTime, analogueJummaTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "ডেটা লোড করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
            }
        });

        // Listen for monthly report data changes (realtime)
        loadReportData();

        // Login button click listener
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "ইমেল এবং পাসওয়ার্ড দিন", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "লগইন সফল: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, UpdatePrayerTimesActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "লগইন ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Initialize AdMob (if you use it)
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("CE1FDF3A2281C0F490245647207A6184"))
                .build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this, status -> {});
    }

    private void initViews() {
        fajrTime = findViewById(R.id.fajrTime);
        dhuhrTime = findViewById(R.id.dhuhrTime);
        asrTime = findViewById(R.id.asrTime);
        maghribTime = findViewById(R.id.maghribTime);
        ishaTime = findViewById(R.id.ishaTime);
        jumaTime = findViewById(R.id.jummaTime);

        analogueFajrTime = findViewById(R.id.analoguefajrTime);
        analogueDhuhrTime = findViewById(R.id.analoguedhuhrTime);
        analogueAsrTime = findViewById(R.id.analogueasrTime);
        analogueMaghribTime = findViewById(R.id.analoguemaghribTime);
        analogueIshaTime = findViewById(R.id.analogueishaTime);
        analogueJummaTime = findViewById(R.id.analoguejummaTime);

        monthly_chada = findViewById(R.id.monthly_chada);
        first_week = findViewById(R.id.first_week);
        second_week = findViewById(R.id.second_week);
        third_week = findViewById(R.id.third_week);
        fourth_week = findViewById(R.id.fourth_week);
        misc_danbox = findViewById(R.id.misc_danbox);
        total_income = findViewById(R.id.total_income);
        imam_salary = findViewById(R.id.imam_salary);
        muajjin_salary = findViewById(R.id.muajjin_salary);
        electricity_bill = findViewById(R.id.electricity_bill);
        misc_expence = findViewById(R.id.misc_expence);
        total_expence = findViewById(R.id.total_expence);
        cash_in_hand = findViewById(R.id.cash_in_hand);
        dev_fund_income = findViewById(R.id.dev_fund_income);
        kollan_fund_income = findViewById(R.id.kollan_fund_income);
        current_balance = findViewById(R.id.current_balance);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
    }

    private void loadReportData() {
        monthlyReportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int monthlyChada = getInt(snapshot, "monthly_chada");
                int week1 = getInt(snapshot, "first_week");
                int week2 = getInt(snapshot, "second_week");
                int week3 = getInt(snapshot, "third_week");
                int week4 = getInt(snapshot, "fourth_week");
                int danbox = getInt(snapshot, "misc_danbox");

                int totalIncome = monthlyChada + week1 + week2 + week3 + week4 + danbox;

                monthly_chada.setText(String.valueOf(monthlyChada));
                first_week.setText(String.valueOf(week1));
                second_week.setText(String.valueOf(week2));
                third_week.setText(String.valueOf(week3));
                fourth_week.setText(String.valueOf(week4));
                misc_danbox.setText(String.valueOf(danbox));
                total_income.setText(String.valueOf(totalIncome));

                int imamSalary = getInt(snapshot, "imam_salary");
                int muajjinSalary = getInt(snapshot, "muajjin_salary");
                int electricity = getInt(snapshot, "electricity_bill");
                int miscExpense = getInt(snapshot, "misc_expence");

                int totalExpense = imamSalary + muajjinSalary + electricity + miscExpense;
                int cashInHand = totalIncome - totalExpense;

                imam_salary.setText(String.valueOf(imamSalary));
                muajjin_salary.setText(String.valueOf(muajjinSalary));
                electricity_bill.setText(String.valueOf(electricity));
                misc_expence.setText(String.valueOf(miscExpense));
                total_expence.setText(String.valueOf(totalExpense));
                cash_in_hand.setText(String.valueOf(cashInHand));

                dev_fund_income.setText(snapshot.child("dev_fund_income").getValue(String.class));
                kollan_fund_income.setText(snapshot.child("kollan_fund_income").getValue(String.class));
                current_balance.setText(snapshot.child("current_balance").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Data load failed: " + error.getMessage());
            }

            private int getInt(DataSnapshot snapshot, String key) {
                String value = snapshot.child(key).getValue(String.class);
                try {
                    return Integer.parseInt(value);
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }

    private void setTimeFromSnapshot(DataSnapshot snapshot, String key, TextView textView, AnalogClockView analogueClockView) {
        String time = snapshot.child(key).getValue(String.class);

        if (time != null) {
            textView.setText(time);

            if (time.contains(":")) {
                try {
                    String[] parts = time.trim().split("[: ]+");
                    int hour = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);
                    String ampm = (parts.length >= 3) ? parts[2].toUpperCase() : "";

                    if (ampm.equals("PM") && hour != 12) {
                        hour += 12;
                    } else if (ampm.equals("AM") && hour == 12) {
                        hour = 0;
                    }

                    if (analogueClockView != null) {
                        analogueClockView.setTime(hour, minute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "সময় সেট করতে সমস্যা হয়েছে: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Example call methods with permission check
    public void cosovapoti_calling(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:01711187317"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        startActivity(callIntent);
        Toast.makeText(this, "আপনি সহ-সভাপতিকে ফোন দিয়েছেন।", Toast.LENGTH_LONG).show();
    }

    public void secratery_calling(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:01716101240"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        startActivity(callIntent);
        Toast.makeText(this, "আপনি সাধারণ সম্পাদককে ফোন দিয়েছেন।", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "কল করার অনুমতি অনুমোদিত হয়েছে।", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "কল করার অনুমতি বাতিল হয়েছে।", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Donation toast method example
    public void don1(View v) {
        Toast.makeText(this, "ব্যাংকের মাধ্যমে দান করুন অথবা সভাপতি/সেক্রেটারীর সঙ্গে যোগাযোগ করুন।", Toast.LENGTH_LONG).show();
    }

    public void don2(View v) { don1(v); }
    public void don3(View v) { don1(v); }
    public void don4(View v) { don1(v); }
    public void don5(View v) { don1(v); }
}
