package pnpmsjm.com.ourmasjid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton; // registerButton যোগ করা হলো
    private FirebaseAuth mAuth; // FirebaseAuth অবজেক্ট

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // আপনার XML লেআউট ফাইলের নাম

        // User chek

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // ইউজার আগে থেকেই লগ ইন করা আছে
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // ইউজার নেই, Login screen দেখাও
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        // UI উপাদানগুলি ইনিশিয়ালাইজ করুন
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);

        // FirebaseAuth ইনস্ট্যান্স পান
        mAuth = FirebaseAuth.getInstance();

        // লগইন বাটনে ক্লিক লিসেনার সেট করুন
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ইমেল এবং পাসওয়ার্ড টেক্সটইনপুট থেকে নিন
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // ইনপুট ভ্যালিডেশন
                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("ইমেল প্রয়োজন!");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordInput.setError("পাসওয়ার্ড প্রয়োজন!");
                    return;
                }

                // Firebase দিয়ে লগইন করার চেষ্টা করুন
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // লগইন সফল হলে
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "লগইন সফল: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                    // এখানে আপনি আপনার প্রধান Activity তে যেতে পারেন
                                    // যেমন: startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    // অথবা যেখানে নামাজের সময় পরিবর্তন করার অপশন আছে সেখানে যেতে পারেন:
                                    startActivity(new Intent(LoginActivity.this, UpdatePrayerTimesActivity.class));
                                    finish(); // লগইন Activity বন্ধ করুন যাতে ব্যবহারকারী ব্যাক বাটন চাপলে আবার এখানে না আসে
                                } else {
                                    // লগইন ব্যর্থ হলে
                                    Toast.makeText(LoginActivity.this, "লগইন ব্যর্থ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    // ব্যবহারকারী যদি ইতিমধ্যে লগইন করা থাকে, তাহলে তাকে সরাসরি মূল Activity তে পাঠান
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // যদি ব্যবহারকারী ইতিমধ্যে লগইন করা থাকে, তবে তাকে সরাসরি নামাজের সময় আপডেট করার স্ক্রিনে পাঠান
            startActivity(new Intent(LoginActivity.this, UpdatePrayerTimesActivity.class));
            finish();
        }
    }
}
