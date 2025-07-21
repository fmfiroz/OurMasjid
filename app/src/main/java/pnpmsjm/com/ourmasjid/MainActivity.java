package pnpmsjm.com.ourmasjid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

    // TextViews
    private TextView donaate1, donaate2, donaate3, donaate4, donaate5, developer_toast, ghosona, mulniti, omorbani,
            gothontontro, gothontontro2, onumodon, comiti,
            fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime, jumaTime;

    // Analogue clocks
    private AnalogClockView analogueFajrTime, analogueDhuhrTime, analogueAsrTime, analogueMaghribTime, analogueIshaTime, analogueJummaTime;

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    private ImageView call_secratery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextViews findViewById
        donaate1 = findViewById(R.id.donaate1);
        donaate2 = findViewById(R.id.donaate2);
        donaate3 = findViewById(R.id.donaate3);
        donaate4 = findViewById(R.id.donaate4);
        donaate5 = findViewById(R.id.donaate5);
        developer_toast = findViewById(R.id.developer_toast);
        ghosona = findViewById(R.id.ghosona);
        mulniti = findViewById(R.id.mulniti);
        omorbani = findViewById(R.id.omorbani);
        gothontontro = findViewById(R.id.gothontontro);
        gothontontro2 = findViewById(R.id.gothontontro2);
        onumodon = findViewById(R.id.onumodon);
        comiti = findViewById(R.id.comiti);

        // Intent listeners
        developer_toast.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Others_1.class)));
        ghosona.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Ghosona.class)));
        mulniti.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Mulniti.class)));
        gothontontro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GothonTontro_1.class)));
        gothontontro2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GothonTontro_1.class)));
        omorbani.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Omorbani.class)));
        onumodon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Onumodon.class)));
        comiti.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Committee.class)));

        // নামাজের সময়ের TextView findViewById
        fajrTime = findViewById(R.id.fajrTime);
        dhuhrTime = findViewById(R.id.dhuhrTime);
        asrTime = findViewById(R.id.asrTime);
        maghribTime = findViewById(R.id.maghribTime);
        ishaTime = findViewById(R.id.ishaTime);
        jumaTime = findViewById(R.id.jummaTime);

        // Analogue clock view findViewById
        analogueFajrTime = findViewById(R.id.analoguefajrTime);
        analogueDhuhrTime = findViewById(R.id.analoguedhuhrTime);
        analogueAsrTime = findViewById(R.id.analogueasrTime);
        analogueMaghribTime = findViewById(R.id.analoguemaghribTime);
        analogueIshaTime = findViewById(R.id.analogueishaTime);
        analogueJummaTime = findViewById(R.id.analoguejummaTime);

        // Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("prayer_times");

        // Firebase থেকে নামাজের সময় নিয়ে সেট করবো
        mDatabase.addValueEventListener(new ValueEventListener() {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "ডেটা লোড করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show();
            }
        });

        // Firebase Authentication initialization
        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

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

        // AdMob initialization
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("CE1FDF3A2281C0F490245647207A6184"))
                .build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this, initializationStatus -> {
            // Optional: handle initialization complete callback here if needed
        });

        // call_secratery init
        call_secratery = findViewById(R.id.call_secratery);

    }

    /**
     * Firebase থেকে নামাজের সময় নিয়ে TextView ও AnalogClockView-এ সেট করার জন্য
     */
    private void setTimeFromSnapshot(DataSnapshot snapshot, String key, TextView textView, AnalogClockView AnalogClockView) {
        String time = snapshot.child(key).getValue(String.class);

        if (time != null) {
            textView.setText(time);

            if (time.contains(":")) {
                try {
                    // Parse time যেমন: "04:50 AM" অথবা "16:30"
                    String[] parts = time.trim().split("[: ]+");
                    int hour = Integer.parseInt(parts[0]);
                    int minute = Integer.parseInt(parts[1]);
                    String ampm = (parts.length >= 3) ? parts[2].toUpperCase() : "";

                    if (ampm.equals("PM") && hour != 12) {
                        hour += 12;
                    } else if (ampm.equals("AM") && hour == 12) {
                        hour = 0;
                    }

                    if (AnalogClockView != null) {
                        AnalogClockView.setTime(hour, minute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "সময় সেট করতে সমস্যা হয়েছে: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // কল করার জন্য permission চেক এবং কলিং মেথড গুলো

    public void don1(View v) {
        Toast.makeText(this, "ব্যাংকের মাধ্যমে দান করুন অথবা সভাপতি/সেক্রেটারীর সঙ্গে যোগাযোগ করুন।", Toast.LENGTH_LONG).show();
    }

    public void don2(View v) { don1(v); }
    public void don3(View v) { don1(v); }
    public void don4(View v) { don1(v); }
    public void don5(View v) { don1(v); }

    // সহ-সভাপতি কল
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

    // সাধারণ সম্পাদক কল
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
}
