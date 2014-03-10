package xzd.mobile.android.ui;

import xzd.mobile.android.R;
import xzd.mobile.android.AppContext;
import xzd.mobile.android.UIHelper;
import xzd.mobile.android.business.LocateService;
import xzd.mobile.android.business.LocationCollection;
import xzd.mobile.android.business.LoginHelper;
import xzd.mobile.android.model.Config;
import xzd.mobile.android.thread.CommonThread;
import xzd.mobile.android.ui.wdiget.MyImageView;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends BaseActivity implements OnClickListener {
	private final String TAG = "xzd.mobile.android.ui.MainActivity";

	private UIHelper uiHelper = null;
	private AppContext appcontext = null;
	public static final int STARTMETHOD_LOGINSUCCESS = 2;
	public static final int STARTMETHOD_START = 3;
	public static final String STARTMETHOD = "startmethod";
	private MyImageView terminate_manage = null;
	private MyImageView orders_manage = null;
	private MyImageView terminate_visit = null;
	private MyImageView mylocation = null;
	private MyImageView customer_cosum = null;
	private MyImageView main_document = null;
	private MyImageView main_count = null;
	private MyImageView main_contact = null;

	private LocateService mBoundService = null;
	private MainHandler mainHandler = null;
	private int startMethod = 0;
	private Intent locationCollectionIntent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		uiHelper = UIHelper.getInstance();
		appcontext = (AppContext) getApplication();

		this.getPreActivityData();
		this.initData();
		this.initView();
		if (appcontext.isNetworkConnected()) {
			CommonThread.getInstance().ConfirmIsCounterMan(appcontext,
					mainHandler);
			CommonThread.getInstance().GetTodayOrdersCount(appcontext,
					mainHandler);
		} else {
			UIHelper.Toast(this, R.string.http_exception_error);
		}

		startService(locationCollectionIntent);
	}

	@Override
	public void getPreActivityData() {
		startMethod = getIntent().getIntExtra(STARTMETHOD, 0);
	}

	@Override
	public void initView() {

		terminate_manage = (MyImageView) findViewById(R.id.terminate_manage);
		orders_manage = (MyImageView) findViewById(R.id.orders_manage);
		terminate_visit = (MyImageView) findViewById(R.id.terminate_visit);
		mylocation = (MyImageView) findViewById(R.id.mylocation);
		customer_cosum = (MyImageView) findViewById(R.id.customer_cosum);
		main_document = (MyImageView) findViewById(R.id.main_document);
		main_count = (MyImageView) findViewById(R.id.main_count);
		main_contact = (MyImageView) findViewById(R.id.main_contact);

		terminate_manage.setOnClickListener(this);
		orders_manage.setOnClickListener(this);
		terminate_visit.setOnClickListener(this);
		mylocation.setOnClickListener(this);
		customer_cosum.setOnClickListener(this);
		main_document.setOnClickListener(this);
		main_count.setOnClickListener(this);
		main_count.setOnClickListener(this);
		main_contact.setOnClickListener(this);
	}

	@Override
	public void initData() {
		mainHandler = new MainHandler();
		locationCollectionIntent = new Intent(this, LocationCollection.class);
	}

	@Override
	public void onClick(View v) {
		Bundle bundle = null;
		switch (v.getId()) {
		case R.id.terminate_manage:
			if (AppContext.is_login()) {
				uiHelper.ShowTerminateMangeActivity(this);
			}

			break;

		case R.id.orders_manage:
			if (AppContext.is_login()) {
				bundle = new Bundle();
				bundle.putInt(OrderManageActivity.START_METHOD, 0);
				UIHelper.showOrderManage(this,bundle);
			}
			break;
		case R.id.terminate_visit:
			if (AppContext.is_login()) {
				uiHelper.ShowTerminateVisitActivity(this);
			}
			break;
		case R.id.mylocation:
			uiHelper.showLocationActivity(this);
			break;
		case R.id.customer_cosum:
			if (AppContext.is_login()) {
				uiHelper.ShowCustomerConsumeActivity(this);
			}
			break;
		case R.id.main_contact:
			if (AppContext.is_login()) {
				uiHelper.showContact(this);
			}
			break;
		}
	}

	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBoundService = ((LocateService.LocalBinder) service).getService();
			// button1.setEnabled(false);
			Log.e(TAG,
					getString(R.string.locate_bind_success)
							+ mBoundService.toString());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// button1.setEnabled(true);
			Log.e(TAG, getString(R.string.locate_unbind));
			mBoundService = null;// 将数据置空
		}

	};

	public final class MainHandler extends android.os.Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case Config.HANDLER_GET_MAIN_ID_FAILED:

				break;
			case Config.HANDLER_GET_MAN_ID_SUCCESS:
				Log.d(TAG, "Get CounterMan Successfully");
				LoginHelper.getInstance().saveUserInfo(appcontext,
						AppContext.user);
				break;
			case Config.HANDLER_GET_TODAY_COUNT_SUCCESS:
				Log.d(TAG, "Get Today Orders Count Success");
				break;
			case Config.HANDLER_GET_TODAY_COUNT_FAILED:
				Log.e(TAG, "Get Today Orders Count failed");
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		LoginHelper.getInstance().saveUserInfo(appcontext, AppContext.user);
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_exits:
			LoginHelper.getInstance().saveUserInfo(appcontext, AppContext.user);
			Bundle data = new Bundle();
			data.putInt(LoginActivity.LOGINTYPE, LoginActivity.LOGOUT);
			uiHelper.showLoginActivty(this, data);
			data = null;
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
		stopService(locationCollectionIntent);
		appcontext.appmanager.AppExit(appcontext);
	}

}
