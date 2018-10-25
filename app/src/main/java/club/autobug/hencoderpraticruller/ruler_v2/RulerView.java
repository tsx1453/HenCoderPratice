package club.autobug.hencoderpraticruller.ruler_v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public class RulerView extends View {

    private final String TAG = "ScaleView";

    private int min = 10, max = 70;
    protected int spaceWidth = 35;
    protected int pointerLong = 120;
    protected int pointerShort = 70;
    private int deltaValue = 1;
    private int rulerHeight = 320;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect textRect = new Rect();
    private float lastX = -999;
    protected int fix, start, end, right, drawY;
    private float nowValue;
    private float realValue;
    private Scroller scroller = new Scroller(getContext(), new BounceInterpolator());
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private float lastScrollX;

    {
        paint.setStrokeWidth(3f);
        paint.setColor(Color.GRAY);
        paint.setTextSize(70f);
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

    private void init() {
        fix = getWidth() / spaceWidth * deltaValue + deltaValue;
        start = min * 10 - fix;
        end = max * 10 + fix;
        right = (max - min + 2) * 10 / deltaValue * spaceWidth;
        drawY = getHeight() / 2 - rulerHeight / 2;
        scrollBy(0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int drawValue = start + getScrollX() / spaceWidth * deltaValue - deltaValue * 2;
        int drawEnd = drawValue + getWidth() / spaceWidth * deltaValue + deltaValue * 3;

        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#D8D8D8"));
        canvas.drawRect(getScrollX(), drawY, getScrollX() + getWidth(), drawY + rulerHeight, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0f);
        paint.setColor(Color.GRAY);
        canvas.drawLine(getScrollX(), drawY, getScrollX() + getWidth(), drawY, paint);
        paint.setStrokeWidth(3f);

        while (drawValue <= drawEnd) {
            int x = (drawValue - start) / deltaValue * spaceWidth;
            if (drawValue % 10 == 0) {
                paint.setStrokeWidth(5f);
                canvas.drawLine(x, drawY, x, drawY + pointerLong, paint);
                paint.setStrokeWidth(0f);
                String str = String.valueOf(drawValue / 10);
                paint.getTextBounds(str, 0, str.length(), textRect);
                canvas.drawText(str, 0, str.length(), x - (textRect.right - textRect.left) / 2,
                        drawY + pointerLong + (textRect.bottom - textRect.top) * 3 / 2, paint);
                paint.setStrokeWidth(3f);
            } else {
                canvas.drawLine(x, drawY, x, drawY + pointerShort, paint);
            }
            drawValue += deltaValue;
        }

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10f);
        int lineX = getScrollX() + getWidth() / 2;
        canvas.drawLine(lineX, drawY, lineX, drawY + 10, paint);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(lineX, drawY + 10, lineX, drawY + pointerLong, paint);
        paint.setStrokeWidth(0f);
        paint.setColor(Color.BLUE);
        String str = String.valueOf(nowValue);
        paint.getTextBounds(str, 0, str.length(), textRect);
        canvas.drawText(str, 0, str.length(), lineX - (textRect.right - textRect.left) / 2,
                drawY - (textRect.bottom - textRect.top) * 3 / 2, paint);
//        Log.d(TAG, "ScaleView->onDraw: ");
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
        float computeValue = ((float) (getScrollX() + getWidth() / 2)) / spaceWidth *
                deltaValue + start;
        realValue = computeValue;
        nowValue = ((int) computeValue) / 10f;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                lastX = event.getX();
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                getParent().requestDisallowInterceptTouchEvent(true);
                if (lastX == -999) {
                    lastX = event.getX();
                    break;
                }
                int deltaX = -(int) (event.getX() - lastX);
                if (getScrollX() > 0 && getScrollX() < right) {
                    scrollFix(deltaX);
                } else if (getScrollX() <= 0) {
                    if (deltaX > 0) {
                        scrollFix(deltaX);
                    }
                } else if (getScrollX() >= right) {
                    if (deltaX < 0) {
                        scrollFix(deltaX);
                    }
                }
                lastX = event.getX();
//                Log.d(TAG, "ScaleView->onTouchEvent: " + getScrollX() + ",right = " + right);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                lastX = -999;
                move2Pointers();
                velocityTracker.computeCurrentVelocity(1000);
                scroller.fling(0, 0, -(int) velocityTracker.getXVelocity(), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
                lastScrollX = 0;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            if (scroller.getCurrX() == scroller.getFinalX()) {
                move2Pointers();
            } else {
//                Log.d(TAG, "RulerView->computeScroll: " + scroller.getCurrX() + ",FinalX : " + scroller.getFinalX());
                scrollFix(scroller.getCurrX() - lastScrollX);
                lastScrollX = scroller.getCurrX();
                postInvalidate();
            }
        }
    }

    private void move2Pointers() {
        int j = ((int) (realValue * 10)) % 10;
        int tNowValue = (int) (nowValue * 10);
        if (j >= 5) {
            tNowValue++;
            nowValue = tNowValue / 10;
            invalidate();
        }
        scrollFix((tNowValue - realValue) / deltaValue * spaceWidth);
    }

    private void scrollFix(float delta) {
        int scrolled = this.getScrollX();
        if (delta + scrolled < 0) {
            this.scrollBy(-scrolled, 0);
        } else if (delta + scrolled > right) {
            this.scrollBy(right - scrolled, 0);
        } else {
            this.scrollBy((int) delta, 0);
        }
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        init();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        init();
    }

    public int getSpaceWidth() {
        return spaceWidth;
    }

    public void setSpaceWidth(int spaceWidth) {
        this.spaceWidth = spaceWidth;
        init();
    }

    public int getPointerLong() {
        return pointerLong;
    }

    public void setPointerLong(int pointerLong) {
        this.pointerLong = pointerLong;
    }

    public int getPointerShort() {
        return pointerShort;
    }

    public void setPointerShort(int pointerShort) {
        this.pointerShort = pointerShort;
    }

    public int getDeltaValue() {
        return deltaValue;
    }

    public void setDeltaValue(int deltaValue) {
        this.deltaValue = deltaValue;
        init();
    }

    public int getRulerHeight() {
        return rulerHeight;
    }

    public void setRulerHeight(int rulerHeight) {
        this.rulerHeight = rulerHeight;
    }
}
