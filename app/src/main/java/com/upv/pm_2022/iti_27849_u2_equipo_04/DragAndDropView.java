package com.upv.pm_2022.iti_27849_u2_equipo_04;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.InputMethodManager;

/**
 * Author: Meta @ vidasconcurrentes
 * Related to: http://vidasconcurrentes.blogspot.com/2011/06/detectando-drag-drop-en-un-canvas-de.html
 * Edited by Andrea Charles, Eduardo Uriegas
 */
public class DragAndDropView extends SurfaceView implements SurfaceHolder.Callback,
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	private DragAndDropThread thread;
	DeleteDialog deleteDialog;
	private ArrayList<Figure> figures;
	private int currentIndex;
	private boolean requestedLink;
	int id = 0;
	private final GestureDetector gestureDetector;
	private static final String TAG = "FSM_canvas";
	private final Paint p;
	private static final int snapToPadding = 18;


	public DragAndDropView(Context context) {
		super(context);
		getHolder().addCallback(this);
		gestureDetector = new GestureDetector(context, this);
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.p = new Paint();
		this.deleteDialog = new DeleteDialog((Activity) context);
		this.requestedLink = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// nothing here
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// Initialize data
		figures = new ArrayList<>();
		figures.add(new Node(id++,500,500));
		currentIndex = -1;
		//Initialize paint
		p.setAntiAlias(true);
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
	 * @param canvas canvas to draw into
	 */
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		if(figures != null)
			try { // Handle multiple access to same resource (Threads issue)
				for(Figure figure : figures)
					figure.draw(canvas);
			} catch (Exception e) {e.printStackTrace();}
	}

	// TODO: Move functionality from onTouchEvent to Gestures
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "Figure -> " + currentIndex);
		gestureDetector.onTouchEvent(event);
		int x = (int) event.getX(); int y = (int) event.getY();
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				getCurrentFigure(x,y);
				break;
			case MotionEvent.ACTION_MOVE:
				if(requestedLink) { // Create new arrow if user clicked in the edge of a circle
					requestedLink = false;
					Node from = (Node)figures.get(currentIndex);
					TempLink arrow = new TempLink(id++, from, x, y);
					figures.add(arrow);
					currentIndex = arrow.onDown(x,y).node_id;
				}
				else if(currentIndex > -1) {
					figures.get(currentIndex).onMove(x, y);
					if(figures.get(currentIndex) instanceof Node)
						snapNode((Node) figures.get(currentIndex));
				}
				break;
			case MotionEvent.ACTION_UP:
				if(currentIndex>-1 && figures.get(currentIndex) instanceof TempLink) {//Crete arrow
					TempLink arrow = (TempLink) figures.get(currentIndex);
					int arr_id = currentIndex;
					currentIndex = -1; //Restart index so search for a new figure is possible
					Figure node  = figures.get(getCurrentFigure(x,y));
					if(node instanceof Node) {
						if(currentIndex == arrow.nodes.get(0).id) // Self link case
							figures.set(arr_id, new SelfLink(arr_id, (Node)node));
						else
							figures.set(arr_id,
									new FixedLink(arr_id, arrow.nodes.get(0), (Node)node));
					} else {
						figures.remove(arr_id);
						id--;
					}
				}
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
	public void onLongPress(MotionEvent event) {
		Log.d(TAG, "On long press: called");
		int x = (int) event.getX(); int y = (int) event.getY();
		getCurrentFigure(x,y);
		deleteDialog.show();
	}

	/**
	 * When an arrow is created it will be locked, thus it can not be redrawn
	 * @param motionEvent event
	 * @param motionEvent1 new event
	 * @param v position v
	 * @param v1 position v1
	 * @return true if handled
	 */
	@Override
	public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		Log.d(TAG, "On fling: called");
		int x = (int) motionEvent.getX(); int y = (int) motionEvent.getY();
		return true;
	}

	/**
	 * On single confirmed click, change name of the state
	 * @param motionEvent touch event
	 * @return true if handled
	 */
	@Override
	public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
		Log.d(TAG, "On single tap confirmed: called");
		int x = (int) motionEvent.getX(); int y = (int) motionEvent.getY();
		getCurrentFigure(x,y);

		if(currentIndex > -1) {
			requestFocus();
			(figures.get(currentIndex)).isEdited(true);
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
		Log.d(TAG, "Key Up " + (char) event.getUnicodeChar() + " pressed");
		//TODO: It makes sense having this code @onKeyDown() since that supports hardware keyboards
		if(currentIndex != -1) {
			Figure figure = figures.get(currentIndex);
			if (keyCode==KeyEvent.KEYCODE_DEL && figure.name.length()>0)
				 figure.name = figure.name.substring(0, figure.name.length()-1);
			else if(keyCode == KeyEvent.KEYCODE_ENTER) {
				clearFocus();
				figure.isEdited(false);
				InputMethodManager imm = (InputMethodManager) getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
			else
				figures.get(currentIndex).name += (char) event.getUnicodeChar();
			Log.d(TAG, "Figure name changed to " + figure.name);
		}
		return false;
	}

	/**
	 * On double click, set final or not final to the state,
	 * if clicked outside of a circle create a new circle
	 * @param motionEvent touch event
	 * @return true if handled
	 */
	@Override
	public boolean onDoubleTap(MotionEvent motionEvent) {
		Log.d(TAG, "On double tap: called");
		int x = (int) motionEvent.getX(); int y = (int) motionEvent.getY();
		getCurrentFigure(x,y);
		if(currentIndex > -1)
			figures.get(currentIndex).setFlag();
		else
			figures.add(new Node(id++, x, y));
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
	 * <p>
	 * @param x position in the x-axis
	 * @param y position in the y-axis
	 * @return id of the current State
	 */
	private int getCurrentFigure(int x, int y) {
		for(Figure figure : figures)
			if(currentIndex == -1) {
				requestedLink = figure.onDown(x, y).requestedLink;
				currentIndex  = figure.onDown(x, y).node_id;
			}
		return currentIndex;
	}

	/**
	 * Get all figures in the canvas
	 * @return a list of figures
	 */
	public ArrayList<Figure> getAllFigures() {
		return this.figures;
	}

	// TODO: Move snapNode() to onMove inside Node
	public void snapNode(Node node) {
		for(Figure figure : figures) {
			if(figure == node) continue;
			if(figure instanceof Node) {
				if(Math.abs(node.x - figure.x) < snapToPadding) node.x = figure.x;
				if(Math.abs(node.y - figure.y) < snapToPadding) node.y = figure.y;
			}
		}
	}
	/*

	@RequiresApi(api = Build.VERSION_CODES.O)
	Bitmap save(View v) {
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGBA_F16);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	public Bitmap loadBitmapFromView(View v) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
				v.getMeasuredHeight(), Bitmap.Config.RGBA_F16);
		Canvas c = new Canvas(returnedBitmap);
		v.draw(c);

		return returnedBitmap;
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	public void bitmapToImage(){
		Bitmap bitmap = save(this);
		try {
			FileOutputStream fileOutputStream = null;
			File path = Environment.getExternalStorageDirectory();
			String unico = UUID.randomUUID().toString();
			File file = new File(path, unico + ".png");
			if (file.exists())
				file.delete();
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fileOutputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			System.out.println(file.getAbsolutePath());
		} catch (IOException e) {
			//
		}
	}

	 */
}