package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.HashMap;
import java.util.List;

/**
 * A state of the FSM. Fixed size
 */
public class State extends Figure {
    private String name;
    private boolean isFinal;
    private Paint paint = new Paint();
    private int r;
    private static final float ratio_percentage = (float)0.2;

    public State(int id, int x, int y, int r, String name, boolean isFinal) {
        this.id = id; this.x = x; this.y = y; this.r = r; this.name = name; this.isFinal = isFinal;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setTextSize(32);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public State(int id, int x, int y, int r, String name) { this(id, x, y, r, name, false); }

    public State(int id, int x, int y) { this(id, x, y, 80, ""); }

    public void draw(Canvas canvas) {
        canvas.drawCircle(this.x, this.y, this.r, paint);
        canvas.drawCircle(this.x, this.y, this.r-this.r*this.ratio_percentage, paint);
        canvas.drawText("S_"+this.id, this.x, this.y, paint);
    }

    /**
     * Check that click is inside the figure
     * @param touchX
     * @param touchY
     * @return if coordinates are inside circle return this id, otherwise return -1
     */
    public int onDown(int touchX, int touchY) {
        // Pythagorean theorem in circle: x^2 + y^2 = r^2, if touched is (x_1, y_1) then
        // the square of the distance from the center to the touched point should be less than r^2:
        // (x-x_1)^2 + (y-y_1)^2 < r^2
        if (((x-touchX) * (x-touchX)) + ((y-touchY) * (y-touchY)) < (r*r)) {
            return this.id;
        }
        return -1;
    }

    /**
     * Change the position of the figure
     * @param touchX
     * @param touchY
     */
    public void onMove(int touchX, int touchY) {
        this.x = touchX; this.y = touchY;
    }

    /**
     * Set the state of
     * @param canvas
     * @return
     */
    public void setFinal() {
        this.isFinal = !this.isFinal;
//        canvas.drawCircle(this.x, this.y, this.r-this.r*this.ratio_percentage, paint);
    }

    /**
     * Set the name of the state
     * @param canvas
     * @param name
     */
    public void setName(String name) {
        this.name = name;
//        canvas.drawText(name, this.x, this.y, this.paint);
    }
}
