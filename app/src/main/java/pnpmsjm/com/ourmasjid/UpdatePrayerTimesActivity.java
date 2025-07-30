package pnpmsjm.com.ourmasjid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.Calendar; // For current year
import java.util.HashMap;
import java.util.Locale; // For consistent month names
import java.util.Map;
import java.text.SimpleDateFormat; // For formatting month numbers

public class UpdatePrayerTimesActivity extends AppCompatActivity {

    private EditText editFajr, editDhuhr, editAsr, editMaghrib, editIsha, editJuma;
    private EditText edit_monthly_chada, edit_first_week, edit_second_week, edit_third_week, edit_fourth_week, edit_misc_danbox;
    private EditText edit_imam_salary, edit_muajjin_salary, edit_electricity_bill, edit_misc_expence;
    private EditText edit_dev_fund_income, edit_kollan_fund_income, edit_current_balance;

    private Spinner monthSpinner, yearSpinner;
    private Button saveButton, hisabsaveButton;

    private DatabaseReference prayerDbRef;
    private DatabaseReference monthlyReportDbRef; // New reference for monthly report
    private FirebaseAuth mAuth;

    private String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private static final String TAG = "UpdatePrayerTimesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_prayer_times);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "অনুগ্রহ করে লগইন করুন", Toast.LENGTH_SHORT).show();
            // Optionally redirect to login screen
            finish(); // Close this activity if user is not logged in
            return;
        }

        // Spinner initialization
        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        String[] years = getYearsArray(); // Dynamically generate years

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Set current month and year as default selection
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH); // 0-indexed (Jan=0)
        int currentYear = calendar.get(Calendar.YEAR);

        monthSpinner.setSelection(currentMonth);

        for (int i = 0; i < years.length; i++) {
            if (years[i].equals(String.valueOf(currentYear))) {
                yearSpinner.setSelection(i);
                break;
            }
        }


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
        saveButton = findViewById(R.id.saveButton); // Prayer times save button
        hisabsaveButton = findViewById(R.id.hisabsaveButton); // Monthly hisab save button

        // Firebase DB references
        prayerDbRef = FirebaseDatabase.getInstance().getReference("prayer_times");
        monthlyReportDbRef = FirebaseDatabase.getInstance().getReference("monthly_report"); // Initialize new ref

        loadCurrentPrayerTimes(); // Load prayer times on activity start

        // --- Add Spinner Listeners for Monthly Hisab Loading ---
        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // This will be called when either spinner's selection changes
                loadMonthlyHisab();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        };

        monthSpinner.setOnItemSelectedListener(spinnerListener);
        yearSpinner.setOnItemSelectedListener(spinnerListener);
        // --- End Spinner Listeners ---

        saveButton.setOnClickListener(v -> savePrayerTimes());
        hisabsaveButton.setOnClickListener(v -> saveMonthlyHisab());

        // Load monthly hisab after spinners are initialized and possibly set to current date
        // (This initial load is important to show data for the default selected month/year)
        loadMonthlyHisab();
    }

    private String[] getYearsArray() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[7]; // From current year -2 to current year +4
        for (int i = 0; i < 7; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        return years;
    }


    private void loadCurrentPrayerTimes() {
        prayerDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Using ternary operator to provide default empty string if value is null
                editFajr.setText(snapshot.child("fajr").getValue(String.class) != null ? snapshot.child("fajr").getValue(String.class) : "");
                editDhuhr.setText(snapshot.child("dhuhr").getValue(String.class) != null ? snapshot.child("dhuhr").getValue(String.class) : "");
                editAsr.setText(snapshot.child("asr").getValue(String.class) != null ? snapshot.child("asr").getValue(String.class) : "");
                editMaghrib.setText(snapshot.child("maghrib").getValue(String.class) != null ? snapshot.child("maghrib").getValue(String.class) : "");
                editIsha.setText(snapshot.child("isha").getValue(String.class) != null ? snapshot.child("isha").getValue(String.class) : "");
                editJuma.setText(snapshot.child("juma").getValue(String.class) != null ? snapshot.child("juma").getValue(String.class) : "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePrayerTimesActivity.this, "নামাজের সময় লোড ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load prayer times: " + error.getMessage());
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
                Toast.makeText(this, "নামাযের সময় সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "সংরক্ষণ ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save prayer times: " + task.getException().getMessage());
            }
        });
    }

    // New method to load monthly hisab based on spinner selection
    private void loadMonthlyHisab() {
        String selectedMonthName = monthSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();

        // Convert month name to 2-digit number (e.g., "July" -> "07")
        String monthNumber = getMonthNumber(selectedMonthName);

        // Construct the Firebase key as YYYY_MM_MonthName
        String monthYearKey = selectedYear + "_" + monthNumber + "_" + selectedMonthName;
        Log.d(TAG, "Loading monthly hisab for key: " + monthYearKey);

        DatabaseReference specificMonthlyReportRef = monthlyReportDbRef.child(monthYearKey);

        specificMonthlyReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Monthly hisab data received. Exists: " + snapshot.exists());
                if (snapshot.exists()) {
                    // Load data into EditText fields
                    edit_monthly_chada.setText(getStringValue(snapshot, "monthly_chada"));
                    edit_first_week.setText(getStringValue(snapshot, "first_week"));
                    edit_second_week.setText(getStringValue(snapshot, "second_week"));
                    edit_third_week.setText(getStringValue(snapshot, "third_week"));
                    edit_fourth_week.setText(getStringValue(snapshot, "fourth_week"));
                    edit_misc_danbox.setText(getStringValue(snapshot, "misc_danbox"));

                    edit_imam_salary.setText(getStringValue(snapshot, "imam_salary"));
                    edit_muajjin_salary.setText(getStringValue(snapshot, "muajjin_salary"));
                    edit_electricity_bill.setText(getStringValue(snapshot, "electricity_bill"));
                    edit_misc_expence.setText(getStringValue(snapshot, "misc_expence"));
                    edit_dev_fund_income.setText(getStringValue(snapshot, "dev_fund_income"));
                    edit_kollan_fund_income.setText(getStringValue(snapshot, "kollan_fund_income"));
                    edit_current_balance.setText(getStringValue(snapshot, "current_balance"));

                    Toast.makeText(UpdatePrayerTimesActivity.this, "হিসাব লোড হয়েছে: " + selectedMonthName + " " + selectedYear, Toast.LENGTH_SHORT).show();
                } else {
                    // Clear all EditText fields if no data exists for the selected month/year
                    clearMonthlyHisabFields();
                    Toast.makeText(UpdatePrayerTimesActivity.this, selectedMonthName + " " + selectedYear + " এর জন্য কোন হিসাব পাওয়া যায়নি।", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdatePrayerTimesActivity.this, "হিসাব লোড ব্যর্থ: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load monthly hisab: " + error.getMessage());
            }
        });
    }

    private void saveMonthlyHisab() {
        String selectedMonthName = monthSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();

        // Convert month name to 2-digit number (e.g., "July" -> "07")
        String monthNumber = getMonthNumber(selectedMonthName);

        // Construct the Firebase key as YYYY_MM_MonthName
        String monthYearKey = selectedYear + "_" + monthNumber + "_" + selectedMonthName;
        Log.d(TAG, "Saving monthly hisab to key: " + monthYearKey);

        // Reference to the specific monthly report entry
        DatabaseReference specificMonthlyReportRef = monthlyReportDbRef.child(monthYearKey);

        Map<String, Object> data = new HashMap<>();
        // It's better to save the actual month name and year separately for clarity
        data.put("month_name", selectedMonthName);
        data.put("year", selectedYear);

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

        specificMonthlyReportRef.updateChildren(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "মাসিক হিসাব সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "হিসাব আপডেট ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save monthly hisab: " + task.getException().getMessage());
            }
        });
    }

    // Helper method to get string value from DataSnapshot, returns empty string if null
    private String getStringValue(DataSnapshot snapshot, String key) {
        String value = snapshot.child(key).getValue(String.class);
        return value != null ? value : "";
    }

    // Helper method to clear all monthly hisab EditText fields
    private void clearMonthlyHisabFields() {
        edit_monthly_chada.setText("");
        edit_first_week.setText("");
        edit_second_week.setText("");
        edit_third_week.setText("");
        edit_fourth_week.setText("");
        edit_misc_danbox.setText("");
        edit_imam_salary.setText("");
        edit_muajjin_salary.setText("");
        edit_electricity_bill.setText("");
        edit_misc_expence.setText("");
        edit_dev_fund_income.setText("");
        edit_kollan_fund_income.setText("");
        edit_current_balance.setText("");
    }

    // Helper method to convert month name to two-digit number
    private String getMonthNumber(String monthName) {
        // Use SimpleDateFormat to parse month name and format to number
        try {
            java.util.Date date = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(monthName);
            if (date != null) {
                return new SimpleDateFormat("MM", Locale.ENGLISH).format(date);
            }
        } catch (java.text.ParseException e) {
            Log.e(TAG, "Error parsing month name: " + monthName, e);
        }
        return "00"; // Fallback
    }
}