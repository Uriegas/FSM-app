package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * A state of the FSM. Fixed size
 */
public class State extends Figure {
    public String name;
    // private boolean isSelected; //Identify if this State has been selected
    private final Paint paint = new Paint();
    public static final int r = 81;
    public static final float ratio_percentage = (float)0.2;

    public State(int id, int x, int y, String name, boolean isFinal) {
        this.id = id; this.x = x; this.y = y; this.name = name; this.flag = isFinal;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setTextSize(38);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.SANS_SERIF);
    }

    public State(int id, int x, int y, String name) { this(id, x, y, name, false); }

    public State(int id, int x, int y) { this(id, x, y, "S_" + id); }

    public void draw(Canvas canvas) {
        canvas.drawCircle(this.x, this.y, r, paint);
        if(this.flag)
            canvas.drawCircle(this.x, this.y, r-r*ratio_percentage, paint);
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
    public int onDown(int touchX, int touchY) {
        // Pythagorean theorem in circle: x^2 + y^2 = r^2, if touched is (x_1, y_1) then
        // the square of the distance from the center to the touched point should be less than r^2:
        // (x-x_1)^2 + (y-y_1)^2 < r^2
        if (((x-touchX) * (x-touchX)) + ((y-touchY) * (y-touchY)) < (r*r))
            return this.id;
        // Andrea, aqui tienes que retornar -2 cuando la persona clickea a la orilla del circulo,
        // es decir, cuando quiere crear una nueva linea. Vas a tener que cambiar un poco la logica
        // de la linea 64, HINT: la variable ratio_percentage te puede servir
//        else if( clicea a la orilla del circulo)
//            return -2
        return -1;
    }

    /**
     * Change the position of the figure
     * @param touchX x touched
     * @param touchY y touched
     */
    public void onMove(int touchX, int touchY) {
        this.x = touchX; this.y = touchY;
        // if border is selected then draw arrow
    }

    /**
     * Set color of the circle if it is being edited
     * @param isEdited true if the name of this object is being edited
     */
    public void isEdited(boolean isEdited) {
        if(isEdited)
            paint.setColor(Color.BLUE);
        else
            paint.setColor(Color.BLACK);
    }
}
