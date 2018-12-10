package com.henallux.bepway.features.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

public class SurfaceMap extends View {

    private Paint paint = new Paint();
    private ArrayList<PointF> points = new ArrayList<>();
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public SurfaceMap(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public SurfaceMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfaceMap(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SurfaceMap(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);

        SurfaceMap.this.setBackgroundColor(Color.BLACK);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(20);
        Log.i("DRAWdezd", "array size : " + points.size());
        for(PointF point : points){
            canvas.drawPoint(point.x,point.y,paint);
            Log.i("DRAWdezd", "drawn point : " + point.x + " ; " + point.y);
        }
        //canvas.drawPoint(50,50,paint);

        canvas.restore();
    }

    public void draw(){
        invalidate();
        requestLayout();
    }

    public void setPoints(ArrayList<PointF> points){
        this.points = (ArrayList<PointF>)points.clone();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }

}


