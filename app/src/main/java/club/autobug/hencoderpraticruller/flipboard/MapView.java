package club.autobug.hencoderpraticruller.flipboard;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {

    private Bitmap bitmap;
    private float degreeX, rotateDrgree;
    private int w, h;
    private Camera camera;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "degreeX", 0, 360);

    {
        camera = new Camera();
        rotateDrgree = 120;
        degreeX = -30f;
        animator.setDuration(8000);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = w / 2;
        int centerY = h / 2;
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        int x = centerX - bitmapW / 2;
        int y = centerY - bitmapH / 2;

        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY);
        camera.rotateX(degreeX);
        canvas.rotate(rotateDrgree);
        camera.applyToCanvas(canvas);
        canvas.clipRect(-centerX, -centerY, centerX, 0);
        canvas.rotate(-rotateDrgree);
        canvas.translate(-centerX, -centerY);
        camera.restore();
//        canvas.rotate(-rotateDrgree, centerX, centerY);
        paint.setAlpha(155);
        canvas.drawBitmap(bitmap, x, y, paint);
        paint.setAlpha(255);
        canvas.restore();

        canvas.save();
        camera.save();
        canvas.translate(centerX, centerY);
        camera.rotateX(0);
        canvas.rotate(rotateDrgree);
        camera.applyToCanvas(canvas);
        canvas.clipRect(-centerX, 0, centerX, centerY);
        canvas.rotate(-rotateDrgree);
        canvas.translate(-centerX, -centerY);
        canvas.drawBitmap(bitmap, x, y, paint);
        camera.restore();
        canvas.restore();
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmap != null) {
            this.bitmap.recycle();
        }
        this.bitmap = bitmap;
    }

    public void setDegreeX(float degreeX) {
        this.rotateDrgree = degreeX;
        invalidate();
    }

}
