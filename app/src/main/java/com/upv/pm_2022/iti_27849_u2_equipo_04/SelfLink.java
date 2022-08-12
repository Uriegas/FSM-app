package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;

public class SelfLink extends Link {
    private double angle;
    public SelfLink(int id, Node node) {
        super(id, node);
        this.textX = (float)(node.x); this.textY = (float)(node.y); // TODO: Check this logic
        this.perpendicular = 1; // NOTE: This has to be not 0 in order to super.draw() call to work
        this.angle = 0;
    }

    @Override
    public void draw(Canvas canvas) {
        Node node = nodes.get(0);

        //Get center of the circle to draw based on the current circle and draw angle
        double circleX = node.x + 1.5 * node.r * Math.cos(angle);
        double circleY = node.y + 1.5 * node.r * Math.sin(angle);
        double circleR = 0.75 * node.r;

        // Get part of the circle to draw, this depends of the draw angle
        double startAngle   = angle - Math.PI * 0.8;
        double endAngle     = angle + Math.PI * 0.8;

        // Get the end points, startX and startY are not needed in the draw() call
        double x_2 = circleX + circleR * Math.cos(endAngle);
        double y_2 = circleY + circleR * Math.sin(endAngle);

        Node circle = new Node(Integer.MAX_VALUE, (int) circleX, (int) circleY, (float) circleR);

        // Draw arched line, head of the arrow and name
        draw(canvas, 0, 0, x_2, y_2, circle, startAngle, endAngle, 0, false);
    }

    @Override
    public Tuple onDown(int touchX, int touchY) {
        Node node = nodes.get(0);
        return onDown(touchX, touchY, node.x, node.y, node.x, node.y);
    }

    @Override
    public void onMove(int touchX, int touchY){ } // TODO: Change where the self link is

    @Override
    public void setFlag() { } // TODO: How to change the direction of this?, is it necessary?

    @Override
    public String toLatex(float resize_factor) {
        Node from = nodes.get(0);
        return toLatex(resize_factor, from.x, from.y, this.x, this.y);
    }
}
