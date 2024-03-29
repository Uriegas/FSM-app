package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;

import java.util.ArrayList;

/**
 * Every type of arrow is associated to at least one node (where the arrow originates)
 * Every class that extends this class should set textX and textY
 */
public abstract class Link extends Figure {
    protected final int fontSize = 38;
    private final Paint p_fill= new Paint();
    // TODO: Same tolerance for width of the arrow head (triangle)
    private final static int tolerance = 24;
    protected float textX, textY;
    protected ArrayList<Node> nodes; // TEST: Assert nodes.length <= 2 == True

    // Parallel = percentage of line node_1 to node_2; Perpendicular = pixels from line
    // LineAdjust is a flag to adjust the angle of the text to be drawn
    protected double perpendicular = 0, parallel = 0.5, lineAdjust = 0;

    protected Link(int id, Node from, Node to) { // Constructor for fixed arrow
        this(id, 0, 0); nodes.add(from); nodes.add(to);
    }
    protected Link(int id, Node from, int x, int y) { // Constructor for temp arrow
        this(id, x, y); nodes.add(from);
    }
    protected Link(int id, Node node) { // Constructor for self links
        this(id, 0, 0); nodes.add(node);
    }

    public Link(int id, int x, int y) {
        super(id,x,y,"a_");nodes=new ArrayList<>();this.flag=false;// Flag for arrow direction
        // Arrow line
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        // Arrow head
        p_fill.setStyle(Paint.Style.FILL);
        p_fill.setColor(Color.BLACK);
        p_fill.setStrokeWidth(2.5f);
        // Arrow name
        paint.setTextSize(fontSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.SANS_SERIF);
    }

    /**
     * Draw method to draw straight lines only.
     * <p>
     * This method suppose that perpendicular = 0, if false this will fail
     * @param canvas canvas to draw in
     * @param x_1 start point in x
     * @param y_1 start point in y
     * @param x_2 end point in x
     * @param y_2 end point in y
     */
    protected void draw(Canvas canvas, int x_1, int y_1, int x_2, int y_2){
        this.perpendicular = 0; // Just in case this var is not set to 0, so this wont fail
        draw(canvas, x_1, y_1, x_2, y_2, null, 0f, 0f, 0, false);
    }

    /**
     * Draw method for curved lines and straight lines
     * @param canvas canvas to draw in
     * @param x_1 start of the figure to create the curve in x
     * @param y_1 start of the figure to create the curve in y
     * @param x_2 end of the figure to create the curve in x
     * @param y_2 end of the figure to create the curve in y
     * @param startAngle start angle for drawing the curve
     * @param endAngle end angle for drawing the curve
     * @param isReversed if the line is to be reversed in direction
     */
    protected void draw(Canvas canvas, double x_1, double y_1, double x_2, double y_2, Node circle,
                        double startAngle, double endAngle, double reverseScale,boolean isReversed){
        // Draw arrow
        Path path = new Path();
        if (this.perpendicular == 0) { // Draw straight line
            // Draw line
            path.moveTo((float) x_1, (float) y_1);
            path.lineTo((float) x_2, (float) y_2);
            drawArrowHead(canvas, x_2, y_2, Math.atan2(y_2-y_1, x_2-x_1));
            // Draw name
            this.textX = (float)(x_1+x_2)/2; this.textY = (float)(y_1+y_2)/2;
            double textAngle = Math.atan2(x_2-x_1, y_1-y_2);
            drawName(canvas, this.textX, this.textY, textAngle + this.lineAdjust);
        } else { // Draw arched line
            if(x_1 == 0 && y_1 == 0 ) { // Draw arrow head for self link, this would fail if the
                                        // user puts a node in 0,0 which is unlikely but possible
                drawArrowHead(canvas, x_2, y_2, endAngle + Math.PI * 0.4);
            } else { // Draw arrow head for an arched link
                drawArrowHead(canvas, x_2, y_2, endAngle - reverseScale * (Math.PI/2));
                // Draw name
                if(endAngle < startAngle) endAngle += Math.PI * 2;
                double textAngle = (startAngle + endAngle) / 2 + (isReversed ? 1 : 0) * Math.PI;
                double tX = circle.x + circle.r * Math.cos(textAngle);
                double tY = circle.y + circle.r * Math.sin(textAngle);
                drawName(canvas, tX, tY, textAngle);
            }
            // Convert radian angles to degrees
            startAngle  = Math.toDegrees(startAngle);
            endAngle    = Math.toDegrees(endAngle);
            // Draw arch
            drawCurvedLine(path, circle.x, circle.y, circle.r, startAngle, endAngle, isReversed);
        }
        // Draw the whole thing
        canvas.drawPath(path, paint);
    }

