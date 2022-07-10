/*
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 */

package com.upv.pm_2022.iti_27849_u2_equipo_04;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback,
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	private DragAndDropThread thread;
	private ArrayList<State> states;
	//TODO: Implement adjacency list instead of ArrayList<State>
	private HashMap<State, ArrayList<State>> adjacency_list;
	private int currentIndex;
	int id = 0;
	private GestureDetector gestureDetector;
	private static final String TAG = "FSM_canvas";
	
	public DragAndDropView(Context context) {
		super(context);
		getHolder().addCallback(this);
		gestureDetector = new GestureDetector(context, this);
		setFocusable(true);
		setFocusableInTouchMode(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// nothing here
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// Initialize data
		states = new ArrayList<State>();
		states.add(new State(id++,500,500));
		currentIndex = -1;
		// Initialize thread
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

	/**
	 * Draws all the states aka circles
	 * @param canvas
	 */
	@Override
	public void onDraw(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);

		canvas.drawColor(Color.WHITE);
		for(State state : states)
			 state.draw(canvas);
	}

	// TODO: Add on click without drag event, focus in center of the figure to write something
	//		 Add on double click inside circle draw or erase final state.
	// TODO: Move functionality from onTouchEvent to Gestures
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		int x = (int) event.getX(); int y = (int) event.getY();
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				getCurrentState(x,y);
				break;
			case MotionEvent.ACTION_MOVE:
				if(currentIndex != -1)
					states.get(currentIndex).onMove(x, y);
				break;
			case MotionEvent.ACTION_UP:
				currentIndex = -1;
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

	/**
	 * On single click, change name of the state
	 * @param motionEvent
	 * @return
	 */
	@Override
	public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
		Log.d(TAG, "On single tap confirmed: called");
		int x = (int) motionEvent.getX(); int y = (int) motionEvent.getY();
		getCurrentState(x,y);

		if(currentIndex != -1) { // TODO: Change color of the circle here
			// Open Keyboard
			requestFocus();
			InputMethodManager imm = (InputMethodManager) getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "Key " + (char) event.getUnicodeChar() + " pressed");
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		char key = (char) event.getUnicodeChar();
		Log.d(TAG, "Key Up " + (char) event.getUnicodeChar() + " pressed");
		if(currentIndex != -1) { //TODO: Implement BACKSPACE and ENTER keys
			if (keyCode == KeyEvent.KEYCODE_DEL)
				states.get(currentIndex).name.substring(0,states.get(currentIndex).name.length()-1);
			else if((keyCode==KeyEvent.ACTION_DOWN)&&(keyCode == KeyEvent.KEYCODE_ENTER)){
				clearFocus();
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
			else
				 states.get(currentIndex).name += (char) event.getUnicodeChar();
		}
		Log.d(TAG, "State name changed to " + states.get(currentIndex).name);
		return false;
	}

	/**
	 * On double click, set final or not final to the state,
	 * if clicked outside of a circle create a new circle
	 * @param motionEvent
	 * @return
	 */
	@Override
	public boolean onDoubleTap(MotionEvent motionEvent) {
		Log.d(TAG, "On double tap: called");
		int x = (int) motionEvent.getX(); int y = (int) motionEvent.getY();
		getCurrentState(x,y);
		if(currentIndex != -1)
			states.get(currentIndex).setFinal();
		else
			states.add(new State(id++, x, y));
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent motionEvent) {
		Log.d(TAG, "On double tap event: called");
		return false;
	}

	/**
	 * Get the current State, that is the clicked Circle.
	 * <p>
	 * This method sets the attribute currentIndex, there is no need to set it outside
	 * @param x
	 * @param y
	 * @return id of the current State
	 */
	private int getCurrentState(int x, int y) {
		for(State state : states)
			if(currentIndex == -1)
				currentIndex = state.onDown(x, y);
		return currentIndex;
	}
}