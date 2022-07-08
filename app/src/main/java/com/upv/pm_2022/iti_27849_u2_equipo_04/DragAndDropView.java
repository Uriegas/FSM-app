/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.upv.pm_2022.iti_27849_u2_equipo_04;

import java.util.ArrayList;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback,
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	private DragAndDropThread thread;
	private ArrayList<Figure> figures;
	private int currentFigure;
	int id = 0;
	private GestureDetector gestureDetector;
	private static final String TAG = "FSM_canvas";
	
	public DragAndDropView(Context context) {
		super(context);
		getHolder().addCallback(this);
		gestureDetector = new GestureDetector(context, this);
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
		currentFigure = -1;
		
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
				e.printStackTrace();
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
		gestureDetector.onTouchEvent(event);
		int x = (int) event.getX(); int y = (int) event.getY();
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				for(Figure figure : figures) {
					if(currentFigure == -1 && figure instanceof State)
							currentFigure = ((State) figure).onDown(x, y);
//						System.out.println("currentFigure: " + currentFigure);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(currentFigure != -1) {
					if(figures.get(currentFigure) instanceof State) {
						figures.get(currentFigure).onMove(x, y);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				currentFigure = -1;
				break;
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent motionEvent) {
		Log.d(TAG, "On down: called");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent motionEvent) {
		Log.d(TAG, "On show press: called");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent motionEvent) {
		Log.d(TAG, "On single tap up press: called");
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		Log.d(TAG, "On scroll: called");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {
		Log.d(TAG, "On long press: called");
	}

	@Override
	public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		Log.d(TAG, "On fling: called");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
		Log.d(TAG, "On single tap confirmed: called");
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent motionEvent) {
		Log.d(TAG, "On double tap: called");
		int x = (int) motionEvent.getX(); int y = (int) motionEvent.getY();
		for(Figure figure : figures)
			if(currentFigure == -1 && figure instanceof State)
				currentFigure = ((State) figure).onDown(x, y);

		 if(currentFigure != -1 && figures.get(currentFigure) instanceof State)
			 ((State)figures.get(currentFigure)).setFinal();
		 else
			figures.add(new State(id++, x, y));
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent motionEvent) {
		Log.d(TAG, "On double tap event: called");
		return false;
	}
}