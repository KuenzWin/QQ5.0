package com.kuenzWin.qq.utils;

import android.util.Log;

public class LogUtils {

	public static boolean isAllow = false;

	public static void d(String msg) {
		if (isAllow)
			Log.d("KuenzWin", msg);
	}

}
