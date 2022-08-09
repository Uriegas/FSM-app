package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;

public class TempArrow extends FinalArrow {
    public TempArrow(int id, State from_node, int to_x, int to_y) {
        super(id, from_node, to_x, to_y);
        this.textX = (float)(from_node.x+to_x)/2; this.textY = (float)(from_node.y+to_y);
    }

    /**
     * Get the middle point between the node and the touched point, then get the closest points for
     * the node, finally graph this as the start of the arrow along with the touched point
     * @param canvas canvas to draw in
     */
    @Override
    public void draw(Canvas canvas) {
        State from = nodes.get(0);
        // Get middle point between the node and the touched point
        int midX = (from.x + this.x)/2;
        int midY = (from.y + this.y)/2;
        // Get the closest point to the node
        State tmp = from.getClosestPoint(midX, midY);
        int x_1 = tmp.x, y_1 = tmp.y; // Dummy assign
        draw(canvas, x_1, y_1, this.x, this.y);
    }

    @Override
    public Tuple onDown(int touchX, int touchY) {
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
