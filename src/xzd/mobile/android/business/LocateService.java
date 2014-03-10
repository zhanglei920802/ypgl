package xzd.mobile.android.business;

import xzd.mobile.android.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import xzd.mobile.android.AppContext;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocateService extends Service {
	/**
	 * 绑定
	 * 
	 * @author ZhangLei
	 * 
	 */
	public final class LocalBinder extends Binder {
		public LocateService getService() {
			return LocateService.this;
		}
	}

	private class MyBDLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			// geoPoint = new GeoPoint(location.getLatitude(), arg1);
			add = location.getAddrStr();
			longtitude = location.getLongitude();
			latidude = location.getLatitude();
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
			longtitude = poiLocation.getLongitude();
			latidude = poiLocation.getLatitude();

		}

	}

	private class MyMKGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {

				Log.e(NAMESPACE, getString(R.string.locate_net_work_error));
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Log.e(NAMESPACE, getString(R.string.locate_serach_input_error));

			}
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Log.e(NAMESPACE, "请在 DemoApplication.java文件输入正确的授权Key！");
			}
		}

	}

	private final String NAMESPACE = "xzd.mobile.android.business.LocateService";

	public static final String STRKEY = "23CBE99EE79B2FB250B20AF009618305AC33811E";// key
	private String add = null;
	private LocalBinder localbinder = null;// 绑定服务

	private BMapManager mBMapManager = null;
	// /Listener
	private MyMKGeneralListener mymkgenerallistener = null;
	private MyBDLocationListener mybdlocationlistener = null;

	private LocationClientOption option = null;

	private LocationClient locationCilent = null;
	// public GeoPoint geoPoint = null;
	public Double longtitude = 0.0;
	public Double latidude = 0.0;

	public String getAdd() {
		return add;
	}

	public void initCommon() {

		mymkgenerallistener = new MyMKGeneralListener();
		mybdlocationlistener = new MyBDLocationListener();

		localbinder = new LocalBinder();

	}

	public void initData() {
		if (mBMapManager == null) {
			try {
				mBMapManager = new BMapManager(this);
				if (!mBMapManager.init(STRKEY, mymkgenerallistener)) {
					Log.e(NAMESPACE,
							getString(R.string.locate_baidumap_initerror));
				}
				mBMapManager.start();
			} catch (Exception e) {
				if (AppContext.DEBUG) {
					Log.e(NAMESPACE, e.getMessage());

				}
			}

		}

		locationCilent = new LocationClient(this);
		locationCilent.registerLocationListener(mybdlocationlistener);

		option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setAddrType("all");
		option.setPoiExtraInfo(true);// 需要POI信息
		option.disableCache(false);// 启用缓存定位
		locationCilent.setLocOption(option);
		locationCilent.start();

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return localbinder;
	}

	@Override
	public void onCreate() {

		initCommon();
		initData();
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mBMapManager.stop();
		locationCilent.stop();
	}

}
