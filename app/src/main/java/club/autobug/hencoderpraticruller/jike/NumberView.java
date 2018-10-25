package club.autobug.hencoderpraticruller.jike;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class NumberView extends View {

    private int countNumber = 1239;
    protected ValueAnimator textAnimator;
    protected boolean hasChecked = false;
    protected String constantNum, oldChangedNum, newChangedNum;
    protected final int ALPHA_MAX = 255;
    protected final int TEXT_OFFSET_MAX = 100;
    protected int textOffset = 0;
    protected int textAlpha = 0;
    //    protected Rect textRect = new Rect();
    protected Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected float spacing = 0f;

    {
        textAnimator = new ValueAnimator();
        textAnimator.setInterpolator(new DecelerateInterpolator());
        constantNum = String.valueOf(countNumber);
        paint.setTextSize(80);
        paint.setColor(Color.GRAY);
        oldChangedNum = "";
        newChangedNum = "";
        paint.setLetterSpacing(0.05f);
    }

    public NumberView(Context context) {
        super(context);
        init();
    }

    public NumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NumberView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getCountNumber() {
        return countNumber;
    }

    public void setCountNumber(int countNumber) {
        this.countNumber = countNumber;
        constantNum = String.valueOf(countNumber);
    }

    private void init() {
        constantNum = String.valueOf(countNumber);
        setClickListener();
        textAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                textOffset = (int) (f * TEXT_OFFSET_MAX);
                textAlpha = (int) ((1f - f) * ALPHA_MAX);
                spacing = f;
                invalidate();
            }
        });
    }

    private void setClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasChecked) {
                    down();
                } else {
                    up();
                }
                hasChecked = !hasChecked;
                if (textAnimator.isRunning()) {
                    textAnimator.end();
                }
                textAnimator.start();
            }
        });
    }

    private void up() {
        computeChange(countNumber++);
        textAnimator.setFloatValues(1f, 0f);
    }

    private void down() {
        computeChange(countNumber--);
        textAnimator.setFloatValues(0f, 1f);
        String temp = oldChangedNum;
        oldChangedNum = newChangedNum;
        newChangedNum = temp;
    }

    private void computeChange(int oldValue) {
        String oldStr = String.valueOf(oldValue);
        String newStr = String.valueOf(countNumber);
        if (oldStr.length() != newStr.length()) {
            oldChangedNum = oldStr;
            newChangedNum = newStr;
            constantNum = "";
            return;
        }
        int index = 0;
        for (int i = 0; i < oldStr.length(); i++) {
            if (oldStr.charAt(i) != newStr.charAt(i)) {
                index = i;
                break;
            }
        }
        constantNum = newStr.substring(0, index);
        newChangedNum = newStr.substring(index);
        oldChangedNum = oldStr.substring(index);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

//        paint.getTextBounds(constantNum + oldChangedNum, 0, constantNum.length() + oldChangedNum.length(),
//                textRect);
        int x = centerX - (int) (paint.measureText(constantNum + oldChangedNum) / 2);

        paint.setAlpha(ALPHA_MAX);
        canvas.drawText(constantNum, 0, constantNum.length(), x, centerY, paint);

//        paint.getTextBounds(constantNum, 0, constantNum.length(), textRect);
        x += (paint.measureText(constantNum) - paint.getLetterSpacing());

        paint.setAlpha(ALPHA_MAX - textAlpha);
        paint.setLetterSpacing(-0.1f + 0.15f * spacing);
        canvas.drawText(oldChangedNum, 0, oldChangedNum.length(), x, centerY - (TEXT_OFFSET_MAX - textOffset), paint);

        paint.setAlpha(textAlpha);
        paint.setLetterSpacing(0.05f + 0.15f * spacing);
        canvas.drawText(newChangedNum, 0, newChangedNum.length(), x, centerY + textOffset, paint);
        paint.setLetterSpacing(0.05f);

    }
}
