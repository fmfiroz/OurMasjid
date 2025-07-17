package pnpmsjm.com.ourmasjid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class UpdatePrayerTimesActivity extends AppCompatActivity {    private EditText editFajr, editDhuhr, editAsr, editMaghrib, editIsha, editJuma;
    private Button saveButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_prayer_times);

        editFajr = findViewById(R.id.editFajr);
        editDhuhr = findViewById(R.id.editDhuhr);
        editAsr = findViewById(R.id.editAsr);
        editMaghrib = findViewById(R.id.editMaghrib);
        editIsha = findViewById(R.id.editIsha);
        editJuma = findViewById(R.id.editJuma);
        saveButton = findViewById(R.id.saveButton);

        mDatabase = FirebaseDatabase.getInstance().getReference("prayer_times");
        mAuth = FirebaseAuth.getInstance();

        // বর্তমান নামাজের সময় লোড করুন
        loadCurrentPrayerTimes();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrayerTimes();
            }
        });
    }

    private void loadCurrentPrayerTimes() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    editFajr.setText(dataSnapshot.child("fajr").getValue(String.class));
                    editDhuhr.setText(dataSnapshot.child("dhuhr").getValue(String.class));
                    editAsr.setText(dataSnapshot.child("asr").getValue(String.class));
                    editMaghrib.setText(dataSnapshot.child("maghrib").getValue(String.class));
                    editIsha.setText(dataSnapshot.child("isha").getValue(String.class));
                    editJuma.setText(dataSnapshot.child("juma").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdatePrayerTimesActivity.this, "সময় লোড করতে ব্যর্থ: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePrayerTimes() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "লগইন করা নেই। অনুগ্রহ করে লগইন করুন।", Toast.LENGTH_SHORT).show();
            // লগইন স্ক্রিনে রিডাইরেক্ট করতে পারেন
            return;
        }

        // ডেটাবেজ রুলস এ নিশ্চিত করুন যে শুধু অথেনটিকেটেড ইউজাররাই পরিবর্তন করতে পারবে।
        // Firebase Console -> Realtime Database -> Rules এ যান।

        Map<String, Object> prayerTimes = new HashMap<>();
        prayerTimes.put("fajr", editFajr.getText().toString());
        prayerTimes.put("dhuhr", editDhuhr.getText().toString());
        prayerTimes.put("asr", editAsr.getText().toString());
        prayerTimes.put("maghrib", editMaghrib.getText().toString());
        prayerTimes.put("isha", editIsha.getText().toString());
        prayerTimes.put("juma", editJuma.getText().toString());

        mDatabase.updateChildren(prayerTimes)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdatePrayerTimesActivity.this, "নামাজের সময় সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdatePrayerTimesActivity.this, "আপডেট ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}