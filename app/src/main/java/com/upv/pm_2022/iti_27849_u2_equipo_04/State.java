package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * A state of the FSM. Fixed size
 */
public class State extends Figure {
    public static final int r = 81;
    public static final float ratio_percentage  = (float)0.35;
    public static final float final_ratio       = (float)0.2;

    public State(int id, int x, int y, boolean isFinal) {
        super(id, x, y, "S_"); this.flag = isFinal;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setTextSize(38);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.SANS_SERIF);
    }

    public State(int id, int x, int y) { this(id, x, y, false); }

    public void draw(Canvas canvas) {
        canvas.drawCircle(this.x, this.y, r, paint);
        if(this.flag)
            canvas.drawCircle(this.x, this.y, r-r*final_ratio, paint);
        canvas.drawText(name, this.x, this.y, paint);
    }

    /**
     * Check that click is inside the figure
     * @param touchX x touched
     * @param touchY y touched
     * @return if coordinates are inside circle return this id,
     *         -1 means no object selected,
     *         -2 means create a new line
     */
    public Tuple onDown(int touchX, int touchY) {
        // Pythagorean theorem in circle: x^2 + y^2 = r^2, if touched is (x_1, y_1) then
        // the square of the distance from the center to the touched point should be less than r^2:
        // (x-x_1)^2 + (y-y_1)^2 < r^2
        if (    ((x-touchX) * (x-touchX)) + ((y-touchY) * (y-touchY)) <
                (r*r*(1.1-ratio_percentage)*(1.1-ratio_percentage)) )
            return new Tuple(this.id);
        else if( (r*r*(1-ratio_percentage)*(1-ratio_percentage)) < //Clicked on edge of the circle
                 ((x-touchX) * (x-touchX)) + ((y-touchY) * (y-touchY)) &&
                 ((x-touchX) * (x-touchX)) + ((y-touchY) * (y-touchY)) <
                 (r*r*(1+ratio_percentage)*(1+ratio_percentage)) )
            return new Tuple(this.id, true); // <- This is why a tuple is used
        return new Tuple(-1);
    }

    /**
     * Check if the click is inside the circle; similar to {@link #onDown}
     * @param touchX x touched
     * @param touchY y touched
     * @return if coordinates are inside circle return this id, -1 otherwise
     */
    public int onCircle(int touchX, int touchY) {
        if ( ((x-touchX) * (x-touchX)) + ((y-touchY) * (y-touchY)) < (r*r))
            return this.id;
        return -1;
    }

    /**
     * Change the position of the figure
     * @param touchX x touched
     * @param touchY y touched
     */
    public void onMove(int touchX, int touchY) {
        this.x = touchX; this.y = touchY;
    }

    /**
     * Converts this state to its latex representation.
     * @param resize_factor factor to resize the coordinates
     * @return latex representation of this state in string format
     */
    public String toLatex(float resize_factor) {
        float x = getX()*resize_factor, y = getY()*resize_factor;
        String latex_output = "";
        // Draw circle
        latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + x + ", -" + y + ") circle (" +
                        (State.r/CONVERSION_RATIO) + ");\n";
        if(!this.name.isEmpty()) // Draw name
            latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + x + ", -" + y + ") " + "node" +
                            " {$" + this.name + "$};\n";
        if(this.flag) // Draw inner circle (final state)
            latex_output += DRAW_COMMAND + ' ' + COLOR + " (" + x + ", -" + y + ") circle (" +
                            ((State.r*(1-State.final_ratio))/CONVERSION_RATIO) + ");\n";
        return latex_output;
    }
}
