package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Editable;
import android.text.TextWatcher;

import java.util.HashMap;
import java.util.List;

/**
 * A state of the FSM. Fixed size
 */
public class State extends Figure {
    public String name;
    // private boolean isSelected; //Identify if this State has been selected
    private Paint paint = new Paint();
    private int r;
    private static final float ratio_percentage = (float)0.2;

    public State(int id, int x, int y, int r, String name, boolean isFinal) {
        this.id = id; this.x = x; this.y = y; this.r = r; this.name = name; this.flag = isFinal;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
        paint.setTextSize(32);
        paint.setTextAlign(Paint.Align.CENTER);
        // TODO: Change letter size, type and vertically align text.
        //       This requires internet
//        Typeface typeface = new Typeface(new FontFamily("Segoe UI"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
//        FormattedText formattedText = new FormattedText("Text to render", CultureInfo.CurrentUICulture, FlowDirection.LeftToRight, typeFace, 16, Brushes.Black);
//
//        Point textLocation = new Point(centerPoint.X - formattedText.WidthIncludingTrailingWhitespace / 2, center.Y - formattedText.Height);
//        drawingContext.DrawText(formattedText, textLocation);
    }

    public State(int id, int x, int y, int r, String name) { this(id, x, y, r, name, false); }

    public State(int id, int x, int y) { this(id, x, y, 80, "S_" + id); }

    public void draw(Canvas canvas) {
        canvas.drawCircle(this.x, this.y, this.r, paint);
        if(this.flag)
            canvas.drawCircle(this.x, this.y, this.r-this.r*this.ratio_percentage, paint);
        canvas.drawText(name, this.x, this.y, paint);
    }

    /**
     * Check that click is inside the figure
     * @param touchX
     * @param touchY
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
     * @param touchX
     * @param touchY
     */
    public void onMove(int touchX, int touchY) {
        this.x = touchX; this.y = touchY;
        // if border is selected then draw arrow
    }

    /**
     * Set the name of the state
     * @param name
     */
    public void setName(String name) { this.name = name; paint.setColor(Color.BLUE); }
}
