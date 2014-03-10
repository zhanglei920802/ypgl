package xzd.mobile.android.ui;

import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;

import java.util.ArrayList;
import java.util.List;

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
import xzd.mobile.android.AppContext;
import xzd.mobile.android.business.OrderHelper;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.Medicine;
import xzd.mobile.android.ui.adapter.MyMedicineSimpleAdapter;
import xzd.mobile.android.ui.intf.ActivityItf;

/**
 * 二〇一三年七月七日 00:17:46
 * 
 * @author ZhangLei
 * 
 */
public class MedicinesActivity extends BaseActivity implements ActivityItf,
		OnItemClickListener, OnClickListener {
	public static final String TAG = "MedicinesActivity";

	public static final int HANDLER_GETDATA_SUCCESS = 1;
	public static final int HANDLER_GEDATA_FAILD = -1;
	public static final String MEDCINECHOOSEDATA = "medcinechoosedata";
	private OrderHelper orderhelper = null;
	private AppContext appcontext = null;
	private UIHelper uihelper = null;

	private ListView medicine_list = null;
	private LinearLayout loading = null;
	private ProgressBar progressbar = null;
	private TextView text = null;
	private TextView add = null;
	private TextView go_back = null;
	private TextView detail_title = null;
	// private LinearLayout btn_add = null;
	// private ImageView btn_send = null;

	// data
	private MyMedicineSimpleAdapter adapter = null;
	private List<Medicine> datas = null;
	private MedicineHandler medicinehandler = null;
	private int saler_id = 0;

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("参数错误！");
		}
		saler_id = intent.getIntExtra(MEDCINECHOOSEDATA, 0);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		medicine_list = (ListView) findViewById(R.id.medicine_list);
		loading = (LinearLayout) findViewById(R.id.loading);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		text = (TextView) findViewById(R.id.text);
		detail_title = (TextView) findViewById(R.id.detail_title);
		// btn_add = (LinearLayout) findViewById(R.id.btn_add);
		// btn_send = (ImageView) findViewById(R.id.btn_send);

		medicine_list.setOnItemClickListener(this);
		text.setText("正在获取中,请稍候");
		detail_title.setText("药品选择");
		// btn_add.setOnClickListener(this);
		// btn_send.setImageResource(R.drawable.send);
		medicine_list.setAdapter(adapter);
		medicine_list.setVisibility(View.GONE);
		loading.setVisibility(View.VISIBLE);
		add = (TextView) findViewById(R.id.confrim);
		add.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		datas = new ArrayList<Medicine>();
		adapter = new MyMedicineSimpleAdapter(this, datas);
		medicinehandler = new MedicineHandler();
		LoadData();
	}

	private void LoadData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<Medicine> obj = null;
				Message msg = medicinehandler.obtainMessage();
				try {
					obj = orderhelper.getMedicineList(saler_id);

					if (obj == null) {
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
				medicinehandler.sendMessage(msg);

			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		orderhelper = OrderHelper.getInstance();
		appcontext = (AppContext) getApplication();
		uihelper = UIHelper.getInstance();

		setContentView(R.layout.medicines_choose);
		getPreActivityData();
		initData();
		initView();

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

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.medicine_list:
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putSerializable(AddOrderActivity.CHOOSE_MEDICINE,
					datas.get(position));
			intent.putExtras(data);
			setResult(Config.RESULT_CODE_CHOOSE_MEDICINE, intent);
			finish();
			break;

		default:
			break;
		}

	}

	private final class MedicineHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MedicinesActivity.HANDLER_GETDATA_SUCCESS:
				if (msg.obj != null) {
					datas.clear();
					datas.addAll((List<Medicine>) msg.obj);

					adapter.notifyDataSetChanged();

					loading.setVisibility(View.GONE);
					medicine_list.setVisibility(View.VISIBLE);

					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					medicine_list.startAnimation(anim);
					anim = null;

				}
				break;

			case MedicinesActivity.HANDLER_GEDATA_FAILD:
				// datas.clear();
				// adapter.notifyDataSetChanged();
				loading.setVisibility(View.VISIBLE);
				medicine_list.setVisibility(View.GONE);
				progressbar.setVisibility(View.GONE);
				text.setVisibility(View.VISIBLE);
				text.setText("没有可用数据");

				break;
			}
		}
	}
}
