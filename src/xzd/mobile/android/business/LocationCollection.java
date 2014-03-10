package xzd.mobile.android.business;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.basic.ApiClient;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * 位置信息采集
 * 
 * @author Administrator
 * 
 */
public class LocationCollection extends Service {
	public static final String TAG = "LocationCollection";
	private int deltaTime = 8 * 60 * 1000;// 5分钟
	private String startTime = "09:00:00";
	private String endTime = "17:30:00";
	private Timer locationTimer = new Timer("locationTimer");
	private IBinder mBinder = null;
	private MyServiceConnection myserviceconnection = null;
	private LocateService mBoundService = null;
	private Intent intent = null;
	private AppContext appContext = null;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case 5:// 时间已经到
				Log.d(TAG, "线程:" + Thread.currentThread().getName()
						+ "收到定时已到消息，即将开启定位服务");
				// 获取定位数据
				if (appContext.isNetworkConnected()) {
					SaveLocation(AppContext.user.getM_id(),
							"" + mBoundService.getAdd(),
							mBoundService.longtitude, mBoundService.latidude);
				}
				break;

			case 6:
				Log.d(TAG, "主线程获取到消息:向服务器保存数据成功。即将停止定位服务");
				// if (mBinder.isBinderAlive()) {
				// UnBindService();
				// }

				break;
			}
		}

	};
	private TimerTask timerTask = new TimerTask() {// 定时任务

		@Override
		public void run() {
			// mHandler.sendEmptyMessage(1);
			Log.d(TAG, "时间已经到");

			mHandler.sendEmptyMessage(5);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		appContext = (AppContext) getApplication();
		locationTimer.schedule(timerTask, 3000, deltaTime);
		myserviceconnection = new MyServiceConnection();
		intent = new Intent(LocationCollection.this, LocateService.class);
		BindService();
	}

	public class LocalBinder extends Binder {
		LocationCollection getService() {
			return LocationCollection.this;
		}
	};

	private void BindService() {

		bindService(intent, myserviceconnection, BIND_AUTO_CREATE);

	}

	/**
	 * 解绑定位服务
	 */
	public void UnBindService() {
		// myserviceconnection.
		unbindService(myserviceconnection);
	}

	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = service;
			mBoundService = ((LocateService.LocalBinder) service).getService();

			Log.d(TAG, "已经绑定服务");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			System.out
					.println("LocationCollection.MyServiceConnection.onServiceDisconnected()");

			mBoundService = null;// 将数据置空
		}
	}

	public void SaveLocation(final int m_id, final String locaiton,
			final Double longtitude, final Double latitude) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int result = 0;
				Log.d(TAG, "数据:\r\n地点:" + locaiton + "\r\nlongtitude:"
						+ longtitude + "\r\nlatitude=" + latitude);
				try {
					result = ApiClient.getInstance().SaveLocation(m_id,
							locaiton, longtitude, latitude);
					if (result > 0) {
						// mHandler.sendEmptyMessage(6);

						Log.d(TAG, new Date().toLocaleString() + ":向服务器保存数据成功");
					} else {
						throw AppException.http(new Exception());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d(TAG, "向服务器保存定位数据失败");
				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		UnBindService();
		System.out.println("LocationCollection.onDestroy()");
		Log.d(TAG, "取消定时器任务:" + timerTask.cancel());
		locationTimer.cancel();
		mHandler = null;

	}

}
