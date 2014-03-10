package xzd.mobile.android.ui;

import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.LocateService;
import xzd.mobile.android.common.ImageUtils;
import xzd.mobile.android.ui.intf.ActivityItf;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LocationActivity extends BaseActivity implements ActivityItf,
		MKGeneralListener, MKSearchListener, MKMapViewListener, OnClickListener {

	// 继承MyLocationOverlay重写dispatchTap实现点击处理
	public class locationOverlay extends MyLocationOverlay {

		public locationOverlay(MapView mapView) {
			super(mapView);
		}

		@Override
		protected boolean dispatchTap() {
			//

			// Log.e(TAG, "缩放级别" + mapView.getZoomLevel());
			return true;
		}

	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || isLocationClientStop)
				return;
			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = location.getRadius();
			locData.direction = location.getDerect();
			// 更新我的位置
			mylocation_desc = "我的位置(精确到" + location.getRadius() + "米)";
			if (location.getAddrStr() == null) {
				myLocation_title = "成都理工大学";
			} else {
				myLocation_title = location.getAddrStr();
			}

			// 更新定位数据
			myLocationOverlay.setData(locData);
			// 更新图层数据执行刷新后生效
			mapView.refresh();
			// 是手动触发请求或首次定位时，移动到定位点
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				mapcontroller.animateTo(new GeoPoint(
						(int) (locData.latitude * 1e6),
						(int) (locData.longitude * 1e6)));
				isRequest = false;
			}
			// 首次定位完成
			isFirstLoc = false;
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public static final String TAG = "LocationActivity";
	public static final int HANDLER_GET_LOCATION_DATA_SUCCESS = 1;
	public static final int HANDLER_GET_LOCATION_DATA_FAILED = -1;
	public static final String ACTION = "cdutapp.locationactivity";
	public static final String INTENT_REQUEST = "request";// 请求
	public static final String ASSETS_NAME = "campus_location.xml";
	// Data
	private BMapManager bmapmanager = null;

	private UIHelper uihelper = null;
	private ImageUtils imageutils = null;

	private MKSearch mkSerach = null;
	private MapController mapcontroller = null;

	private boolean islayerDialogShow = false;
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;

	public MyLocationListenner myListener = null;
	// 定位图层
	private locationOverlay myLocationOverlay = null;

	// View
	private MyLocationMapView mapView = null;
	private PopupWindow layerSelector = null;
	private ImageButton locateBtn = null;
	// 弹出泡泡图层
	private PopupOverlay pop = null;// 弹出泡泡图层，浏览节点时使用
	private TextView location_title = null;// 标题
	private TextView location_desc = null;// 描述信息
	private LinearLayout main_box = null;

	private String mylocation_desc = null;// 位置描述

	private String myLocation_title = null;// 当前位置
	boolean isRequest = false;// 是否手动触发请求定位
	boolean isFirstLoc = true;// 是否首次定位

	boolean isLocationClientStop = false;

	@Override
	public void initData() {
		// TODO Auto-generated method stub

		mkSerach = new MKSearch();
		mkSerach.init(bmapmanager, this);
		myListener = new MyLocationListenner();

	}

	@Override
	public void initView() {
		// 初始化简单控件
		locateBtn = (ImageButton) findViewById(R.id.locateBtn);
		locateBtn.setOnClickListener(this);
		// 初始化MapVIew
		mapView = (MyLocationMapView) findViewById(R.id.mapView);
		mapView.getController().enableClick(true);
		mapView.getController().setZoom(12);
		mapView.setDoubleClickZooming(true);

		// 初始化控制杆
		mapcontroller = mapView.getController();
		mapcontroller.enableClick(true);
		mapcontroller.setZoom(mapView.getMaxZoomLevel());

		mapView.regMapViewListener(bmapmanager, this);

		// 地图设置监听
		// mapView.setBuiltInZoomControls(true);

		// /初始化底部按钮
		// ViewStub footerStub = (ViewStub) findViewById(R.id.bottom_view_stub);
		// // footerStub.inflate();
		// footerStub = (ViewStub) findViewById(R.id.map_quick_bar);
		// footerStub.inflate();
		// layerButton = (ImageButton) findViewById(R.id.layerButton);
		// layerButton.setOnClickListener(this);

		// 定位初始化
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		// 定位图层初始化
		myLocationOverlay = new locationOverlay(mapView);
		modifyLocationOverlayIcon(getResources().getDrawable(
				R.drawable.icon_loc_light));// 设置定位图标

		// 设置定位数据
		myLocationOverlay.setData(locData);
		// 添加定位图层
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mapView.refresh();

		// 搜索
		// searchBox = (Button) findViewById(R.id.searchBox);
		// searchBox.setOnClickListener(this);
	}

	public void modifyLocationOverlayIcon(Drawable marker) {
		// 当传入marker为null时，使用默认图标绘制
		myLocationOverlay.setMarker(marker);
		// 修改图层，需要刷新MapView生效
		mapView.refresh();
	}

	@Override
	public void onBackPressed() {
		if (islayerDialogShow) { // 如果 Menu已经打开 ，先关闭Menu
			layerSelector.dismiss();
			islayerDialogShow = false;
		} else {
			finish();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.locateBtn:
			requestLocClick();
			break;

		}
	}

	/* 地图监听 */
	@Override
	public void onClickMapPoi(MapPoi mapPoiInfo) {
		// TODO Auto-generated method stub
		/**
		 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过： mMapController.enableClick(true);
		 * 时，此回调才能被触发
		 * 
		 */
		String title = "";
		if (mapPoiInfo != null) {
			title = mapPoiInfo.strText;

			mapcontroller.animateTo(mapPoiInfo.geoPt);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// System.out.println("LocationActivity.onCreate()");
		initBaiduMap();

		setContentView(R.layout.location_main);

		uihelper = UIHelper.getInstance();
		imageutils = ImageUtils.getInstance();

		getPreActivityData();
		initData();
		initView();

	}

	private void initBaiduMap() {
		if (bmapmanager == null) {
			bmapmanager = new BMapManager(this);
		}

		if (!bmapmanager.init(LocateService.STRKEY, this)) {
			Log.e(TAG, "Init BaiduMapManager Failed");
		}

		bmapmanager.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		System.out.println("LocationActivity.onDestroy()");
		if (mLocClient != null)
			mLocClient.stop();
		isLocationClientStop = true;
		mapView.destroy();
		super.onDestroy();
	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* 地图监听 */
	@Override
	public void onGetCurrentMap(Bitmap arg0) {
		// TODO Auto-generated method stub
		/**
		 * 当调用过 mMapView.getCurrentMap()后，此回调会被触发 可在此保存截图至存储设备
		 */
	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* 百度地图监听器1 */
	@Override
	public void onGetNetworkState(int arg0) {
		// TODO Auto-generated method stub

	}

	/* 百度地图监听器2 */
	@Override
	public void onGetPermissionState(int arg0) {
		// TODO Auto-generated method stub

	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/** 百度地图搜索监听器 */
	@Override
	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	/* 地图监听 */
	@Override
	public void onMapAnimationFinish() {
		// TODO Auto-generated method stub

	}

	/* 地图监听 */
	@Override
	public void onMapMoveFinish() {
		// TODO Auto-generated method stub
		/**
		 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
		 */
	}

	@Override
	protected void onPause() {
		System.out.println("LocationActivity.onPause()");
		// TODO Auto-generated method stub
		mapView.onPause();
		isLocationClientStop = true;
		super.onPause();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		System.out.println("LocationActivity.onRestoreInstanceState()");
		super.onRestoreInstanceState(savedInstanceState);
		mapView.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		System.out.println("LocationActivity.onResume()");
		isLocationClientStop = false;
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		System.out.println("LocationActivity.onSaveInstanceState()");

		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);

	};

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		isRequest = true;
		mLocClient.requestLocation();

	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub

	}

}