    /**
     * Draw curved line given a circle and starting and ending angles
     * @param path path to draw in
     * @param x coordinate of the circle in x
     * @param y coordinate of the circle in x
     * @param r radius of the circle
     * @param startAngle angle to start drawing in
     * @param endAngle angle to draw line unto
     * @param isReversed Specifies whether the drawing should be counterclockwise or clockwise
     */
    protected void drawCurvedLine(Path path, int x, int y, float r, double startAngle,
                                double endAngle, boolean isReversed) {
        // Create the RectF based on circle info (x, y, r)
        RectF oval  = new RectF(x-r, y-r, x+r, y+r);
//        path.addRect(oval, Path.Direction.CW);
        endAngle    = endAngle - startAngle; // Get how many degrees to move to reach the endAngle
        // Apply the isReversed flag
        if(isReversed) endAngle = -(360 - endAngle);
        // Draw the arc
        path.addArc(oval, (float) startAngle, (float) endAngle);
    }


    /**
     * Calculates the distance between the touched point E and line <b>segment</b> AB (this arrow)
     * <p>
     * This function uses dot products to take into account that a line segment has bounds
     * @param e_x touched point in x
     * @param e_y touched point in y
     * @param a_x start point in x
     * @param a_y start point in y
     * @param b_x end point in x
     * @param b_y end point in y
     * @return object id if the distance AB_E is less than the tolerance parameter, -1 otherwise
     */
    protected Tuple onDown(int e_x, int e_y, int a_x, int a_y, int b_x, int b_y) {
        // Find distance from point E to line segment (AB)
        double d; // Distance between point E (touchX, touchY) and line segment AB (this arrow)

        // Vectors
        double ab_x, ab_y, be_x, be_y, ae_x, ae_y;
        ab_x = b_x - a_x; ab_y = b_y - a_y;
        be_x = e_x - b_x; be_y = e_y - b_y;
        ae_x = e_x - a_x; ae_y = e_y - a_y;

        // Dot products
        double ab_be = (ab_x * be_x + ab_y * be_y), ab_ae = (ab_x * ae_x + ab_y * ae_y);

        if(ab_be > 0 || ab_ae < 0) // Point E out of bounds
            d = tolerance+1;
        else // Point E inside bounds
            d = Math.abs((b_x-a_x) * (a_y-e_y) - (a_x-e_x) * (b_y-a_y))
                    /Math.sqrt(Math.pow(b_x-a_x, 2) + Math.pow(b_y-a_y, 2));

        if(d < tolerance || Double.isNaN(d)) // We also consider the case: E inside line segment AB
            return new Tuple(this.id);
        return new Tuple(-1);
    }

    /**
     * Changes the direction of the arrow; swap nodes
     */
    public abstract void setFlag();

    /**
     * Draws a triangle pointing to the direction of the drawn arrow
     * @param canvas canvas draw in
     * @param x coordinate of the last point of the arrow
     * @param y coordinate of the last point of the arrow
     * @param alpha angle from the cartesian plane to the given coordinate (x,y)
     */
    protected void drawArrowHead(Canvas canvas, double x, double y, double alpha) {
        double dx = Math.cos(alpha);
        double dy = Math.sin(alpha);

        Path path = new Path();
        path.moveTo((float) x, (float) y);
        path.lineTo((float)(x - 24 * dx + 15 * dy), (float)(y - 24 * dy - 15 * dx));
        path.lineTo((float)(x - 24 * dx - 15 * dy), (float)(y - 24 * dy + 15 * dx));
        canvas.drawPath(path, p_fill);
    }

    /**
     * Draws the name of the current link in the given position
     * @param canvas canvas to draw in
     * @param x x-position to draw in
     * @param y y-position to draw in
     * @param angle angle of the current link
     */
    private void drawName(Canvas canvas, double x, double y, double angle) {
        double width = this.name.length()*fontSize*0.5;
        x -= width/4;

        if(angle != 0) { // position text if an angle is given
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double cornerX = (width + 5) * (cos > 0 ? 1 : -1);
            double cornerY = 15 * (sin > 0 ? 1 : -1);
            double slide = sin * Math.pow(Math.abs(sin), 40) * cornerX
                         - cos * Math.pow(Math.abs(cos), 10) * cornerY;
            x += cornerX - sin * slide;
            y += cornerY + cos * slide;
        }
        this.textX = (float)x; this.textY = (float)y + 12;
        canvas.drawText(this.name, this.textX, this.textY, paint);
    }

    /**
     * Override isEdited method to include coloring of arrow head (triangle) when isEdited is set
     * @param isEdited true if the name of this object is being edited
     */
    @Override
    public void isEdited(boolean isEdited) {
        super.isEdited(isEdited);
        p_fill.setColor(isEdited ? Color.BLUE : Color.BLACK);
    }

    /**
     * Converts this arrow to its latex representation.
     * @param resize_factor factor to resize the coordinates
     * @return latex representation of this arrow in string format
     */
    protected String toLatex(float resize_factor, float x, float y, float endX, float endY) {
        x = x*resize_factor;
        y = y*resize_factor;
        endX = endX*resize_factor;
        endY = endY*resize_factor;

        float textX = this.textX*resize_factor, textY = this.textY*resize_factor;
        double alpha = Math.atan2(endY-y, endX-x);

        String latex_output = "";
        // Draw arrow
        latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + x + ", -" + y + ") --" +
                " (" + endX + ", -" + endY + ");\n";
        // Draw arrow head (triangle)
        latex_output += toLatexArrowHead(resize_factor, endX, -endY, alpha);
        // Draw arrow name
        if(!this.name.isEmpty())
            latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + textX + ", -" + textY + ") " +
                    "node" + " {$" + this.name + "$};\n";
        return latex_output;
    }

