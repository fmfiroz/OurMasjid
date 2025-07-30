package pnpmsjm.com.ourmasjid;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdatePrayerTimesActivity extends AppCompatActivity {

    private EditText editFajr, editDhuhr, editAsr, editMaghrib, editIsha, editJuma;
    private EditText edit_monthly_chada, edit_first_week, edit_second_week, edit_third_week, edit_fourth_week, edit_misc_danbox;
    private EditText edit_imam_salary, edit_muajjin_salary, edit_electricity_bill, edit_misc_expence;
    private EditText edit_dev_fund_income, edit_kollan_fund_income, edit_current_balance;

    private Spinner monthSpinner, yearSpinner;
    private Button saveButton, hisabsaveButton;

    private DatabaseReference prayerDbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_prayer_times);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "অনুগ্রহ করে লগইন করুন", Toast.LENGTH_SHORT).show();
            return;
        }

        // Spinner initialization
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        String[] years = {"2024", "2025", "2026", "2027", "2028", "2029", "2030"};

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Prayer Time Inputs
        editFajr = findViewById(R.id.editFajr);
        editDhuhr = findViewById(R.id.editDhuhr);
        editAsr = findViewById(R.id.editAsr);
        editMaghrib = findViewById(R.id.editMaghrib);
        editIsha = findViewById(R.id.editIsha);
        editJuma = findViewById(R.id.editJuma);

        // Monthly Hisab Inputs
        edit_monthly_chada = findViewById(R.id.edit_monthly_chada);
        edit_first_week = findViewById(R.id.edit_first_week);
        edit_second_week = findViewById(R.id.edit_second_week);
        edit_third_week = findViewById(R.id.edit_third_week);
        edit_fourth_week = findViewById(R.id.edit_fourth_week);
        edit_misc_danbox = findViewById(R.id.edit_misc_danbox);

        edit_imam_salary = findViewById(R.id.edit_imam_salary);
        edit_muajjin_salary = findViewById(R.id.edit_muajjin_salary);
        edit_electricity_bill = findViewById(R.id.edit_electricity_bill);
        edit_misc_expence = findViewById(R.id.edit_misc_expence);
        edit_dev_fund_income = findViewById(R.id.edit_dev_fund_income);
        edit_kollan_fund_income = findViewById(R.id.edit_kollan_fund_income);
        edit_current_balance = findViewById(R.id.edit_current_balance);

        // Buttons
        saveButton = findViewById(R.id.saveButton);
        hisabsaveButton = findViewById(R.id.hisabsaveButton);

        // Firebase DB reference
        prayerDbRef = FirebaseDatabase.getInstance().getReference("prayer_times");

        loadCurrentPrayerTimes();

        saveButton.setOnClickListener(v -> savePrayerTimes());
        hisabsaveButton.setOnClickListener(v -> saveMonthlyHisab());
    }

    private void loadCurrentPrayerTimes() {
        prayerDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editFajr.setText(snapshot.child("fajr").getValue(String.class));
                editDhuhr.setText(snapshot.child("dhuhr").getValue(String.class));
                editAsr.setText(snapshot.child("asr").getValue(String.class));
                editMaghrib.setText(snapshot.child("maghrib").getValue(String.class));
                editIsha.setText(snapshot.child("isha").getValue(String.class));
                editJuma.setText(snapshot.child("juma").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePrayerTimesActivity.this, "ডেটা লোড ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePrayerTimes() {
        Map<String, Object> timeMap = new HashMap<>();
        timeMap.put("fajr", editFajr.getText().toString());
        timeMap.put("dhuhr", editDhuhr.getText().toString());
        timeMap.put("asr", editAsr.getText().toString());
        timeMap.put("maghrib", editMaghrib.getText().toString());
        timeMap.put("isha", editIsha.getText().toString());
        timeMap.put("juma", editJuma.getText().toString());

        prayerDbRef.updateChildren(timeMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "নামাযের সময় সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "সংরক্ষণ ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMonthlyHisab() {
        String monthYearKey = monthSpinner.getSelectedItem().toString() + "_" + yearSpinner.getSelectedItem().toString();

        DatabaseReference monthlyReportRef = FirebaseDatabase.getInstance().getReference("monthly_report").child(monthYearKey);

        Map<String, Object> data = new HashMap<>();
        data.put("month_name", monthYearKey);
        data.put("monthly_chada", edit_monthly_chada.getText().toString());
        data.put("first_week", edit_first_week.getText().toString());
        data.put("second_week", edit_second_week.getText().toString());
        data.put("third_week", edit_third_week.getText().toString());
        data.put("fourth_week", edit_fourth_week.getText().toString());
        data.put("misc_danbox", edit_misc_danbox.getText().toString());
        data.put("imam_salary", edit_imam_salary.getText().toString());
        data.put("muajjin_salary", edit_muajjin_salary.getText().toString());
        data.put("electricity_bill", edit_electricity_bill.getText().toString());
        data.put("misc_expence", edit_misc_expence.getText().toString());
        data.put("dev_fund_income", edit_dev_fund_income.getText().toString());
        data.put("kollan_fund_income", edit_kollan_fund_income.getText().toString());
        data.put("current_balance", edit_current_balance.getText().toString());

        monthlyReportRef.updateChildren(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "মাসিক হিসাব সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "হিসাব আপডেট ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
