package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Arrow extends Figure {
    // TODO: Use a boolean variable to change the direction of the line
    // private boolean direction;
    private int endX;
    private int endY;
    private Paint paint = new Paint();

    public Arrow(int id, int x, int y) {
        this.id=id; this.x=x; this.y=y;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);
    }

    public void draw(Canvas canvas){
        // Andrea, el codigo comentado es para hacer una linea curva, por el momento no hagas las
        // lineas curvas, es mas facil con el que esta alli
//        canvas.drawArc(this.x, this.y,this.x+200,this.y+200,(float)100,(float)10,false,paint);
        canvas.drawLine(this.x, this.y, this.x+this.endX, this.y+this.endY, paint);
        // TODO: draw triangle at end of arc (complete arrow)
        // Andrea, esto de dibujar el triangulito en la punta de la flecha dejalo para el final
    }

    public int onDown(int touchX, int touchY){
        return this.id;
    }

    public void onMove(int touchX, int touchY){ this.endX = touchX; this.endY = touchY; }
}
