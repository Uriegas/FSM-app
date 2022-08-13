package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;

import java.util.HashMap;

public class FixedLink extends Link {
    public FixedLink(int id, Node from, Node to) {
        super(id, from, to);
        this.textX = (float)(from.x+to.x)/2; this.textY = (float)(from.y+to.y);
    }

    /**
     * Get the data necessary to draw the line
     * <p>
     *     For straight lines:
     * <p>
     * Get the middle point between the 2 nodes, then get the closest points for the 2 circles,
     * finally graph this 2 as the start and end of the arrow
     * <p>
     *     For curved lines:
     * <p>
     * Get anchor point (Node), then create a circle from the 3 points (node_1, node_2, anchor),
     * set the starting and ending points to draw the line
     * @param canvas canvas to draw in
     */
    @Override
    public void draw(Canvas canvas) {
        Node from = nodes.get(0); Node to = nodes.get(1);
        int x_1, y_1, x_2, y_2;
        // TODO: This should be in getLinkInfo
        if(this.perpendicular == 0) { // Get data for straight line
            // Get middle point between the 2 nodes
            int midX = (from.x + to.x)/2;
            int midY = (from.y + to.y)/2;
            // Get the 2 nodes representing the closest points
            Node tmp_1 = from.getClosestPoint(midX, midY);
            Node tmp_2 = to.getClosestPoint(midX, midY);
            x_1 = tmp_1.x; y_1 = tmp_1.y; x_2 = tmp_2.x; y_2 = tmp_2.y; // Dummy assign
            draw(canvas, x_1, y_1, x_2, y_2);
        } else { // Get data for curved line
            Node anchor = getAnchorPoint();
            Node circle = circleFromThreePoints(from.x, from.y, to.x, to.y, anchor.x, anchor.y);
            boolean isReversed = (this.perpendicular > 0);
            int reverseScale = isReversed ? 1 : -1;
            float startAngle = (float) Math.atan2(from.y - circle.y, from.x - circle.x)
                                - reverseScale * from.r / circle.r;
            float endAngle   = (float) Math.atan2(to.y - circle.y, to.x - circle.x)
                                + reverseScale * to.r / circle.r;
            x_1 = (int) (circle.x + circle.r * Math.cos(startAngle));
            y_1 = (int) (circle.y + circle.r * Math.sin(startAngle));
            x_2 = (int) (circle.x + circle.r * Math.cos(endAngle));
            y_2 = (int) (circle.y + circle.r * Math.sin(endAngle));
            draw(canvas, x_1, y_1, x_2, y_2, circle, startAngle, endAngle, reverseScale, isReversed);
        }
    }

    /**
     * Get information of the current link
     * @return
     */
    private HashMap<String, Object> getLinkInfo() {
        Node from = nodes.get(0); Node to = nodes.get(1);
        int x_1, y_1, x_2, y_2; // TODO: Convert to double and test accuracy
        HashMap map = new HashMap<String, Object>();
        if(this.perpendicular == 0) {
            return null;
        } else {
            Node anchor = getAnchorPoint();
            Node circle = circleFromThreePoints(from.x, from.y, to.x, to.y, anchor.x, anchor.y);
            boolean isReversed = (this.perpendicular > 0);
            int reverseScale = isReversed ? 1 : -1;
            double startAngle = Math.atan2(from.y - circle.y, from.x - circle.x)
                    - reverseScale * from.r / circle.r;
            double endAngle   = Math.atan2(to.y - circle.y, to.x - circle.x)
                    + reverseScale * to.r / circle.r;
            x_1 = (int) (circle.x + circle.r * Math.cos(startAngle));
            y_1 = (int) (circle.y + circle.r * Math.sin(startAngle));
            x_2 = (int) (circle.x + circle.r * Math.cos(endAngle));
            y_2 = (int) (circle.y + circle.r * Math.sin(endAngle));

            // Add stuff to HashMap
            map.put("circle", circle);
            map.put("startAngle", startAngle);
            map.put("endAngle", endAngle);
            map.put("reverseScale", reverseScale);
            map.put("isReversed", isReversed);
            return map;
        }
    }

