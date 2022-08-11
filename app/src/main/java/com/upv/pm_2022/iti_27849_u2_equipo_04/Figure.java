/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 * Eduardo Uriegas - Added getX() and getY() methods
 */

package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public abstract class Figure {
	protected int id;
	protected int x;
	protected int y;
	protected boolean flag;
	public static final int snapPad = 18; // Used for snap padding between figures
	protected final Paint paint = new Paint();
	public String name;

	protected Figure(int id, int x, int y, String prefix) {
		this.id = id; this.x = x; this.y = y; this.name = prefix + id;
	}

	// Variables for toLatex implementations
	protected final static String DRAW_COMMAND	= "\\draw";
	protected final static String FILL_COMMAND 	= "\\fill";
	protected final static String COLOR       	= "[black]";
	protected final static int CONVERSION_RATIO 	= 27;

	public abstract void draw(Canvas canvas);
	public abstract Tuple onDown(int touchX, int touchY);
	public abstract void onMove(int touchX, int touchY);
	public void setFlag() { this.flag = !this.flag; }
	public int getX(){return this.x;}
	public int getY(){return this.y;}
	/**
	 * Set color of the circle if it is being edited
	 * @param isEdited true if the name of this object is being edited
	 */
	public void isEdited(boolean isEdited){paint.setColor(isEdited ? Color.BLUE : Color.BLACK);}
	public abstract String toLatex(float resize_factor);
}
