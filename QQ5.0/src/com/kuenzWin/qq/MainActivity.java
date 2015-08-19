package com.kuenzWin.qq;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kuenzWin.qq.view.DragLayout;
import com.kuenzWin.qq.view.DragLayout.OnDragStateChangeListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends Activity implements OnDragStateChangeListener {

	private List<String> data = new ArrayList<String>();

	private ListView mLv_left;
	private ListView mLv_main;

	private DragLayout mDragLayout;

	private ImageView mIv_header;
	private ImageView mIv_leftHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无Title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		setData();

		mLv_left = (ListView) this.findViewById(R.id.lv_left);
		mLv_main = (ListView) this.findViewById(R.id.lv_main);

		MyAdapter leftAdapter = new MyAdapter(Color.WHITE);
		MyAdapter mainAdapter = new MyAdapter(Color.BLACK);

		mLv_left.setAdapter(leftAdapter);
		mLv_main.setAdapter(mainAdapter);

		mIv_header = (ImageView) this.findViewById(R.id.iv_header);
		mIv_leftHeader = (ImageView) this.findViewById(R.id.iv_left_header);

		mDragLayout = (DragLayout) this.findViewById(R.id.dl);
		mDragLayout.setOnDragStateChangeListener(this);

	}

	private void setData() {
		data.add("China");
		data.add("USA");
		data.add("Russian");
		data.add("UK");
		data.add("France");
		data.add("Germany");
		data.add("Italy");
		data.add("Japan");
		data.add("Iran");
	}

	private class MyAdapter extends BaseAdapter {

		private int color;

		public MyAdapter(int color) {
			this.color = color;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(MainActivity.this, R.layout.view_lv,
						null);
				holder = new ViewHolder();
				holder.tv = (TextView) convertView
						.findViewById(R.id.tv_lv_cell);
				convertView.setTag("holder");
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(data.get(position));
			holder.tv.setTextColor(color);
			return convertView;
		}

		class ViewHolder {
			TextView tv;
		}

	}

	@Override
	public void onOpen() {
		mLv_left.smoothScrollToPosition(new Random().nextInt(30));
	}

	@Override
	public void onClose() {
		// 让头像来回震动
		ObjectAnimator oa = ObjectAnimator.ofFloat(mIv_header, "translationX",
				15f);
		// 设置震动的圈数
		oa.setInterpolator(new CycleInterpolator(4f));
		// 设置震动的时间
		oa.setDuration(500);
		// 开始震动
		oa.start();
	}

	@Override
	public void onDraging(float percent) {
		// 拉拽时左面板的头像透明度变化
		ViewHelper.setAlpha(mIv_leftHeader, 1 - percent);
	}

}
