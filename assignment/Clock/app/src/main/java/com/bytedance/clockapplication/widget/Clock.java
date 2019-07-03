package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import com.bytedance.clockapplication.MainActivity;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }

    }

    private void drawDegrees(Canvas canvas) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        Paint hoursValuePaint = new Paint();
        hoursValuePaint.setColor(hoursValuesColor);
        hoursValuePaint.setAntiAlias(true);
        hoursValuePaint.setTextSize(mWidth*0.08f);
        hoursValuePaint.setStyle(Paint.Style.FILL);
        hoursValuePaint.setTextAlign(Paint.Align.CENTER);

        float valueCenter = mRadius*0.75f;

        for (int i = 0; i < FULL_ANGLE; i += 30 /* Step */) {

            int X = (int) (mCenterX + valueCenter * Math.cos(Math.toRadians(i)));
            int Y = (int) (mCenterX + valueCenter * Math.sin(Math.toRadians(i)));

            Paint.FontMetrics fontMetrics = hoursValuePaint.getFontMetrics();
            float top = fontMetrics.top;
            float bottom = fontMetrics.bottom;
            int baseLineY = (int)(Y-top/2-bottom/2);
            int value = i/30+3;
            value = value > 12 ? value - 12 : value;
            String text = value > 9 ? ""+value : "0"+value;
            canvas.drawText(text, X, baseLineY,hoursValuePaint);

        }

    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        float accurateMinute = minute + (float)second/60;
        float accurateHour   = hour + accurateMinute/60;

        float secondNeedleLength = (float)(mRadius * 0.55);
        float minuteNeedleLength = (float)(mRadius * 0.45);
        float hourNeedleLength = (float)(mRadius * 0.35);

        Paint secondPaint = new Paint();
        Paint minutePaint = new Paint();
        Paint hourPaint   = new Paint();
        secondPaint.setColor(secondsNeedleColor);
        secondPaint.setStyle(Paint.Style.FILL);
        secondPaint.setAntiAlias(true);
        secondPaint.setStrokeWidth(4f);
        minutePaint.setColor(minutesNeedleColor);
        minutePaint.setStyle(Paint.Style.FILL);
        minutePaint.setAntiAlias(true);
        minutePaint.setStrokeWidth(7f);
        hourPaint.setColor(hoursNeedleColor);
        hourPaint.setStyle(Paint.Style.FILL);
        hourPaint.setAntiAlias(true);
        hourPaint.setStrokeWidth(10f);

        int secondEndX = (int) (mCenterX + secondNeedleLength * Math.cos(Math.toRadians((second-15)*6)));
        int secondEndY = (int) (mCenterX + secondNeedleLength * Math.sin(Math.toRadians((second-15)*6)));

        int minuteEndX = (int) (mCenterX + minuteNeedleLength * Math.cos(Math.toRadians((accurateMinute-15)*6)));
        int minuteEndY = (int) (mCenterX + minuteNeedleLength * Math.sin(Math.toRadians((accurateMinute-15)*6)));

        int hourEndX   = (int) (mCenterX + hourNeedleLength * Math.cos(Math.toRadians((accurateHour-3)*30)));
        int hourEndY   = (int) (mCenterX + hourNeedleLength * Math.sin(Math.toRadians((accurateHour-3)*30)));

        System.out.println(""+second+" "+accurateMinute+" "+accurateHour);

        canvas.drawLine(mCenterX,mCenterY,secondEndX,secondEndY,secondPaint);
        canvas.drawLine(mCenterX,mCenterY,minuteEndX,minuteEndY,minutePaint);
        canvas.drawLine(mCenterX,mCenterY,hourEndX,hourEndY,hourPaint);
    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint InnerPaint = new Paint();
        Paint OuterPaint = new Paint();

        InnerPaint.setStyle(Paint.Style.FILL);
        InnerPaint.setColor(centerInnerColor);
        InnerPaint.setAntiAlias(true);
        OuterPaint.setStyle(Paint.Style.FILL);
        OuterPaint.setColor(centerOuterColor);
        OuterPaint.setAntiAlias(true);

        canvas.drawCircle(mCenterX,mCenterY,(float) (mWidth*0.02),OuterPaint);
        canvas.drawCircle(mCenterX,mCenterY,(float) (mWidth*0.013),InnerPaint);


    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

    private static class myHandler extends Handler {
        private final WeakReference<MainActivity> myActivity;

        public myHandler(MainActivity activity){
            myActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = myActivity.get();
            if(activity!=null){
                activity.getClock().invalidate();
            }
        }

    }
    private myHandler timeHandler;
    public void start(MainActivity activity){
        timeHandler = new myHandler(activity);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                timeHandler.sendEmptyMessage(0);
                timeHandler.postDelayed(this,1000);
            }
        };

        timeHandler.postDelayed(runnable,1000);
    }

}