package xzd.mobile.android.ui;

import xzd.mobile.android.AppContext;
import xzd.mobile.android.R;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.LocateService;
import xzd.mobile.android.business.TerminateBiz;
import xzd.mobile.android.common.StringUtils;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.model.VisitRecordInfo;
import xzd.mobile.android.ui.intf.ActivityItf;
import xzd.mobile.android.ui.wdiget.MyLoadingDialog;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;

public class AddRemark extends BaseActivity implements ActivityItf,
		OnClickListener, OnItemClickListener {

	public static final String TAG = "AddRemark";

	public static final int HANDLER_PUB_ORDER_SUCCESS = 1;
	public static final int HANDLER_PUB_ORDER_FAILED = -1;
	public static final int HANDLER_PUB_ORDER_EXCEPTION = 2;
	public static final int LOCATE_SUCCESS = 3;
	public static final int LOCATE_FAILED = 4;
	public static final int HANDLER_GET_ORDER_SUCCESS = 7;
	public static final int HANDLER_GET_ORDER_FAILED = 8;
	public static final int HANDLER_UPDATE_ORDER_SUCCESS = 9;
	public static final int HANDLER_UPDATE_ORDER_FAILED = 10;

	private int action_type = 0;;// 操作类型
	private int visit_id = 0;// 拜访ID
	private VisitRecordInfo visitRecordInfo = null;
	private AppContext appContext = null;
	private UIHelper uiHelper = null;
	private StringUtils stringUtils = null;
	private TerminateBiz terminateBiz = null;

	public static final String VISIT_DATA = "visit_data";
	// /View
	private TextView detail_title = null;
	private TextView go_back = null;
	private TextView confrim = null;
	private MyLoadingDialog loadingDialog = null;

	// Content
	private TextView saler_name_content = null;
	private TextView custum_name_content = null;
	private TextView visit_detail_content = null;;
	private EditText mark_content_content = null;
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
		case R.id.go_back:
			onDestroy();
			break;
		case R.id.confrim:
			if (!AppContext.is_login()) {
				UIHelper.Toast(this, R.string.xzd_no_login);
				return;
			}

			if (!appContext.isNetworkConnected()) {

				UIHelper.Toast(this, R.string.network_not_connected);
				return;
			}

			if (StringUtils.getInstance().isEmpty(
					mark_content_content.getText().toString())) {

				UIHelper.Toast(this, R.string.input_can_not_be_null);
				return;
			}

			if (null == visitRecordInfo) {
				return;
			}

			SaveVisitReview(visitRecordInfo.getVisitRecord_id(),
					AppContext.user.getM_id(), AppContext.user.getM_mgr_name(),
					mark_content_content.getText().toString(), location);
			break;
		}
	}

	public void SaveVisitReview(final int visitid, final int managerid,
			final String managername, final String answer, final String location) {
		if (loadingDialog == null) {
			loadingDialog = new MyLoadingDialog(R.string.loading, this);
			loadingDialog.setCanceledOnTouchOutside(false);
		}
		loadingDialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					int code = TerminateBiz.SaveVisitReview(visitid, managerid,
							managername, answer, location);
					if (code > 0) {
						addToLibHandler.sendEmptyMessage(11);
					} else {
						addToLibHandler.sendEmptyMessage(-11);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					addToLibHandler.sendEmptyMessage(-11);
				}

			}
		}).start();

	}

	@Override
	public void getPreActivityData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (null == intent) {
			throw new IllegalArgumentException("You Must Provide params!!!");
		}
		visitRecordInfo = (VisitRecordInfo) getIntent().getSerializableExtra(VISIT_DATA);
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		go_back = (TextView) findViewById(R.id.go_back);
		go_back.setOnClickListener(this);
		detail_title = (TextView) findViewById(R.id.detail_title);

		loadingDialog = new MyLoadingDialog(R.string.loading, this);
		confrim = (TextView) findViewById(R.id.confrim);
		confrim.setOnClickListener(this);

		// content
		{
			// private TextView custum_name_content = null;
			// private TextView visit_detail_content = null;
			// ;
			// private EditText mark_content_content = null;

			saler_name_content = (TextView) findViewById(R.id.saler_name_content);
			custum_name_content = (TextView) findViewById(R.id.custum_name_content);
			visit_detail_content = (TextView) findViewById(R.id.visit_detail_content);
			mark_content_content = (EditText) findViewById(R.id.mark_content_content);

			if (null != visitRecordInfo) {
				saler_name_content.setText(visitRecordInfo.getSaler_Name());
				custum_name_content.setText(visitRecordInfo.getCustomer_Name());
				visit_detail_content
						.setText(visitRecordInfo.getVisit_Address());
			}

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

		setContentView(R.layout.xzd_add_lib);
		appContext = (AppContext) getApplication();
		uiHelper = UIHelper.getInstance();
		stringUtils = StringUtils.getInstance();
		terminateBiz = TerminateBiz.getInstance();

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

				break;

			case HANDLER_PUB_ORDER_FAILED:
				loadingDialog.dismiss();
				UIHelper.Toast(AddRemark.this, "添加失败");
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
			case 11:
				UIHelper.Toast(AddRemark.this, "点评成功");
				onDestroy();
				break;
			case -11:
				UIHelper.Toast(AddRemark.this, "点评失败");
				// onDestroy();
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

	/**
	 * 开始定位服务
	 */
	private void doBindService() {

		bindService(new Intent(AddRemark.this, LocateService.class),
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
}
