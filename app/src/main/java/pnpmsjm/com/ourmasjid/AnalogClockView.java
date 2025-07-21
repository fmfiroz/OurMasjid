package pnpmsjm.com.ourmasjid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class AnalogClockView extends View {

    private Paint paint;
    private float hour = 0;
    private float minute = 0;
    private Bitmap dialBitmap;

    public AnalogClockView(Context context) {
        super(context);
        init(context);
    }

    public AnalogClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnalogClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);

        // 🎯 Load FM dial PNG image from drawable
        dialBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.analogue_clock_dial);
    }

    /**
     * স্ট্রিং টাইম পাস করে সেট করার জন্য (যেমন "04:50 AM")
     */
    public void setPrayerTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            this.hour = 0;
            this.minute = 0;
            invalidate();
            return;
        }

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
            java.util.Date date = sdf.parse(timeString);
            if (date != null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(date);
                this.hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
                this.minute = calendar.get(java.util.Calendar.MINUTE);
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            this.hour = 0;
            this.minute = 0;
        }
        invalidate();
    }

    /**
     * সময় hours ও minutes হিসেবে সেট করার জন্য
     */
    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY);

        canvas.save();
        canvas.translate(centerX, centerY);

        // 🖼 Draw the FM dial background
        if (dialBitmap != null) {
            int imageSize = 2 * radius;
            Rect src = new Rect(0, 0, dialBitmap.getWidth(), dialBitmap.getHeight());
            Rect dst = new Rect(-radius, -radius, radius, radius);
            canvas.drawBitmap(dialBitmap, src, dst, null);
        }

        // ⏰ Hour hand
        paint.setStrokeWidth(12);
        paint.setColor(Color.BLUE);
        float hourAngle = (hour % 12 + minute / 60.0f) * 30;
        canvas.save();
        canvas.rotate(hourAngle);
        canvas.drawLine(0, 0, 0, -radius * 0.5f, paint);
        canvas.restore();

        // ⏰ Minute hand
        paint.setStrokeWidth(8);
        paint.setColor(Color.RED);
        float minuteAngle = minute * 6;
        canvas.save();
        canvas.rotate(minuteAngle);
        canvas.drawLine(0, 0, 0, -radius * 0.8f, paint);
        canvas.restore();

        // ⚫ Center dot
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(0, 0, 15, paint);

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        setMeasuredDimension(size, size);
    }
}
