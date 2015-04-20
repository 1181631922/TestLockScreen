package com.example.testlockscreen;

import java.util.List;

import com.example.screen_manage.ScreenObserver;
import com.example.screen_manage.ScreenObserver.ScreenStateListener;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class MyApp extends Application {

	private ScreenObserver mScreenObserver;

	static MyApp app;
	// 是否出于前台
	private boolean isForeground = true;
	// 是否出于后台
	private boolean isBackground = false;
	// 保存配置信息
	private SharedPreferences preferences;
	// 设置好的图形锁密码
	private String drawKey = "";

	private boolean isSetting = false;

	public static MyApp getInsten() {
		return app;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		app = MyApp.this;

		preferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);

		// 屏幕的监听类
		mScreenObserver = new ScreenObserver(this);
		mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {
			@Override
			public void onScreenOn() {
				// 屏幕的解锁操作
			}

			@Override
			public void onScreenOff() {
				// 屏幕上锁操作
				doSomethingOnScreenOff();
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					isBackground = !isAppOnForeground();
					if (isForeground) {
						doSomethingOnScreenOff();
						isForeground = false;
					}
				}
			}
		}).start();
	}

	private void doSomethingOnScreenOff() {

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String className = GestureLockAty.class.getName();
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			Log.v("Log", tasksInfo.get(0).topActivity.getClassName());
			if (!className.equals(tasksInfo.get(0).topActivity.getClassName())) {
				Log.i("Log", "Screen is off");
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				drawKey = preferences.getString("drawKey", "");
				isSetting = drawKey == null || "".equals(drawKey);
				intent.putExtra("isSetting", isSetting);
				intent.setClass(getApplicationContext(), GestureLockAty.class);
				startActivity(intent);
			}
		}

	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				if (isBackground) {
					isForeground = true;
				}
				return true;
			}
		}
		return false;
	}

}
