package pnpmsjm.com.ourmasjid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {


    String uriString;
    ImageView call_secratery;
    public TextView donaate1, donaate2, donaate3, donaate4, donaate5, developer_toast, ghosona, mulniti, omorbani, gothontontro, gothontontro2, onumodon, comiti,
            dhara1, dhara2, dhara3, dhara4, dhara5, dhara6, dhara7, dhara8, dhara9, dhara10, dhara11, dhara12, dhara13, dhara14, dhara15, dhara16, dhara17, dhara18, dhara19, dhara20, dhara21, dhara22, fajrTime, dhuhrTime, asrTime, maghribTime, ishaTime, jumaTime;;

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;


    public void init() {
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

        //Intent Listeners
        developer_toast.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Others_1.class)));
        ghosona.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Ghosona.class)));
        mulniti.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Mulniti.class)));
        gothontontro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GothonTontro_1.class)));
        gothontontro2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GothonTontro_1.class)));
        omorbani.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Omorbani.class)));
        onumodon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Onumodon.class)));
        comiti.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Committee.class)));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI উপাদানগুলি ইনিশিয়ালাইজ করুন
        fajrTime = findViewById(R.id.fajrTime);
        dhuhrTime = findViewById(R.id.dhuhrTime);
        asrTime = findViewById(R.id.asrTime);
        maghribTime = findViewById(R.id.maghribTime);
        ishaTime = findViewById(R.id.ishaTime);
        jumaTime = findViewById(R.id.jumaTime);

        // Firebase Realtime Database রেফারেন্স পান
        mDatabase = FirebaseDatabase.getInstance().getReference("prayer_times");

        // ডেটা পড়ার জন্য ValueEventListener যোগ করুন
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // যখন ডেটা পরিবর্তন হয়, তখন এই মেথড কল হয়
                if (dataSnapshot.exists()) {
                    String fajr = dataSnapshot.child("fajr").getValue(String.class);
                    String dhuhr = dataSnapshot.child("dhuhr").getValue(String.class);
                    String asr = dataSnapshot.child("asr").getValue(String.class);
                    String maghrib = dataSnapshot.child("maghrib").getValue(String.class);
                    String isha = dataSnapshot.child("isha").getValue(String.class);
                    String juma = dataSnapshot.child("juma").getValue(String.class);

                    // TextView গুলিতে ডেটা সেট করুন
                    fajrTime.setText("ফজর: " + fajr);
                    dhuhrTime.setText("যোহর: " + dhuhr);
                    asrTime.setText("আসর: " + asr);
                    maghribTime.setText("মাগরিব: " + maghrib);
                    ishaTime.setText("এশা: " + isha);
                    jumaTime.setText("জুমআ: " + juma);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ডেটা পড়তে ব্যর্থ হলে এখানে হ্যান্ডেল করুন
                // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "ইমেল এবং পাসওয়ার্ড দিন", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // লগইন সফল
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(MainActivity.this, "লগইন সফল: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                    // নামাজের সময় পরিবর্তনের স্ক্রিনে যান
                                    startActivity(new Intent(MainActivity.this, UpdatePrayerTimesActivity.class));
                                    finish(); // লগইন স্ক্রিন বন্ধ করুন
                                } else {
                                    // লগইন ব্যর্থ
                                    Toast.makeText(MainActivity.this, "লগইন ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // 🔹 STEP 1: Test device ID সেট করা
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("CE1FDF3A2281C0F490245647207A6184")) // আপনার ডিভাইস ID
                .build();
        MobileAds.setRequestConfiguration(configuration);

        // 🔹 STEP 2: Initialize AdMob
        MobileAds.initialize(this, initializationStatus -> {});





        // Views and Listeners
        call_secratery = findViewById(R.id.call_secratery);

        init();


        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        final BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset:" + slideOffset);
            }
        });
    }




    public void don1(View v) {
        Toast.makeText(this, "ব্যাংকের মাধ্যমে দান করুন অথবা সভাপতি/সেক্রেটারীর সঙ্গে যোগাযোগ করুন।", Toast.LENGTH_LONG).show();
    }
    public void don2(View v) { don1(v); }
    public void don3(View v) { don1(v); }
    public void don4(View v) { don1(v); }
    public void don5(View v) { don1(v); }

    // Soho-sovapoti 01711187317

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

    // Salam 01716101240
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

    // Handle permission request result (optional but recommended)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

