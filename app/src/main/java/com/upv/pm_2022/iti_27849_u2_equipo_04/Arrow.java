package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

public class Arrow extends Figure {
    // TODO: Set snap padding for curved lines -> straight lines
    public int endX;
    public int endY;
    private final int fontSize = 38;
    public boolean isLocked;
    private final Paint p_fill= new Paint();
    // TODO: Same tolerance for width of the arrow head (triangle)
    private final static int tolerance = 24;
    private float textX, textY;

    public Arrow(int id, int x, int y) {
        super(id, x, y, "a_"); this.flag = false; // Flag used to change direction of arrow
        this.endX = x; this.endY = y; this.isLocked = false; // Flag used to fix the arrow
        this.textX = (float)(this.x+this.endX)/2; textY = (float)(this.y+this.endY)/2;

        paint.setAntiAlias(true);
        // Arrow line
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

    public void draw(Canvas canvas){
        //canvas.drawArc(this.x, this.y,this.x+200,this.y+200,(float)100,(float)10,false,paint);
        Path path = new Path();
        path.moveTo(this.endX, this.endY);
        path.lineTo(this.x, this.y);
        canvas.drawPath(path, paint);
        // Draw name of the arrow (centered, not considering arch yet)
        drawName(canvas, this.x, this.y, this.endX, this.endY);
        // Draw Head of the Arrow (triangle) - Get the angle
        drawArrowHead(canvas, this.endX, this.endY, Math.atan2(this.endY-this.y, this.endX-this.x));
    }

    /**
     * Calculates the distance between the touched point E and line <b>segment</b> AB (this arrow)
     * <p>
     * This function uses dot products to take into account that a line segment has bounds
     * @param touchX touched point in x
     * @param touchY touched point in y
     * @return object id if the distance AB_E is less than the tolerance parameter, -1 otherwise
     */
    public Tuple onDown(int touchX, int touchY){
        // Find distance from point E to line segment (AB)
        double d; // Distance between point E (touchX, touchY) and line segment AB (this arrow)
        int a_x = this.x, a_y = this.y, b_x = this.endX, b_y = this.endY, e_x = touchX, e_y =touchY;

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

    public void onMove(int touchX, int touchY){ this.endX = touchX; this.endY = touchY; }

    /**
     * Changes the direction of the arrow by swapping the start and end coordinates
     */
    @Override
    public void setFlag() {
        this.flag = !this.flag;
        // O(0) memory usage swap
        this.x = this.x + this.endX; this.endX = this.x - this.endX; this.x = this.x - this.endX;
        this.y = this.y + this.endY; this.endY = this.y - this.endY; this.y = this.y - this.endY;
    }

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
    public String toLatex(float resize_factor) {
        float x = getX()*resize_factor, y = getY()*resize_factor, x_1, y_1, x_2, y_2;
        float endX = this.endX*resize_factor, endY = this.endY*resize_factor;
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
}
