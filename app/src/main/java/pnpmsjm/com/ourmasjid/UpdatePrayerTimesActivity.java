package pnpmsjm.com.ourmasjid;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date; // Import Date class

public class UpdatePrayerTimesActivity extends AppCompatActivity {

    private EditText editFajr, editDhuhr, editAsr, editMaghrib, editIsha, editJuma;
    private EditText edit_monthly_chada, edit_first_week, edit_second_week, edit_third_week, edit_fourth_week, edit_misc_danbox;
    private EditText edit_imam_salary, edit_muajjin_salary, edit_electricity_bill, edit_misc_expence;
    private EditText edit_dev_fund_income, edit_kollan_fund_income, edit_current_balance;

    private Spinner monthSpinner, yearSpinner;
    private Button saveButton, hisabsaveButton;

    private DatabaseReference prayerDbRef;
    private DatabaseReference monthlyReportDbRef;
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
            finish();
            return;
        }

        monthSpinner = findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);

        String[] years = getYearsArray();

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        monthSpinner.setSelection(currentMonth);

        for (int i = 0; i < years.length; i++) {
            if (years[i].equals(String.valueOf(currentYear))) {
                yearSpinner.setSelection(i);
                break;
            }
        }

        editFajr = findViewById(R.id.editFajr);
        editDhuhr = findViewById(R.id.editDhuhr);
        editAsr = findViewById(R.id.editAsr);
        editMaghrib = findViewById(R.id.editMaghrib);
        editIsha = findViewById(R.id.editIsha);
        editJuma = findViewById(R.id.editJuma);

        applyPrayerTimeInputFilter(editFajr);
        applyPrayerTimeInputFilter(editDhuhr);
        applyPrayerTimeInputFilter(editAsr);
        applyPrayerTimeInputFilter(editMaghrib);
        applyPrayerTimeInputFilter(editIsha);
        applyPrayerTimeInputFilter(editJuma);


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

        saveButton = findViewById(R.id.saveButton);
        hisabsaveButton = findViewById(R.id.hisabsaveButton);

        prayerDbRef = FirebaseDatabase.getInstance().getReference("prayer_times");
        monthlyReportDbRef = FirebaseDatabase.getInstance().getReference("monthly_report");

        loadCurrentPrayerTimes();

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadMonthlyHisab();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        monthSpinner.setOnItemSelectedListener(spinnerListener);
        yearSpinner.setOnItemSelectedListener(spinnerListener);

        saveButton.setOnClickListener(v -> savePrayerTimes());
        hisabsaveButton.setOnClickListener(v -> saveMonthlyHisab());

        loadMonthlyHisab();
    }

    private String[] getYearsArray() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[7];
        for (int i = 0; i < 7; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        return years;
    }

    private void loadCurrentPrayerTimes() {
        prayerDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editFajr.setText(removeAmPm(snapshot.child("fajr").getValue(String.class)));
                editDhuhr.setText(removeAmPm(snapshot.child("dhuhr").getValue(String.class)));
                editAsr.setText(removeAmPm(snapshot.child("asr").getValue(String.class)));
                editMaghrib.setText(removeAmPm(snapshot.child("maghrib").getValue(String.class)));
                editIsha.setText(removeAmPm(snapshot.child("isha").getValue(String.class)));
                editJuma.setText(removeAmPm(snapshot.child("juma").getValue(String.class)));
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

        // Format and save prayer times with explicit AM/PM
        timeMap.put("fajr", formatPrayerTime(editFajr.getText().toString(), "AM"));
        timeMap.put("dhuhr", formatPrayerTime(editDhuhr.getText().toString(), "PM"));
        timeMap.put("asr", formatPrayerTime(editAsr.getText().toString(), "PM"));
        timeMap.put("maghrib", formatPrayerTime(editMaghrib.getText().toString(), "PM"));
        timeMap.put("isha", formatPrayerTime(editIsha.getText().toString(), "PM"));
        timeMap.put("juma", formatPrayerTime(editJuma.getText().toString(), "PM"));

        prayerDbRef.updateChildren(timeMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "নামাযের সময় সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "সংরক্ষণ ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to save prayer times: " + task.getException().getMessage());
            }
        });
    }

    // --- Updated Helper Method for Prayer Time Formatting ---
    private String formatPrayerTime(String inputTime, String forceAmPm) {
        // Expected input format: "HHmm" (e.g., "0550", "1330")
        if (inputTime == null || inputTime.length() != 4 || !inputTime.matches("\\d{4}")) {
            return ""; // Return empty string for invalid input
        }

        SimpleDateFormat inputSdf = new SimpleDateFormat("HHmm", Locale.ENGLISH);
        SimpleDateFormat outputSdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH); // 'a' for AM/PM

        try {
            Date date = inputSdf.parse(inputTime);
            if (date == null) {
                return "";
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Get the current AM/PM status of the parsed time
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // 0-23

            // Force AM/PM based on the 'forceAmPm' parameter
            if ("AM".equals(forceAmPm)) {
                if (currentHour >= 12 && currentHour != 0) { // If it's PM (12 PM to 11 PM), convert to AM
                    calendar.add(Calendar.HOUR_OF_DAY, -12);
                } else if (currentHour == 0) { // If it's 12 AM (midnight), keep it 12 AM
                    // No change needed, 00:xx is already 12:xx AM
                }
            } else if ("PM".equals(forceAmPm)) {
                if (currentHour >= 0 && currentHour < 12) { // If it's AM (12 AM to 11 AM), convert to PM
                    if (currentHour != 0) { // Don't add 12 hours to 12 AM (00:xx)
                        calendar.add(Calendar.HOUR_OF_DAY, 12);
                    }
                }
            }
            // If currentHour is already 12-23 (PM) and forceAmPm is PM, no change needed.

            return outputSdf.format(calendar.getTime());

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing prayer time: " + inputTime, e);
        }
        return ""; // Fallback for parse errors
    }

    private String removeAmPm(String timeWithAmPm) {
        if (timeWithAmPm == null || timeWithAmPm.isEmpty()) {
            return "";
        }
        String cleaned = timeWithAmPm.replace(" AM", "").replace(" PM", "").replace(":", "");
        if (cleaned.length() == 3) {
            cleaned = "0" + cleaned;
        }
        return cleaned;
    }

    private void applyPrayerTimeInputFilter(EditText editText) {
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
        editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!text.matches("\\d*")) {
                    s.replace(0, s.length(), text.replaceAll("\\D", ""));
                }
            }
        });
    }
    // --- End Updated Helper Method ---


    private void loadMonthlyHisab() {
        String selectedMonthName = monthSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();

        String monthNumber = getMonthNumber(selectedMonthName);

        String monthYearKey = selectedYear + "_" + monthNumber + "_" + selectedMonthName;
        Log.d(TAG, "Loading monthly hisab for key: " + monthYearKey);

        DatabaseReference specificMonthlyReportRef = monthlyReportDbRef.child(monthYearKey);

        specificMonthlyReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Monthly hisab data received. Exists: " + snapshot.exists());
                if (snapshot.exists()) {
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

        String monthNumber = getMonthNumber(selectedMonthName);

        String monthYearKey = selectedYear + "_" + monthNumber + "_" + selectedMonthName;
        Log.d(TAG, "Saving monthly hisab to key: " + monthYearKey);

        DatabaseReference specificMonthlyReportRef = monthlyReportDbRef.child(monthYearKey);

        Map<String, Object> data = new HashMap<>();
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

    private String getStringValue(DataSnapshot snapshot, String key) {
        String value = snapshot.child(key).getValue(String.class);
        return value != null ? value : "";
    }

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

    private String getMonthNumber(String monthName) {
        try {
            Date date = new SimpleDateFormat("MMMM", Locale.ENGLISH).parse(monthName);
            if (date != null) {
                return new SimpleDateFormat("MM", Locale.ENGLISH).format(date);
            }
        } catch (java.text.ParseException e) {
            Log.e(TAG, "Error parsing month name: " + monthName, e);
        }
        return "00";
    }
}