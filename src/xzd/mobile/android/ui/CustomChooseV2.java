package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import xzd.mobile.android.AppContext;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.ui.adapter.MyCustomersAdapter;
import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.R;

/**
 * 二〇一三年七月七日 00:45:20
 * 
 * @author ZhangLei
 * 
 */
public class CustomChooseV2 extends BaseActivity implements ActivityItf,
		OnItemClickListener, OnClickListener {

	public static final String TAG = "MedicinesActivity";

	public static final String CUSTOMCHOOSEDATA = "customchoosedata";
	public static final int HANDLER_GETDATA_SUCCESS = 1;
	public static final int HANDLER_GEDATA_FAILD = -1;

	private OrderHelper orderhelper = null;
	private AppContext appcontext = null;
	private LinearLayout search_box = null;
	private ListView custom_list = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;
	private TextView detail_title = null;
	private TextView add = null;
	private TextView go_back = null;
	// data
	private MyCustomersAdapter adapter = null;
	private List<CustomerSimple> datas = null;
	private CustomHandler medicinehandler = null;
	private int saler_id = 0;
	private EditText countermen_input1 = null;
	private Button countermen_search = null;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("参数错误！");
		}
		saler_id = intent.getIntExtra(CUSTOMCHOOSEDATA, 0);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		custom_list = (ListView) findViewById(R.id.custom_list);
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		detail_title = (TextView) findViewById(R.id.detail_title);
		// btn_add = (LinearLayout) findViewById(R.id.btn_add);
		// btn_send = (ImageView) findViewById(R.id.btn_send);
		
		custom_list.setOnItemClickListener(this);
		text.setText("正在获取中,请稍候");
		detail_title.setText("客户选择");
		// btn_add.setOnClickListener(this);
		// btn_send.setImageResource(R.drawable.send);
		custom_list.setAdapter(adapter);
		custom_list.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		add = (TextView) findViewById(R.id.confrim);
		add.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		{
			countermen_input1 = (EditText) findViewById(R.id.countermen_input1);
			countermen_search = (Button) findViewById(R.id.countermen_search);
			countermen_search.setOnClickListener(this);
		}
		
		search_box =(LinearLayout) findViewById(R.id.search_box);
		search_box.setVisibility(View.GONE);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		datas = new ArrayList<CustomerSimple>();
		adapter = new MyCustomersAdapter(this, datas);
		medicinehandler = new CustomHandler();

	}

	private void LoadData(final String name) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<CustomerSimple> obj = null;
				Message msg = medicinehandler.obtainMessage();
				try {
					if (AppContext.user.getCounterMain_ID() < 0) {
						medicinehandler.sendEmptyMessage(-1);
						return;

					}
					if (!appcontext.isNetworkConnected()) {
						medicinehandler.sendEmptyMessage(-2);
						return;
					}

					
						if (TextUtils.isEmpty(name)) {

							obj = OrderHelper.GetSimCustomersBySalerID(saler_id);
						} else {
							obj = OrderHelper.QuerySimCustomerListByCounter(
									AppContext.user.getM_id(), name);
						}
				

					if (obj == null || obj.isEmpty()) {
						medicinehandler.sendEmptyMessage(-3);
						return;
					}

					msg.what = HANDLER_GETDATA_SUCCESS;
					msg.obj = obj;
					medicinehandler.sendMessage(msg);

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					medicinehandler.sendEmptyMessage(-4);
				}

			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		orderhelper = OrderHelper.getInstance();
		appcontext = (AppContext) getApplication();

		setContentView(R.layout.custom_choose);
		getPreActivityData();
		initData();
		initView();
		LoadData("");

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			setResult(RESULT_CANCELED, new Intent());
			onDestroy();
			break;
		case R.id.countermen_search:
			if (!AppContext.is_login()) {
				UIHelper.Toast(this, "未登录");
				return;
			}

			if (!appcontext.isNetworkConnected()) {
				UIHelper.Toast(this, "无网络连接");
				return;
			}

			if (TextUtils.isEmpty(countermen_input1.getText().toString())) {
				UIHelper.Toast(this, "输入为空");
				return;
			}

			LoadData(countermen_input1.getText().toString());
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.custom_list:
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putSerializable(AddOrderActivity.CHOOSE_CUSTOM,
					datas.get(position));
			intent.putExtras(data);
			setResult(Config.RESULT_CODE_CHOOSE_CUSTOM, intent);
			finish();
			break;

		default:
			break;
		}
	}

	private final class CustomHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (msg.obj != null) {
					datas.clear();

					datas.addAll((List<CustomerSimple>) msg.obj);

					adapter.notifyDataSetChanged();

					loading.setVisibility(View.GONE);
					custom_list.setVisibility(View.VISIBLE);

					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					custom_list.startAnimation(anim);
					anim = null;

				}
				break;

			case -1:
			case -2:
			case -3:
			case -4:
				// datas.clear();
				// adapter.notifyDataSetChanged();
				loading.setVisibility(View.VISIBLE);
				custom_list.setVisibility(View.GONE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				text.setText("对不起,没有可用数据");

				break;
			}
		}
	}
}
