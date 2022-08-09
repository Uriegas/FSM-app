package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;

public class SelfArrow extends FinalArrow {
    public SelfArrow(int id, State node) {
        super(id, node);
        this.textX = (float)(node.x); this.textY = (float)(node.y); // TODO: Check this logic
    }

    @Override
    public void draw(Canvas canvas) {
        State node = nodes.get(0);
        // TODO: Custom draw here or in abstract class for selfArrows
        draw(canvas, node.x, node.y, node.x, node.y);
    }

    @Override
    public int onDown(int touchX, int touchY) {
        State node = nodes.get(0);
        return onDown(touchX, touchY, node.x, node.y, node.x, node.y);
    }

    @Override
    public void onMove(int touchX, int touchY){ } // TODO: Change where the self link is

    @Override
    public void setFlag() { } // TODO: How to change the direction of this?, is it necessary?

    @Override
    public String toLatex(float resize_factor) {
        State from = nodes.get(0);
        return toLatex(resize_factor, from.x, from.y, this.x, this.y);
    }
}
