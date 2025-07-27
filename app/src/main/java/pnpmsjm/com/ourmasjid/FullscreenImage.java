package pnpmsjm.com.ourmasjid;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;

public class FullscreenImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        PhotoView photoView = findViewById(R.id.fullImageView);

        int imageResId = getIntent().getIntExtra("imageResId", 0);
        if (imageResId != 0) {
            photoView.setImageResource(imageResId);
        }

        photoView.setOnClickListener(v -> finish()); // ক্লিক করলে back
    }
}
