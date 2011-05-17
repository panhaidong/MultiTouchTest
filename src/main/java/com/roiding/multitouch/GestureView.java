package com.roiding.multitouch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GestureView extends View {

	private Bitmap footprintBitmap;
	private final Paint footprintPaint;
	private Canvas footprintCanvas;
	private int footprintColor = Color.RED;
	private int footprintWidth = 5;

	private final Rect mRect = new Rect();

	public GestureView(Context c, AttributeSet attrs) {
		super(c, attrs);

		footprintPaint = new Paint();
		footprintPaint.setAntiAlias(true);
		footprintPaint.setColor(footprintColor);
		footprintPaint.setStyle(Paint.Style.STROKE);
		footprintPaint.setStrokeWidth(footprintWidth);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		footprintBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		footprintCanvas = new Canvas();
		footprintCanvas.setBitmap(footprintBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(footprintBitmap, 0, 0, null);
	}

	private void clear() {
		footprintBitmap.eraseColor(0);
		points.clear();
		invalidate();
	}

	private void drawLine(Point p1, Point p2) {
		if (footprintBitmap == null)
			return;

		Log.d("GV", p1.x + "," + p1.y + " " + p2.x + "," + p2.y);
		if (p1.x == 0 && p1.y == 0)
			return;

		footprintCanvas.drawLine(p1.x, p1.y, p2.x, p2.y, footprintPaint);

		int x1 = Math.min(p1.x, p2.x) - 10;
		int y1 = Math.min(p1.y, p2.y) - 10;
		int x2 = Math.max(p1.x, p2.x) + 10;
		int y2 = Math.max(p1.y, p2.y) + 10;

		mRect.set(x1, y1, x2, y2);
		invalidate(mRect);
	}

	private List<Point> points;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.d("MT", "getPointerCount=" + ev.getPointerCount());

		if (points == null || points.size() != ev.getPointerCount()) {
			points = new ArrayList<Point>(ev.getPointerCount());
			for (int i = 0; i < ev.getPointerCount(); i++) {
				points.add(new Point());
			}
		}

		for (int i = 0; i < ev.getPointerCount(); i++) {

			int x0 = (int) ev.getX(i);
			int y0 = (int) ev.getY(i);
			Point p0 = new Point(x0, y0);
			if (ev.getAction() != MotionEvent.ACTION_DOWN) {
				drawLine(points.get(i), p0);
			}
			points.set(i, p0);
		}

		if (ev.getAction() == MotionEvent.ACTION_UP) {
			clear();
		}
		return true;
	}
}
