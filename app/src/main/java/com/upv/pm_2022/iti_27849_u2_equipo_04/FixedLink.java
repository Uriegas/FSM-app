package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;

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

    // TODO: Handle onDown when curved line
    @Override
    public Tuple onDown(int touchX, int touchY) {
        Node from = nodes.get(0); Node to = nodes.get(1);
        return onDown(touchX, touchY, from.x, from.y, to.x, to.y);
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
        this.parallel       = (dx * (x - node_1.x) + dy * (y - node_1.y)) / (distance * distance);
        this.perpendicular  = (dx * (y - node_1.y) - dy * (x - node_1.x)) / distance;
        // TODO: Add snap to straight line
//        if(this.parallel > 0 && this.parallel < 1 && Math.abs(this.perpendicular) < snapPadding) {
//            this.perpendicular = 0;
//        }
    }

    @Override
    public void setFlag() {
        Node from = nodes.get(0); Node to = nodes.get(1);
        nodes.set(0, to); nodes.set(1, from);
    }

    @Override
    public String toLatex(float resize_factor) {
        Node from = nodes.get(0); Node to = nodes.get(1);
        return toLatex(resize_factor, from.x, from.y, to.x, to.y);
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
