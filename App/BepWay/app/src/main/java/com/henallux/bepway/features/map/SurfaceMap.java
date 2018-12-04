package com.henallux.bepway.features.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class SurfaceMap extends View {

    private Paint paint = new Paint();
    private ArrayList<PointF> points;

    public SurfaceMap(Context context) {
        super(context);
        points = new ArrayList<>();
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
    protected void onDraw(Canvas canvas) {
        SurfaceMap.this.setBackgroundColor(Color.BLACK);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(20);
        for(PointF point : points){
            canvas.drawPoint(point.x, point.y, paint);
        }
        super.onDraw(canvas);
    }

    public void draw(ArrayList<PointF> points){
        this.points = points;
        invalidate();
        requestLayout();
    }

    public void addPoint(PointF point){
        points.add(point);
    }
}
