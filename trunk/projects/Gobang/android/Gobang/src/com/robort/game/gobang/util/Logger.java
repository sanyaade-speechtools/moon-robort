package com.robort.game.gobang.util;

public class Logger {
	private final static String TAG = "GoBang";
	public static void i(String msg) {
		android.util.Log.i(TAG, msg);
	}
	public static void e(String msg) {
		android.util.Log.e(TAG, msg);
	}
}
