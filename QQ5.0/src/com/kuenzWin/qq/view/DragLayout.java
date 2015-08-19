package com.kuenzWin.qq.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kuenzWin.qq.utils.LogUtils;
import com.nineoldandroids.view.ViewHelper;

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
	 * 主面板高度
	 */
	private int mHeight;
	/**
	 * 拖拽范围，屏幕宽度的60%
	 */
	private int mRange;

	/**
	 * 状态
	 * 
	 * @author KuenzWin
	 * @date 2015-8-20
	 */
	public static enum Status {
		Open, Close, Draging
	}

	public static interface OnDragStateChangeListener {
		void onOpen();

		void onClose();

		void onDraging(float percent);
	}

	/**
	 * 状态位,初始化状态为关闭
	 */
	private Status mStatus = Status.Close;

	public Status getStatus() {
		return mStatus;
	}

	public void setStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	/**
	 * 拉拽状态改变监听器
	 */
	private OnDragStateChangeListener onDragStateChangeListener;

	public void setOnDragStateChangeListener(
			OnDragStateChangeListener onDragStateChangeListener) {
		this.onDragStateChangeListener = onDragStateChangeListener;
	}

	public OnDragStateChangeListener getOnDragStateChangeListener() {
		return onDragStateChangeListener;
	}

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
		 * 决定是否要拖拽child，判断child是否是进行拖拽，第二参数为多点触摸，此时用不到
		 */
		public boolean tryCaptureView(View child, int pointerId) {
			// 设置只能拉拽主面板
			return child == mMainContent || child == mLeftContent;
		}

		@Override
		/**
		 * 设置child横向拖拽的范围
		 */
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}

		@Override
		// 决定了view将要放置的位置（在这里进行位置的修正）
		// child:拖拽的子控件
		// left:只是建议移动的位置
		// dx:这次拉拽的长度 (oldLeft+dx = left)
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			LogUtils.d("left:" + left);
			// 如果拉拽的是主面板的话才对位置进行修正
			if (child == mMainContent) {
				left = fixLeft(left);
			}
			return left;
		}

		/**
		 * 拉拽边界处理,修复新的left位置
		 * 
		 * @param left
		 *            理论上的left
		 * @return
		 */
		private int fixLeft(int left) {
			if (left < 0)
				left = 0;
			if (left > mRange)
				left = mRange;
			return left;
		}

		// 当view位置被改变时，要做的其他事情：伴随动画
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

			// 分发拖拽事件，执行伴随动画
			dispatchEvent(mMainLeft);

			// 经过查看源码时发现Android2.3的view位置改变后并不进行界面重绘，
			// 而Android4.0以上则会进行重绘，因而，如果不加上这一句代码就会
			// 发生在Android4.0以上的版本可以发送拉拽而Android4.02.3的就不行
			invalidate();
		}

		@Override
		// 决定了松手之后要处理的事情，view被释放，动画释放
		// releasedChild:被释放的view
		// xvel:x轴的释放速度，往右为正，往正为负
		// yvel:y轴的释放速度
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

	// 由ViewDragHelper判断是否需要拦截Touch事件进行处理
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mHelper.shouldInterceptTouchEvent(ev);
	}

	/**
	 * 分发拖拽事件，执行伴随动画
	 * 
	 * @param mMainLeft
	 */

	protected void dispatchEvent(int mMainLeft) {
		float percent = mMainLeft * 1.0f / mRange;
		LogUtils.d("percent:" + percent);
		animView(percent);

		// 更新状态
		Status lastStatus = mStatus;
		mStatus = updateStatus(percent);
		if(lastStatus != mStatus && onDragStateChangeListener != null){
				if(mStatus == Status.Open)
					onDragStateChangeListener.onOpen();
				else if(mStatus == Status.Close)
					onDragStateChangeListener.onClose();
				else
					onDragStateChangeListener.onDraging(percent);
			}
	}

	/**
	 * 由拉拽百分比获取当前的最新状态
	 * 
	 * @param percent
	 *            拉拽百分比
	 */
	private Status updateStatus(float percent) {
		if (percent == 0)
			mStatus = Status.Close;
		else if (percent == 1)
			mStatus = Status.Open;
		else
			mStatus = Status.Draging;
		return mStatus;
	}

	/**
	 * 执行拉拽状态
	 * 
	 * @param percent
	 *            拉拽百分比
	 */
	private void animView(float percent) {
		// 主面板：缩放动画,随着左面板的增大，主面板减小
		// 0.8f-1.0f
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));
		// 左面板：缩放动画、平移动画、
		// 0.5f~1.0f
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setTranslationX(mLeftContent,
				evaluate(percent, -mWidth / 2.0, 0));
		// 透明度动画
		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.0f, 1.0f));

		getBackground().setColorFilter(
				evaluateColor(percent, Color.BLACK, Color.TRANSPARENT),
				PorterDuff.Mode.SRC_OVER);
	}

	/**
	 * Float估值器
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	/**
	 * 颜色变换器
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public int evaluateColor(float fraction, Object startValue, Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	/**
	 * 关闭动画
	 */
	protected void close(boolean isSmooth) {
		if (isSmooth) {
			// 触发一个平滑的动画，平滑到指定位置
			if (mHelper.smoothSlideViewTo(mMainContent, 0, 0)) {
				// 如果返回值为true，则说明控件还未平移到指定位置
				// 下面这个方法表示在动画执行完成后进行界面重绘
				// 接着执行computeScroll()
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
		}
	}

	/**
	 * 打开动画
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
		// 判断当前的view是否已经移到指定位置了，是高频率的调用
		// 返回true表示还未移动到指定位置
		if (mHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
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
		mWidth = mMainContent.getMeasuredWidth();
		mHeight = mMainContent.getMeasuredHeight();
		mRange = (int) (mWidth * 0.6f);
		LogUtils.d("width:" + mWidth + " range:" + mRange);
	}

}