    /**
     * Generates Latex code of the current arched line
     * @param resize_factor refactor size to draw
     * @param circle circle to be drawn
     * @param startAngle starting angle to draw from
     * @param endAngle ending angle to draw to
     * @param isReversed whether the starting and ending angle should be flipped
     * @return
     */
    protected String toLatexArc(float resize_factor, Node circle, double startAngle,double endAngle,
                              boolean isReversed, boolean isSelfLink) {
        String latex_output = "";

        // Draw arched line
        float x = circle.x * resize_factor;
        float y = circle.y * resize_factor;
        float r= circle.r * resize_factor;
        double angle = isSelfLink ? endAngle : endAngle - (isReversed ? 1 : -1) * (Math.PI/2);

        if(isReversed) { // Apply the isReversed flag (swap angles)
            double tmp = startAngle; startAngle = endAngle; endAngle = tmp;
        }
        if(endAngle < startAngle) endAngle += Math.PI * 2;
        if(Math.min(startAngle, endAngle) < -2 * Math.PI) {
            startAngle  += 2 * Math.PI;
            endAngle    += 2 * Math.PI;
        } else if(Math.max(startAngle, endAngle) > 2 * Math.PI) {
            startAngle  -= 2 * Math.PI;
            endAngle    -= 2 * Math.PI;
        }
        startAngle  = -startAngle;
        endAngle    = -endAngle;

        x = (float) (x + r * Math.cos(startAngle));
        y = (float) (-y + r * Math.sin(startAngle));

        startAngle  = (startAngle * 180 / Math.PI);
        endAngle    = (endAngle * 180 / Math.PI);

        latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + x + ", " + y + ") " + "arc (" +
                        startAngle + ":" + endAngle + ":" + r + ");\n";

        // Draw arrow head
        latex_output += toLatexArrowHead(resize_factor, x, y, angle );
//        drawArrowHead(canvas, x_2, y_2, endAngle + Math.PI * 0.4);

        // Draw arrow name
        float textX = this.textX*resize_factor, textY = this.textY*resize_factor;
        if(!this.name.isEmpty())
            latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + textX + ", -" + textY + ") " +
                    "node" + " {$" + this.name + "$};\n";
        return latex_output;
    }


    private String toLatexArrowHead(float resize_factor, double x, double y, double alpha) {
        double dx = Math.cos(alpha);
        double dy = Math.sin(alpha);

        float x_1 = (float)(x - 24 * dx * resize_factor + 15 * dy * resize_factor);
        float y_1 = (float)(y + 24 * dy * resize_factor + 15 * dx * resize_factor);
        float x_2 = (float)(x - 24 * dx * resize_factor - 15 * dy * resize_factor);
        float y_2 = (float)(y + 24 * dy * resize_factor - 15 * dx * resize_factor);

        return  FILL_COMMAND + ' ' + COLOR + " (" + x + ", " + y + ") -- (" +
                x_1 + ", " + y_1 + ") -- (" + x_2 + ", " + y_2 + ");\n";
    }

    /**
     * Creates a circle from 3 points, using matrices is faster than the traditional
     * circumscribed circle in a polygon.
     * @see <a href="https://web.archive.org/web/20161011113446/http://www.abecedarical.com/zenosamples/zs_circle3pts.html">
     *     Center and Radius of a Circle from Three Points
     * </a>
     * @param x1 first point, x coordinate
     * @param y1 first point, y coordinate
     * @param x2 second point, x coordinate
     * @param y2 second point, y coordinate
     * @param x3 third point, x coordinate
     * @param y3 third point, y coordinate
     * @return a circle from the three points
     */
    protected Node circleFromThreePoints(int x1, int y1, int x2, int y2, int x3, int y3) {
        double a = det(x1, y1, 1, x2, y2, 1, x3, y3, 1);
        double bx = -det(x1*x1 + y1*y1, y1, 1, x2*x2 + y2*y2, y2, 1, x3*x3 + y3*y3,
                    y3, 1);
        double by = det(x1*x1 + y1*y1, x1, 1, x2*x2 + y2*y2, x2, 1, x3*x3 + y3*y3,
                    x3, 1);
        double c = -det(x1*x1 + y1*y1, x1, y1, x2*x2 + y2*y2, x2, y2, x3*x3 + y3*y3, x3,
                    y3);
        return new Node(Integer.MAX_VALUE, (int)(-bx / (2*a)), (int)(-by / (2*a)),
                (float)(Math.sqrt(bx*bx + by*by - 4*a*c) / (2*Math.abs(a))) );
    }

    /**
     * Get the determinant for the given matrix
     * @return the determinant
     */
    private double det(double a, double b, double c, double d, double e, double f, double g,
                       double h, double i) {
        return a*e*i + b*f*g + c*d*h - a*f*h - b*d*i - c*e*g;
    }
}
