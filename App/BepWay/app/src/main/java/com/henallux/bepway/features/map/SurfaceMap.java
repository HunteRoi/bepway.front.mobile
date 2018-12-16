package com.henallux.bepway.features.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.henallux.bepway.model.Coordinate;

import java.util.ArrayList;

public class SurfaceMap extends View {

    private Paint paint = new Paint();
    private ArrayList<Coordinate> points = new ArrayList<>();
    private ArrayList<Coordinate> convertedPoints = new ArrayList<>();
    private Coordinate center = new Coordinate(1,1);

    public SurfaceMap(Context context) {
        super(context);
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
        super.onDraw(canvas);

        convertCoordIntoXY(canvas.getWidth(), canvas.getHeight());

        canvas.save();
        //canvas.translate(((float)canvas.getWidth()/2)-1000,(float)canvas.getHeight()/2);
        //canvas.scale(30f,30f);

        SurfaceMap.this.setBackgroundColor(Color.BLACK);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(20);
        Log.i("DRAWdezd", "width : " + canvas.getWidth() + " - " + "Height : " + canvas.getHeight());
        for(Coordinate coord : convertedPoints){
            canvas.drawPoint((float)coord.getX(),(float)coord.getY(),paint);
        }
        //canvas.drawPoint(50,50,paint);

        canvas.restore();
    }

    public void draw(){
        invalidate();
        requestLayout();
    }

    private void convertCoordIntoXY(int width, int height){
        for(Coordinate coordinate : points){
            double x = (coordinate.getX() + 180) * (width/360);
            double latRad = coordinate.getY()*Math.PI/180;
            double mercN = Math.log(Math.tan(Math.PI)+(latRad/2));
            double y = (height/2)-(width*mercN/(2*Math.PI));
            convertedPoints.add(new Coordinate(x,y));
            Log.i("DRAWdezd", x + " - " + y);
        }
        if(center != null){
            double x = (center.getX() + 180) * (width/360);
            double latRad = center.getY()*Math.PI/180;
            double mercN = Math.log(Math.tan(Math.PI)+(latRad/2));
            double y = (height/2)-(width*mercN/(2*Math.PI));
            Log.i("DRAWdezd", "Center : " + x + " - " + y);
        }
    }

    public void setPoints(ArrayList<Coordinate> points, Coordinate center){
        this.points = (ArrayList<Coordinate>)points.clone();
        this.center = center;
    }


}


