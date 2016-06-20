package com.example.waveview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class WaveView extends View {
	private float mInitialRadius; // 初始波纹半径
	private float mMaxRadiusRate = 1.0f; // 如果没有设置mMaxRadius，可mMaxRadius = 最小长度 * mMaxRadiusRate;
	private float mMaxRadius; // 最大波纹半径
	private long mDuration = 10000; // 一个波纹从创建到消失的持续时间
	private int mSpeed = 500; // 波纹的创建速度，每500ms创建一个
	private Interpolator mInterpolator = new LinearInterpolator();

	private List<Circle> mCircleList = new ArrayList<Circle>();
	private boolean mIsRunning;

	private boolean mMaxRadiusSet;

	private Paint mPaint;
	private long mLastCreateTime;

	private float circleX;
	private float circleY;

	private Runnable mCreateCircle = new Runnable() {
		@Override
		public void run() {
			if (mIsRunning) {
				newCircle();
				postDelayed(mCreateCircle, mSpeed);
			}
		}
	};

	public WaveView(Context context) {
		this(context, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		circleX = event.getX();
		circleY = event.getY();
		mCircleList.clear();
		this.start();
		return true;
	}

	public WaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		setStyle(Paint.Style.FILL);
	}

	public void setStyle(Paint.Style style) {
		mPaint.setStyle(style);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (!mMaxRadiusSet) {
			mMaxRadius = Math.max(w, h) * mMaxRadiusRate / 2.0f;
		}
	}

	public void setMaxRadiusRate(float maxRadiusRate) {
		this.mMaxRadiusRate = maxRadiusRate;
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

	/**
	 * 开始
	 */
	public void start() {
		if (!mIsRunning) {
			mIsRunning = true;
			mCreateCircle.run();
		}
	}

	/**
	 * 停止
	 */
	public void stop() {
		mIsRunning = false;
	}

	protected void onDraw(Canvas canvas) {
		Iterator<Circle> iterator = mCircleList.iterator();
		while (iterator.hasNext()) {
			Circle circle = iterator.next();
			if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
				mPaint.setAlpha(circle.getAlpha());
				canvas.drawCircle(circleX, circleY, circle.getCurrentRadius(), mPaint);
			} else {
				iterator.remove();
			}
		}
		if (mCircleList.size() > 0) {
			postInvalidateDelayed(100);
		}
	}

	public void setInitialRadius(float radius) {
		mInitialRadius = radius;
	}

	public void setDuration(long duration) {
		this.mDuration = duration;
	}

	public void setMaxRadius(float maxRadius) {
		this.mMaxRadius = maxRadius;
		mMaxRadiusSet = true;
	}

	public void setSpeed(int speed) {
		mSpeed = speed;
	}

	private void newCircle() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - mLastCreateTime < mSpeed) {
			return;
		}
		Circle circle = new Circle();
		mCircleList.add(circle);
		invalidate();
		mLastCreateTime = currentTime;
	}

	private class Circle {
		private long mCreateTime;

		public Circle() {
			this.mCreateTime = System.currentTimeMillis();
		}

		public int getAlpha() {
			float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
			return (int) ((1.0f - mInterpolator.getInterpolation(percent)) * 255);
		}

		public float getCurrentRadius() {
			float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
			return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
		}
	}

	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
		if (mInterpolator == null) {
			mInterpolator = new LinearInterpolator();
		}
	}
}