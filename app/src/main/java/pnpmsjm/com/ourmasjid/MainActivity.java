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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // LinearLayouts for navigation
    LinearLayout ghosona1, mulniti1, gothontontro1, omorbani1, onumodon1, gth3;

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
    private TextView month_year; // month_year TextView (must be in your layout)
    private TextView monthly_chada, first_week, second_week, third_week, fourth_week, misc_danbox, total_income;
    private TextView imam_salary, muajjin_salary, electricity_bill, misc_expence, total_expence;
    private TextView cash_in_hand, dev_fund_income, kollan_fund_income, current_balance;

    // Firebase references
    private DatabaseReference prayerTimesRef, monthlyReportRootRef; // Changed to monthlyReportRootRef

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize database references
        prayerTimesRef = FirebaseDatabase.getInstance().getReference("prayer_times");
        monthlyReportRootRef = FirebaseDatabase.getInstance().getReference("monthly_report"); // Root reference

        // Initialize Views
        initViews();

        // XML থেকে LinearLayout গুলো bind করা
        ghosona1 = findViewById(R.id.ghosona1);
        mulniti1 = findViewById(R.id.mulniti1);
        gothontontro1 = findViewById(R.id.gothontontro1);
        omorbani1 = findViewById(R.id.omorbani1);
        onumodon1 = findViewById(R.id.onumodon1);
        gth3 = findViewById(R.id.gth3);

        // Setup LinearLayout click listeners
        setupLinearLayoutClicks();

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
                Toast.makeText(MainActivity.this, "নামাজের সময় লোড করতে সমস্যা হয়েছে: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Prayer times data load failed: " + error.getMessage());
            }
        });

        // Load current month/year to TextView first, then load report data
        // Order by key and limit to last 1 to get the latest month/year
        monthlyReportRootRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String monthYearKey = childSnapshot.getKey(); // e.g. "July_2025"
                        if (monthYearKey != null) {
                            // Set the TextView with readable format (e.g., "July 2025")
                            month_year.setText(monthYearKey.replace("_", " "));
                            // Now load the monthly report data for this specific month/year
                            loadReportData(monthYearKey);
                        }
                    }
                } else {
                    // Handle case where no monthly report data exists
                    month_year.setText("মাস/বছর পাওয়া যায়নি");
                    Toast.makeText(MainActivity.this, "কোনো মাসিক রিপোর্ট ডেটা পাওয়া যায়নি।", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "মাস/বছর লোড করতে ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Month/Year data load failed: " + error.getMessage());
            }
        });

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

        month_year = findViewById(R.id.month_year); // Initialize month_year TextView here

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

    private void setupLinearLayoutClicks() {
        // Ensure these IDs exist in your activity_main.xml and corresponding Activities are created
        if (ghosona1 != null) {
            ghosona1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Ghosona.class)));
        }
        if (mulniti1 != null) {
            mulniti1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Mulniti.class)));
        }
        if (gothontontro1 != null) {
            gothontontro1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GothonTontro.class)));
        }
        if (omorbani1 != null) {
            omorbani1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Omorbani.class)));
        }
        if (onumodon1 != null) {
            onumodon1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Onumodon.class)));
        }
        if (gth3 != null) {
            // Make sure "CommitteeActivity.class" matches your actual committee activity class name
            gth3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Committee.class)));
        }
    }

    // Monthly report data loading method, now takes monthYearKey as parameter
    private void loadReportData(String monthYearKey) {
        if (monthYearKey == null || monthYearKey.isEmpty()) {
            Toast.makeText(this, "মাস এবং বছর পাওয়া যায়নি, রিপোর্ট লোড করা যাচ্ছে না।", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a specific reference to the monthly report for the fetched month/year
        DatabaseReference specificMonthlyReportRef = monthlyReportRootRef.child(monthYearKey);

        // Using addValueEventListener for real-time updates for the report
        specificMonthlyReportRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
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

                    // Safely get String values, providing default if null
                    dev_fund_income.setText(snapshot.child("dev_fund_income").getValue(String.class) != null ? snapshot.child("dev_fund_income").getValue(String.class) : "0");
                    kollan_fund_income.setText(snapshot.child("kollan_fund_income").getValue(String.class) != null ? snapshot.child("kollan_fund_income").getValue(String.class) : "0");
                    current_balance.setText(snapshot.child("current_balance").getValue(String.class) != null ? snapshot.child("current_balance").getValue(String.class) : "0");

                } else {
                    // Set all TextViews to 0 or appropriate default if data for this month doesn't exist
                    monthly_chada.setText("0");
                    first_week.setText("0");
                    second_week.setText("0");
                    third_week.setText("0");
                    fourth_week.setText("0");
                    misc_danbox.setText("0");
                    total_income.setText("0");
                    imam_salary.setText("0");
                    muajjin_salary.setText("0");
                    electricity_bill.setText("0");
                    misc_expence.setText("0");
                    total_expence.setText("0");
                    cash_in_hand.setText("0");
                    dev_fund_income.setText("0");
                    kollan_fund_income.setText("0");
                    current_balance.setText("0");

                    Toast.makeText(MainActivity.this, monthYearKey.replace("_", " ") + " মাসের হিসাব পাওয়া যায়নি", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Monthly report data load failed for " + monthYearKey + ": " + error.getMessage());
                Toast.makeText(MainActivity.this, "মাসিক রিপোর্ট লোড ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to get integer values from DataSnapshot safely
    private int getInt(DataSnapshot snapshot, String key) {
        try {
            Object value = snapshot.child(key).getValue();
            if (value instanceof Long) {
                return ((Long) value).intValue(); // Firebase stores numbers as Long
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            } else if (value != null) {
                // Try converting other types to String and then parse
                return Integer.parseInt(value.toString());
            } else {
                return 0; // Default to 0 if value is null
            }
        } catch (Exception e) {
            Log.e("ParseError", "Error parsing integer for key: " + key + ", Value: " + snapshot.child(key).getValue(), e);
            return 0; // Default to 0 on parsing error
        }
    }

    private void setTimeFromSnapshot(DataSnapshot snapshot, String key, TextView textView, AnalogClockView analogueClockView) {
        String time = snapshot.child(key).getValue(String.class);
        if (time != null) {
            textView.setText(time);
            if (analogueClockView != null) { // Ensure analogueClockView is not null before using it
                // Pass the raw time string to setPrayerTime, assuming it handles parsing
                analogueClockView.setPrayerTime(time);
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
        Toast.makeText(this, "আপনি সহ-সভাপতিকে ফোন দিয়েছেন।", Toast.LENGTH_LONG).show();
    }

    public void secratery_calling(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:01716101240"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        startActivity(callIntent);
        Toast.makeText(this, "আপনি সাধারণ সম্পাদককে ফোন দিয়েছেন।", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "কল করার অনুমতি অনুমোদিত হয়েছে।", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "কল করার অনুমতি বাতিল হয়েছে।", Toast.LENGTH_SHORT).show();
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