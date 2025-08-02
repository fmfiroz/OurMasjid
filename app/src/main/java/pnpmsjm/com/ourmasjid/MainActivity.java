package pnpmsjm.com.ourmasjid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

    // নতুন যোগ করা বাটন:
    private Button updatePrayerTimesButton; // <-- নতুন বাটন ভ্যারিয়েবল

    // Monthly report TextViews
    private TextView month_year; // month_year TextView (must be in your layout)
    private TextView monthly_chada, first_week, second_week, third_week, fourth_week, misc_danbox, total_income;
    private TextView imam_salary, muajjin_salary, electricity_bill, misc_expence, total_expence;
    private TextView cash_in_hand, dev_fund_income, kollan_fund_income, current_balance;

    // Firebase references
    private DatabaseReference prayerTimesRef, monthlyReportRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton fbPageBtn = findViewById(R.id.fb_page);

        fbPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookUrl = "fb://facewebmodal/f?href=https://www.facebook.com/pnpmsjm";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    // যদি FB অ্যাপ না থাকে, তাহলে ব্রাউজারে খুলবে
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.facebook.com/pnpmsjm"));
                    startActivity(webIntent);
                }
            }
        });
        ImageButton shareapp = findViewById(R.id.share_app);
        shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Our Masjid App");

                    String shareMessage = "আমাদের মসজিদের অ্যাপটি ডাউনলোড করুন!\n\n";
                    shareMessage += "https://play.google.com/store/apps/details?id=" + getPackageName();

                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize database references
        prayerTimesRef = FirebaseDatabase.getInstance().getReference("prayer_times");
        monthlyReportRootRef = FirebaseDatabase.getInstance().getReference("monthly_report");

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

        // Firebase থেকে সর্বশেষ মাস/বছরের ডেটা লোড করার জন্য
        monthlyReportRootRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String monthYearKey = childSnapshot.getKey();
                        if (monthYearKey != null) {
                            String[] parts = monthYearKey.split("_");
                            if (parts.length == 3) {
                                String monthNameDisplay = parts[2].substring(0, 1).toUpperCase() + parts[2].substring(1);
                                month_year.setText(monthNameDisplay + " " + convertToBengali(parts[0])); // Convert year to Bengali
                            } else {
                                month_year.setText(convertToBengali(monthYearKey.replace("_", " "))); // Convert to Bengali
                            }
                            loadReportData(monthYearKey);
                        }
                    }
                } else {
                    month_year.setText("মাস/বছর পাওয়া যায়নি");
                    Toast.makeText(MainActivity.this, "কোনো মাসিক রিপোর্ট ডেটা পাওয়া যায়নি।", Toast.LENGTH_SHORT).show();
                    setAllReportTextViewsToZero();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "মাস/বছর লোড করতে ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Month/Year data load failed: " + error.getMessage());
                setAllReportTextViewsToZero();
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
                            // লগইন সফল হলে সরাসরি অন্য অ্যাক্টিভিটিতে না গিয়ে বাটনটি দৃশ্যমান করুন
                            loginButton.setVisibility(View.GONE); // লগইন বাটন লুকানো
                            emailInput.setVisibility(View.GONE); // ইমেল ইনপুট লুকানো
                            passwordInput.setVisibility(View.GONE); // পাসওয়ার্ড ইনপুট লুকানো
                            updatePrayerTimesButton.setVisibility(View.VISIBLE); // নতুন বাটন দৃশ্যমান
                            // এখানে MainActivity শেষ করবেন না, যাতে বাটনটি দেখা যায়
                        } else {
                            Toast.makeText(MainActivity.this, "লগইন ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // লগইন ব্যর্থ হলে বাটনটি হাইডই থাকবে
                            updatePrayerTimesButton.setVisibility(View.GONE);
                        }
                    });

        });

        // নতুন বাটনের জন্য OnClickListener সেট করুন
        updatePrayerTimesButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UpdatePrayerTimesActivity.class));
            // আপনি চাইলে UpdatePrayerTimesActivity তে যাওয়ার পর MainActivity শেষ করতে পারেন
            // finish();
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

        month_year = findViewById(R.id.month_year);

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
        updatePrayerTimesButton = findViewById(R.id.updatePrayerTimesButton); // <-- নতুন বাটন ইনিশিয়ালাইজ করুন
    }

    private void setupLinearLayoutClicks() {
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
            gth3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Committee.class)));
        }
    }

    private void loadReportData(String monthYearKey) {
        if (monthYearKey == null || monthYearKey.isEmpty()) {
            Toast.makeText(this, "মাস এবং বছর পাওয়া যায়নি, রিপোর্ট লোড করা যাচ্ছে না।", Toast.LENGTH_SHORT).show();
            setAllReportTextViewsToZero();
            return;
        }

        DatabaseReference specificMonthlyReportRef = monthlyReportRootRef.child(monthYearKey);

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

                    monthly_chada.setText(formatAmountInBengali(monthlyChada));
                    first_week.setText(formatAmountInBengali(week1));
                    second_week.setText(formatAmountInBengali(week2));
                    third_week.setText(formatAmountInBengali(week3));
                    fourth_week.setText(formatAmountInBengali(week4));
                    misc_danbox.setText(formatAmountInBengali(danbox));
                    total_income.setText(formatAmountInBengali(totalIncome));

                    int imamSalary = getInt(snapshot, "imam_salary");
                    int muajjinSalary = getInt(snapshot, "muajjin_salary");
                    int electricity = getInt(snapshot, "electricity_bill");
                    int miscExpense = getInt(snapshot, "misc_expence");

                    int totalExpense = imamSalary + muajjinSalary + electricity + miscExpense;
                    int cashInHand = totalIncome - totalExpense;

                    imam_salary.setText(formatAmountInBengali(imamSalary));
                    muajjin_salary.setText(formatAmountInBengali(muajjinSalary));
                    electricity_bill.setText(formatAmountInBengali(electricity));
                    misc_expence.setText(formatAmountInBengali(miscExpense));
                    total_expence.setText(formatAmountInBengali(totalExpense));
                    cash_in_hand.setText(formatAmountInBengali(cashInHand));

                    // For these fields, if they are stored as Strings, you might need to parse them to int first
                    // if you want to apply the same formatting. Assuming they are numeric, we'll try to parse.
                    dev_fund_income.setText(formatAmountInBengali(getInt(snapshot, "dev_fund_income")));
                    kollan_fund_income.setText(formatAmountInBengali(getInt(snapshot, "kollan_fund_income")));
                    current_balance.setText(formatAmountInBengali(getInt(snapshot, "current_balance")));

                } else {
                    setAllReportTextViewsToZero();
                    Toast.makeText(MainActivity.this, monthYearKey.replace("_", " ") + " মাসের হিসাব পাওয়া যায়নি", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Monthly report data load failed for " + monthYearKey + ": " + error.getMessage());
                Toast.makeText(MainActivity.this, "মাসিক রিপোর্ট লোড ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                setAllReportTextViewsToZero();
            }
        });
    }

    private int getInt(DataSnapshot snapshot, String key) {
        try {
            Object value = snapshot.child(key).getValue();
            if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof String) {
                // Try to parse string to int, if it's not a valid number, return 0
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    Log.e("ParseError", "NumberFormatException for key: " + key + ", Value: " + value, e);
                    return 0;
                }
            } else if (value != null) {
                // In case it's another numeric type, convert to string and then parse
                try {
                    return Integer.parseInt(value.toString());
                } catch (NumberFormatException e) {
                    Log.e("ParseError", "NumberFormatException for key: " + key + ", Value: " + value, e);
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            Log.e("ParseError", "Error parsing integer for key: " + key + ", Value: " + snapshot.child(key).getValue(), e);
            return 0;
        }
    }

    // New method to convert English digits to Bengali digits
    private String convertToBengali(String number) {
        if (number == null || number.isEmpty()) {
            return "";
        }
        char[] englishDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] bengaliDigits = {'০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'};

        StringBuilder bengaliNumber = new StringBuilder();
        for (char c : number.toCharArray()) {
            boolean found = false;
            for (int i = 0; i < englishDigits.length; i++) {
                if (c == englishDigits[i]) {
                    bengaliNumber.append(bengaliDigits[i]);
                    found = true;
                    break;
                }
            }
            if (!found) {
                bengaliNumber.append(c); // Append non-digit characters as they are
            }
        }
        return bengaliNumber.toString();
    }

    // New method to format amounts with ".00" and in Bengali
    private String formatAmountInBengali(int amount) {
        String formattedAmount = String.format(Locale.US, "%d.00", amount);
        return convertToBengali(formattedAmount);
    }

    private void setAllReportTextViewsToZero() {
        // Use the new formatting method for all zero values as well
        monthly_chada.setText(formatAmountInBengali(0));
        first_week.setText(formatAmountInBengali(0));
        second_week.setText(formatAmountInBengali(0));
        third_week.setText(formatAmountInBengali(0));
        fourth_week.setText(formatAmountInBengali(0));
        misc_danbox.setText(formatAmountInBengali(0));
        total_income.setText(formatAmountInBengali(0));
        imam_salary.setText(formatAmountInBengali(0));
        muajjin_salary.setText(formatAmountInBengali(0));
        electricity_bill.setText(formatAmountInBengali(0));
        misc_expence.setText(formatAmountInBengali(0));
        total_expence.setText(formatAmountInBengali(0));
        cash_in_hand.setText(formatAmountInBengali(0));
        dev_fund_income.setText(formatAmountInBengali(0));
        kollan_fund_income.setText(formatAmountInBengali(0));
        current_balance.setText(formatAmountInBengali(0));
    }

    private void setTimeFromSnapshot(DataSnapshot snapshot, String key, TextView textView, AnalogClockView analogueClockView) {
        String time = snapshot.child(key).getValue(String.class);
        if (time != null) {
            textView.setText(convertToBengali(time)); // Convert prayer times to Bengali too
            if (analogueClockView != null) {
                // Assuming AnalogClockView handles string input, if it expects formatted time,
                // you might need to adjust its setPrayerTime method or pass the original time.
                // For now, passing the Bengali formatted time.
                analogueClockView.setPrayerTime(convertToBengali(time));
            }
        }
    }

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

    public void don1(View v) {
        Toast.makeText(this, "ব্যাংকের মাধ্যমে দান করুন অথবা সভাপতি/সেক্রেটারীর সঙ্গে যোগাযোগ করুন।", Toast.LENGTH_LONG).show();
    }

    public void don2(View v) { don1(v); }
    public void don3(View v) { don1(v); }
    public void don4(View v) { don1(v); }
    public void don5(View v) { don1(v); }

}