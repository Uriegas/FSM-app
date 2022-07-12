package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

public class Arrow extends Figure {
    // TODO: Arched arrow
    public int endX;
    public int endY;
    private final Paint paint = new Paint();
    private final Paint p_fill= new Paint();

    public Arrow(int id, int x, int y) {
        this.id=id; this.x=x; this.y=y; this.flag = false; // Flag used to change direction of arrow
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        p_fill.setStyle(Paint.Style.FILL);
        p_fill.setColor(Color.BLACK);
        p_fill.setStrokeWidth(2.5f);
        setFlag();
    }

    public void draw(Canvas canvas){
        //canvas.drawArc(this.x, this.y,this.x+200,this.y+200,(float)100,(float)10,false,paint);
        Path path = new Path();
        path.moveTo(this.endX, this.endY);
        path.lineTo(this.x, this.y);
        canvas.drawPath(path, paint);
        // Draw Head of the Arrow (triangle) - Get the angle
        drawArrowHead(canvas, this.endX, this.endY, Math.atan2(this.endY-this.y, this.endX-this.x));
    }

    public int onDown(int touchX, int touchY){ onMove(touchX, touchY); return this.id; }

    public void onMove(int touchX, int touchY){ this.endX = touchX; this.endY = touchY; }

    /**
     * Changes the direction of the arrow by swapping the start and end coordinates
     */
    @Override
    public void setFlag() {
        this.flag = !this.flag;
        // O(0) memory usage swap
        this.x = this.x + this.endX; this.endX = this.x - this.endX; this.x = this.x - this.endX;
        this.y = this.y + this.endY; this.endY = this.y - this.endY; this.y = this.y - this.endY;
    }

    /**
     * Draws a triangle pointing to the direction of the drawn arrow
     * @param canvas canvast draw in
     * @param x coordinate of the last point of the arrow
     * @param y coordinate of the last point of the arrow
     * @param alpha angle from the cartesian plane to the given coordinate (x,y)
     */
    private void drawArrowHead(Canvas canvas, int x, int y, double alpha) {
        double dx = Math.cos(alpha);
        double dy = Math.sin(alpha);

        Path path = new Path();
        path.moveTo(x,y);
        path.lineTo((float)(x - 24 * dx + 15 * dy), (float)(y - 24 * dy - 15 * dx));
        path.lineTo((float)(x - 24 * dx - 15 * dy), (float)(y - 24 * dy + 15 * dx));
        canvas.drawPath(path, p_fill);
    }
}
