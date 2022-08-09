package com.upv.pm_2022.iti_27849_u2_equipo_04;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

import java.util.ArrayList;


/**
 * Every type of arrow is associated to at least one node (where the arrow originates)
 * Every class that extends this class should set textX and textY
 */
public abstract class Link extends Figure {
    // TODO: Set snap padding for curved lines -> straight lines
    protected final int fontSize = 38;
    private final Paint p_fill= new Paint();
    // TODO: Same tolerance for width of the arrow head (triangle)
    private final static int tolerance = 24;
    protected float textX, textY;
    protected ArrayList<Node> nodes; // TODO: Assert nodes.length <= 2 == True

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
        super(id,x,y,"a_");nodes=new ArrayList<>();this.flag=false; // Flag for arrow direction
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
     * Abstract method to draw a line
     * TODO: This function only draws straight lines; support curved lines
     * @param canvas canvas to draw in
     * @param x_1 start point in x
     * @param y_1 start point in y
     * @param x_2 end point in x
     * @param y_2 end point in y
     */
    protected void draw(Canvas canvas, int x_1, int y_1, int x_2, int y_2){
        //canvas.drawArc(this.x, this.y,this.x+200,this.y+200,(float)100,(float)10,false,paint);
        // Draw arrow
        Path path = new Path();

        // Arched line
//        RectF oval = new RectF(x_1, y_1, x_2, y_2);
//        path.arcTo(oval, 45, 128);

        // Straight line
        path.moveTo(x_1, y_1); // No needed since arcTo execs moveTo when path is empty
        path.lineTo(x_2, y_2);

        canvas.drawPath(path, paint);

        // Draw name of the arrow (centered, not considering arch yet)
        drawName(canvas, x_1, y_1, x_2, y_2);
        // Draw Head of the Arrow (triangle) - Get the angle
        drawArrowHead(canvas, x_2, y_2, Math.atan2(y_2-y_1, x_2-x_1));
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
    protected void drawArrowHead(Canvas canvas, int x, int y, double alpha) {
        double dx = Math.cos(alpha);
        double dy = Math.sin(alpha);

        Path path = new Path();
        path.moveTo(x,y);
        path.lineTo((float)(x - 24 * dx + 15 * dy), (float)(y - 24 * dy - 15 * dx));
        path.lineTo((float)(x - 24 * dx - 15 * dy), (float)(y - 24 * dy + 15 * dx));
        canvas.drawPath(path, p_fill);
    }

    /**
     * Intelligently draw the name of the arrow in an specific position
     * @param canvas
     * @param x_1 x coordinate of the first point
     * @param y_1 y coordinate of the first point
     * @param x_2 x coordinate of the second point
     * @param y_2 y coordinate of the second point
     */
    protected void drawName(Canvas canvas, int x_1, int y_1, int x_2, int y_2) {
        // TODO: Fine tune this
        float width = this.name.length()*fontSize;
        textX = (float)(x_1+x_2)/2; textY = (float)(y_1+y_2)/2;
        double textAngle = Math.atan2(x_2-x_1, y_1-y_2);
        double cos = Math.cos(textAngle), sin = Math.sin(textAngle);
        double cornerX = (width/2 + 5)*(cos > 0 ? 1 : -1), cornerY = (fontSize)*(sin > 0 ? 1 : -1);
        double slide = sin * Math.pow(Math.abs(sin), 40) * cornerX - cos *
                Math.pow(Math.abs(cos), 15) * cornerY;
        textX += cornerX - sin * slide;
        textY += cornerY + cos * slide;
        canvas.drawText(name, textX, textY, paint);
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

        float x_1, y_1, x_2, y_2;
        float textX = this.textX*resize_factor, textY = this.textY*resize_factor;
        double alpha = Math.atan2(endY-y, endX-x);
        double dx = Math.cos(alpha);
        double dy = Math.sin(alpha);
        x_1 = (float)(endX - 24 * dx * resize_factor + 15 * dy * resize_factor);
        y_1 = (float)(endY - 24 * dy * resize_factor - 15 * dx * resize_factor);
        x_2 = (float)(endX - 24 * dx * resize_factor - 15 * dy * resize_factor);
        y_2 = (float)(endY - 24 * dy * resize_factor + 15 * dx * resize_factor);

        String latex_output = "";
        // Draw arrow
        latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + x + ", -" + y + ") --" +
                " (" + endX + ", -" + endY + ");\n";
        // Draw arrow head (triangle)
        latex_output += FILL_COMMAND + ' ' + COLOR + " (" + endX + ", -" + endY + ") -- (" +
                x_1 + ", -" + y_1 + ") -- (" + x_2 + ", -" + y_2 + ");\n";
        if(!this.name.isEmpty()) // Draw name
            latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + textX + ", -" + textY + ") " +
                    "node" + " {$" + this.name + "$};\n";
        return latex_output;
    }

    /**
     * Creates a circle from 3 points, using matrices is faster than the traditional
     * circumscribed circle in a polygon.
     * @see <a href="https://web.archive.org/web/20161011113446/http://www.abecedarical.com/zenosamples/zs_circle3pts.html">
     *     Center and Radius of a Circle from Three Points
     * </a>
     * @param x_1 first point, x coordinate
     * @param y_1 first point, y coordinate
     * @param x_2 second point, x coordinate
     * @param y_2 second point, y coordinate
     * @param x_3 third point, x coordinate
     * @param y_3 third point, y coordinate
     * @return a circle from the three points
     */
    private Node circleFromThreePoints(int x_1, int y_1, int x_2, int y_2, int x_3, int y_3) {
        float a  = det(x_1, y_1, 1, x_2, y_2, 1, x_3, y_3, 1);
        float bx = -det(x_1*x_1 + y_1*y_1, y_1, 1, x_2*x_2 + y_2*y_2, y_2, 1,
                        x_3*x_3 + y_3*y_3, y_3, 1);
        float by =  det(x_1*x_1 + y_1*y_1, x_1, 1, x_2*x_2 + y_2*y_2, x_2, 1,
                        x_3*x_3 + y_3*y_3, x_3, 1);
        float c  = -det(x_1*x_1 + y_1*y_1, x_1, y_1, x_2*x_2 + y_2*y_2, x_2, y_2,
                        x_3*x_3 + y_3*y_3, x_3, y_3);
        return new Node(Integer.MAX_VALUE,  (int) (-bx / (2*a)), (int) (-by / (2*a)),
                         (float) (Math.sqrt(bx*bx + by*by - 4*a*c) / (2*Math.abs(a))));
    }

    /**
     * Get the determinant for the given matrix
     * @return the determinant
     */
    private float det(int a, int b, int c, int d, int e, int f, int g, int h, int i) {
        return a*e*i + b*f*g + c*d*h - a*f*h - b*d*i - c*e*g;
    }
}
