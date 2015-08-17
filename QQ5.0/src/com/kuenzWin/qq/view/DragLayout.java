package com.kuenzWin.qq.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kuenzWin.qq.utils.LogUtils;

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
	 * �����߶�
	 */
	private int mHeight;
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

		mHelper = ViewDragHelper.create(this, 1.0f, mCallBack);

	}

	ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {

		@Override
		/**
		 * �����Ƿ�Ҫ��קchild���ж�child�Ƿ��ǽ�����ק���ڶ�����Ϊ��㴥������ʱ�ò���
		 */
		public boolean tryCaptureView(View child, int pointerId) {
			// ����ֻ����ק�����
			return child == mMainContent || child == mLeftContent;
		}

		@Override
		/**
		 * ����child������ק�ķ�Χ
		 */
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}

		@Override
		// ������view��Ҫ���õ�λ�ã����������λ�õ�������
		// child:��ק���ӿؼ�
		// left:ֻ�ǽ����ƶ���λ��
		// dx:�����ק�ĳ��� (oldLeft+dx = left)
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			LogUtils.d("left:" + left);
			// �����ק���������Ļ��Ŷ�λ�ý�������
			if (child == mMainContent) {
				left = fixLeft(left);
			}
			return left;
		}

		/**
		 * ��ק�߽紦��,�޸��µ�leftλ��
		 * 
		 * @param left
		 *            �����ϵ�left
		 * @return
		 */
		private int fixLeft(int left) {
			if (left < 0)
				left = 0;
			if (left > mRange)
				left = mRange;
			return left;
		}

		// ��viewλ�ñ��ı�ʱ��Ҫ�����������飺���涯��
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {

			int mMainLeft = mMainContent.getLeft();
			if (changedView != mMainContent) {
				mMainLeft += dx;
			}
			mMainLeft = fixLeft(mMainLeft);
			if (changedView == mLeftContent) {
				mLeftContent.layout(0, 0, mWidth, mHeight);
				mMainContent.layout(mMainLeft, 0, mMainLeft + mWidth,
						0 + mHeight);
			}

			// �����鿴Դ��ʱ����Android2.3��viewλ�øı�󲢲����н����ػ棬
			// ��Android4.0�����������ػ棬����������������һ�����ͻ�
			// ������Android4.0���ϵİ汾���Է�����ק��Android4.02.3�ľͲ���
			invalidate();
		}

		@Override
		// ����������֮��Ҫ��������飬view���ͷţ������ͷ�
		// releasedChild:���ͷŵ�view
		// xvel:x����ͷ��ٶȣ�����Ϊ��������Ϊ��
		// yvel:y����ͷ��ٶ�
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (xvel > 0) {
				open(true);
			} else if (xvel == 0 && releasedChild.getLeft() > mRange * 0.5f) {
				open(true);
			} else {
				close(true);
			}
		}

	};

	// ��ViewDragHelper�ж��Ƿ���Ҫ����Touch�¼����д���
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mHelper.shouldInterceptTouchEvent(ev);
	}

	/**
	 * �رն���
	 */
	protected void close(boolean isSmooth) {
		if (isSmooth) {
			// ����һ��ƽ���Ķ�����ƽ����ָ��λ��
			if (mHelper.smoothSlideViewTo(mMainContent, 0, 0)) {
				// �������ֵΪtrue����˵���ؼ���δƽ�Ƶ�ָ��λ��
				// �������������ʾ�ڶ���ִ����ɺ���н����ػ�
				// ����ִ��computeScroll()
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
		}
	}

	/**
	 * �򿪶���
	 */
	protected void open(boolean isSmooth) {
		if (isSmooth) {
			if (mHelper.smoothSlideViewTo(mMainContent, mRange, 0)) {
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(mRange, 0, mRange + mWidth, 0 + mHeight);
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		// �жϵ�ǰ��view�Ƿ��Ѿ��Ƶ�ָ��λ���ˣ��Ǹ�Ƶ�ʵĵ���
		// ����true��ʾ��δ�ƶ���ָ��λ��
		if (mHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
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
		mWidth = mMainContent.getMeasuredWidth();
		mHeight = mMainContent.getMeasuredHeight();
		mRange = (int) (mWidth * 0.6f);
		LogUtils.d("width:" + mWidth + " range:" + mRange);
	}

}
