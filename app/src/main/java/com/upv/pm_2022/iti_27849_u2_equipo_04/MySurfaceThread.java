package com.upv.pm_2022.iti_27849_u2_equipo_04;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MySurfaceThread extends Thread {

	private SurfaceHolder myThreadSurfaceHolder;
	private com.upv.pm_2022.iti_27849_u2_equipo_04.MySurfaceView myThreadSurfaceView;
	private boolean myThreadRun = false;

	public MySurfaceThread(SurfaceHolder surfaceHolder,
						   com.upv.pm_2022.iti_27849_u2_equipo_04.MySurfaceView surfaceView) {
		myThreadSurfaceHolder = surfaceHolder;
		myThreadSurfaceView = surfaceView;
	}

	public void setRunning(boolean b) {
		myThreadRun = b;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// super.run();
		while (myThreadRun) {
			Canvas c = null;
			try {
				c = myThreadSurfaceHolder.lockCanvas(null);
				synchronized (myThreadSurfaceHolder) {
// Por andar de GÜEY hacíendole caso a
					myThreadSurfaceView.onDraw(c);
					//myThreadSurfaceView.draw(c);
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					myThreadSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}