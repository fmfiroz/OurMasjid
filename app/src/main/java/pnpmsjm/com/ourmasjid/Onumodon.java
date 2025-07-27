package pnpmsjm.com.ourmasjid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class Onumodon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onumodon);
        ImageView OnumodonImage = findViewById(R.id.OnumodonImage);

        OnumodonImage.setOnClickListener(v -> {
            Intent intent = new Intent(Onumodon.this, FullscreenImage.class);
            intent.putExtra("imageResId", R.drawable.onumodon); // তুমি চাইলে URI or URL পাঠাতে পারো
            startActivity(intent);
        });
    }
}
