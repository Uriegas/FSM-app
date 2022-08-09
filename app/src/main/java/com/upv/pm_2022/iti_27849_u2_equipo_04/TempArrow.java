package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;

public class TempArrow extends FinalArrow {
    public TempArrow(int id, State from_node, int to_x, int to_y) {
        super(id, from_node, to_x, to_y);
        this.textX = (float)(from_node.x+to_x)/2; this.textY = (float)(from_node.y+to_y);
    }

    @Override
    public void draw(Canvas canvas) {
        State from = nodes.get(0);
        draw(canvas, from.x, from.y, this.x, this.y);
    }

    @Override
    public int onDown(int touchX, int touchY) {
        State from = nodes.get(0);
        return onDown(touchX, touchY, from.x, from.y, this.x, this.y);
    }

    @Override
    public void onMove(int touchX, int touchY){ this.x = touchX; this.y = touchY; }

    @Override
    public void setFlag() { } // Method not allowed for temporary arrows

    @Override
    public String toLatex(float resize_factor) {
        State from = nodes.get(0);
        return toLatex(resize_factor, from.x, from.y, this.x, this.y);
    }
}