    @Override
    public Tuple onDown(int touchX, int touchY) {
        Node from = nodes.get(0); Node to = nodes.get(1);

        if(this.perpendicular == 0) { // It is a straight line
            return onDown(touchX, touchY, from.x, from.y, to.x, to.y);
        } else { // It is a curved line
            // Get necessary info
            HashMap map = getLinkInfo();
            Node circle = (Node) map.get("circle");
            double startAngle = (Double) map.get("startAngle");
            double endAngle   = (Double) map.get("endAngle");
            int reverseScale  = (Integer)map.get("reverseScale");
            boolean isReversed= (Boolean)map.get("isReversed");

            // Compute distance between clicked point and valid curved line of circle
            double dx = touchX - circle.x;
            double dy = touchY - circle.y;
            double distance = Math.sqrt(dx*dx + dy*dy) - circle.r;
            if(Math.abs(distance) < 24) { // This number changes the sensibility to the onTouch
                double angle = Math.atan2(dy, dx);
                if(isReversed) {
                    double tmp = startAngle;
                    startAngle = endAngle;
                    endAngle   = tmp;
                }
                if(endAngle < startAngle) endAngle += Math.PI * 2;
                if(angle < startAngle)    angle    += Math.PI * 2;
                else if(angle > endAngle) angle    -= Math.PI * 2;

                if (angle > startAngle && angle < endAngle) return new Tuple(this.id);
            }
            return new Tuple(-1);
        }
    }

    /**
     * Every time the link is moved the corresponding parameters of a curved line get updated
     * @param x touched point in the x-axis
     * @param y touched point in the y-axis
     */
    @Override
    public void onMove(int x, int y) { // Update perpendicular and parallel parameters
        Node node_1 = nodes.get(0); Node node_2 = nodes.get(1);
        double dx = node_2.x - node_1.x;
        double dy = node_2.y - node_1.y;
        double distance = Math.sqrt(dx*dx + dy*dy);
        // Update parameters
        this.parallel       = (dx * ((double)x - node_1.x) + dy * ((double)y - node_1.y))
                              / (distance * distance);
        this.perpendicular  = (dx * ((double)y - node_1.y) - dy * ((double)x - node_1.x))
                              / distance;
        // Snap to straight line
        if(this.parallel > 0 && this.parallel < 1 && Math.abs(this.perpendicular) < Figure.snapPad){
            this.lineAdjust = (this.perpendicular < 0 ? 1 : 0) * Math.PI; this.perpendicular = 0;
        }
    }

    @Override
    public void setFlag() {
        Node from = nodes.get(0); Node to = nodes.get(1);
        nodes.set(0, to); nodes.set(1, from);
    }

    @Override
    public String toLatex(float resize_factor) {
        Node from = nodes.get(0); Node to = nodes.get(1);
        if(this.perpendicular == 0) {
            // Get middle point between the 2 nodes
            int midX = (from.x + to.x)/2;
            int midY = (from.y + to.y)/2;
            // Get the 2 nodes representing the closest points
            Node tmp_1 = from.getClosestPoint(midX, midY);
            Node tmp_2 = to.getClosestPoint(midX, midY);
            return toLatex(resize_factor, tmp_1.x, tmp_1.y, tmp_2.x, tmp_2.y);
        } else {
            Node anchor = getAnchorPoint();
            Node circle = circleFromThreePoints(from.x, from.y, to.x, to.y, anchor.x, anchor.y);
            boolean isReversed = (this.perpendicular > 0);
            int reverseScale = isReversed ? 1 : -1;
            float startAngle = (float) Math.atan2(from.y - circle.y, from.x - circle.x)
                    - reverseScale * from.r / circle.r;
            float endAngle   = (float) Math.atan2(to.y - circle.y, to.x - circle.x)
                    + reverseScale * to.r / circle.r;
            return toLatexArc(resize_factor, circle, startAngle, endAngle, isReversed, false);
        }
    }

    private Node getAnchorPoint() {
        Node node_1 = nodes.get(0); Node node_2 = nodes.get(1);
        int dx = node_2.x - node_1.x;
        int dy = node_2.y - node_1.y;
        float scale = (float) (this.perpendicular / Math.sqrt(dx*dx + dy*dy));
        return new Node(Integer.MAX_VALUE, (int) (node_1.x + dx * this.parallel - dy * scale),
                                           (int) (node_1.y + dy * this.parallel + dx * scale));
    }
}
