package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
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

public class ActivityAreaChoose extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {
	public static final int HANDLER_GETDATA_SUCCESS = 1;
	public static final int HANDLER_GEDATA_FAILD = -1;
	public static final String TAG = "ActivityAreaChoose";

	private AppContext appContext = null;

	private TerminateBiz terminateBiz = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;

	private List<AreaInfo> data = null;
	private AdapterCommon adapter = null;
	private TextView go_back = null;
	private TextView detail_title = null;
	private ListView lv_area = null;
	private AreaChooseHandler areaChooseHandler = null;
	private AreaInfo areaInfo = null;
	private TextView add = null;
 
	public static final String AREA_DATA = "area_data";

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		Intent intent = new Intent();
		intent.putExtra(ActivityAddOrEditConsumer.ADDRESS_DATA,
				this.data.get(position).getArea_Name());
		String code = this.data.get(position).getArea_Code();
		appContext.area_code = code;
		setResult(Config.RESULT_CODE_CHOOSE_AREA, intent);
		intent = null;

		onDestroy();
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
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("输入不能为空");
		}
		areaInfo = (AreaInfo) intent.getSerializableExtra(AREA_DATA);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		lv_area = (ListView) findViewById(R.id.lv_area);
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		detail_title = (TextView) findViewById(R.id.detail_title);
		// btn_add = (LinearLayout) findViewById(R.id.btn_add);
		// btn_send = (ImageView) findViewById(R.id.btn_send);

		lv_area.setOnItemClickListener(this);
		text.setText("正在获取中,请稍候");
		detail_title.setText("县区选择");
		// btn_add.setOnClickListener(this);
		// btn_send.setImageResource(R.drawable.send);
		lv_area.setAdapter(adapter);
		lv_area.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		add = (TextView) findViewById(R.id.confrim);
		add.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		data = new ArrayList<AreaInfo>();
		adapter = new AdapterCommon(this, data);
		areaChooseHandler = new AreaChooseHandler();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.area_choose);
		appContext =(AppContext) getApplication();
		terminateBiz = TerminateBiz.getInstance();
		getPreActivityData();
		initData();
		initView();
		LoadData();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		appContext = null;

		terminateBiz = null;
		loading = null;
		progressbar = null;
		text = null;

		data = null;
		adapter = null;
		go_back = null;
		detail_title = null;
		lv_area = null;
		areaChooseHandler = null;

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
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void LoadData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<AreaInfo> obj = null;
				Message msg = areaChooseHandler.obtainMessage();
				try {
					obj = terminateBiz.GetAreaList("2", areaInfo.getArea_Code());

					if (obj == null ) {
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
				areaChooseHandler.sendMessage(msg);

			}
		}).start();
	}

	private final class AreaChooseHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MedicinesActivity.HANDLER_GETDATA_SUCCESS:
				if (msg.obj != null) {
					data.clear();

					data.addAll((List<AreaInfo>) msg.obj);

					adapter.notifyDataSetChanged();

					loading.setVisibility(View.GONE);
					lv_area.setVisibility(View.VISIBLE);

					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					lv_area.startAnimation(anim);
					anim = null;

				}
				break;

			case MedicinesActivity.HANDLER_GEDATA_FAILD:

				Intent intent = new Intent();
				intent.putExtra(ActivityAddOrEditConsumer.ADDRESS_DATA, "");
				setResult(Config.RESULT_CODE_CHOOSE_AREA, intent);
				intent = null;
				onDestroy();
				break;
			}
		}
	}
}
