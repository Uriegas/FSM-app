package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;

public class FixedArrow extends FinalArrow {
    public FixedArrow(int id, State from, State to) {
        super(id, from, to);
        this.textX = (float)(from.x+to.x)/2; this.textY = (float)(from.y+to.y);
    }

    @Override
    public void draw(Canvas canvas) {
        State from = nodes.get(0); State to = nodes.get(1);
        draw(canvas, from.x, from.y, to.x, to.y);
    }

    @Override
    public int onDown(int touchX, int touchY) {
        State from = nodes.get(0); State to = nodes.get(1);
        return onDown(touchX, touchY, from.x, from.y, to.x, to.y);
    }

    @Override
    public void onMove(int touchX, int touchY) { nodes.get(1).x = touchX; nodes.get(1).y = touchY; }

    @Override
    public void setFlag() {
        State from = nodes.get(0); State to = nodes.get(1);
        nodes.set(0, to); nodes.set(1, from);
    }

    @Override
    public String toLatex(float resize_factor) {
        State from = nodes.get(0); State to = nodes.get(1);
        return toLatex(resize_factor, from.x, from.y, to.x, to.y);
    }
}
