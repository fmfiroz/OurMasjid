package pnpmsjm.com.bd.ourmasjid_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;


public class MainActivity extends AppCompatActivity {


    String uriString;
    ImageView call_secratery;
    public static TextView donaate1, donaate2, donaate3, donaate4, donaate5, developer_toast, ghosona, mulniti, omorbani, gothontontro, gothontontro2, onumodon, comiti,
            dhara1, dhara2, dhara3, dhara4, dhara5, dhara6, dhara7, dhara8, dhara9, dhara10, dhara11, dhara12, dhara13, dhara14, dhara15, dhara16, dhara17, dhara18, dhara19, dhara20, dhara21, dhara22;


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
        dhara1 = findViewById(R.id.dhara1);
        dhara2 = findViewById(R.id.dhara2);
        dhara3 = findViewById(R.id.dhara3);
        dhara4 = findViewById(R.id.dhara4);
        dhara5 = findViewById(R.id.dhara5);
        dhara6 = findViewById(R.id.dhara6);
        dhara7 = findViewById(R.id.dhara7);
        dhara8 = findViewById(R.id.dhara8);
        dhara9 = findViewById(R.id.dhara9);
        dhara10 = findViewById(R.id.dhara10);
        dhara11 = findViewById(R.id.dhara11);
        dhara12 = findViewById(R.id.dhara12);
        dhara13 = findViewById(R.id.dhara13);
        dhara14 = findViewById(R.id.dhara14);
        dhara15 = findViewById(R.id.dhara15);
        dhara16 = findViewById(R.id.dhara16);
        dhara17 = findViewById(R.id.dhara17);
        dhara18 = findViewById(R.id.dhara18);
        dhara19 = findViewById(R.id.dhara19);
        dhara20 = findViewById(R.id.dhara20);
        dhara21 = findViewById(R.id.dhara21);
        dhara22 = findViewById(R.id.dhara22);

        //Intent Listeners
        developer_toast.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Others_1.class)));
        ghosona.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Ghosona.class)));
        mulniti.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Mulniti.class)));
        gothontontro.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GothonTontro_1.class)));
        gothontontro2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GothonTontro_1.class)));
        omorbani.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Omorbani.class)));
        onumodon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Onumodon.class)));
        comiti.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Comittee.class)));
        dhara1.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_1.class)));
        dhara2.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_2.class)));
        dhara3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_3.class)));
        dhara4.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_4.class)));
        dhara5.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_5.class)));
        dhara6.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_6.class)));
        dhara7.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_7.class)));
        dhara8.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_8.class)));
        dhara9.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_9.class)));
        dhara10.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_10.class)));
        dhara11.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_11.class)));
        dhara12.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_12.class)));
        dhara13.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_13.class)));
        dhara14.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_14.class)));
        dhara15.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_15.class)));
        dhara16.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_16.class)));
        dhara17.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_17.class)));
        dhara18.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_18.class)));
        dhara19.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_19.class)));
        dhara20.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_20.class)));
        dhara21.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_21.class)));
        dhara22.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Dhara_22.class)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
