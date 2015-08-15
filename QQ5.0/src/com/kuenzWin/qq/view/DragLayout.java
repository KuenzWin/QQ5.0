package com.kuenzWin.qq.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout {

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		ViewDragHelper helper = ViewDragHelper.create(this,
				new ViewDragHelper.Callback() {

					@Override
					public boolean tryCaptureView(View child, int pointerId) {
						// TODO Auto-generated method stub
						return false;
					}
				});
	}

}
