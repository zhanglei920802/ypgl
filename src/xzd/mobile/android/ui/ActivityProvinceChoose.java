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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityProvinceChoose extends BaseActivity implements
		ActivityItf, OnClickListener, OnItemClickListener {
	public static final String TAG = "ActivityChooseProvince";

	public static final String CUSTOMCHOOSEDATA = "customchoosedata";
	public static final int HANDLER_GETDATA_SUCCESS = 1;
	public static final int HANDLER_GEDATA_FAILD = -1;

	private TerminateBiz terminateBiz = null;
	private ListView lv_province = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;
	private TextView detail_title = null;
	private TextView add = null;
	private TextView go_back = null;
	// data
	private AdapterCommon adapter = null;
	private List<AreaInfo> datas = null;
	private ProvinceChooseHandler provinceChooseHandler = null;

	private String province = null;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		lv_province = (ListView) findViewById(R.id.lv_province);
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		detail_title = (TextView) findViewById(R.id.detail_title);
		// btn_add = (LinearLayout) findViewById(R.id.btn_add);
		// btn_send = (ImageView) findViewById(R.id.btn_send);

		lv_province.setOnItemClickListener(this);
		text.setText("正在获取中,请稍候");
		detail_title.setText("省份选择");
		// btn_add.setOnClickListener(this);
		// btn_send.setImageResource(R.drawable.send);
		lv_province.setAdapter(adapter);
		lv_province.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);

		add = (TextView) findViewById(R.id.confrim);
		add.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		datas = new ArrayList<AreaInfo>();
		adapter = new AdapterCommon(this, datas);
		provinceChooseHandler = new ProvinceChooseHandler();
	}

	private void LoadData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<AreaInfo> obj = null;
				Message msg = provinceChooseHandler.obtainMessage();
				try {
					obj = terminateBiz.GetAreaList("0", "");

					if (obj == null) {
						//msg.what = HANDLER_GEDATA_FAILD;
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
				provinceChooseHandler.sendMessage(msg);

			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "POSITION:" + position);
		Bundle data = new Bundle();
		data.putSerializable(ActivityCityChoose.PROVINCE_DATA,
				this.datas.get(position));
		province = this.datas.get(position).getArea_Name();

		Intent intent = new Intent(this, ActivityCityChoose.class);
		intent.putExtras(data);
		startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_CITY);
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		terminateBiz = TerminateBiz.getInstance();

		setContentView(R.layout.province_choose);

		getPreActivityData();
		initData();
		initView();
		LoadData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		terminateBiz = null;
		lv_province = null;
		loading = null;
		progressbar = null;
		text = null;
		detail_title = null;

		// data
		adapter = null;
		datas = null;
		provinceChooseHandler = null;

		province = null;

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

		case Config.RESULT_CODE_CHOOSE_CITY:
			Intent intent = new Intent();
			intent.putExtra(
					ActivityAddOrEditConsumer.ADDRESS_DATA,
					province
							+ data.getStringExtra(ActivityAddOrEditConsumer.ADDRESS_DATA));
			setResult(Config.RESULT_CODE_CHOOSE_PROVINCE, intent);

			onDestroy();

			intent = null;
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private final class ProvinceChooseHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MedicinesActivity.HANDLER_GETDATA_SUCCESS:
				if (msg.obj != null) {
					datas.clear();

					datas.addAll((List<AreaInfo>) msg.obj);
					datas.remove(0);
					adapter.notifyDataSetChanged();

					loading.setVisibility(View.GONE);
					lv_province.setVisibility(View.VISIBLE);

					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					lv_province.startAnimation(anim);
					anim = null;

				}
				break;

			case MedicinesActivity.HANDLER_GEDATA_FAILD:
				// datas.clear();
				// adapter.notifyDataSetChanged();
				loading.setVisibility(View.VISIBLE);
				lv_province.setVisibility(View.GONE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				text.setText("获取数据失败");

				break;
			}
		}
	}
}
