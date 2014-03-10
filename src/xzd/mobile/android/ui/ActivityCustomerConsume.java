package xzd.mobile.android.ui;

import java.util.ArrayList;
import java.util.List;
import xzd.mobile.android.AppContext;
import xzd.mobile.android.AppException;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustomerConsume;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.Medicine;
import xzd.mobile.android.model.SalerSimple;
import xzd.mobile.android.ui.adapter.AdaperConsume;
import xzd.mobile.android.ui.intf.ActivityItf;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActivityCustomerConsume extends BaseActivity implements
		ActivityItf, OnClickListener, OnItemClickListener, OnScrollListener {

	public static final String TAG = "ActivityCustomerConsume";

	public static final int HANDLER_ERROR = -1;
	public static final int HANDLER_SUCCESS = 1;
	public static final int HANDLER_GET_MAN_ID_SUCCESS = 2;
	public static final int HANDLER_GET_MAIN_ID_FAILED = 3;
	// Helper
	private UIHelper uihelper = null;
	private StringUtils stringutils = null;
	private AppContext appcontext = null;
	private TerminateBiz terminateBiz = null;

	private TextView detail_title = null;
	private ListView lv_consume = null;
	private TextView add = null;
	private TextView go_back = null;
	private Button btn_choose = null;
	// Loading
	private ProgressBar progressbar = null;
	private TextView text = null;
	private LinearLayout loading = null;

	// ************************************************data**********************************************///
	private CustomerSimple chooed_custom = null;;// 选择的销售人员
	private Medicine chooed_medine = null;
	private SalerSimple choosedSalerMan;;// 选择药品

	private List<CustomerConsume> datas = null;
	private AdaperConsume adpter = null;
	private TerminateHandeler terminateHandeler = null;

	// *************************************************data END

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.lv_consume:
			if (position < 1 || datas == null || datas.isEmpty()
					|| chooed_custom == null || chooed_medine == null) {
				return;
			}
			Bundle bundle = new Bundle();
			bundle.putInt(ConsDetail.EXTRAS_CUSTUMID,
					chooed_custom.getCustomer_id());
			bundle.putInt(ConsDetail.EXTRAS_MEDICINEID,
					chooed_medine.getMedicine_id());
			bundle.putSerializable(ConsDetail.EXTRAS_DATA, datas.get(position));
			UIHelper.showConsDetail(this, bundle);
			bundle = null;
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_choose:
			if (AppContext.is_login()) {
				if (appcontext.isNetworkConnected()) {// 检测网络是否连接
					if (AppContext.user.getCounterMain_ID() == 0) {// 销售人员

					} else {// 管理人员,选择销售人员

					}
				} else {
					UIHelper.Toast(this, R.string.http_exception_error);
				}
			} else {
				UIHelper.Toast(this, "对不起,您还未进行登录");
			}
			break;

		case R.id.go_back:
			onDestroy();
			break;
		}
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		detail_title = (TextView) findViewById(R.id.detail_title);
		detail_title.setText("客户消费");
		lv_consume = (ListView) findViewById(R.id.lv_consume);

		lv_consume.setAdapter(adpter);
		lv_consume.setOnItemClickListener(this);
		lv_consume.setOnScrollListener(this);

		add = (TextView) findViewById(R.id.add);
		add.setOnClickListener(this);

		loading = (LinearLayout) findViewById(R.id.loading);
		loading.setVisibility(View.GONE);
		text = (TextView) findViewById(R.id.text);
		text.setText("没有数据,点击按钮选择加载");
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		loading.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.GONE);
		text.setVisibility(View.VISIBLE);
		lv_consume.setVisibility(View.GONE);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		btn_choose = (Button) findViewById(R.id.btn_choose);
		btn_choose.setOnClickListener(this);
		btn_choose.setVisibility(View.GONE);
		if (AppContext.user.getCounterMain_ID() == 1
				|| AppContext.user.getCounterMain_ID() == 2) {

			// 选择销售人员

			Intent intent = new Intent(this, ActivityCounterManChoose.class);
			intent.putExtra(ActivityCounterManChoose.COUNTERMANDATA,
					AppContext.user.getM_id());
			startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_CONTERMAN);
		} else if (AppContext.user.getCounterMain_ID() == 0) {
			// 隐藏选择框

			// 选择终端客户
			Intent intent = new Intent(this, CustomChooseV2.class);
			intent.putExtra(CustomChooseV2.CUSTOMCHOOSEDATA,
					AppContext.user.getSale_id());
			startActivityForResult(intent, Config.REQUEST_CODE_CHOOSE_CUSTOM);
		}

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		terminateHandeler = new TerminateHandeler();
		datas = new ArrayList<CustomerConsume>();
		adpter = new AdaperConsume(datas, this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminate_consume);
		appcontext = (AppContext) getApplication();
		if (!AppContext.is_login() || AppContext.user.getCounterMain_ID() == -1) {
			UIHelper.Toast(this, R.string.xzd_init_user_faild);
			onDestroy();
			return;
		}

		if (!appcontext.isNetworkConnected()) {

			UIHelper.Toast(this, R.string.zxd_no_network);
		}

		uihelper = UIHelper.getInstance();
		stringutils = StringUtils.getInstance();
		appcontext = (AppContext) getApplication();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();
		// getData();

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (resultCode) {
		case Config.RESULT_CODE_CHOOSE_CUSTOM:// 选择终端客户

			chooed_custom = (CustomerSimple) data
					.getSerializableExtra(AddOrderActivity.CHOOSE_CUSTOM);
			Log.d(TAG, "选择终端客户:" + chooed_custom.toString());
			// 选择药品
			if (chklogin()) {
				if (AppContext.user.getCounterMain_ID() == 1
						|| AppContext.user.getCounterMain_ID() == 2) {

					// 选择销售人员

					if (choosedSalerMan == null) {
						return;
					}
					intent = new Intent(this, MedicinesV2.class);
					intent.putExtra(MedicinesV2.MEDCINECHOOSEDATA,
							choosedSalerMan.getSaler_id());
					startActivityForResult(intent,
							Config.REQUEST_CODE_CHOOSE_MEDICNE);
				} else if (AppContext.user.getCounterMain_ID() == 0) {
					// 隐藏选择框

					intent = new Intent(this, MedicinesActivity.class);
					intent.putExtra(MedicinesActivity.MEDCINECHOOSEDATA,
							AppContext.user.getM_id());
					startActivityForResult(intent,
							Config.REQUEST_CODE_CHOOSE_MEDICNE);
				}

			} else {
				UIHelper.Toast(appcontext, R.string.http_exception_error);
			}

			break;

		case Config.RESULT_CODE_CHOOSE_MEDICINE:// 选择药品

			chooed_medine = (Medicine) data
					.getSerializableExtra(AddOrderActivity.CHOOSE_MEDICINE);
			Log.d(TAG, "选择药品:" + chooed_medine.toString());
			// 加载数据
		
			if (AppContext.user.getCounterMain_ID() == 0) {
				if(chooed_custom==null || chooed_medine ==null){
					return ;
				}
				LoadData(AppContext.user.getSale_id(),
						chooed_custom.getCustomer_id(),
						chooed_medine.getMedicine_id(), terminateHandeler);
			} else if (AppContext.user.getCounterMain_ID() == 1
					|| AppContext.user.getCounterMain_ID() == 2) {
				if(chooed_custom==null || chooed_medine ==null || choosedSalerMan==null){
					return ;
				}
				LoadData(choosedSalerMan.getSaler_id(),
						chooed_custom.getCustomer_id(),
						chooed_medine.getMedicine_id(), terminateHandeler);
			}

			break;
		case Config.RESULT_CODE_CHOOSE_COUNTMAN:// 选择销售人员
			if (AppContext.user.getCounterMain_ID() == 0) {// 销售人员不执行
				return;
			}

			choosedSalerMan = (SalerSimple) data
					.getSerializableExtra(OrderManageActivity.COUNTER_MAN_DATA);

			if (choosedSalerMan == null) {
				return;
			}
			// AppContext.user.gets
			Log.d(TAG, "选择销售人员:" + choosedSalerMan.toString());
			if (chklogin()) {
				// 选择终端客户
				intent = new Intent(this, CustomChooseV2.class);
				intent.putExtra(CustomChooseV2.CUSTOMCHOOSEDATA,
						choosedSalerMan.getSaler_id());
				startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_CUSTOM);
			} else {
				UIHelper.Toast(appcontext, R.string.http_exception_error);
			}

			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private final class TerminateHandeler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_SUCCESS:
				if (msg.obj != null) {
					datas.clear();

					datas.addAll((List<CustomerConsume>) msg.obj);

					adpter.notifyDataSetChanged();

					loading.setVisibility(View.GONE);
					lv_consume.setVisibility(View.VISIBLE);

					AlphaAnimation anim = new AlphaAnimation(0.7f, 1.0f);
					lv_consume.startAnimation(anim);
					anim = null;

				}
				break;

			case HANDLER_ERROR:

				UIHelper.Toast(ActivityCustomerConsume.this,
						R.string.http_exception_error);
				break;
			case HANDLER_GET_MAIN_ID_FAILED:

				break;
			case HANDLER_GET_MAN_ID_SUCCESS:
				loading.setVisibility(View.GONE);
				if (AppContext.user.getCounterMain_ID() == 0) {// 销售人员
					if (chklogin()) {
						Intent intent = new Intent(
								ActivityCustomerConsume.this,
								CustomChooseActivity.class);
						intent.putExtra(CustomChooseActivity.CUSTOMCHOOSEDATA,
								AppContext.user.getSale_id());
						startActivityForResult(intent,
								Config.REQUEST_CODE_CHOOSE_CUSTOM);

					}
				} else {// 选择counterman
					if (chklogin()) {
						Intent counterman_intent = new Intent(
								ActivityCustomerConsume.this,
								ActivityCounterManChoose.class);
						counterman_intent.putExtra(
								ActivityCounterManChoose.COUNTERMANDATA,
								AppContext.user.getM_id());
						startActivityForResult(counterman_intent,
								Config.REQUEST_CODE_CHOOSE_CONTERMAN);
					}
				}
				break;
			}
		}
	}

	private boolean chklogin() {
		// TODO Auto-generated method stub
		return AppContext.is_login();
	};

	private void LoadData(final int saleid, final int customerid,
			final int medicineid, final TerminateHandeler handler) {
		// TODO Auto-generated method stub
		/**
		 * 开启线程加载数据
		 */

		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();

				List<CustomerConsume> datas = null;
				try {
					Log.d(TAG, "Load DataIng please wait.....");
					datas = terminateBiz.GetCustomerConsume(saleid, customerid,
							medicineid);

					if (datas == null) {
						throw new AppException();
					}
					msg.what = HANDLER_SUCCESS;
					msg.obj = datas;

					Log.d(TAG, "Load Data successfully......");
				} catch (AppException e) {
					Log.d(TAG, "Load Data Faild......");
					e.printStackTrace();
					msg.what = HANDLER_ERROR;
					msg.obj = e;
				}

				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
}
