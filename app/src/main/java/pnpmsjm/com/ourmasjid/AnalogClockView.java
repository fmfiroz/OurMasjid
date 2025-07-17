package pnpmsjm.com.ourmasjid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class AnalogClockView extends View {

    private Paint paint;
    private float hour = 0; // ঘন্টা
    private float minute = 0; // মিনিট

    public AnalogClockView(Context context) {
        super(context);
        init();
    }

    public AnalogClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnalogClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true); // স্মুথ আঁকার জন্য
        paint.setStyle(Paint.Style.STROKE); // শুধু স্ট্রোক আঁকবে, পূরণ করবে না
        paint.setStrokeWidth(5); // স্ট্রোকের পুরুত্ব
        paint.setColor(Color.BLACK); // ডিফল্ট রঙ
    }

    /**
     * নামাজের সময় (যেমন "04:30 AM") সেট করার মেথড।
     * @param timeString নামাজের সময় স্ট্রিং ফরম্যাটে (যেমন "04:30 AM", "01:00 PM")
     */
    public void setPrayerTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            this.hour = 0;
            this.minute = 0;
            invalidate(); // UI আপডেট করুন
            return;
        }

        try {
            // সময় স্ট্রিং পার্স করার চেষ্টা করুন
            // উদাহরণ: "04:30 AM" -> hour=4, minute=30
            // উদাহরণ: "01:00 PM" -> hour=13, minute=0
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
            java.util.Date date = sdf.parse(timeString);
            if (date != null) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.setTime(date);
                this.hour = calendar.get(java.util.Calendar.HOUR_OF_DAY); // 24-ঘন্টার ফরম্যাটে ঘন্টা
                this.minute = calendar.get(java.util.Calendar.MINUTE);
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            // পার্সিং ব্যর্থ হলে ডিফল্ট মান সেট করুন বা ত্রুটি দেখান
            this.hour = 0;
            this.minute = 0;
        }
        invalidate(); // UI আপডেট করুন
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - (int) paint.getStrokeWidth(); // ঘড়ির ব্যাসার্ধ
        int centerX = width / 2;
        int centerY = height / 2;

        canvas.translate(centerX, centerY); // ক্যানভাসের অরিজিনকে সেন্টারে নিয়ে আসুন

        // ১. ঘড়ির ডায়াল আঁকুন
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(0, 0, radius, paint); // মাঝখানে বৃত্ত আঁকুন

        // ২. ঘন্টার দাগ আঁকুন (১২টি বড় দাগ)
        paint.setStrokeWidth(8);
        for (int i = 0; i < 12; i++) {
            canvas.drawLine(0, -radius, 0, -radius + 40, paint); // 40 হল দাগের দৈর্ঘ্য
            canvas.rotate(30); // 360 / 12 = 30 ডিগ্রি করে ঘোরান
        }

        // ৩. মিনিটের দাগ আঁকুন (৬০টি ছোট দাগ)
        paint.setStrokeWidth(3);
        canvas.save(); // বর্তমান ক্যানভাস স্টেট সেভ করুন
        for (int i = 0; i < 60; i++) {
            if (i % 5 != 0) { // প্রতি 5 মিনিটের দাগ ছাড়া
                canvas.drawLine(0, -radius, 0, -radius + 20, paint); // 20 হল দাগের দৈর্ঘ্য
            }
            canvas.rotate(6); // 360 / 60 = 6 ডিগ্রি করে ঘোরান
        }
        canvas.restore(); // আগের সেভ করা ক্যানভাস স্টেট পুনরুদ্ধার করুন

        // ৪. ঘন্টার কাঁটা আঁকুন
        paint.setStrokeWidth(12);
        paint.setColor(Color.BLUE); // ঘন্টার কাঁটার রঙ
        float hourAngle = (hour % 12 + minute / 60.0f) * 30; // 30 ডিগ্রি প্রতি ঘণ্টা
        canvas.save();
        canvas.rotate(hourAngle);
        canvas.drawLine(0, 0, 0, -radius * 0.5f, paint); // 0.5f হল দৈর্ঘ্যের অনুপাত
        canvas.restore();

        // ৫. মিনিটের কাঁটা আঁকুন
        paint.setStrokeWidth(8);
        paint.setColor(Color.RED); // মিনিটের কাঁটার রঙ
        float minuteAngle = minute * 6; // 6 ডিগ্রি প্রতি মিনিট
        canvas.save();
        canvas.rotate(minuteAngle);
        canvas.drawLine(0, 0, 0, -radius * 0.8f, paint); // 0.8f হল দৈর্ঘ্যের অনুপাত
        canvas.restore();

        // ৬. কেন্দ্র বিন্দু আঁকুন
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.DKGRAY);
        canvas.drawCircle(0, 0, 15, paint); // মাঝখানে ছোট বৃত্ত

        // ক্যানভাসের অরিজিনকে আগের অবস্থায় ফিরিয়ে আনুন (যদিও translate() স্থায়ী নয়)
        // canvas.translate(-centerX, -centerY); // এই লাইনটি সাধারণত প্রয়োজন হয় না কারণ onDraw নতুন ক্যানভাস পায়
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // ঘড়ির আকারের জন্য একটি স্কোয়ার পরিমাপ সেট করুন
        int size = Math.min(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        setMeasuredDimension(size, size);
    }
}
