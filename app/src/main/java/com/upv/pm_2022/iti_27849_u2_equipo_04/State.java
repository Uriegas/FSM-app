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
    private boolean state;
    private Paint paint = new Paint();
    private int r;

    public State(int id, int x, int y, int r, String name, boolean state) {
        this.id = id; this.x = x; this.y = y; this.r = r; this.name = name; this.state = state;
        paint.setAntiAlias(true);
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2.5f);
    }

    public State(int id, int x, int y, int r, String name) { this(id, x, y, r, name, false); }

    public State(int id, int x, int y) { this(id, x, y, 80, ""); }

    public void draw(Canvas canvas) { canvas.drawCircle(this.x, this.y, 80, paint); }

    /**
     * Check that click is inside the figure
     * @param touchX
     * @param touchY
     * @return if coordinates are inside circle return this id, otherwise return -1
     * TODO: Add logic of circle
     */
    public int onDown(int touchX, int touchY) {
        if(touchX > this.x)
            return this.id;
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
}
