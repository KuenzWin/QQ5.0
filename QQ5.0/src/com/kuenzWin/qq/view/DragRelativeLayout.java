package com.kuenzWin.qq.view;

import com.kuenzWin.qq.view.DragLayout.Status;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class DragRelativeLayout extends RelativeLayout {

	public DragRelativeLayout(Context context) {
		this(context, null);
	}

	public DragRelativeLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DragRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout getDragLayout() {
		return mDragLayout;
	}

	public void setDragLayout(DragLayout mDragLayout) {
		this.mDragLayout = mDragLayout;
	}

	private DragLayout mDragLayout;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// ���DragLayout�Ǵ�״̬�Ͷ�Touch�¼���������
		if (mDragLayout.getStatus() == Status.Close)
			return super.onInterceptTouchEvent(ev);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mDragLayout.getStatus() == Status.Close)
			return super.onTouchEvent(event);
		// ���DragLayout�Ǵ�״̬�Ͷ�Touch�¼���������,����һ̧��ͻ�ر�
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mDragLayout.close(true);
		}
		return true;
	}

}
