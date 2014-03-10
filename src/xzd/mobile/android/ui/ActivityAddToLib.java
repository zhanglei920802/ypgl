package xzd.mobile.android.ui;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.LocateService;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.CustomerSimple;
import xzd.mobile.android.model.Medicine;
import xzd.mobile.android.model.VisitRecordInfo;
import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ActivityAddToLib extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {

	public static final String TAG = "ActivityAddToLib";

	public static final int HANDLER_PUB_ORDER_SUCCESS = 1;
	public static final int HANDLER_PUB_ORDER_FAILED = -1;
	public static final int HANDLER_PUB_ORDER_EXCEPTION = 2;
	public static final int LOCATE_SUCCESS = 3;
	public static final int LOCATE_FAILED = 4;
	public static final int HANDLER_GET_ORDER_SUCCESS = 7;
	public static final int HANDLER_GET_ORDER_FAILED = 8;
	public static final int HANDLER_UPDATE_ORDER_SUCCESS = 9;
	public static final int HANDLER_UPDATE_ORDER_FAILED = 10;

	public static final String VISIT_DATA = "visit_data";
	public static final String VISIST_ID = "visist_id";
	public static final String ACTION = "action";

	public static final int ACTION_ADD = 1;
	public static final int ACTION_EDIT = 2;
	public static final int ACTION_ADD2 = 3;// 从详情中进行添加
	private CustomerSimple customerSimple = null;// 如果是添加库存,次参数从上一个界面传过来
	private Medicine medicine = null;// 当用户选择药品之后,
	private int action_type = 0;;// 操作类型
	private int visit_id = 0;// 拜访ID
	private VisitRecordInfo visitRecordInfo = null;
	private AppContext appContext = null;
	private UIHelper uiHelper = null;
	private StringUtils stringUtils = null;
	private TerminateBiz terminateBiz = null;

	// /View
	private TextView detail_title = null;
	private TextView go_back = null;
	private TextView confrim = null;
	private MyLoadingDialog loadingDialog = null;

	// Content
	private TextView medicine_name = null;
	private TextView customer_name = null;
	private TextView count = null;;
	private String location = "";
	// data
	private AddToLibHandler addToLibHandler = null;

	// locate
	// 定位
	private LocateService mBoundService = null;
	private MyServiceConnection myserviceconnection = null;
	private boolean isBind = false;
	private boolean locationFinish = false;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.medicine_name:
			if (chklogin()) {
				Intent intent = new Intent(this, MedicinesActivity.class);
				intent.putExtra(MedicinesActivity.MEDCINECHOOSEDATA,
						AppContext.user.getM_id());
				this.startActivityForResult(intent,
						Config.REQUEST_CODE_CHOOSE_MEDICNE);
				this.overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			} else {
				UIHelper.Toast(this, "您还未登录");
			}
			break;
		case R.id.confrim:
			if (chklogin()) {
				if (!chkIpt()) {
					if (appContext.isNetworkConnected()) {
						if (action_type == ACTION_ADD) {

							// pubOrder();
							pubAddToLib(
									AppContext.user.getM_id(),
									visit_id,
									customerSimple.getCustomer_id(),
									medicine.getMedicine_id(),
									Integer.valueOf(count.getText().toString()),
									location);
						} else if (action_type == ACTION_ADD2) {
							pubAddToLib(
									AppContext.user.getM_id(),
									visitRecordInfo.getVisitRecord_id(),
									visitRecordInfo.getCustomer_ID(),
									medicine.getMedicine_id(),
									Integer.valueOf(count.getText().toString()),
									visitRecordInfo.getVisit_location());
						}
					} else {
						UIHelper.Toast(this, R.string.http_exception_error);
					}
				} else {
					UIHelper.Toast(this, "对不起,您的输入有误");
				}
			}

			break;
		case R.id.go_back:
			onDestroy();
			break;
		}
	}

	private boolean chkIpt() {
		// TODO Auto-generated method stub
		return stringUtils.isEmpty(count.getText().toString())
				|| stringUtils.isEmpty(medicine_name.getText().toString())
				|| stringUtils.isEmpty(customer_name.getText().toString());
	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("You Must Provide params!!!");
		}
		action_type = intent.getIntExtra(ACTION, ACTION_ADD);
		if (action_type == ACTION_ADD) {
			visit_id = intent.getIntExtra(VISIST_ID, 0);
			customerSimple = (CustomerSimple) intent
					.getSerializableExtra(VISIT_DATA);

		} else if (action_type == ACTION_ADD2) {
			visitRecordInfo = (VisitRecordInfo) intent
					.getSerializableExtra(VISIT_DATA);
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		medicine_name = (TextView) findViewById(R.id.medicine_name);
		customer_name = (TextView) findViewById(R.id.customer_name);
		count = (TextView) findViewById(R.id.count);
		medicine_name.setOnClickListener(this);
		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		detail_title = (TextView) findViewById(R.id.detail_title);

		loadingDialog = new MyLoadingDialog(R.string.loading, this);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setOnClickListener(this);
		if (action_type == ACTION_ADD) {
			detail_title.setText("添加库存");
			customer_name.setText(customerSimple.getCustomer_Name());
		} else if (action_type == ACTION_EDIT) {
			detail_title.setText("修改库存");
		} else if (action_type == ACTION_ADD2) {
			detail_title.setText("添加库存");
			customer_name.setText(visitRecordInfo.getCustomer_Name() + "");

		}
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		addToLibHandler = new AddToLibHandler();
		myserviceconnection = new MyServiceConnection();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_to_lib);
		appContext = (AppContext) getApplication();
		uiHelper = UIHelper.getInstance();
		stringUtils = StringUtils.getInstance();
		terminateBiz = TerminateBiz.getInstance();

		getPreActivityData();
		initData();
		initView();
		if (action_type == ACTION_ADD) {
			doBindService();
		} else {

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case Config.RESULT_CODE_CHOOSE_MEDICINE:
			medicine = (Medicine) data
					.getSerializableExtra(AddOrderActivity.CHOOSE_MEDICINE);
			medicine_name.setText(medicine.getMedicine_Name());
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private final class AddToLibHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);

			switch (msg.what) {
			case HANDLER_PUB_ORDER_SUCCESS:
				loadingDialog.dismiss();
				medicine_name.setText("");
				count.setText("");
				showAddDialog();

				break;

			case HANDLER_PUB_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(ActivityAddToLib.this, "添加失败");
				break;

			case HANDLER_PUB_ORDER_EXCEPTION:
				loadingDialog.dismiss();

				break;
			case HANDLER_GET_ORDER_FAILED:
				loadingDialog.dismiss();

				break;
			case HANDLER_GET_ORDER_SUCCESS:
				loadingDialog.dismiss();

				break;
			case HANDLER_UPDATE_ORDER_FAILED:

				break;
			case HANDLER_UPDATE_ORDER_SUCCESS:

				break;
			case LOCATE_SUCCESS:// 定位成功
				// Log.e("定位处理器", "接收到定位数据:" + msg.obj.toString());
				if (msg.obj != null) {
					location = (String) msg.obj;
					// 定位完成
					locationFinish = true;
					UnBindService();
				}

				break;
			}
		}
	}

	private boolean chklogin() {
		// TODO Auto-generated method stub
		return AppContext.is_login();
	};

	public void showAddDialog() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("添加库存");
		builder.setMessage("添加库成功,是否继续添加?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
			}
		});
		builder.create();
		builder.show();

	}

	/**
	 * 开始定位服务
	 */
	private void doBindService() {

		bindService(new Intent(ActivityAddToLib.this, LocateService.class),
				myserviceconnection, Context.BIND_AUTO_CREATE);

	}

	/**
	 * 解绑定位服务
	 */
	public void UnBindService() {
		unbindService(myserviceconnection);
	}

	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((LocateService.LocalBinder) service).getService();
			isBind = true;
			StartLocation(addToLibHandler);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBind = false;
			mBoundService = null;// 将数据置空
		}

	}

	public void StartLocation(final AddToLibHandler mHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = mHandler.obtainMessage();

				while (true) {
					if (null != mBoundService
							&& !stringUtils.isEmpty(mBoundService.getAdd())
							&& !locationFinish) {// 精度小雨1000

						msg.what = LOCATE_SUCCESS;// 定位成功
						msg.obj = mBoundService.getAdd();
						mHandler.sendMessage(msg);
						break;

					} else {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();
	}

	private void pubAddToLib(final int userid, final int visitid,
			final int customerid, final int medicineid, final int num,
			final String location) {
		loadingDialog.show();
		loadingDialog.setCanceledOnTouchOutside(false);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = addToLibHandler.obtainMessage();
				int result;
				try {
					result = terminateBiz.SaveStoChangeInfo(userid, visitid,
							customerid, medicineid, num, location);
					if (result > 0) {
						msg.what = HANDLER_PUB_ORDER_SUCCESS;

					} else {
						msg.what = HANDLER_PUB_ORDER_FAILED;

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = HANDLER_PUB_ORDER_EXCEPTION;
				}

				addToLibHandler.sendMessage(msg);
			}
		}).start();
	}
}
