package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.R;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.model.AdapterCommon;
import xzd.mobile.android.model.AreaInfo;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.ui.intf.ActivityItf;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityCityChoose extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {

	public static final String PROVINCE_DATA = "province_data";
	public static final String TAG = "ActivityCityChoose";

	public static final int HANDLER_GETDATA_SUCCESS = 1;
	public static final int HANDLER_GEDATA_FAILD = -1;

	private TerminateBiz terminateBiz = null;
	private ListView lv_city = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;
	private TextView detail_title = null;
	private TextView add = null;
	private TextView go_back = null;
	// data
	private AdapterCommon adapter = null;
	private List<AreaInfo> datas = null;
	private CityChooseHandler cityChooseHandler = null;

	private AreaInfo areaInfo = null;
	private String city = null;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Bundle data = new Bundle();
		data.putSerializable(ActivityAreaChoose.AREA_DATA,
				this.datas.get(position));
		city = this.datas.get(position).getArea_Name();
		Intent intent = new Intent(this, ActivityAreaChoose.class);
		intent.putExtras(data);
		startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_AREA);
		intent = null;
		data = null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			setResult(RESULT_CANCELED, new Intent());
			onDestroy();

			break;
		}
	}

	@Override
	public void getPreActivityData() {

		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("输入不能为空");
		}
		areaInfo = (AreaInfo) intent.getSerializableExtra(PROVINCE_DATA);
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		lv_city = (ListView) findViewById(R.id.lv_city);
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		detail_title = (TextView) findViewById(R.id.detail_title);
		// btn_add = (LinearLayout) findViewById(R.id.btn_add);
		// btn_send = (ImageView) findViewById(R.id.btn_send);
		add = (TextView) findViewById(R.id.confrim);
		add.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		lv_city.setOnItemClickListener(this);
		text.setText("正在获取中,请稍候");
		detail_title.setText("城市选择");
		// btn_add.setOnClickListener(this);
		// btn_send.setImageResource(R.drawable.send);
		lv_city.setAdapter(adapter);
		lv_city.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		datas = new ArrayList<AreaInfo>();
		adapter = new AdapterCommon(this, datas);
		cityChooseHandler = new CityChooseHandler();
	}

	private void LoadData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<AreaInfo> obj = null;
				Message msg = cityChooseHandler.obtainMessage();
				try {
					obj = terminateBiz.GetAreaList("1", areaInfo.getArea_Code());

					if (obj == null ) {
//						msg.what = HANDLER_GEDATA_FAILD;
						throw new NullPointerException();
					} else {
						msg.what = HANDLER_GETDATA_SUCCESS;
						msg.obj = obj;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {

				}
				cityChooseHandler.sendMessage(msg);

			}
		}).start();
	}

	private final class CityChooseHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MedicinesActivity.HANDLER_GETDATA_SUCCESS:
				if (msg.obj != null) {
					datas.clear();

					datas.addAll((List<AreaInfo>) msg.obj);

					adapter.notifyDataSetChanged();

					loading.setVisibility(View.GONE);
					lv_city.setVisibility(View.VISIBLE);

					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					lv_city.startAnimation(anim);
					anim = null;

				}
				break;

			case MedicinesActivity.HANDLER_GEDATA_FAILD:
				// datas.clear();
				// adapter.notifyDataSetChanged();
				Intent intent = new Intent();
				intent.putExtra(
						ActivityAddOrEditConsumer.ADDRESS_DATA,"");
				setResult(Config.RESULT_CODE_CHOOSE_CITY, intent);
				onDestroy();
				intent = null;

				// loading.setVisibility(View.VISIBLE);
				// lv_city.setVisibility(View.GONE);
				// progressbar.setVisibility(View.GONE);
				// text.setVisibility(View.VISIBLE);
				// text.setText("获取数据失败");

				break;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		terminateBiz = TerminateBiz.getInstance();
		setContentView(R.layout.city_choose);

		getPreActivityData();
		initData();
		initView();
		LoadData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		terminateBiz = null;
		lv_city = null;
		loading = null;
		progressbar = null;
		text = null;
		detail_title = null;

		// data
		adapter = null;
		datas = null;
		cityChooseHandler = null;

		city = null;
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_CANCELED, new Intent());
		onDestroy();
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case RESULT_CANCELED:

			break;

		case Config.RESULT_CODE_CHOOSE_AREA:
			Intent intent = new Intent();
			intent.putExtra(
					ActivityAddOrEditConsumer.ADDRESS_DATA,
					city
							+ data.getStringExtra(ActivityAddOrEditConsumer.ADDRESS_DATA));
			setResult(Config.RESULT_CODE_CHOOSE_CITY, intent);
			onDestroy();
			intent = null;

			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
