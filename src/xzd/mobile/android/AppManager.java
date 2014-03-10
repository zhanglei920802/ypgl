package xzd.mobile.android;

import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class AppManager {
	private static Stack<Activity> activityStack = null;

	public static AppManager mAppManger = null;

	public synchronized static AppManager getInstance() {
		if (null == mAppManger) {
			mAppManger = new AppManager();
		}
		return mAppManger;
	}

	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}

	}

	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();

	}

	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Stack<Activity> getActivityStack() {
		return activityStack;
	}

}
