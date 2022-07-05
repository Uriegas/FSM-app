/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.upv.pm_2022.iti_27849_u2_equipo_04;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback {

	private DragAndDropThread thread;
	private ArrayList<Figure> figures;
	private int figuraActiva;
	int id = 0;
	
	public DragAndDropView(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// nothing here
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		figures = new ArrayList<Figure>();
		figures.add(new State(id++,400,500));
		figures.add(new State(id++,800,500));
		figuraActiva = -1;
		
		thread = new DragAndDropThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);

		canvas.drawColor(Color.WHITE);
		for(Figure figure : figures){
			if(figure instanceof State ) {
				State andGate = (State) figure;
				andGate.draw(canvas);
			}
		}
	}

	// TODO: Add on click without drag event, focus in center of the figure to write something
	//		 Add on double click inside circle draw or erase final state.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		switch(event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				for(Figure figure : figures) {
					if(figuraActiva == -1) {
						if (figure instanceof State) {
							figuraActiva = ((State) figure).onDown(x, y);
						}
						System.out.println("figuraActiva: " + figuraActiva);
					}
				}
				break;

			case MotionEvent.ACTION_MOVE:
				if(figuraActiva != -1) {
					if(figures.get(figuraActiva) instanceof State) {
						figures.get(figuraActiva).onMove(x, y);
					}
				}
				break;

			case MotionEvent.ACTION_UP:
				figuraActiva = -1;
				break;
		}

		return true;
	}
}