package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;

public class FixedArrow extends FinalArrow {
    public FixedArrow(int id, State from, State to) {
        super(id, from, to);
        this.textX = (float)(from.x+to.x)/2; this.textY = (float)(from.y+to.y);
    }

    /**
     * Get the middle point between the 2 nodes, then get the closest points for the 2 circles,
     * finally graph this 2 as the start and end of the arrow
     * @param canvas canvas to draw in
     */
    @Override
    public void draw(Canvas canvas) {
        State from = nodes.get(0); State to = nodes.get(1);
        // Get middle point between the 2 nodes
        int midX = (from.x + to.x)/2;
        int midY = (from.y + to.y)/2;
        // Get the 2 nodes representing the closest points
        State tmp_1 = from.getClosestPoint(midX, midY);
        State tmp_2 = to.getClosestPoint(midX, midY);
        int x_1 = tmp_1.x, y_1 = tmp_1.y, x_2 = tmp_2.x, y_2 = tmp_2.y; // Dummy assign
        draw(canvas, x_1, y_1, x_2, y_2);
    }

    @Override
    public Tuple onDown(int touchX, int touchY) {
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
