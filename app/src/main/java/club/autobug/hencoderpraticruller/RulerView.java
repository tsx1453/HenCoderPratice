package club.autobug.hencoderpraticruller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class RulerView extends View {

    private final String TAG = "RulerView";

    protected int rulerShort = 90;
    protected int rulerLong = 120;
    protected int rulerHeight = 300;
    private Paint paint;
    protected float nowValue = 55.6f;
    protected float pointerWidth = 10;
    protected float spaceWidth = 35;
    protected float lastX;

    {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(60f);
    }

    public RulerView(Context context) {
        super(context);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int h = getHeight();
        int y = h / 2 - rulerHeight / 2;
        int centerX = getWidth() / 2;

        //背景
        paint.setStrokeWidth(0f);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, y, getWidth(), y + rulerHeight, paint);
        paint.setColor(Color.GRAY);
        canvas.drawLine(0, y, getWidth(), y, paint);


        //刻度
        paint.setAlpha(155);
        paint.setStrokeWidth(3f);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);
        float drawX = centerX;
        float value = nowValue;
        while (drawX >= 0) {
            if (checkLittlePoint(value)) {
                paint.setStrokeWidth(5f);
                canvas.drawLine(drawX, y, drawX, y + rulerLong, paint);
                String str = String.valueOf((int) value);
                paint.setStrokeWidth(0f);
                canvas.drawText(str, 0, str.length(), drawX, y + rulerLong + paint.getTextSize(), paint);
                paint.setStrokeWidth(3f);
            } else {
                canvas.drawLine(drawX, y, drawX, y + rulerShort, paint);
            }
            drawX -= spaceWidth;
            value -= 0.1f;
        }
        drawX = centerX + spaceWidth;
        value = nowValue + 0.1f;
        while (drawX <= getWidth()) {
            if (checkLittlePoint(value)) {
                paint.setStrokeWidth(5f);
                canvas.drawLine(drawX, y, drawX, y + rulerLong, paint);
                value = (int) (value + 0.1f);
                String str = String.valueOf((int) value);
                paint.setStrokeWidth(0f);
                canvas.drawText(str, 0, str.length(), drawX, y + rulerLong + paint.getTextSize(), paint);
                paint.setStrokeWidth(3f);
            } else {
                canvas.drawLine(drawX, y, drawX, y + rulerShort, paint);
            }
            drawX += spaceWidth;
            value += 0.1f;
        }
        paint.setAlpha(255);

        //指针
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(pointerWidth);
        paint.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawLine(centerX, y, centerX, y + 10, paint);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(centerX, y + 10, centerX, y + rulerLong, paint);

//        paint.setStrokeWidth(0f);
//        paint.setColor(Color.BLACK);
//        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - lastX;
                int changeValueCount = (int) ((deltaX / spaceWidth));
                float delta = changeValueCount * 0.1f;
                if (Math.abs(delta) >= 0.0999) {
                    lastX = event.getX();
                }
                nowValue -= delta;
                invalidate();
                break;
        }
        return true;
    }

    private boolean checkLittlePoint(float va) {
//        Log.d(TAG, "RulerView->checkLittlePoint: " + va);
        int vai = (int) (va + 0.1f);
        return Math.abs(vai - va) < 0.09;
    }
}
