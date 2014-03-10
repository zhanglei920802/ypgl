package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.SalerSimple;
import xzd.mobile.android.ui.adapter.AdaterCouterMan;
import xzd.mobile.android.ui.intf.ActivityItf;
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

public class ActivityCounterManChoose extends BaseActivity implements
		ActivityItf, OnClickListener, OnItemClickListener {
	public static final String TAG = "ActivityCounterManChoose";

	public static final int HANDLER_GETDATA_SUCCESS = 1;
	public static final int HANDLER_GEDATA_FAILD = -1;

	private OrderHelper orderhelper = null;
	private AppContext appcontext = null;

	private ListView lv_counterman = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;
	private TextView detail_title = null;
	private TextView confrim = null;
	int user_id = 0;
	private TextView add = null;
	private TextView go_back = null;
	private EditText countermen_input1 = null;
	private Button countermen_search = null;
	// data
	public static final String COUNTERMANDATA = "countermandata";
	private AdaterCouterMan adapter = null;
	private List<SalerSimple> datas = null;
	private CounterManHandler counterManHandler = null;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.go_back:
			setResult(RESULT_CANCELED, new Intent());
			onDestroy();
			break;

		case R.id.countermen_search:
			if(!AppContext.is_login()){
				UIHelper.Toast(this,"未登录");
				return;
			}
			
			if(!appcontext.isNetworkConnected()){
				UIHelper.Toast(this, "无网络连接");
				return;
			}
			
			 if(TextUtils.isEmpty(countermen_input1.getText().toString())){
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
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("参数错误！");
		}
		user_id = intent.getIntExtra(COUNTERMANDATA, 0);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		lv_counterman = (ListView) findViewById(R.id.lv_counterman);
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		detail_title = (TextView) findViewById(R.id.detail_title);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setVisibility(View.GONE);
		
		countermen_input1 = (EditText) findViewById(R.id.countermen_input1);
		countermen_search = (Button) findViewById(R.id.countermen_search);
		countermen_search.setOnClickListener(this);
		
		// btn_add = (LinearLayout) findViewById(R.id.btn_add);
		// btn_send = (ImageView) findViewById(R.id.btn_send);

		lv_counterman.setOnItemClickListener(this);
		text.setText("正在获取中,请稍候");
		detail_title.setText("销售人员选择");
		// btn_add.setOnClickListener(this);
		// btn_send.setImageResource(R.drawable.send);
		lv_counterman.setAdapter(adapter);
		lv_counterman.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);

		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		datas = new ArrayList<SalerSimple>();
		adapter = new AdaterCouterMan(this, datas);
		counterManHandler = new CounterManHandler();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.counterman_choose);
		orderhelper = OrderHelper.getInstance();
		appcontext = (AppContext) getApplication();

		getPreActivityData();
		initData();
		initView();
		LoadData(null);
	}

	private void LoadData(final String name) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<SalerSimple> obj = null;
				Message msg = counterManHandler.obtainMessage();
				try {
						if(TextUtils.isEmpty(name)){
								obj = orderhelper.GetCountermen(user_id);

						}else{
							obj = OrderHelper.QueryCountermen(user_id,name);
						}
				
					if (obj == null) {
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
				counterManHandler.sendMessage(msg);

			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	private final class CounterManHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MedicinesActivity.HANDLER_GETDATA_SUCCESS:
				if (msg.obj != null) {
					datas.clear();

					datas.addAll((List<SalerSimple>) msg.obj);

					adapter.notifyDataSetChanged();

					loading.setVisibility(View.GONE);
					lv_counterman.setVisibility(View.VISIBLE);

					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					lv_counterman.startAnimation(anim);
					anim = null;

				}
				break;

			case MedicinesActivity.HANDLER_GEDATA_FAILD:
				// datas.clear();
				// adapter.notifyDataSetChanged();
				loading.setVisibility(View.VISIBLE);
				lv_counterman.setVisibility(View.GONE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				text.setText("没有可用数据");

				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.lv_counterman:
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putSerializable(OrderManageActivity.COUNTER_MAN_DATA,
					datas.get(position));
			intent.putExtras(data);
			setResult(Config.RESULT_CODE_CHOOSE_COUNTMAN, intent);
			finish();
			break;

		default:
			break;
		}

	}
}
