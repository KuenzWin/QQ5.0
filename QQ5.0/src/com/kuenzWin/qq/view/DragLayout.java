package com.kuenzWin.qq.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
		public boolean tryCaptureView(View child, int pointerId) {
			// TODO Auto-generated method stub
			return false;
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
			throw new IllegalStateException("需要左右面板才行");
		}

		mLeftContent = (ViewGroup) this.getChildAt(0);
		mMainContent = (ViewGroup) this.getChildAt(1);

	}

}
