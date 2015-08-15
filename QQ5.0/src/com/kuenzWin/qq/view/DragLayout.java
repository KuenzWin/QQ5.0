package com.kuenzWin.qq.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout {

	private ViewDragHelper mHelper;

	/**
	 * 左面板ViewGroup
	 */
	private ViewGroup mLeftContent;
	/**
	 * 主面板ViewGroup
	 */
	private ViewGroup mMainContent;
	/**
	 * 屏幕宽度
	 */
	private int mWidth;

	/**
	 * 拖拽范围，屏幕宽度的60%
	 */
	private int mRange;

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mHelper = ViewDragHelper.create(this, mCallBack);

	}

	ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {

		@Override
		/**
		 * 决定是否要拖拽child，判断child是否是进行拖拽，第二参数为多点触摸，此时用不到
		 */
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}

		@Override
		/**
		 * 设置child横向拖拽的范围
		 */
		public int getViewHorizontalDragRange(View child) {
			// TODO Auto-generated method stub
			return mRange;
		}

		@Override
		//
		 // child:拖拽的子控件
		 //left:
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// TODO Auto-generated method stub
			return super.clampViewPositionHorizontal(child, left, dx);
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// TODO Auto-generated method stub
			super.onViewPositionChanged(changedView, left, top, dx, dy);
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// TODO Auto-generated method stub
			super.onViewReleased(releasedChild, xvel, yvel);
		}

	};

	// 由ViewDragHelper判断是否需要拦截Touch事件进行处理
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mHelper.shouldInterceptTouchEvent(ev);
	}

	// 由ViewDragHelper拦截Touch事件，即让ViewDragHelper处理Touch事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	// 在所有的控件被加载后就会被调用
	protected void onFinishInflate() {
		super.onFinishInflate();

		int childCount = this.getChildCount();
		if (childCount < 2) {
			throw new IllegalStateException("需要左面板和主面板才行");
		}
		if (!(getChildAt(0) instanceof ViewGroup)
				|| !(getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalStateException("左面板和主面板都应该是ViewGroup的子类");
		}

		mLeftContent = (ViewGroup) this.getChildAt(0);
		mMainContent = (ViewGroup) this.getChildAt(1);

	}

	@Override
	/**
	 * 当尺寸发生变化时会回调这个方法
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = getMeasuredWidth() * 60;
		mRange = (int) (mWidth * 0.6f);
	}

}
