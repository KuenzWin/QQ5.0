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
	 * �����ViewGroup
	 */
	private ViewGroup mLeftContent;
	/**
	 * �����ViewGroup
	 */
	private ViewGroup mMainContent;
	/**
	 * ��Ļ���
	 */
	private int mWidth;

	/**
	 * ��ק��Χ����Ļ��ȵ�60%
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
		 * �����Ƿ�Ҫ��קchild���ж�child�Ƿ��ǽ�����ק���ڶ�����Ϊ��㴥������ʱ�ò���
		 */
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}

		@Override
		/**
		 * ����child������ק�ķ�Χ
		 */
		public int getViewHorizontalDragRange(View child) {
			// TODO Auto-generated method stub
			return mRange;
		}

		@Override
		//
		 // child:��ק���ӿؼ�
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

	// ��ViewDragHelper�ж��Ƿ���Ҫ����Touch�¼����д���
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mHelper.shouldInterceptTouchEvent(ev);
	}

	// ��ViewDragHelper����Touch�¼�������ViewDragHelper����Touch�¼�
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
	// �����еĿؼ������غ�ͻᱻ����
	protected void onFinishInflate() {
		super.onFinishInflate();

		int childCount = this.getChildCount();
		if (childCount < 2) {
			throw new IllegalStateException("��Ҫ��������������");
		}
		if (!(getChildAt(0) instanceof ViewGroup)
				|| !(getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalStateException("����������嶼Ӧ����ViewGroup������");
		}

		mLeftContent = (ViewGroup) this.getChildAt(0);
		mMainContent = (ViewGroup) this.getChildAt(1);

	}

	@Override
	/**
	 * ���ߴ緢���仯ʱ��ص��������
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mWidth = getMeasuredWidth() * 60;
		mRange = (int) (mWidth * 0.6f);
	}

}
